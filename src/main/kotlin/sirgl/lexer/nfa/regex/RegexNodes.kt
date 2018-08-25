package sirgl.lexer.nfa.regex

import sirgl.common.IntList

abstract class RegexNode {
    abstract fun buildNFA() : Nfa
}

// probably codepoint should be used
class CharNode(private val codePoint: Int) : RegexNode() {
    constructor(char: Char) : this(char.toString().codePointAt(0))

    override fun buildNFA(): Nfa {
        val end = NfaNode()
        val start = NfaNode(mutableListOf(CharEdge(end, codePoint)))
        return Nfa(start, end)
    }
}

abstract class RepeatNode(val node: RegexNode) : RegexNode()

class RepeatZeroOrMoreNode(node: RegexNode) : RepeatNode(node) {
    override fun buildNFA(): Nfa {
        val start = NfaNode()
        val end = NfaNode()
        start.edges.add(EpsilonEdge(end))
        end.edges.add(EpsilonEdge(start))
        val nodeNfa = node.buildNFA()
        start.edges.add(EpsilonEdge(nodeNfa.entrace))
        nodeNfa.exit.edges.add(EpsilonEdge(end))
        return Nfa(start, end)
    }
}

class RepeatOneOrMoreNode(node: RegexNode) : RepeatNode(node) {
    override fun buildNFA(): Nfa {
        val start = NfaNode()
        val end = NfaNode()
        end.edges.add(EpsilonEdge(start))
//        start.edges.add(EpsilonEdge(end))
        val nodeNfa = node.buildNFA()
        start.edges.add(EpsilonEdge(nodeNfa.entrace))
        nodeNfa.exit.edges.add(EpsilonEdge(end))
        return Nfa(start, end)
    }

}

class ChoiceNode(private val nodes: List<RegexNode>) : RegexNode() {
    override fun buildNFA(): Nfa {
        val start = NfaNode()
        val end = NfaNode()
        for (node in nodes) {
            val nodeNfa = node.buildNFA()
            val nodeEntrance = nodeNfa.entrace
            start.edges.add(EpsilonEdge(nodeEntrance))
            nodeNfa.exit.edges.add(EpsilonEdge(end))
        }
        return Nfa(start, end)
    }
}


class OptionalNode(val node: RegexNode) : RegexNode() {
    override fun buildNFA(): Nfa {
        val start = NfaNode()
        val end = NfaNode()
        val nfa = node.buildNFA()
        start.edges.add(EpsilonEdge(end))
        start.edges.add(EpsilonEdge(nfa.entrace))
        nfa.exit.edges.add(EpsilonEdge(end))
        return Nfa(start, end)
    }
}

fun keyword(kwName: String) : RegexNode = SequenceNode(kwName)

class SequenceNode(private val nodes: List<RegexNode>) : RegexNode() {
    constructor(str: String) : this(str.map { CharNode(it) })

    override fun buildNFA(): Nfa {
        val start = NfaNode()
        val end = NfaNode()
        var last = start
        for (node in nodes) {
            val nodeNfa = node.buildNFA()
            last.edges.add(EpsilonEdge(nodeNfa.entrace))
            last = nodeNfa.exit
        }
        last.edges.add(EpsilonEdge(end))
        return Nfa(start, end)
    }

}

// TODO invert single character
class InvertedCharsNode(private val codePoints: IntList) : RegexNode() {
    constructor(codePointString: String) : this(
            IntList(codePointString.codePoints().toArray())
    )

    override fun buildNFA(): Nfa {
        val end = NfaNode()
        val start = NfaNode(mutableListOf(NotEdge(end, CharsEdge(end, codePoints))))
        return Nfa(start, end)
    }
}

class CharsNode(private val codePoints: IntList) : RegexNode() {
    constructor(codePointString: String) : this(
            IntList(codePointString.codePoints().toArray())
    )

    override fun buildNFA(): Nfa {
        val end = NfaNode()
        val start = NfaNode(mutableListOf(CharsEdge(end, codePoints)))
        return Nfa(start, end)
    }
}

class AnyNode : RegexNode() {

    override fun buildNFA(): Nfa {
        val end = NfaNode()
        val start = NfaNode(mutableListOf(AnyEdge(end)))
        return Nfa(start, end)
    }
}