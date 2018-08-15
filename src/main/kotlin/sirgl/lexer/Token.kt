package sirgl.lexer

data class Token<T> (val text: CharSequence, val type: T) {
    override fun toString(): String {
        return "Token(text=\"$text\", type=$type)"
    }

    fun pretty() : String = "$type@\"$text\""
}