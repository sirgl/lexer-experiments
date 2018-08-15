package sirgl.parser

import sirgl.SimpleTokenType
import sirgl.ast.*
import sirgl.ast.Function
import sirgl.lexer.Token


class ParseException(reason: String) : Exception(reason)

// Example parser

// functions expects, that conditions to parse what function represents already checked.
// no error recovery provided
class RecursiveDescentHandwrittenParser : Parser() {
    override fun parse(tokens: List<Token<SimpleTokenType>>): Function {
        return ParserState(tokens).parse()
    }
}

private class ParserState(private val tokens: List<Token<SimpleTokenType>>) {
    var position: Int = 0

    fun parse(): Function {
        val function = function()
        if (!at(SimpleTokenType.End)) fail("End expected")
        return function
    }

    // Service functions

    fun current(): Token<SimpleTokenType> {
        return tokens[position]
    }

    fun advance(): Token<SimpleTokenType> {
        val token = tokens[position]
        position++
        return token
    }

    fun fail(reason: String) : Nothing {
        throw ParseException(reason)
    }

    fun expect(type: SimpleTokenType): Token<SimpleTokenType> {
        if (!at(type)) fail("Expected $type, but was ${current().type}")
        return advance()
    }

    // checks
    fun at(type: SimpleTokenType): Boolean {
        return current().type == type
    }

    // Rules

    private fun function(): Function {
        expect(SimpleTokenType.FunKw)
        val name = expect(SimpleTokenType.Identifier).text
        val parameterList = parameterList()
        val block = block()
        return Function(name.toString(), parameterList, BlockStmt(block))
    }

    private fun parameterList(): ParameterList {
        expect(SimpleTokenType.LPar)
        var first = true
        val parameters = mutableListOf<Parameter>()
        while (!at(SimpleTokenType.RPar)) {
            if (first) {
                first = false
            } else {
                expect(SimpleTokenType.Comma)
            }
            parameters.add(parameter())
        }
        advance()
        return ParameterList(parameters)
    }

    private fun block(): Block {
        expect(SimpleTokenType.LCurly)
        val stmts = mutableListOf<Stmt>()
        while (!at(SimpleTokenType.RCurly)) {
            stmts.add(stmt())
        }
        expect(SimpleTokenType.RCurly)
        return Block(stmts)
    }

    private fun expr(): Expr {
        val type = current().type
        return when (type) {
            SimpleTokenType.IntegralNumber -> numberLiteral()
            else -> fail("Expression expected, but found $type")
        }
    }

    private fun numberLiteral() : NumberLiteral {
        return NumberLiteral(expect(SimpleTokenType.IntegralNumber).text.toString().toInt())
    }

    private fun exprStmt() : ExprStmt {
        val expr = expr()
        expect(SimpleTokenType.Semi)
        return ExprStmt(expr)
    }

    private fun stmt(): Stmt {
        return when (current().type) {
            SimpleTokenType.ValKw -> declStmt()
            else -> exprStmt()
        }

    }

    private fun declStmt(): DeclStmt {
        expect(SimpleTokenType.ValKw)
        val name = expect(SimpleTokenType.Identifier).text.toString()
        expect(SimpleTokenType.Colon)
        val typeElement = typeElement()
        val initializer = if (at(SimpleTokenType.Eq)) {
            advance()
            expr()
        } else {
            null
        }
        expect(SimpleTokenType.Semi)
        return DeclStmt(name, typeElement, initializer)
    }

    private fun parameter(): Parameter {
        val name = expect(SimpleTokenType.Identifier).text.toString()
        expect(SimpleTokenType.Colon)
        val typeElement = typeElement()
        return Parameter(name, typeElement)
    }

    private fun typeElement(): TypeElement {
        return TypeElement(expect(SimpleTokenType.Identifier).text.toString())
    }
}