package sirgl.lexer.nfa

import sirgl.lexer.PreparedLexerDefinition
import sirgl.lexer.nfa.regex.Nfa
import sirgl.lexer.nfa.regex.NfaNode
import sirgl.lexer.nfa.regex.eliminateEpsilonEdges

class NfaLexerOptimized(definition: PreparedLexerDefinition) : NfaLexerBase(definition, { setOf(it.entrace) }) {
    override fun postprocessNfa(nfa: Nfa) {
        eliminateEpsilonEdges(nfa.entrace)
    }

    override fun match(node: NfaNode, codePoint: Int): Collection<NfaNode> {
        return node.matchWithoutEpsilonClosure(codePoint)
    }
}