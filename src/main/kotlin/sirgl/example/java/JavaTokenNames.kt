package sirgl.example.java

object JavaTokenNames {
    const val whitespace = "whitespace"
    const val endOfLineComment = "endOfLineComment"
    const val traditionalComment = "traditionalComment"
    const val error = "<error>"
    const val identifier = "identifier"

    // literals
    const val trueLiteral = "true"
    const val falseLiteral = "false"
    const val nullLiteral = "null"
    const val integerLiteral = "integerLiteral"
    const val floatLiteral = "floatLiteral"
    const val charLiteral = "charLiteral"
    const val stringLiteral = "stringLiteral"

    // keywords
    const val abstractKw = "abstract"
    const val continueKw = "continue"
    const val forKw = "for"
    const val newKw = "new"
    const val switchKw = "switch"
    const val assertKw = "assert"
    const val defaultKw = "default"
    const val ifKw = "if"
    const val packageKw = "package"
    const val synchronizedKw = "synchronized"
    const val booleanKw = "boolean"
    const val doKw = "do"
    const val gotoKw = "goto"
    const val privateKw = "private"
    const val thisKw = "this"
    const val breakKw = "break"
    const val doubleKw = "double"
    const val implementsKw = "implements"
    const val protectedKw = "protected"
    const val throwKw = "throw"
    const val byteKw = "byte"
    const val elseKw = "else"
    const val importKw = "import"
    const val publicKw = "public"
    const val throwsKw = "throws"
    const val caseKw = "case"
    const val enumKw = "enum"
    const val instanceofKw = "instanceof"
    const val returnKw = "return"
    const val transientKw = "transient"
    const val catchKw = "catch"
    const val extendsKw = "extends"
    const val intKw = "int"
    const val shortKw = "short"
    const val tryKw = "try"
    const val charKw = "char"
    const val finalKw = "final"
    const val interfaceKw = "interface"
    const val staticKw = "static"
    const val voidKw = "void"
    const val classKw = "class"
    const val finallyKw = "finally"
    const val longKw = "long"
    const val strictfpKw = "strictfp"
    const val volatileKw = "volatile"
    const val constKw = "const"
    const val floatKw = "float"
    const val nativeKw = "native"
    const val superKw = "super"
    const val whileKw = "while"

    // punctuators
    const val lPar = "("
    const val rPar = ")"
    const val lBrace = "{"
    const val rBrace = "}"
    const val lBracket = "["
    const val rBracket = "]"
    const val semi = ";"
    const val comma = ","
    const val dot = "."
    const val dotDotDot = "..."
    const val at = "@"
    const val colonColon = "::"

    // operators
    const val eq = "="
    const val gt = ">"
    const val lt = "<"
    const val excl = "!"
    const val tilde = "~"
    const val question = "?"
    const val colon = ":"
    const val dashGt = "->"
    const val eqEq = "=="
    const val gtEq = ">="
    const val ltEq = "<="
    const val exclEq = "!="
    const val andAnd = "&&"
    const val orOr = "||"
    const val plusPlus = "++"
    const val dashDash = "--"
    const val plus = "+"
    const val dash = "-"
    const val asterisk = "*"
    const val div = "/"
    const val and = "&"
    const val or = "|"
    const val caret = "^"
    const val percent = "%"
    const val ltLt = "<<"
    const val gtGt = ">>"
    const val gtGtGt = ">>>"
    const val plusEq = "+="
    const val dashEq = "-="
    const val asteriskEq = "*="
    const val divEq = "/="
    const val andEq = "&="
    const val orEq = "|="
    const val caretEq = "^="
    const val percentEq = "%="
    const val ltLtEq = "<<="
    const val gtGtEq = ">>="
    const val gtGtGtEq = ">>>="
}

fun main(args: Array<String>) {
//    println(keywords().joinToString (separator = "\n", transform = { "const val ${it}Kw = \"$it\"" }))
//    println(keywords().joinToString (separator = "\n", transform = { "val ${it}Kw = regex(keyword(\"$it\"), JavaTokenNames.${it}Kw)" }))
//    println(operatorToAbbrs().joinToString(separator = "\n") { "    const val ${it.second} = \"${it.first}\"" })
    println(operatorToAbbrs().joinToString(separator = "\n") { "val ${it.second} = punctuator(JavaTokenNames.${it.second})" })
}

fun operatorToAbbrs(): List<Pair<String, String>> {
    return operators().map { operator ->
        operator to buildString {
            for ((index, c) in operator.withIndex()) {
                val abbr = charToAbbr[c] ?: throw IllegalArgumentException("$c")
                append(if (index != 0) {
                    abbr[0].toUpperCase() + abbr.substring(1)
                } else {
                    abbr
                })
            }
        }
    }
}

private fun keywords(): List<String> {
    return """
            abstract continue for new switch
    assert default if package synchronized
    boolean do goto private this
    break double implements protected throw
    byte else import public throws
    case enum instanceof return transient
    catch extends int short try
    char final interface static void
    class finally long strictfp volatile
    const float native super while
        """.trimIndent()
            .split(Regex("\\s"))
            .filter { it.isNotEmpty() }
}

private fun operators(): List<String> {
    return """= > < ! ~ ? : ->
== >= <= != && || ++ --
+ - * / & | ^ % << >> >>>
+= -= *= /= &= |= ^= %= <<= >>= >>>=""".trimIndent().trim()
            .split(Regex("\\s"))
            .filter { it.isNotEmpty() }
}

val charToAbbr = mapOf(
        '=' to "eq",
        '>' to "gt",
        '<' to "lt",
        '!' to "excl",
        '~' to "tilde",
        '?' to "question",
        ':' to "colon",
        '&' to "and",
        '|' to "or",
        '%' to "percent",
        '+' to "plus",
        '-' to "minus",
        '^' to "caret",
        '-' to "dash",
        '*' to "asterisk",
        '/' to "div"
        )