package eu.kanade.tachiyomi.ui.reader.viewer.navigation

import android.graphics.RectF
import eu.kanade.tachiyomi.ui.reader.viewer.ViewerNavigation

/**
 * Default Etsume navigation:
 * +---+---+---+
 * | P | P | P |
 * +---+---+---+
 * | M | M | M |
 * +---+---+---+
 * | N | N | N |
 * +---+---+---+
 */
class TopBottomNavigation : ViewerNavigation() {

    override var regionList: List<Region> = listOf(
        Region(
            rectF = RectF(0f, 0f, 1f, 0.33f),
            type = NavigationRegion.PREV,
        ),
        Region(
            rectF = RectF(0f, 0.66f, 1f, 1f),
            type = NavigationRegion.NEXT,
        ),
    )
}
