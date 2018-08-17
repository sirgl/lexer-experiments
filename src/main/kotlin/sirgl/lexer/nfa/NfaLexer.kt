package sirgl.lexer.nfa

import sirgl.lexer.PreparedLexerDefinition
import sirgl.lexer.nfa.regex.Nfa
import sirgl.lexer.nfa.regex.NfaNode
import sirgl.lexer.nfa.regex.nodeEpsilonClosure


class NfaLexer(definition: PreparedLexerDefinition) : NfaLexerBase(definition, { it.entrace.nodeEpsilonClosure() }) {

    override fun postprocessNfa(nfa: Nfa) {}

    override fun match(node: NfaNode, codePoint: Int): Collection<NfaNode> {
        return node.match(codePoint)
    }
}