package sirgl.lexer.nfa

import sirgl.lexer.*
import sirgl.lexer.nfa.regex.EpsilonEdge
import sirgl.lexer.nfa.regex.Nfa
import sirgl.lexer.nfa.regex.NfaNode
import sirgl.lexer.nfa.regex.nodeEpsilonClosure

abstract class NfaLexerBase<T>(definition: LexerDefinition<T>) : Lexer<T> {
    protected val indicesToTokens = hashMapOf<Int, T>()
    protected val indicesToExternalLexer = hashMapOf<Int, ExternalLexer>()
    protected val nfa = initNfa(definition)
    protected val initialState = findInitialState(nfa.entrace)
    protected var currentStates = initialState
    protected val endToken = definition.endLexeme
    protected val whitespaceType = definition.whitespaces
    protected val commentType = definition.comments

    private fun findInitialState(entrance: NfaNode): Set<NfaNode> {
        return entrance.nodeEpsilonClosure()
    }

    private fun initNfa(definition: LexerDefinition<T>): Nfa {
        val rules = definition.rules.toMutableMap() // to copy
        val externalTokenToLexer = hashMapOf<T, ExternalLexer>()
        for ((description: ExternalTokenDescription, label: T) in definition.externalLexers) {
            val (startNode, externalLexer) = description
            rules[startNode] = label
            externalTokenToLexer[label] = externalLexer
        }
        var index = 0
        val start = NfaNode()
        val end = NfaNode()
        for ((node, label) in rules) {
            val nfa = node.buildNFA()
            indicesToTokens[index] = label
            val externalLexer = externalTokenToLexer[label]
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

    override fun tokenize(text: CharSequence, skipWhitespace: Boolean, skipComments: Boolean): List<Token<T>> {
        val tokens = mutableListOf<Token<T>>()

        var startIndex = 0
        while (true) {
            val token = nextToken(startIndex, text) ?: break

            if (
                    !(skipWhitespace && token.type == whitespaceType || skipComments && token.type == commentType)
            ) {
                tokens.add(token)
            }
            val length = token.text.length
            if (length == 0) break
            startIndex += length
        }
        tokens.add(Token("<end>", endToken))
        return tokens
    }

    private fun nextToken(startIndex: Int, text: CharSequence): Token<T>? {
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
        val tokenText = text.subSequence(startIndex, candidateInfo.endNodeIndex)
        val tokenType = indicesToTokens[candidateInfo.tokenTypeIndex]
                ?: throw IllegalStateException("Unexpected token index ${candidateInfo.tokenTypeIndex}")
        return Token(tokenText, tokenType)
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

