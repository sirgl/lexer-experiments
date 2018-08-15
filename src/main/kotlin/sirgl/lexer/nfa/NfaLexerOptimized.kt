package sirgl.lexer.nfa

import sirgl.lexer.Lexer
import sirgl.lexer.LexerDefinition
import sirgl.lexer.Token
import sirgl.lexer.nfa.regex.*

// Lexer consumes definition (changes its internal structure in epsilon elimination)
class NfaLexerOptimized<T>(definition: LexerDefinition<T>) : Lexer<T>(){
    private val indicesToTokens = hashMapOf<Int, T>()
    val nfa = initNfa(definition)
    private val initialState = hashSetOf(nfa.entrace)
    private var currentStates = initialState
    private val endToken = definition.endLexeme
    private val whitespaceType = definition.whitespaces

    private fun initNfa(definition: LexerDefinition<T>): Nfa {
        val rules = definition.rules
        var index = 0
        val start = NfaNode()
        val end = NfaNode()
        for ((node, label) in rules) {
            val nfa = node.buildNFA()
            indicesToTokens[index] = label
            nfa.exit.endIndex = index
            index++
            start.edges.add(EpsilonEdge(nfa.entrace))
            nfa.exit.edges.add(EpsilonEdge(end))
        }
        eliminateEpsilonEdges(start)
        return Nfa(start, end)
    }

    override fun tokenize(text: CharSequence, skipWhitespace: Boolean): List<Token<T>> {
        val tokens = mutableListOf<Token<T>>()

        var startIndex = 0
        while (true) {
            val token = nextToken(startIndex, text)
                    ?: break
            if (!skipWhitespace || token.type != whitespaceType) {
                tokens.add(token)
            }
            val length = token.text.length
            if (length == 0) break
            startIndex += length
        }
        tokens.add(Token("<end>", endToken))
        return tokens
    }

    private fun nextToken(startIndex: Int, text: CharSequence) : Token<T>? {
        var currentToken: Token<T>? = null

        var index = startIndex
        val length = text.length
        while (index < length) {
            val codePoint = Character.codePointAt(text, index)
            val nextStates = hashSetOf<NfaNode>()
            // required for handling of the First rule wins
            var nextToken: Token<T>? = null
            var currentTokenIndex = Int.MAX_VALUE
            // immediately(through epsilon edge) reachable nodes check
            for (stateNode in currentStates) {
                val endIndex = stateNode.endIndex ?: continue
                if (endIndex > currentTokenIndex) continue
                currentTokenIndex = endIndex
                val tokenType = indicesToTokens[endIndex] ?: continue
                val tokenText = text.subSequence(startIndex, index)
                nextToken = Token(tokenText, tokenType)
            }
            if (nextToken != null) {
                currentToken = nextToken
            }
            var matched = false
            for (stateNode in currentStates) {
                val (exitIndex, matchedNodes) = stateNode.matchWithoutEpsilonClosure(codePoint)
                if (matchedNodes.isNotEmpty()) {
                    matched = true
                }
                if (exitIndex != null) {

                    val tokenType = indicesToTokens[exitIndex] ?: continue
                    if (exitIndex > currentTokenIndex) continue // Handling of the First rule wins
                    currentTokenIndex = exitIndex
                    val tokenText = text.subSequence(startIndex, index)
                    nextToken = Token(tokenText, tokenType)
                }
                // Handling of the First rule wins (required to apply to current token)
                currentToken = nextToken

                nextStates.addAll(matchedNodes)
            }
            if (!matched || nextStates.isEmpty()) {
                currentStates = initialState
                return currentToken
            }
            currentStates = nextStates

            index++
        }
        currentStates = initialState
        return currentToken
    }

}