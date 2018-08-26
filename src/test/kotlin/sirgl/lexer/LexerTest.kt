package sirgl.lexer

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import sirgl.example.java.JavaLexerDefinition
import sirgl.language.Language
import sirgl.lexer.nfa.NfaLexer
import sirgl.lexer.nfa.NfaLexerOptimized

class LexerTest {
    val lexers = createLexers()

    @Test
    fun whitespace() {
        testLexers(lexers, "  \r\n", """
whitespace@"  \r\n"
<end>@""
""".trim(), false, true)
    }

    @Test
    fun `whitespace 2`() {
        testLexers(lexers, " foo  \r\n", """
whitespace@" "
identifier@"foo"
whitespace@"  \r\n"
<end>@""
""".trim(), false, true)
    }

    @Test
    fun `end of line comment `() {
        testLexers(lexers, """
            // asdsakdjasd ываодылв/%kasd
        """.trimIndent(), """
endOfLineComment@"// asdsakdjasd ываодылв/%kasd"
<end>@""
""".trim(), false, false)
    }

    @Test
    fun `traditional comment`() {
        testLexers(lexers, """
            /* /* asd */ **/
        """.trimIndent(), """
traditionalComment@"/* /* asd */ **/"
<end>@""
""".trim(), false, false)
    }

    @Test
    fun identifier() {
        testLexers(lexers, """
            foo test123
        """.trimIndent(), """
identifier@"foo"
whitespace@" "
identifier@"test123"
<end>@""
""".trim(), false, false)
    }

    @Test
    fun boolLiterals() {
        testLexers(lexers, """
            true false
        """.trimIndent(), """
true@"true"
whitespace@" "
false@"false"
<end>@""
""".trim(), false, false)
    }

    @Test
    fun `keywords test`() {
        testLexers(lexers, """
            class finally abstract
        """.trimIndent(), """
class@"class"
finally@"finally"
abstract@"abstract"
<end>@""
""".trim(), true, false)
    }

    @Test
    fun `integer literal`() {
        testLexers(lexers, """
            123 23l 55L 0x7fff_ffff 0b0111_1111_1111_1111_1111_1111_1111_1111
        """.trimIndent(), """
integerLiteral@"123"
integerLiteral@"23l"
integerLiteral@"55L"
integerLiteral@"0x7fff_ffff"
integerLiteral@"0"
integerLiteral@"b0111_1111_1111_1111_1111_1111_1111_1111"
<end>@""
""".trim(), true, false)
    }

    @Test
    fun `float literal`() {
        testLexers(lexers, """
            1e1f 2.f .3f 0f 3.14f 6.022137e+23f 1e1 2. .3 0.0 3.14 1e-9d 1e137
        """.trimIndent(), """
floatLiteral@"1e1f"
floatLiteral@"2.f"
floatLiteral@".3f"
floatLiteral@"0f"
floatLiteral@"3.14f"
floatLiteral@"6.022"
floatLiteral@"137e+23f"
floatLiteral@"1e1"
floatLiteral@"2."
floatLiteral@".3"
floatLiteral@"0.0"
floatLiteral@"3.14"
floatLiteral@"1e-9d"
floatLiteral@"1e137"
<end>@""
""".trim(), true, false)
    }

    @Test
    fun `char literal`() {
        testLexers(lexers, """
            'a'
        """.trimIndent(), """
charLiteral@"'a'"
<end>@""
""".trim(), true, false)
    }

    @Test
    fun `string literal`() {
        testLexers(lexers, """
            "asdas" "\""
        """.trimIndent(), """
stringLiteral@""asdas""
stringLiteral@""\""${'"'}
<end>@""
""".trim(), true, false)
    }

    private fun testLexers(
            lexers: List<Lexer>,
            text: String,
            tokenText: String,
            skipWhitespace: Boolean,
            skipComments: Boolean
    ) {
        val definition = createJavaLexerDefinition(Language("java", 100))
        definition.comments // dirty hack
        for (lexer in lexers) {
            val tokens = lexer.tokenizeAll(text)
            var position = 0
            val tokenToText = tokens.asSequence().map {
                val txt = it.textFromPosition(text, position)
                position += it.length
                it to txt
            }
            Assertions.assertEquals(tokenText, tokenToText.filter {
                val type = it.first.type
                if (skipComments) {
                    if (type.name in definition.comments) {
                        return@filter false
                    }
                }
                if (skipWhitespace) {
                    if (type.name in definition.whitespaces) {
                        return@filter false
                    }
                }
                return@filter true
            }.joinToString("\n") {
                it.first.pretty(it.second)
            })
        }
    }

    private fun createLexers(): List<Lexer> {
        val javaLanguage = Language("java", 100)
        val factory = TokenTypeFactory(javaLanguage)
        return listOf(
                NfaLexer(PreparedLexerDefinition(createJavaLexerDefinition(javaLanguage), factory)),
                NfaLexerOptimized(PreparedLexerDefinition(createJavaLexerDefinition(javaLanguage), factory))
        )
    }

    private fun createJavaLexerDefinition(language: Language): JavaLexerDefinition {
        return JavaLexerDefinition(language)
    }
}