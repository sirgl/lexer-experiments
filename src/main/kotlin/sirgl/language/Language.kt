package sirgl.language

class Language(val name: String, val id: Int) {
    override fun toString(): String {
        return "Language(name='$name', id=$id)"
    }
}