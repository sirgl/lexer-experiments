package sirgl.example.java

import sirgl.language.Language
import sirgl.lexer.ExternalLexer
import sirgl.lexer.LexerDefinition
import sirgl.lexer.nfa.regex.*


class JavaLexerDefinition(override val language: Language) : LexerDefinition() {
    override val endLexeme: String = "<end>"
    override val whitespaces: Set<String> = setOf(JavaTokenNames.whitespace)
    override val comments: Set<String> = setOf(JavaTokenNames.endOfLineComment)

    val unicodeMarker = { CharNode('u') }

    val unicodeEscape = {
        SequenceNode(listOf(
                CharNode('\\'),
                unicodeMarker(),
                hexDigit(),
                hexDigit(),
                hexDigit(),
                hexDigit()
        ))
    }

    val lineTerminator = {
        ChoiceNode(listOf(
                CharNode('\n'),
                CharNode('\r'),
                SequenceNode("\n\r")
        ))
    }

    val inputCharacter = {
        InvertedCharsNode("\r\n")
    }

    val whitespaceRegex = regex(RepeatOneOrMoreNode(CharsNode(" \t\r\n")), JavaTokenNames.whitespace)

    val endOfLineComment = regex(
            SequenceNode(listOf(
                    SequenceNode("//"),
                    RepeatZeroOrMoreNode(inputCharacter())
            )),
            JavaTokenNames.endOfLineComment
    )

    val traditionalComment = externalLexer(
            SequenceNode("/*"),
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
            JavaTokenNames.traditionalComment
    )

    private val lettersOnlyStr = "abcdefghigklmnopqrstuvwxyz"

    private val lettersStr: String
        get() = "_$" + lettersOnlyStr + lettersOnlyStr.toUpperCase()

    private val nonZeroDigitStr: String
        get() = "123456789"
    private val digitsStr: String
        get() = "0$nonZeroDigitStr"

    val letterOrDigit = { CharsNode(lettersStr + lettersStr.toUpperCase() + digitsStr) }

    val letter = { CharsNode(lettersStr) }

    val trueLiteral = regex(keyword("true"), JavaTokenNames.trueLiteral)
    val falseLiteral = regex(keyword("false"), JavaTokenNames.falseLiteral)
    val nullLiteral = regex(keyword("null"), JavaTokenNames.nullLiteral)

