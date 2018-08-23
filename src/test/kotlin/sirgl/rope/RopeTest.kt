package sirgl.rope

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class RopeTest {
    @Test
    fun `access flat by index`() {
        assertEquals('f', Rope("foo")[0])
    }

    @Test
    fun `flat length`() {
        assertEquals(3, Rope("foo").length)
    }

    @Test
    fun `concat length`() {
        val rope = Rope("foo") + Rope("bar")
        assertEquals(6, rope.length)
    }

    @Test
    fun concat() {
        val rope = Rope("foo") + Rope("bar")
        assertEquals("foobar", rope.toString())
    }

    @Test
    fun `iterator nodes`() {
        val n12 = Rope("1") + Rope("2")
        val n34 = Rope("3") + Rope("4")
        val n1234 = n12 + n34
        val n56 = Rope("5") + Rope("6")
        val rope = n1234 + n56
        assertEquals("1, 2, 3, 4, 5, 6", rope.leafs().joinToString(", "))
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
        assertEquals("a", Rope("a").leafs().joinToString(", "))
    }

    @Test
    fun `subRope test 1`() {
        assertEquals("12233", createTestRope().subRope(1, 6).toString())
    }

    @Test
    fun `subRope test 2`() {
        assertEquals("112", createTestRope().subRope(0, 3).toString())
    }

    @Test
    fun `delete test`() {
        val text = createTestRope().delete(1, 3).toString()
        assertEquals("133445566", text)
    }

    @Test
    fun `delete single test`() {
        val text = createTestRope().delete(1).toString()
        assertEquals("1233445566", text)
    }


    @Test
    fun `insert test`() {
        val text = createTestRope().insert(1, "foo").toString()
        assertEquals("1foo12233445566", text)
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
        assertEquals(start, rope1.toString())
        assertEquals(end, rope2.toString())
    }

    private fun splitIterationTest(index: Int, rope: Rope, start: String, end: String) {
        val (rope1, rope2) = rope.splitByIndex(index)
        assertEquals(start, rope1.leafs().joinToString(", "), "Start incorrect")
        assertEquals(end, rope2.leafs().joinToString(", "), "End incorrect")
    }
}