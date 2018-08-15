package sirgl.lexer

abstract class Lexer<T> {
    abstract fun tokenize(text: CharSequence, skipWhitespace: Boolean) : List<Token<T>>
}