    val abstractKw = regex(keyword("abstract"), JavaTokenNames.abstractKw)
    val continueKw = regex(keyword("continue"), JavaTokenNames.continueKw)
    val forKw = regex(keyword("for"), JavaTokenNames.forKw)
    val newKw = regex(keyword("new"), JavaTokenNames.newKw)
    val switchKw = regex(keyword("switch"), JavaTokenNames.switchKw)
    val assertKw = regex(keyword("assert"), JavaTokenNames.assertKw)
    val defaultKw = regex(keyword("default"), JavaTokenNames.defaultKw)
    val ifKw = regex(keyword("if"), JavaTokenNames.ifKw)
    val packageKw = regex(keyword("package"), JavaTokenNames.packageKw)
    val synchronizedKw = regex(keyword("synchronized"), JavaTokenNames.synchronizedKw)
    val booleanKw = regex(keyword("boolean"), JavaTokenNames.booleanKw)
    val doKw = regex(keyword("do"), JavaTokenNames.doKw)
    val gotoKw = regex(keyword("goto"), JavaTokenNames.gotoKw)
    val privateKw = regex(keyword("private"), JavaTokenNames.privateKw)
    val thisKw = regex(keyword("this"), JavaTokenNames.thisKw)
    val breakKw = regex(keyword("break"), JavaTokenNames.breakKw)
    val doubleKw = regex(keyword("double"), JavaTokenNames.doubleKw)
    val implementsKw = regex(keyword("implements"), JavaTokenNames.implementsKw)
    val protectedKw = regex(keyword("protected"), JavaTokenNames.protectedKw)
    val throwKw = regex(keyword("throw"), JavaTokenNames.throwKw)
    val byteKw = regex(keyword("byte"), JavaTokenNames.byteKw)
    val elseKw = regex(keyword("else"), JavaTokenNames.elseKw)
    val importKw = regex(keyword("import"), JavaTokenNames.importKw)
    val publicKw = regex(keyword("public"), JavaTokenNames.publicKw)
    val throwsKw = regex(keyword("throws"), JavaTokenNames.throwsKw)
    val caseKw = regex(keyword("case"), JavaTokenNames.caseKw)
    val enumKw = regex(keyword("enum"), JavaTokenNames.enumKw)
    val instanceofKw = regex(keyword("instanceof"), JavaTokenNames.instanceofKw)
    val returnKw = regex(keyword("return"), JavaTokenNames.returnKw)
    val transientKw = regex(keyword("transient"), JavaTokenNames.transientKw)
    val catchKw = regex(keyword("catch"), JavaTokenNames.catchKw)
    val extendsKw = regex(keyword("extends"), JavaTokenNames.extendsKw)
    val intKw = regex(keyword("int"), JavaTokenNames.intKw)
    val shortKw = regex(keyword("short"), JavaTokenNames.shortKw)
    val tryKw = regex(keyword("try"), JavaTokenNames.tryKw)
    val charKw = regex(keyword("char"), JavaTokenNames.charKw)
    val finalKw = regex(keyword("final"), JavaTokenNames.finalKw)
    val interfaceKw = regex(keyword("interface"), JavaTokenNames.interfaceKw)
    val staticKw = regex(keyword("static"), JavaTokenNames.staticKw)
    val voidKw = regex(keyword("void"), JavaTokenNames.voidKw)
    val classKw = regex(keyword("class"), JavaTokenNames.classKw)
    val finallyKw = regex(keyword("finally"), JavaTokenNames.finallyKw)
    val longKw = regex(keyword("long"), JavaTokenNames.longKw)
    val strictfpKw = regex(keyword("strictfp"), JavaTokenNames.strictfpKw)
    val volatileKw = regex(keyword("volatile"), JavaTokenNames.volatileKw)
    val constKw = regex(keyword("const"), JavaTokenNames.constKw)
    val floatKw = regex(keyword("float"), JavaTokenNames.floatKw)
    val nativeKw = regex(keyword("native"), JavaTokenNames.nativeKw)
    val superKw = regex(keyword("super"), JavaTokenNames.superKw)
    val whileKw = regex(keyword("while"), JavaTokenNames.whileKw)

    // Decimal literal
    val integerTypeSuffix = { CharsNode("lL") }

    val underscores = { RepeatOneOrMoreNode(CharNode('_')) }

    val digitOrUnderscore = digitsStr + "_"

    val digitsOrUnderscores = { CharsNode(digitOrUnderscore) }


    val digitsAndUnderscores = { RepeatOneOrMoreNode(digitsOrUnderscores()) }

    val nonZeroDigit = { CharsNode(nonZeroDigitStr) }

    val digits = {
        ChoiceNode(listOf(
                CharsNode(digitsStr),
                SequenceNode(listOf(
                        CharsNode(digitsStr),
                        OptionalNode(CharsNode(digitsStr)),
                        CharsNode(digitsStr)
                ))

        ))
    }

    val decimalNumeral = {
        ChoiceNode(listOf(
                CharNode('0'),
                SequenceNode(listOf(
                        nonZeroDigit(),
                        OptionalNode(digits())
                )),
                SequenceNode(listOf(
                        nonZeroDigit(),
                        underscores(),
                        digits()
                ))

        ))
    }


    val decimalIntegerLiteral = {
        SequenceNode(listOf(
                decimalNumeral(),
                OptionalNode(integerTypeSuffix())
        ))
    }

    // hex integer literal

    val hexCharsLowercasedStr = "abcdef"

