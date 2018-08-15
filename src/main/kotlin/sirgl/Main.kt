package sirgl

import sirgl.ast.pretty
import sirgl.lexer.nfa.NfaLexerOptimized
import sirgl.parser.RecursiveDescentHandwrittenParser

fun main(args: Array<String>) {
    val lexer = NfaLexerOptimized(SimpleLexerDefinition)
//    println(DotGraphBuilder().build(lexer.nfa.entrace))

    val tokens = lexer.tokenize("fun foo (y: Int){val x: Int = 12;}", true)
    val parser = RecursiveDescentHandwrittenParser()
    println(tokens.joinToString(separator = "\n") { it.pretty() })
    val function = parser.parse(tokens)
    println(function.pretty())

}