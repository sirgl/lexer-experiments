package sirgl.lexer

import sirgl.lexer.nfa.regex.RegexNode

class PreparedLexerDefinition(definition: LexerDefinition, factory: TokenTypeFactory) {
    val endLexemes = factory.getOrCreate(definition.endLexeme)
    val whitespaces = TokenTypeSet(definition.whitespaces.map { factory.getOrCreate(it) })
    val comments = TokenTypeSet(definition.comments.map { factory.getOrCreate(it) })

    val rules: Map<RegexNode, TokenType> = definition.rules.mapValues { factory.getOrCreate(it.value) }

    val externalLexers: Map<ExternalLexerDescription, TokenType> =
            definition.externalLexers.mapValues { factory.getOrCreate(it.value) }

    /**
     * Ordered by strength
     */
    val tokenTypes: Array<TokenType> = definition.tokenNames.map { factory.getOrCreate(it) }.toTypedArray()

    val tokensCount: Int
        get() = rules.size
}