    val hexDigit = { CharsNode(digitsStr + hexCharsLowercasedStr + hexCharsLowercasedStr.toUpperCase()) }

    val hexDigitOrUnderscore = {
        ChoiceNode(listOf(
                CharNode('_'),
                hexDigit()
        ))
    }

    val hexDigitAndUnderscore = {
        RepeatOneOrMoreNode(hexDigitOrUnderscore())
    }


    val hexDigits = {
        ChoiceNode(listOf(
                hexDigit(),
                SequenceNode(listOf(
                        hexDigit(),
                        OptionalNode(hexDigitAndUnderscore()),
                        hexDigit()
                ))
        ))
    }

    val hexNumeral = {
        ChoiceNode(listOf(
                SequenceNode(listOf(
                        SequenceNode("0x"),
                        hexDigits()
                )),
                SequenceNode(listOf(
                        SequenceNode("0X"),
                        hexDigits()
                ))
        ))
    }


    val hexIntegerLiteral = {
        SequenceNode(listOf(
                hexNumeral(),
                OptionalNode(integerTypeSuffix())
        ))
    }

    // octal integer literal

    val octalDigitStr = "01234567"

    val octalDigit = { CharsNode(octalDigitStr) }

    val octalDigitOrUnderscore = {
        ChoiceNode(listOf(
                CharNode('_'),
                octalDigit()
        ))
    }

    val octalDigitAndUnderscore = { RepeatOneOrMoreNode(octalDigitOrUnderscore()) }

    val octalDigits = {
        ChoiceNode(listOf(
                octalDigit(),
                SequenceNode(listOf(
                        octalDigit(),
                        OptionalNode(octalDigitAndUnderscore()),
                        octalDigit()
                ))
        ))
    }

    val octalNumeral = {
        ChoiceNode(listOf(
                SequenceNode(listOf(
                        CharNode('0'),
                        octalDigits()
                )),
                SequenceNode(listOf(
                        CharNode('0'),
                        underscores(),
                        octalDigits()
                ))
        ))
    }

    val octalIntegerLiteral = {
        SequenceNode(listOf(
                octalNumeral(),
                OptionalNode(integerTypeSuffix())
        ))
    }

    // Binary integer literal

    val binaryDigitStr = "01"

    val binaryDigit = { CharsNode("01") }

    val binaryDigitOrUnderscore = {
        ChoiceNode(listOf(
                CharNode('_'),
                binaryDigit()
        ))
    }

    val binaryDigitAndUnderscore = { RepeatOneOrMoreNode(binaryDigitOrUnderscore()) }

    val binaryDigits = {
        ChoiceNode(listOf(
                binaryDigit(),
                SequenceNode(listOf(
                        binaryDigit(),
                        OptionalNode(binaryDigitAndUnderscore()),
                        binaryDigit()
                ))
        ))
    }

    val binaryNumeral = {
        ChoiceNode(listOf(
                SequenceNode(listOf(
                        CharsNode("0b"),
                        binaryDigits()
                )),
                SequenceNode(listOf(
                        CharsNode("0B"),
                        binaryDigits()
                ))
        ))
    }


    val binaryIntegerLiteral = {
        SequenceNode(listOf(
                binaryNumeral(),
                OptionalNode(integerTypeSuffix())
        ))
    }

    // numerals finished

    val integerLiteral = regex(
            ChoiceNode(listOf(
                    decimalIntegerLiteral(),
                    hexIntegerLiteral(),
                    octalIntegerLiteral(),
                    binaryIntegerLiteral()
            )),
            JavaTokenNames.integerLiteral
    )

    // float literals

    val floatTypeSuffixStr = "fFdD"

    val floatTypeSuffix = { CharsNode(floatTypeSuffixStr) }

    val sign = { CharsNode("+-") }

    val signedInteger = {
        SequenceNode(listOf(
                OptionalNode(sign()),
                digits()
        ))
    }

