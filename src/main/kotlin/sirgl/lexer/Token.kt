package sirgl.lexer

data class Token(val length: Int, val type: TokenType) {
    override fun toString(): String {
        return "Token(length=\"$length\", type=$type)"
    }

    fun pretty(text: CharSequence) : String = "$type@\"${text.escape()}\""
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

fun Token.textFromPosition(fullText: CharSequence, offset: Int) : CharSequence {
    return fullText.subSequence(offset, offset + length)
}