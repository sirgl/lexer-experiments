package sirgl.rope

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class RopeTest {
    @Test
    fun `access flat by index`() {
        Assertions.assertEquals('f', Rope("foo")[0])
    }

    @Test
    fun `flat length`() {
        Assertions.assertEquals(3, Rope("foo").length)
    }

    @Test
    fun `concat length`() {
        val rope = Rope("foo") + Rope("bar")
        Assertions.assertEquals(6, rope.length)
    }

    @Test
    fun concat() {
        val rope = Rope("foo") + Rope("bar")
        Assertions.assertEquals("foobar", rope.toString())
    }

    @Test
    fun `iterator nodes`() {
        val n12 = Rope("1") + Rope("2")
        val n34 = Rope("3") + Rope("4")
        val n1234 = n12 + n34
        val n56 = Rope("5") + Rope("6")
        val rope = n1234 + n56
        Assertions.assertEquals("1, 2, 3, 4, 5, 6", rope.leafs().joinToString(", "))
    }


    @Test
    fun `split test 1`() {
        val rope = createTestRope()
        splitIterationTest(1, rope, "1", "1, 22, 33, 44, 55, 66")
    }

    @Test
    fun `split test 2`() {
        val rope = createTestRope()
        splitIterationTest(2, rope, "11", ", 22, 33, 44, 55, 66")
    }

    @Test
    fun `split by negative`() {
        val rope = createTestRope()
        splitIterationTest(-2, rope, "", "11, 22, 33, 44, 55, 66")
    }

    @Test
    fun `split by too big index`() {
        val rope = createTestRope()
        splitIterationTest(100, rope, "11, 22, 33, 44, 55, 66", "")
    }

    @Test
    fun `split only`() {
        val rope = createTestRope()
        splitTest(3, rope)
    }

    @Test
    fun `iterator single node`() {
        Assertions.assertEquals("a", Rope("a").leafs().joinToString(", "))
    }

    @Test
    fun `subRope test 1`() {
        Assertions.assertEquals("12233", createTestRope().subRope(1, 6).toString())
    }
    @Test
    fun `subRope test 2`() {
        Assertions.assertEquals("112", createTestRope().subRope(0, 3).toString())
    }

    private fun createTestRope(): Rope {
        val n12 = Rope("11") + Rope("22")
        val n34 = Rope("33") + Rope("44")
        val n1234 = n12 + n34
        val n56 = Rope("55") + Rope("66")
        return n1234 + n56
    }

    private fun splitTest(index: Int, rope: Rope) {
        val start = rope.toString().substring(0, index)
        val end = rope.toString().substring(index)
        val (rope1, rope2) = rope.splitByIndex(index)
        Assertions.assertEquals(start, rope1.toString())
        Assertions.assertEquals(end, rope2.toString())
    }

    private fun splitIterationTest(index: Int, rope: Rope, start: String, end: String) {
        val (rope1, rope2) = rope.splitByIndex(index)
        Assertions.assertEquals(start, rope1.leafs().joinToString(", "), "Start incorrect")
        Assertions.assertEquals(end, rope2.leafs().joinToString(", "), "End incorrect")
    }
}