package sirgl

import sirgl.ast.pretty
import sirgl.example.SimpleLexerDefinition
import sirgl.example.SimpleTokenTypes
import sirgl.language.Language
import sirgl.lexer.PreparedLexerDefinition
import sirgl.lexer.TokenTypeFactory
import sirgl.lexer.nfa.NfaLexerOptimized
import sirgl.example.parser.RecursiveDescentHandwrittenParser
import sirgl.lexer.nfa.regex.DotGraphBuilder

fun main(args: Array<String>) {
    val language = Language("Test", 100)
    val definition = SimpleLexerDefinition(language)
    val languageTypeFactory = TokenTypeFactory(language)

    val preparedLexerDefinition = PreparedLexerDefinition(definition, languageTypeFactory)
    val lexer = NfaLexerOptimized(preparedLexerDefinition)
//    println(DotGraphBuilder().build(lexer.nfa.entrace))

    val tokens = lexer.tokenize(
            "fun foo (y: Int){val x: Int = 12; /* bar /* foo */ */}",
            true,
            true
    )
    val tokenTypes = SimpleTokenTypes(languageTypeFactory)
    val parser = RecursiveDescentHandwrittenParser(tokenTypes)
    println(tokens.joinToString(separator = "\n") { it.pretty() })
    println("----")
    val function = parser.parse(tokens)
    println(function.pretty())

}