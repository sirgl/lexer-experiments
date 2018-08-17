package sirgl.example

import sirgl.lexer.TokenTypeFactory

object SimpleTokenNames {
     const val endToken = "<end>"
     const val integralNumber = "IntegralNumber" 
     const val fixedPointNumber = "FixedPointNumber" 
     const val space = "Space" 
     const val error = "<error>"
     const val identifier = "Identifier" 
     const val funKw = "FunKw" 
     const val valKw = "ValKw" 
     const val lPar = "LPar" 
     const val rPar = "RPar" 
     const val lCurly = "LCurly" 
     const val rCurly = "RCurly" 
     const val plus = "Plus" 
     const val minus = "Minus" 
     const val div = "Div" 
     const val asterisk = "Asterisk" 
     const val semi = "Semi" 
     const val colon = "Colon" 
     const val comma = "Comma" 
     const val eq = "Eq" 
     const val comment = "Comment"
}

class SimpleTokenTypes(tokenTypeFactory: TokenTypeFactory) {
    val end = tokenTypeFactory.get(SimpleTokenNames.endToken)
    val integralNumber = tokenTypeFactory.get(SimpleTokenNames.integralNumber)
    val fixedPointNumber = tokenTypeFactory.get(SimpleTokenNames.fixedPointNumber)
    val space = tokenTypeFactory.get(SimpleTokenNames.space)
    val error = tokenTypeFactory.get(SimpleTokenNames.error)
    val identifier = tokenTypeFactory.get(SimpleTokenNames.identifier)
    val funKw = tokenTypeFactory.get(SimpleTokenNames.funKw)
    val valKw = tokenTypeFactory.get(SimpleTokenNames.valKw)
    val lPar = tokenTypeFactory.get(SimpleTokenNames.lPar)
    val rPar = tokenTypeFactory.get(SimpleTokenNames.rPar)
    val lCurly = tokenTypeFactory.get(SimpleTokenNames.lCurly)
    val rCurly = tokenTypeFactory.get(SimpleTokenNames.rCurly)
    val plus = tokenTypeFactory.get(SimpleTokenNames.plus)
    val minus = tokenTypeFactory.get(SimpleTokenNames.minus)
    val div = tokenTypeFactory.get(SimpleTokenNames.div)
    val asterisk = tokenTypeFactory.get(SimpleTokenNames.asterisk)
    val semi = tokenTypeFactory.get(SimpleTokenNames.semi)
    val colon = tokenTypeFactory.get(SimpleTokenNames.colon)
    val comma = tokenTypeFactory.get(SimpleTokenNames.comma)
    val eq = tokenTypeFactory.get(SimpleTokenNames.eq)
    val comment = tokenTypeFactory.get(SimpleTokenNames.comment)
}