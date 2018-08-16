package sirgl.lexer.nfa

import sirgl.lexer.LexerDefinition
import sirgl.lexer.nfa.regex.Nfa
import sirgl.lexer.nfa.regex.NfaNode


class NfaLexer<T>(definition: LexerDefinition<T>) : NfaLexerBase<T>(definition) {
    override fun postprocessNfa(nfa: Nfa) {}

    override fun match(node: NfaNode, codePoint: Int): Collection<NfaNode> {
        return node.match(codePoint)
    }
}