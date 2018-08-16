package sirgl

import sirgl.lexer.ExternalLexer
import sirgl.lexer.LexerDefinition
import sirgl.lexer.nfa.regex.*


enum class SimpleTokenType {
    IntegralNumber,
    FixedPointNumber,
    Space,
    Error,
    Identifier,
    FunKw,
    ValKw,
    LPar,
    RPar,
    LCurly,
    RCurly,
    Plus,
    Minus,
    Div,
    Asterisk,
    Semi,
    Colon,
    Comma,
    Eq,
    End,
    Comment,
}

@Suppress("unused")
object SimpleLexerDefinition : LexerDefinition<SimpleTokenType>() {
    override val comments: SimpleTokenType = SimpleTokenType.Comment
    override val whitespaces: SimpleTokenType = SimpleTokenType.Space
    override val endLexeme: SimpleTokenType = SimpleTokenType.End
    private const val digits = "0123456789"
    private const val letters = "abcdefghigklmnopqrstuvwxyz"
    private val identifierNonFirstLetters = letters + letters.toUpperCase() + digits

    // Keywords

    private val functionKw = regex(
            keyword("fun"),
            SimpleTokenType.FunKw
    )

    private val valKw = regex(
            keyword("val"),
            SimpleTokenType.ValKw
    )

    // Keywords end

    // Punctuation signs


    private val lPar = regex(
            keyword("("),
            SimpleTokenType.LPar
    )

    private val rPar = regex(
            keyword(")"),
            SimpleTokenType.RPar
    )

    private val lcurly = regex(
            keyword("{"),
            SimpleTokenType.LCurly
    )

    private val rCurly = regex(
            keyword("}"),
            SimpleTokenType.RCurly
    )

    private val plus = regex(
            keyword("+"),
            SimpleTokenType.Plus
    )

    private val minus = regex(
            keyword("-"),
            SimpleTokenType.Minus
    )

    private val asterisk = regex(
            keyword("*"),
            SimpleTokenType.Asterisk
    )

    private val div = regex(
            keyword("/"),
            SimpleTokenType.Div
    )

    private val semi = regex(
            keyword(";"),
            SimpleTokenType.Semi
    )

    private val colon = regex(
            keyword(":"),
            SimpleTokenType.Colon
    )

    private val comma = regex(
            keyword(","),
            SimpleTokenType.Colon
    )

    private val eq = regex(
            keyword("="),
            SimpleTokenType.Eq
    )

    // Punctuation signs end

    private val integralNumber = regex(
            RepeatOneOrMoreNode(CharsNode(digits)),
            SimpleTokenType.IntegralNumber
    )

    private val fixedPointNumber = regex(
            SequenceNode(listOf(
                    RepeatZeroOrMoreNode(CharsNode(digits)),
                    CharNode('.'),
                    RepeatOneOrMoreNode(CharsNode(digits))
            )),
            SimpleTokenType.FixedPointNumber
    )

    private val spaces = regex(
            RepeatOneOrMoreNode(CharsNode(" \n\t")),
            SimpleTokenType.Space
    )

    private val identifier = regex(
            SequenceNode(listOf(
                    CharsNode(letters + letters.toUpperCase()),
                    RepeatZeroOrMoreNode(CharsNode(identifierNonFirstLetters))
            )),
            SimpleTokenType.Identifier
    )

    // /* /* */ */  -- it should be considered as a single token
    private val extDeepComment = externalLexer(
            keyword("/*"),
            object: ExternalLexer {
                override fun tryTokenize(sequence: CharSequence, startIndex: Int, endIndexExclusive: Int): Int? {
                    var unbalancedOpeningCount = 1
                    var current = endIndexExclusive
                    val length = sequence.length
                    while (current < length) {
                        val ch = sequence[current]
                        when (ch) {
                            '*' -> {
                                if (current + 1 >= length) return null
                                val next = sequence[current + 1]
                                if (next == '/') {
                                    unbalancedOpeningCount--
                                    if (unbalancedOpeningCount == 0) return current + 2
                                }
                            }
                            '/' -> {
                                if (current + 1 >= length) return null
                                val next = sequence[current + 1]
                                if (next == '*') {
                                    unbalancedOpeningCount++
                                }
                            }
                        }
                        current++
                    }
                    return null
                }
            },
            SimpleTokenType.Comment
    )

    private val errorElement = regex(
            AnyNode(),
            SimpleTokenType.Error
    )


}