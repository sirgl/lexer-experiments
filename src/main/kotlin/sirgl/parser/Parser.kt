package sirgl.parser

import sirgl.SimpleTokenType
import sirgl.ast.Function
import sirgl.lexer.Token

abstract class Parser {
    abstract fun parse(tokens: List<Token<SimpleTokenType>>) : Function
}