package sirgl.lexer

interface Lexer {
    fun tokenizeAll(text: CharSequence, skipWhitespace: Boolean, skipComments: Boolean) : List<Token>
}