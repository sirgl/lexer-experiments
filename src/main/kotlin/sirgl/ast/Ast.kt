package sirgl.ast

sealed class AstNode {
    abstract override fun toString(): String
    abstract val children: List<AstNode>
}

fun AstNode.pretty(): String {
    val sb = StringBuilder()
    pretty(sb, 0)
    return sb.toString()
}

private fun AstNode.pretty(sb: StringBuilder, depth: Int) {
    addPadding(sb, depth)
    sb.append(toString())
    sb.append("\n")
    for (child in children) {
        child.pretty(sb, depth + 1)
    }
}

private fun addPadding(sb: StringBuilder, count: Int) {
    for (i in 0 until count) {
        sb.append("  ")
    }
}

class Function(
        val name: String,
        val parameterList: ParameterList,
        val block: BlockStmt
) : AstNode() {
    override val children: List<AstNode> = listOf(parameterList, block)

    override fun toString() = "Funciton $name"
}

class ParameterList(val args: List<Parameter>) : AstNode() {
    override val children: List<AstNode> = args
    override fun toString() = "ParameterList"
}

class Parameter(val name: String, val type: TypeElement) : AstNode() {
    override val children: List<AstNode> = listOf(type)
    override fun toString() = "Parameter $name"
}

class Block(val stmts: List<Stmt>) : AstNode() {
    override val children: List<AstNode> = stmts
    override fun toString() = "Block"
}

class TypeElement(val name: String) : AstNode() {
    override val children: List<AstNode> = emptyList()
    override fun toString() = "TypeElement $name"
}

sealed class Stmt : AstNode()

class BlockStmt(val block: Block) : Stmt() {
    override val children: List<AstNode> = listOf(block)
    override fun toString() = "BlockStmt"

    val stmts: List<Stmt>
        get() = block.stmts
}

class ExprStmt(val expr: Expr) : Stmt() {
    override val children: List<AstNode> = listOf(expr)
    override fun toString() = "ExprStmt"
}

class DeclStmt(val name: String, val type: TypeElement, val initializer: Expr?) : Stmt() {
    override val children: List<AstNode> = if (initializer == null) listOf(type) else listOf(initializer, type)
    override fun toString() = "DeclStmt $name"
}

sealed class Expr : AstNode()

class BinExpr(val left: Expr, val right: Expr, val type: BinExprType) : Expr() {
    override val children: List<AstNode> = listOf(left, right)
    override fun toString(): String {
        return "BinExpr $type"
    }
}

class NumberLiteral(val value: Int) : Expr() {
    override val children: List<AstNode> = emptyList()
    override fun toString() = "NumberLiteral $value"
}

enum class BinExprType {
    Plus,
    Minus,
    Multiply,
    Div
}

