package sirgl.lexer.nfa

import sirgl.lexer.LexerDefinition
import sirgl.lexer.nfa.regex.Nfa
import sirgl.lexer.nfa.regex.NfaNode
import sirgl.lexer.nfa.regex.eliminateEpsilonEdges

class NfaLexerOptimized<T>(definition: LexerDefinition<T>) : NfaLexerBase<T>(definition) {
    override fun postprocessNfa(nfa: Nfa) {
        eliminateEpsilonEdges(nfa.entrace)
    }

    override fun match(node: NfaNode, codePoint: Int): Collection<NfaNode> {
        return node.matchWithoutEpsilonClosure(codePoint)
    }
}