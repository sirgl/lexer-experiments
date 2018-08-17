package sirgl.example.parser

import sirgl.ast.Function
import sirgl.lexer.Token

abstract class Parser {
    abstract fun parse(tokens: List<Token>) : Function
}