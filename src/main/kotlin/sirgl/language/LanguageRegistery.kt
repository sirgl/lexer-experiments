package sirgl.language

class LanguageRegistery {
    private var nextLanguageId = 0
    private val languageMap = mutableMapOf<String, Language>()
    // TODO id to language map

    /**
     * Returns [Language] if name was unique
     */
    fun registerLanguage(name: String) : Language? {
        if (languageMap[name] != null) return null
        val language = Language(name, nextLanguageId)
        languageMap[name] = language
        nextLanguageId++
        return language
    }
}