package sirgl.common

private const val DEFAULT_CAPACITY = 8

class IntList(capacity: Int = DEFAULT_CAPACITY) {
    var storage = IntArray(capacity)
    private var _size = 0

    constructor(list: IntArray) : this(list.size) {
        System.arraycopy(list, 0, storage, 0, list.size)
        size = list.size
    }


    var size: Int
        get() = _size
        private set(value) {
            _size = value
        }

    fun add(value: Int) {
        if (storage.size >= size) {
            increaseStorage()
        }
        storage[size] = value
        size++
    }

    operator fun contains(value: Int): Boolean {
        for (index in (0 until size)) {
            if (storage[index] == value) return true
        }
        return false
    }

    fun increaseStorage() {
        val newStorage = IntArray(storage.size * 2)
        System.arraycopy(storage, 0, newStorage, 0, storage.size)
        storage = newStorage
    }

    operator fun get(index: Int) : Int {
        if (index >= size || index < 0) {
            throw IndexOutOfBoundsException("Size was $size, but accessed $index")
        }
        return storage[index]
    }

    override fun toString(): String {
        return buildString {
            append("{")
            for (index in (0 until size)) {
                if (index != 0) {
                    append(",")
                }
                val value = storage[index]
                append(value)
            }
            append("}")
        }
    }
}