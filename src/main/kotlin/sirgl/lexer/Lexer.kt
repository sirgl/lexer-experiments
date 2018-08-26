package sirgl.lexer

interface Lexer {
    fun tokenizeAll(text: CharSequence) : List<Token>
}