    val exponentIndicator = { CharsNode("eE") }

    val exponentPart = {
        SequenceNode(listOf(
                exponentIndicator(),
                signedInteger()
        ))
    }

    val decimalFloatingPointLiteral = {
        ChoiceNode(listOf(
                SequenceNode(listOf(
                        digits(),
                        CharNode('.'),
                        OptionalNode(digits()),
                        OptionalNode(exponentPart()),
                        OptionalNode(floatTypeSuffix())
                )),
                SequenceNode(listOf(
                        CharNode('.'),
                        digits(),
                        OptionalNode(exponentPart()),
                        OptionalNode(floatTypeSuffix())
                )),
                SequenceNode(listOf(
                        digits(),
                        exponentPart(),
                        OptionalNode(floatTypeSuffix())
                )),
                SequenceNode(listOf(
                        digits(),
                        OptionalNode(exponentPart()),
                        floatTypeSuffix()
                ))
        ))
    }

    val binaryExponentIndicator = { CharsNode("pP") }

    val binaryExponent = {
        SequenceNode(listOf(
                binaryExponentIndicator(),
                signedInteger()
        ))
    }

    val hexSignificand = {
        ChoiceNode(listOf(
                SequenceNode(listOf(
                        hexNumeral(),
                        OptionalNode(CharNode('.'))
                )),
                SequenceNode(listOf(
                        SequenceNode("0x"),
                        OptionalNode(hexDigits()),
                        CharNode('.'),
                        hexDigits()
                )),
                SequenceNode(listOf(
                        SequenceNode("0X"),
                        OptionalNode(hexDigits()),
                        CharNode('.'),
                        hexDigits()
                ))

        ))
    }

    val hexadecimalFloatingPointLiteral = {
        SequenceNode(listOf(
                hexSignificand(),
                binaryExponent(),
                OptionalNode(floatTypeSuffix())
        ))
    }

    val floatingPointLiteral = regex(
            ChoiceNode(listOf(
                    decimalFloatingPointLiteral(),
                    hexadecimalFloatingPointLiteral()
            )),
            JavaTokenNames.floatLiteral
    )


    // escape sequences

    val zeroToThree = { CharsNode("0123") }

    val octalEscape = {
        ChoiceNode(listOf(
                SequenceNode(listOf(
                        CharNode('\\'),
                        octalDigit()
                )),
                SequenceNode(listOf(
                        CharNode('\\'),
                        octalDigit(),
                        octalDigit()
                )),
                SequenceNode(listOf(
                        CharNode('\\'),
                        zeroToThree(),
                        octalDigit(),
                        octalDigit()
                ))
        ))
    }

    val escapeSequence = {
        ChoiceNode(listOf(
                SequenceNode("\\b"),
                SequenceNode("\\t"),
                SequenceNode("\\n"),
                SequenceNode("\\f"),
                SequenceNode("\\r"),
                SequenceNode("\\\""),
                SequenceNode("\\'"),
                SequenceNode("\\\\'"),
                octalEscape()
        ))
    }

    // char literal

    val singleCharacter = { InvertedCharsNode("\\'") }

    val characterLiteral = regex(
            ChoiceNode(listOf(
                    SequenceNode(listOf(
                            CharNode('\''),
                            singleCharacter(),
                            CharNode('\'')
                    )),
                    SequenceNode(listOf(
                            CharNode('\''),
                            escapeSequence(),
                            CharNode('\'')
                    ))

            )),
            JavaTokenNames.charLiteral
    )

    // string literal

    val stringCharacter = {
        ChoiceNode(listOf(
                InvertedCharsNode("\\\""),
                escapeSequence()
        ))
    }


    val stringLiteral = regex(
            SequenceNode(listOf(
                    CharNode('"'),
                    RepeatOneOrMoreNode(stringCharacter()),
                    CharNode('"')
            )),
            JavaTokenNames.stringLiteral
    )

