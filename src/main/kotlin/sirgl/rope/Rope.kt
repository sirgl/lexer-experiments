package sirgl.rope

import java.util.NoSuchElementException

// Immutable structure
class Rope(var root: Node) : CharSequence {
    constructor(text: String) : this(Leaf(text.toCharArray()))

    override val length: Int
        get() = root.length

    override fun get(index: Int): Char {
        var current = root
        var correctedIndex = index
        while (current !is Leaf) {
            val left = current.left!!
            if (index < left.length) {
                current = left
            } else {
                correctedIndex -= left.length
                current = current.right!!
            }
        }
        return current.chars[correctedIndex]
    }

    override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
        return subRope(startIndex, endIndex)
    }

    /**
     * [startIndex] inclusive
     * [endIndex] exclusive
     */
    fun subRope(startIndex: Int, endIndex: Int): Rope {
        if (startIndex > endIndex) {
            throw IllegalArgumentException("end index ($endIndex) is less than start index ($startIndex)")
        }
        val start = truncate(startIndex, length)
        val end = truncate(endIndex, length)
        val (beforeStart, afterStart) = splitByIndex(start)
        val (inner, afterEnd) = afterStart.splitByIndex(end - start)
        return inner
    }

    operator fun plus(another: CharSequence) : Rope {
        if (another is Rope) {
            return Rope(Inner(root, another.root))
        }
        if (another is String) {
            return Rope(Inner(root, Leaf(another.toCharArray())))
        }
        return Rope(Inner(root, Leaf(another.toString().toCharArray())))
    }

    override fun toString(): String = buildString {
        for (leaf in leafs()) {
            append(leaf.chars)
        }
    }

    fun leafs()  = Leafs(root)

    fun splitByIndex(index: Int): Pair<Rope, Rope> {
        val correctedIndex = when {
            index < 0 -> 0
            index > length -> length
            else -> index
        }
        val res = splitByIndex(root, correctedIndex)
        return Rope(res.first) to Rope(res.second)
    }
}

sealed class Node (
        var left: Node?,
        var right: Node?,
        var length: Int,
        var parent: Node? = null
) {
     fun getTheOnlyChild(): Node? {
        this as? Inner ?: return null
        val left = left
        val right = right
        return when {
            left != null && right == null -> left
            left == null && right != null -> right
            else -> null
        }
    }
}

class Leaf(var chars: CharArray) : Node(null, null, chars.size) {
    override fun toString() = String(chars)
}


class Inner(left: Node?, right: Node?, length: Int = (left?.length ?: 0) + (right?.length ?: 0)) : Node(left, right, length) {
    init {
        left?.parent = this
        right?.parent = this
    }

    override fun toString(): String {
        return left.toString() + right.toString()
    }
}

class LeafIterator(current: Leaf) : Iterator<Leaf> {
    var current: Leaf? = current
    var nextObtained = true

    override fun hasNext(): Boolean {
        tryAdvance()
        return current != null
    }

    private fun tryAdvance() {
        if (!nextObtained) {
            current = getNext()
            nextObtained = true
        }
    }

    override fun next(): Leaf {
        tryAdvance()
        nextObtained = false
        return current ?: throw NoSuchElementException()
    }

    private fun getNext() : Leaf? {
        val top = goUp(current ?: return null) ?: return null
        return getLeftDownLeaf(top)
    }

    // rise until node where current (lower) node is at the left side (if no parent - return null)
    private fun goUp(node: Node) : Node? {
        var current: Node = node
        while (true) {
            val parent = current.parent ?: return null
            if (parent.left === current) {
                return parent.right
            }
            current = parent
        }
    }
}

// get down choosing always left
private fun getLeftDownLeaf(node: Node) : Leaf {
    var current = node
    while (current !is Leaf) {
        // Inner can have less than 2 nodes
        current = current.left ?: (current.right ?: throw IllegalStateException())
    }
    return current
}

class Leafs(private val root: Node) : Iterable<Leaf> {
    override fun iterator(): Iterator<Leaf> {
        val leftDown = getLeftDownLeaf(root)
        return LeafIterator(leftDown)
    }
}

internal fun splitByIndex(node: Node, index: Int) : Pair<Node, Node> {
    val tree1: Node
    val tree2: Node
    val nodeLeft = node.left
    if (nodeLeft != null) {
        if (nodeLeft.length >= index) {
            val res = splitByIndex(nodeLeft, index)
            tree1 = res.first
            tree2 = Inner(res.second, node.right)
        } else {
            val res = splitByIndex(node.right!!, index - node.left!!.length)
            tree1 = Inner(node.left, res.first)
            tree2 = res.second
        }
    } else {
        node as Leaf
        val chars = node.chars
        tree1 = Leaf(chars.copyOfRange(0, index))
        tree2 = Leaf(chars.copyOfRange(index, chars.size))
    }
    return (tree1.getTheOnlyChild() ?: tree1) to (tree2.getTheOnlyChild() ?: tree2)
}

private fun truncate(value: Int, max: Int) : Int {
    return when {
        value < 0 -> 0
        value >= max -> max
        else -> value
    }
}