package sirgl.lexer

data class Token(val text: CharSequence, val type: TokenType) {
    override fun toString(): String {
        return "Token(text=\"$text\", type=$type)"
    }

    fun pretty() : String = "$type@\"$text\""
}