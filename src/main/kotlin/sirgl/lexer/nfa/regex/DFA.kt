package sirgl.lexer.nfa.regex

import java.util.*

fun NfaNode.dfa(callback: (NfaNode) -> Unit) {
    val stack: Deque<NfaNode> = ArrayDeque<NfaNode>()
    val processed = hashSetOf<NfaNode>()
    stack.push(this)
    while (stack.isNotEmpty()) {
        val node = stack.pop()
        processed.add(node)
        callback(node)
        for (edge in node.edges) {
            val end = edge.end
            if (end !in processed) {
                stack.push(end)
            }
        }
    }
}