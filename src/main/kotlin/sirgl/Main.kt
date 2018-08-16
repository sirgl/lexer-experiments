package sirgl

import sirgl.ast.pretty
import sirgl.lexer.nfa.NfaLexerOptimized
import sirgl.parser.RecursiveDescentHandwrittenParser

fun main(args: Array<String>) {
    val lexer = NfaLexerOptimized(SimpleLexerDefinition)
//    println(DotGraphBuilder().build(lexer.nfa.entrace))

    val tokens = lexer.tokenize(
            "fun foo (y: Int){val x: Int = 12; /* bar /* foo */ */}",
            true,
            true
    )
    val parser = RecursiveDescentHandwrittenParser()
    println(tokens.joinToString(separator = "\n") { it.pretty() })
    println("----")
    val function = parser.parse(tokens)
    println(function.pretty())

}