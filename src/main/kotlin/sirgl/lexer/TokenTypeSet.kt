package sirgl.lexer

class TokenTypeSet(vararg tokenTypes: TokenType) {
    constructor(tokenList: List<TokenType>) : this(*tokenList.toTypedArray())

    // TODO replace with bitset
    private val tokenTypes = tokenTypes.toSet()

    fun match(tokenType: TokenType): Boolean {
        return tokenType in tokenTypes
    }
}