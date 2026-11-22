package com.mamxanh.shared.localization

/** Application-owned copy only. User-authored titles, names and notes bypass this API. */
enum class AppLanguage(
    val tag: String,
) {
    Vietnamese("vi"),
    English("en"),
    ;

    companion object {
        fun fromTag(tag: String) = if (tag.substringBefore('-') == English.tag)English else Vietnamese
    }
}

object AppText {
    private val plantNames =
        mapOf(
            "succulent" to TextKey.CopySenDaNgocAba46f,
            "monstera" to TextKey.CopyTrauBaLaXeF22d98,
            "ficus" to TextKey.CopyCayBangBupEb6814,
            "snake" to TextKey.CopyLuoiHoXanh998396,
            "cactus" to TextKey.CopyXuongRongHoa723f84,
            "aloe" to TextKey.CopyNhaDamMongD63ca0,
            "lavender" to TextKey.CopyOaiHuongTim2ffaf9,
            "pilea" to TextKey.CopyCoDongTienF85b17,
            "cherry" to TextKey.CopyDaoHongXuanC2fd99,
            "pine" to TextKey.CopyThongXanhBiecBb5db6,
            "mandarin" to TextKey.CopyQuytNang1c3e1e,
        )
    private val placeholder = Regex("\\{(\\d+)\\}")

    /** One app language per process; Android applies configuration recreation when changed. */
    var language: AppLanguage = AppLanguage.Vietnamese

    /** Translate a saved default species name, never an arbitrary custom nickname. */
    fun plantName(
        speciesId: String,
        savedName: String,
    ): String {
        val key = plantNames[speciesId] ?: return savedName
        val isDefault = savedName.isBlank() || AppLanguage.entries.any { savedName == forLanguage(key, it) }
        return if (isDefault)text(key)else savedName
    }

    fun text(
        key: TextKey,
        vararg arguments: Any?,
    ): String = forLanguage(key, language, *arguments)

    fun canonical(
        key: TextKey,
        vararg arguments: Any?,
    ): String =
        GeneratedCopy.storageValues[key.ordinal].ifEmpty {
            forLanguage(key, AppLanguage.Vietnamese, *arguments)
        }

    fun forLanguage(
        key: TextKey,
        language: AppLanguage,
        vararg arguments: Any?,
    ): String {
        val pluralArgument = GeneratedCopy.pluralArguments[key.ordinal]
        val singular = language == AppLanguage.English && pluralArgument >= 0 && arguments.getOrNull(pluralArgument)?.toString() == "1"
        val template =
            if (singular) {
                GeneratedCopy.en_one[key.ordinal]
            } else {
                (
                    if (language ==
                        AppLanguage.English
                    ) {
                        GeneratedCopy.en
                    } else {
                        GeneratedCopy.vi
                    }
                )[key.ordinal]
            }
        return placeholder.replace(template) { match ->
            val index = match.groupValues[1].toInt()
            require(index < arguments.size) { "Missing argument $index for $key" }
            arguments[index]?.toString().orEmpty()
        }
    }

    /** Existing category storage values stay stable across language changes and upgrades. */
    fun category(storageValue: String): String {
        val key = GeneratedCopy.categories.firstOrNull { canonical(it) == storageValue } ?: return storageValue
        return text(key)
    }

    /** Resolve old app-owned catalog/history labels at an explicit presentation boundary. */
    fun builtIn(value: String): String {
        val index = GeneratedCopy.vi.indexOf(value).takeIf { it >= 0 } ?: GeneratedCopy.en.indexOf(value).takeIf { it >= 0 } ?: return value
        return (if (language == AppLanguage.English)GeneratedCopy.en else GeneratedCopy.vi)[index]
    }

    /** App-generated history titles may predate the current locale. User notes never enter here. */
    fun history(value: String): String {
        for ((prefix, key) in listOf("SOLD · " to TextKey.HistoryTreeSold, "REMOVED · " to TextKey.HistoryTreeRemoved)) {
            if (value.startsWith(prefix))return text(key, value.removePrefix(prefix))
        }
        val exact = builtIn(value)
        if (exact != value)return exact
        for (pattern in historyPatterns) {
            val matched = pattern.regex.matchEntire(value) ?: continue
            val arguments = arrayOfNulls<Any?>(pattern.slots.maxOrNull()?.plus(1) ?: 0)
            pattern.slots.forEachIndexed { group, slot ->
                val captured = matched.groupValues[group + 1]
                arguments[slot] = if (pattern.key == TextKey.CopyBoSuuTam0C3b9c2)builtIn(captured)else captured
            }
            return text(pattern.key, *arguments)
        }
        return value
    }

    private data class HistoryPattern(
        val key: TextKey,
        val regex: Regex,
        val slots: List<Int>,
        val specificity: Int,
    )

    private val historyPatterns by lazy {
        listOf(GeneratedCopy.vi, GeneratedCopy.en)
            .flatMap { table ->
                table.mapIndexedNotNull { index, template ->
                    val matches = placeholder.findAll(template).toList()
                    if (matches.isEmpty())return@mapIndexedNotNull null
                    val specificity = placeholder.replace(template, "").length
                    if (specificity < 6)return@mapIndexedNotNull null
                    var end = 0
                    val pattern =
                        buildString {
                            append('^')
                            matches.forEach { match ->
                                append(Regex.escape(template.substring(end, match.range.first)))
                                append("(.*?)")
                                end =
                                    match.range.last + 1
                            }
                            append(Regex.escape(template.substring(end)))
                            append('$')
                        }
                    HistoryPattern(TextKey.entries[index], Regex(pattern), matches.map { it.groupValues[1].toInt() }, specificity)
                }
            }.sortedByDescending { it.specificity }
    }
}
