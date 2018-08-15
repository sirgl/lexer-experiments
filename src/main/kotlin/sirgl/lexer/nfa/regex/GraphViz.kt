package sirgl.lexer.nfa.regex

class DotGraphBuilder {
    val nodes = mutableMapOf<NfaNode, Int>()
    var index = 0

    private fun getIndex(node: NfaNode): Int {
        return nodes.computeIfAbsent(node) {
            index++
            index - 1
        }
    }

    fun build(node: NfaNode): String {
        return buildString {
            node.dfa {
                val nodeIndex = getIndex(it)
                for (edge in it.edges) {
                    val endIndex = getIndex(edge.end)
                    append("$nodeIndex -> $endIndex ${edge.drawingAttribute}\n")
                }
            }
        }
    }
}