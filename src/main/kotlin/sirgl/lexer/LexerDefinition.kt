package sirgl.lexer

import sirgl.lexer.nfa.regex.RegexNode

/**
 * Order of declarations is important, rule that declared first will win in equal other conditions
 */
abstract class LexerDefinition<T> {
    // Maybe not even T, but factory for T
    val rules = mutableMapOf<RegexNode, T>()

    val externalLexers = mutableMapOf<ExternalTokenDescription, T>()

    abstract val endLexeme: T

    abstract val whitespaces: T // TODO maybe place here TokenSet

    abstract val comments: T

    fun regex(node: RegexNode, label: T): RegexNode {
        rules[node] = label
        return node
    }

    fun externalLexer(startNode: RegexNode, externalLexer: ExternalLexer, label: T): ExternalLexer {
        externalLexers[ExternalTokenDescription(startNode, externalLexer)] = label
        return externalLexer
    }
}

data class ExternalTokenDescription (
        val startNode: RegexNode,
        val externalLexer: ExternalLexer
)

interface ExternalLexer {
    /**
     *  Called when startNode met
     *  @param sequence all sequence to tokenize
     *  @param startIndex index of beginning of recognized startNode
     *  @param endIndexExclusive index of end of recognized startNode
     *  @return index of the end (exclusive) of token, if recognized, or null otherwise
     */
    fun tryTokenize(sequence: CharSequence, startIndex: Int, endIndexExclusive: Int) : Int?
}