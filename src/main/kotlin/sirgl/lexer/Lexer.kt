package sirgl.lexer

interface Lexer<T> {
    fun tokenize(text: CharSequence, skipWhitespace: Boolean) : List<Token<T>>
}