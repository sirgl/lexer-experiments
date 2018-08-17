package sirgl.lexer

import sirgl.language.Language

// Language local
class TokenTypeFactory(private val language: Language) {
    private var nextTokenTypeId = 0.toShort()
    private val tokens = mutableMapOf<String, TokenType>()

    fun getOrCreate(name: String) : TokenType {
        val previous = tokens[name]
        if (previous != null) return previous
        val tokenType = TokenType(nextTokenTypeId, name, language)
        tokens[name] = tokenType
        nextTokenTypeId++
        return tokenType
    }

    fun get(name: String) : TokenType {
        return tokens[name]
                ?: throw IllegalStateException("Requested $name token type, but not found for this language")
    }
}