package sirgl.lexer

interface Lexer {
    fun tokenize(text: CharSequence, skipWhitespace: Boolean, skipComments: Boolean) : List<Token>
}