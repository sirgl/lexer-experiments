package sirgl.lexer.nfa

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import sirgl.lexer.*
import sirgl.lexer.nfa.regex.EpsilonEdge
import sirgl.lexer.nfa.regex.Nfa
import sirgl.lexer.nfa.regex.NfaNode

abstract class NfaLexerBase(definition: PreparedLexerDefinition, initialStateFinder: (Nfa) -> Set<NfaNode>) : Lexer {
    // TODO replace with primitive collections
    private val tokenTypes = definition.tokenTypes
    private val indicesToExternalLexer = hashMapOf<Int, ExternalLexer>() // tokenIndex to external lexer
    val nfa = initNfa(definition)
    private val initialState = initialStateFinder(nfa)
    private var currentStates = initialState
    private val endToken = definition.endLexemes
    private val whitespaceTokenTypes = definition.whitespaces
    private val commentTypes = definition.comments


    private fun initNfa(definition: PreparedLexerDefinition): Nfa {
        val rules = definition.rules.toMutableMap() // to copy
        val externalTokenToLexer = hashMapOf<TokenType, ExternalLexer>()
        for ((description, tokenType) in definition.externalLexers) {
            val (startNode, externalLexer) = description
            rules[startNode] = tokenType
            externalTokenToLexer[tokenType] = externalLexer
        }
        var index = 0
        val start = NfaNode()
        val end = NfaNode()
        for ((node, tokenType) in rules) {
            val nfa = node.buildNFA()
            tokenTypes[index] = tokenType
            val externalLexer = externalTokenToLexer[tokenType]
            if (externalLexer != null) {
                indicesToExternalLexer[index] = externalLexer
            }
            nfa.exit.endIndex = index
            index++
            start.edges.add(EpsilonEdge(nfa.entrace))
            nfa.exit.edges.add(EpsilonEdge(end))
        }
        val nfa = Nfa(start, end)
        postprocessNfa(nfa)
        return nfa
    }

    abstract fun postprocessNfa(nfa: Nfa)


    override fun tokenizeAll(text: CharSequence): List<Token> {
        val tokens = mutableListOf<Token>()

        var startIndex = 0
        while (true) {
            val token = nextToken(startIndex, text) ?: break
            tokens.add(token)
            val length = token.length
            if (length == 0) break // only end type can have zero size
            startIndex += length
        }
        tokens.add(Token(0, endToken))
        return tokens
    }

    private fun nextToken(startIndex: Int, text: CharSequence): Token? {
        var index = startIndex
        val length = text.length
        val candidateInfo = CandidateInfo()
        handleImmediatelyReachableNodes(text, index, candidateInfo, startIndex)
        while (index < length) {
            val codePoint = Character.codePointAt(text, index)
            val nextStates = hashSetOf<NfaNode>()

            // required for handling of the First rule wins
            val nextCandidate = CandidateInfo()

            // currently reachable nodes were checked at previous iteration
            // checking only next nodes
            for (stateNode in currentStates) {
                val matchedNodes = match(stateNode, codePoint)
                nextStates.addAll(matchedNodes)
                for (matchedNode in matchedNodes) {
                    handleCandidate(text, nextCandidate, matchedNode, index, false, startIndex)
                }
            }

            if (nextCandidate.isMeaningful()) {
                candidateInfo.endNodeIndex = nextCandidate.endNodeIndex
                candidateInfo.tokenTypeIndex = nextCandidate.tokenTypeIndex
            }
            currentStates = nextStates
            index++
        }
        currentStates = initialState
        if (!candidateInfo.isMeaningful()) return null
        val tokenType = tokenTypes[candidateInfo.tokenTypeIndex]
        return Token(candidateInfo.endNodeIndex - startIndex, tokenType)
    }

    abstract fun match(node: NfaNode, codePoint: Int): Collection<NfaNode>

    private fun handleImmediatelyReachableNodes(text: CharSequence, index: Int, candidateInfo: CandidateInfo, startIndex: Int) {
        for (stateNode in currentStates) {
            handleCandidate(text, candidateInfo, stateNode, index, true, startIndex)
        }
    }

    private fun handleCandidate(
            text: CharSequence,
            candidateInfo: CandidateInfo,
            node: NfaNode,
            index: Int,
            reachableOnThisIteration: Boolean,
            startIndex: Int
    ) {
        val tokenIndex = node.endIndex ?: return
        if (tokenIndex > candidateInfo.tokenTypeIndex) return
        val newEndNodeIndex = if (reachableOnThisIteration) index else index + 1
        if (newEndNodeIndex < candidateInfo.endNodeIndex) return
        val externalLexer = indicesToExternalLexer[tokenIndex]
        val endNodeIndexFinal = if (externalLexer != null) {
            externalLexer.tryTokenize(text, startIndex, newEndNodeIndex) ?: newEndNodeIndex
        } else {
            newEndNodeIndex
        }
        candidateInfo.tokenTypeIndex = tokenIndex
        candidateInfo.endNodeIndex = endNodeIndexFinal
    }
}

class CandidateInfo {
    var endNodeIndex: Int = -1 // index of the end of token
    var tokenTypeIndex: Int = Int.MAX_VALUE

    fun isMeaningful(): Boolean {
        return endNodeIndex != -1 || tokenTypeIndex != Int.MAX_VALUE
    }
}
