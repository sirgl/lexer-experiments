package sirgl.lexer

import sirgl.language.Language
import sirgl.lexer.nfa.regex.RegexNode

/**
 * Order of declarations is important, rule that declared first will win in equal other conditions
 */
abstract class LexerDefinition {
    val tokenNames = mutableSetOf<String>()

    val rules = mutableMapOf<RegexNode, String>()

    val externalLexers = mutableMapOf<ExternalLexerDescription, String>()

    abstract val endLexeme: String

    abstract val whitespaces: Set<String> // TODO maybe place here TokenSet

    abstract val comments: Set<String>

    abstract val language: Language

    fun regex(node: RegexNode, tokenTypeName: String): String {
        rules[node] = tokenTypeName
        addTokenName(tokenTypeName)
        return tokenTypeName
    }

    fun addTokenName(tokenTypeName: String) {
        if (tokenNames.contains(tokenTypeName)) throw IllegalStateException("Name is not unique ($tokenTypeName) for language $language")
        tokenNames.add(tokenTypeName)
    }

    fun externalLexer(startNode: RegexNode, externalLexer: ExternalLexer, tokenTypeName: String): String {
        externalLexers[ExternalLexerDescription(startNode, externalLexer)] = tokenTypeName
        addTokenName(tokenTypeName)
        return tokenTypeName
    }
}

data class ExternalLexerDescription (
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