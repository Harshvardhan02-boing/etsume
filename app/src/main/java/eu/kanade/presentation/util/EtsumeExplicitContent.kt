package eu.kanade.presentation.util

import eu.kanade.tachiyomi.extension.anime.AnimeExtensionManager
import eu.kanade.tachiyomi.extension.manga.MangaExtensionManager

private val explicitKeywords = listOf(
    "18+",
    "18 plus",
    "18plus",
    "r18",
    "adult",
    "adult only",
    "bara",
    "bdsm",
    "blowjob",
    "doujin",
    "doujinshi",
    "ecchi",
    "erotic",
    "erotica",
    "fetish",
    "harem",
    "hentai",
    "incest",
    "lewd",
    "lolicon",
    "mature",
    "mature content",
    "milf",
    "netorare",
    "ntr",
    "nsfw",
    "porn",
    "xxx",
    "rape",
    "sexual",
    "sex",
    "smut",
    "succubus",
    "uncensored",
    "incest",
    "paizuri",
    "creampie",
    "blowjob",
    "handjob",
    "anal",
    "oral",
    "threesome",
    "orgy",
    "rape",
    "yaoi",
    "yuri",
    "x-rated",
)

fun isProbablyExplicitContent(vararg fields: String?): Boolean =
    fields.any(::containsExplicitKeyword)

fun isProbablyExplicitContent(fields: Iterable<String?>): Boolean =
    fields.any(::containsExplicitKeyword)

fun isBlockedExplicitContent(
    showNsfw: Boolean,
    fields: Iterable<String?>,
    sourceId: Long? = null,
    mangaExtensionManager: MangaExtensionManager? = null,
    animeExtensionManager: AnimeExtensionManager? = null,
): Boolean {
    if (showNsfw) return false
    return isProbablyExplicitContent(fields) || isNsfwSource(sourceId, mangaExtensionManager, animeExtensionManager)
}

private fun isNsfwSource(
    sourceId: Long?,
    mangaExtensionManager: MangaExtensionManager?,
    animeExtensionManager: AnimeExtensionManager?,
): Boolean {
    if (sourceId == null) return false
    val mangaNsfw = mangaExtensionManager
        ?.installedExtensionsFlow
        ?.value
        ?.firstOrNull { extension -> extension.sources.any { it.id == sourceId } }
        ?.isNsfw == true
    val animeNsfw = animeExtensionManager
        ?.installedExtensionsFlow
        ?.value
        ?.firstOrNull { extension -> extension.sources.any { it.id == sourceId } }
        ?.isNsfw == true
    return mangaNsfw || animeNsfw
}

private fun containsExplicitKeyword(text: String?): Boolean {
    if (text.isNullOrBlank()) return false
    val normalized = text.lowercase()
    return explicitKeywords.any(normalized::contains)
}
