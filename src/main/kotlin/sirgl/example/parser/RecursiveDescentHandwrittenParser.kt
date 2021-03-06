package sirgl.example.parser

import sirgl.ast.*
import sirgl.ast.Function
import sirgl.example.simple.SimpleTokenTypes
import sirgl.lexer.Token
import sirgl.lexer.TokenType


class ParseException(reason: String) : Exception(reason)

// Example parser

// functions expects, that conditions to parse what function represents already checked.
// no error recovery provided
class RecursiveDescentHandwrittenParser(private val tokenTypes: SimpleTokenTypes) : Parser() {
    override fun parse(tokens: List<Token>, text: CharSequence): Function {
        return ParserState(tokens, text, tokenTypes).parse()
    }
}

private class ParserState(private val tokens: List<Token>, private val text: CharSequence, val tokenTypes: SimpleTokenTypes) {
    var position: Int = 0
    private var textPosition = 0

    fun parse(): Function {
        val function = function()
        if (!at(tokenTypes.end)) fail("End expected")
        return function
    }

    // Service functions

    fun current(): Token {
        return tokens[position]
    }

    // use only this function to advance in token stream
    fun advance(): Token {
        val token = tokens[position]
        position++
        textPosition += token.length
        return token
    }

    fun currentTokenText() : CharSequence {
        val length = current().length
        return text.subSequence(textPosition, textPosition + length)
    }

    fun fail(reason: String) : Nothing {
        throw ParseException(reason)
    }

    fun expect(type: TokenType): Token {
        if (!at(type)) fail("Expected $type, but was ${current().type}")
        return advance()
    }

    // checks
    fun at(type: TokenType): Boolean {
        return current().type == type
    }

    // Rules

    private fun function(): Function {
        expect(tokenTypes.funKw)
        expect(tokenTypes.identifier)
        val name = currentTokenText()
        val parameterList = parameterList()
        val block = block()
        return Function(name.toString(), parameterList, BlockStmt(block))
    }

    private fun parameterList(): ParameterList {
        expect(tokenTypes.lPar)
        var first = true
        val parameters = mutableListOf<Parameter>()
        while (!at(tokenTypes.rPar)) { // TokenType required before
            if (first) {
                first = false
            } else {
                expect(tokenTypes.comma)
            }
            parameters.add(parameter())
        }
        advance()
        return ParameterList(parameters)
    }

    private fun block(): Block {
        expect(tokenTypes.lCurly)
        val stmts = mutableListOf<Stmt>()
        while (!at(tokenTypes.rCurly)) {
            stmts.add(stmt())
        }
        expect(tokenTypes.rCurly)
        return Block(stmts)
    }

    private fun expr(): Expr {
        val type = current().type
        return when (type) {
            tokenTypes.integralNumber -> numberLiteral()
            else -> fail("Expression expected, but found $type")
        }
    }

    private fun numberLiteral() : NumberLiteral {
        expect(tokenTypes.integralNumber)
        return NumberLiteral(currentTokenText().toString().toInt())
    }

    private fun exprStmt() : ExprStmt {
        val expr = expr()
        expect(tokenTypes.semi)
        return ExprStmt(expr)
    }

    private fun stmt(): Stmt {
        return when (current().type) {
            tokenTypes.valKw -> declStmt()
            else -> exprStmt()
        }

    }

    private fun declStmt(): DeclStmt {
        expect(tokenTypes.valKw)
        expect(tokenTypes.identifier)
        val name = currentTokenText().toString()
        expect(tokenTypes.colon)
        val typeElement = typeElement()
        val initializer = if (at(tokenTypes.eq)) {
            advance()
            expr()
        } else {
            null
        }
        expect(tokenTypes.semi)
        return DeclStmt(name, typeElement, initializer)
    }

    private fun parameter(): Parameter {
        expect(tokenTypes.identifier)
        val name = currentTokenText().toString()
        expect(tokenTypes.colon)
        val typeElement = typeElement()
        return Parameter(name, typeElement)
    }

    private fun typeElement(): TypeElement {
        expect(tokenTypes.identifier)
        return TypeElement(currentTokenText().toString())
    }
}