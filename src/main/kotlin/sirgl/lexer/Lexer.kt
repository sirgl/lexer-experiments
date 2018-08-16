package sirgl.lexer

interface Lexer<T> {
    fun tokenize(text: CharSequence, skipWhitespace: Boolean, skipComments: Boolean) : List<Token<T>>
}