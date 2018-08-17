package sirgl.lexer

import sirgl.language.Language

class TokenType(val tokenId: Short, val name: String, val language: Language) {
    override fun toString(): String = name

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TokenType) return false

        if (tokenId != other.tokenId) return false
        if (name != other.name) return false
        if (language != other.language) return false

        return true
    }

    override fun hashCode(): Int {
        var result = tokenId.toInt()
        result = 31 * result + name.hashCode()
        result = 31 * result + language.hashCode()
        return result
    }


}