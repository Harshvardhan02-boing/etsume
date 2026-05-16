package eu.kanade.presentation.library.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import eu.kanade.presentation.category.visualName
import eu.kanade.presentation.theme.etsumeAccentBrush
import eu.kanade.presentation.theme.etsumeGlassBorderColor
import eu.kanade.presentation.theme.etsumeGlassContainerColor
import tachiyomi.domain.category.model.Category
import tachiyomi.presentation.core.components.Pill
import tachiyomi.presentation.core.components.material.padding

@Composable
internal fun LibraryTabs(
    categories: List<Category>,
    pagerState: PagerState,
    getNumberOfItemsForCategory: (Category) -> Int?,
    onTabItemClick: (Int) -> Unit,
) {
    val currentPageIndex = pagerState.currentPage.coerceAtMost(categories.lastIndex)
    Row(
        modifier = Modifier
            .zIndex(1f)
            .fillMaxWidth()
            .padding(
                horizontal = MaterialTheme.padding.medium,
                vertical = MaterialTheme.padding.medium,
            )
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.padding.small),
    ) {
        categories.forEachIndexed { index, category ->
            val isSelected = currentPageIndex == index
            Surface(
                onClick = { onTabItemClick(index) },
                shape = MaterialTheme.shapes.extraLarge,
                color = if (isSelected) Color.Transparent else etsumeGlassContainerColor(0.56f),
                contentColor = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                tonalElevation = 0.dp,
                shadowElevation = 0.dp,
                border = BorderStroke(
                    1.dp,
                    if (isSelected) Color.Transparent else etsumeGlassBorderColor(0.22f),
                ),
            ) {
                Row(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.extraLarge)
                        .then(if (isSelected) Modifier.background(etsumeAccentBrush(0.95f)) else Modifier)
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = category.visualName,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                    )
                    getNumberOfItemsForCategory(category)?.let { count ->
                        Pill(
                            text = "$count",
                            color = if (isSelected) {
                                MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.18f)
                            } else {
                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.16f)
                            },
                            fontSize = 12.sp,
                        )
                    }
                }
            }
        }
    }
}