    // punctuators
    fun punctuator(text: String): String = regex(SequenceNode(text), text)

    //    ( ) { } [ ] ; , . ... @ ::
    val lPar = punctuator(JavaTokenNames.lPar)
    val rPar = punctuator(JavaTokenNames.rPar)
    val lBrace = punctuator(JavaTokenNames.lBrace)
    val rBrace = punctuator(JavaTokenNames.rBrace)
    val lBracket = punctuator(JavaTokenNames.lBracket)
    val rBracket = punctuator(JavaTokenNames.rBracket)
    val semi = punctuator(JavaTokenNames.semi)
    val comma = punctuator(JavaTokenNames.comma)
    val dot = punctuator(JavaTokenNames.dot)
    val dotDotDot = punctuator(JavaTokenNames.dotDotDot)
    val at = punctuator(JavaTokenNames.at)
    val colonColon = punctuator(JavaTokenNames.colonColon)


    // operators

    //= > < ! ~ ? : ->
    //== >= <= != && || ++ --
    //+ - * / & | ^ % << >> >>>
    //+= -= *= /= &= |= ^= %= <<= >>= >>>=
    val eq = punctuator(JavaTokenNames.eq)
    val gt = punctuator(JavaTokenNames.gt)
    val lt = punctuator(JavaTokenNames.lt)
    val excl = punctuator(JavaTokenNames.excl)
    val tilde = punctuator(JavaTokenNames.tilde)
    val question = punctuator(JavaTokenNames.question)
    val colon = punctuator(JavaTokenNames.colon)
    val dashGt = punctuator(JavaTokenNames.dashGt)
    val eqEq = punctuator(JavaTokenNames.eqEq)
    val gtEq = punctuator(JavaTokenNames.gtEq)
    val ltEq = punctuator(JavaTokenNames.ltEq)
    val exclEq = punctuator(JavaTokenNames.exclEq)
    val andAnd = punctuator(JavaTokenNames.andAnd)
    val orOr = punctuator(JavaTokenNames.orOr)
    val plusPlus = punctuator(JavaTokenNames.plusPlus)
    val dashDash = punctuator(JavaTokenNames.dashDash)
    val plus = punctuator(JavaTokenNames.plus)
    val dash = punctuator(JavaTokenNames.dash)
    val asterisk = punctuator(JavaTokenNames.asterisk)
    val div = punctuator(JavaTokenNames.div)
    val and = punctuator(JavaTokenNames.and)
    val or = punctuator(JavaTokenNames.or)
    val caret = punctuator(JavaTokenNames.caret)
    val percent = punctuator(JavaTokenNames.percent)
    val ltLt = punctuator(JavaTokenNames.ltLt)
    val gtGt = punctuator(JavaTokenNames.gtGt)
    val gtGtGt = punctuator(JavaTokenNames.gtGtGt)
    val plusEq = punctuator(JavaTokenNames.plusEq)
    val dashEq = punctuator(JavaTokenNames.dashEq)
    val asteriskEq = punctuator(JavaTokenNames.asteriskEq)
    val divEq = punctuator(JavaTokenNames.divEq)
    val andEq = punctuator(JavaTokenNames.andEq)
    val orEq = punctuator(JavaTokenNames.orEq)
    val caretEq = punctuator(JavaTokenNames.caretEq)
    val percentEq = punctuator(JavaTokenNames.percentEq)
    val ltLtEq = punctuator(JavaTokenNames.ltLtEq)
    val gtGtEq = punctuator(JavaTokenNames.gtGtEq)
    val gtGtGtEq = punctuator(JavaTokenNames.gtGtGtEq)

    // other

    // TODO not only ascii
    val identifier = regex(
            SequenceNode(listOf(
                    letter(),
                    RepeatZeroOrMoreNode(letterOrDigit())
            )),
            JavaTokenNames.identifier
    )

    val error = regex(AnyNode(), JavaTokenNames.error)
}
