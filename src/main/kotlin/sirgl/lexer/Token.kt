package sirgl.lexer

data class Token(val text: CharSequence, val type: TokenType) {
    override fun toString(): String {
        return "Token(text=\"${text.escape()}\", type=$type)"
    }

    fun pretty() : String = "$type@\"${text.escape()}\""
}

fun CharSequence.escape() : CharSequence {
    return buildString {
        for (index in this@escape.indices) {
            val ch = this@escape[index]
            when (ch) {
                '\r' -> append("\\r")
                '\n' -> append("\\n")
                '\t' -> append("\\t")
                else -> append(ch)
            }
        }
    }
}