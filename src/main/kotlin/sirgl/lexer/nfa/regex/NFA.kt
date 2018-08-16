package sirgl.lexer.nfa.regex

import sirgl.common.IntList


class Nfa(val entrace: NfaNode, val exit: NfaNode)

data class MatchResult (val exitIndices: IntList, val matchedNodes: List<NfaNode>)

class NfaNode(val edges: MutableList<NfaEdge> = mutableListOf(), var endIndex: Int? = null) {
    // Matches edges of current node only (without epsilon closure) and adds epsilon closure of ens of
    // matched edges to result as well as indices of exits
    fun match(codePoint: Int) : Collection<NfaNode> {
        val nodes = hashSetOf<NfaNode>()
        for (edge in edges) {
            if (edge is StableEdge) {
                if (!edge.matches(codePoint)) continue
                val edgeEnd = edge.end
                nodes.add(edgeEnd)
                val closure = edgeEnd.nodeEpsilonClosure()
                for (node in closure) {
                    nodes.add(node)
                }
            }
        }
        return nodes
    }


    fun matchWithoutEpsilonClosure(codePoint: Int): Collection<NfaNode> {
        val nodes = mutableListOf<NfaNode>()
        var exitIndex = Int.MAX_VALUE
        val endIndex = this.endIndex
        if (endIndex != null) exitIndex = endIndex
        for (edge in edges) {
            edge as StableEdge
            if (edge.matches(codePoint)) {
                val edgeEnd = edge.end
                val edgeEndIndex = edgeEnd.endIndex
                if (edgeEndIndex != null) {
                    if (edgeEndIndex < exitIndex) {
                        exitIndex = edgeEndIndex
                    }
                }
                nodes.add(edgeEnd)
            }
        }
        return nodes
    }
}

class EpsilonClosureResult(
        val edges: List<StableEdge>,
        val exitIndices: List<Int>
)

fun epsilonClosure(node: NfaNode): EpsilonClosureResult {
    val nonEpsilonEdge = mutableListOf<StableEdge>()
    val processed = hashSetOf<NfaNode>()
    val nodes = mutableListOf<NfaNode>()
    val exitIndices = mutableListOf<Int>()
    nodes.add(node)
    while (nodes.isNotEmpty()) {
        val current = nodes.removeAt(nodes.lastIndex)
        processed.add(current)
        val endIndex = current.endIndex
        if(endIndex != null) {
            exitIndices.add(endIndex)
        }
        for (edge in current.edges) {
            if (edge is StableEdge) {
                nonEpsilonEdge.add(edge)
            } else {
                if (!processed.contains(edge.end)) {
                    nodes.add(edge.end)
                }
            }
        }
    }
    return EpsilonClosureResult(nonEpsilonEdge, exitIndices)
}

fun NfaNode.nodeEpsilonClosure() : Set<NfaNode> {
    val processed = hashSetOf<NfaNode>()
    val nodes = mutableListOf<NfaNode>()
    nodes.add(this)
    while (nodes.isNotEmpty()) {
        val current = nodes.removeAt(nodes.lastIndex)
        processed.add(current)
        for (edge in current.edges) {
            if (edge !is EpsilonEdge) continue
            val endNode = edge.end
            if (endNode !in processed) {
                nodes.add(endNode)
            }
        }
    }
    return processed
}

sealed class NfaEdge(val end: NfaNode) {
    abstract val drawingAttribute: String
}

abstract class StableEdge(end: NfaNode) : NfaEdge(end) {
    abstract fun matches(codePoint: Int): Boolean
}

class EpsilonEdge(end: NfaNode) : NfaEdge(end) {
    override val drawingAttribute: String
        get() = "[style=dotted]"
}

class CharEdge(end: NfaNode, private val myCodePoint: Int) : StableEdge(end) {
    override val drawingAttribute: String
        get() = "[label=\"${String(Character.toChars(myCodePoint))}\"]"

    override fun matches(codePoint: Int): Boolean = codePoint == myCodePoint
}

class CharsEdge(end: NfaNode, private val codePoints: IntList) : StableEdge(end) {
    override val drawingAttribute: String
        get() = "[label=\"${codePoints.toCharString()}\"]"

    override fun matches(codePoint: Int): Boolean = codePoint in codePoints
}

class AnyEdge(end: NfaNode) : StableEdge(end) {
    override val drawingAttribute: String
        get() = "[label=\"<any>\"]"

    override fun matches(codePoint: Int): Boolean = true
}

fun IntList.toCharString() : String {
    return buildString {
        append("{")
        for (index in (0 until size)) {
            if (index != 0) {
                append(",")
            }
            val codePoint = storage[index]
            append(Character.toChars(codePoint))
        }
        append("}")
    }
}