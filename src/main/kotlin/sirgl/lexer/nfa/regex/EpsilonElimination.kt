package sirgl.lexer.nfa.regex

fun eliminateEpsilonEdges(entrance: NfaNode) {
    EpsilonEliminator(entrance).eliminate()}

// TODO not change internal structure, return copy
private class EpsilonEliminator(private val entrance: NfaNode) {
    private val toProcess = mutableListOf<NfaNode>()
    private val toProcessSet = hashSetOf<NfaNode>()
    private val processed = hashSetOf<NfaNode>()

    fun eliminate() {
        tryAddToProcess(entrance)
        while (toProcess.isNotEmpty()) {
            val current = pop()
            val closure = current.nodeEpsilonClosure()
            val newEdges = mutableListOf<NfaEdge>()
            for (closureNode in closure) {
                for (closureEdge in closureNode.edges) {
                    if (closureEdge is StableEdge) {
                        newEdges.add(closureEdge)
                        tryAddToProcess(closureEdge.end)
                    }
                }
                tryReplaceEndIndex(current, closureNode)
            }
            current.edges.clear()
            current.edges.addAll(newEdges)
        }
    }

    private fun pop() : NfaNode {
        val node = toProcess.removeAt(toProcess.lastIndex)
        toProcessSet.remove(node)
        processed.add(node)
        return node
    }

    private fun tryAddToProcess(node: NfaNode) : Boolean {
        val nodeInProcessed = node in processed
        if (nodeInProcessed || node in toProcessSet) return nodeInProcessed
        toProcess.add(node)
        toProcessSet.add(node)
        return nodeInProcessed
    }
}

fun tryReplaceEndIndex(nodeWhichIndexToReplace: NfaNode, nodeWhichIndexToCheck: NfaNode) {
    val edgeEndIndex = nodeWhichIndexToCheck.endIndex
    if (edgeEndIndex != null) {
        val currentEndIndex = nodeWhichIndexToReplace.endIndex
        if (currentEndIndex == null) {
            nodeWhichIndexToReplace.endIndex = edgeEndIndex
        } else if (currentEndIndex > edgeEndIndex) {
            nodeWhichIndexToReplace.endIndex = edgeEndIndex
        }
    }
}