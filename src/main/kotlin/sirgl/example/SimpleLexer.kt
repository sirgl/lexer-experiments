package sirgl.example

import sirgl.language.Language
import sirgl.lexer.ExternalLexer
import sirgl.lexer.LexerDefinition
import sirgl.lexer.nfa.regex.*


private const val digits = "0123456789"
private const val letters = "abcdefghigklmnopqrstuvwxyz"

@Suppress("unused")
class SimpleLexerDefinition(override val language: Language) : LexerDefinition() {
    override val comments = setOf(SimpleTokenNames.comment)
    override val whitespaces = setOf(SimpleTokenNames.space)
    override val endLexeme = SimpleTokenNames.endToken

    private val identifierNonFirstLetters = letters + letters.toUpperCase() + digits

    // Keywords

    private val functionKw = regex(
            keyword("fun"),
            SimpleTokenNames.funKw
    )

    private val valKw = regex(
            keyword("val"),
            SimpleTokenNames.valKw
    )

    // Keywords end

    // Punctuation signs


    private val lPar = regex(
            keyword("("),
            SimpleTokenNames.lPar
    )

    private val rPar = regex(
            keyword(")"),
            SimpleTokenNames.rPar
    )

    private val lcurly = regex(
            keyword("{"),
            SimpleTokenNames.lCurly
    )

    private val rCurly = regex(
            keyword("}"),
            SimpleTokenNames.rCurly
    )

    private val plus = regex(
            keyword("+"),
            SimpleTokenNames.plus
    )

    private val minus = regex(
            keyword("-"),
            SimpleTokenNames.minus
    )

    private val asterisk = regex(
            keyword("*"),
            SimpleTokenNames.asterisk
    )

    private val div = regex(
            keyword("/"),
            SimpleTokenNames.div
    )

    private val semi = regex(
            keyword(";"),
            SimpleTokenNames.semi
    )

    private val colon = regex(
            keyword(":"),
            SimpleTokenNames.colon
    )

    private val comma = regex(
            keyword(","),
            SimpleTokenNames.comma
    )

    private val eq = regex(
            keyword("="),
            SimpleTokenNames.eq
    )

    // Punctuation signs end

    private val integralNumber = regex(
            RepeatOneOrMoreNode(CharsNode(digits)),
            SimpleTokenNames.integralNumber
    )

    private val fixedPointNumber = regex(
            SequenceNode(listOf(
                    RepeatZeroOrMoreNode(CharsNode(digits)),
                    CharNode('.'),
                    RepeatOneOrMoreNode(CharsNode(digits))
            )),
            SimpleTokenNames.fixedPointNumber
    )

    private val spaces = regex(
            RepeatOneOrMoreNode(CharsNode(" \n\t")),
            SimpleTokenNames.space
    )

    private val identifier = regex(
            SequenceNode(listOf(
                    CharsNode(letters + letters.toUpperCase()),
                    RepeatZeroOrMoreNode(CharsNode(identifierNonFirstLetters))
            )),
            SimpleTokenNames.identifier
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
            SimpleTokenNames.comment
    )

    private val errorElement = regex(
            AnyNode(),
            SimpleTokenNames.error
    )


}