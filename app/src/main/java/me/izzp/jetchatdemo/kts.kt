package me.izzp.jetchatdemo

import android.os.Bundle
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.LayoutDirection
import androidx.navigation.NavController
import androidx.navigation.navOptions
import com.google.accompanist.insets.Insets
import com.google.accompanist.insets.WindowInsets
import kotlin.math.absoluteValue

fun WindowInsets.dump() {
    val statusBarHeight = (statusBars.top - statusBars.bottom).absoluteValue
    val navigationBarHeight = (navigationBars.top - navigationBars.bottom).absoluteValue
    println("statusBarHeight: ${statusBarHeight}, navigationBarHeight:${navigationBarHeight}")
}

fun Insets.dump() {
    println("left: $left, right: $right, top: $top, bottom: $bottom")
}

fun PaddingValues.dump() {
    val top = calculateTopPadding()
    val bottom = calculateBottomPadding()
    val left = calculateLeftPadding(LayoutDirection.Ltr)
    val right = calculateRightPadding(LayoutDirection.Ltr)
    println("top: $top, bottom: $bottom, left: $left, right: $right")
}

fun NavController.navigateTo(resId: Int, args: Bundle? = null) {
    navigate(resId, args, navOptions {
        launchSingleTop = true
        popUpTo = graph.startDestination
    })
}

fun calculateFractionColor(start: Color, end: Color, fraction: Float): Color {
    val percent = fraction.coerceIn(0f, 1f)
    return Color(
        red = start.red + (end.red - start.red) * percent,
        green = start.green + (end.green - start.green) * percent,
        blue = start.blue + (end.blue - start.blue) * percent,
        alpha = start.alpha + (end.alpha - start.alpha) * percent,
    )
}

fun LazyListState.dump() {
    println(buildString {
        appendLine("count: ${layoutInfo.totalItemsCount}")
        appendLine("lastVisibleIndex: $lastVisibleItemIndex")
        appendLine("firstVisibleIndex: $firstVisibleItemIndex")
        appendLine("firstVisibleOffset: $firstVisibleItemScrollOffset")
        appendLine("startOffset: ${layoutInfo.viewportStartOffset}")
        appendLine("endOffset: ${layoutInfo.viewportEndOffset}")
        val info =
            layoutInfo.visibleItemsInfo.map { "index=${it.index}, offset=${it.offset}, size=${it.size}" }
                .joinToString("; ")
        appendLine("visibleItems: $info")
    })
}

val LazyListState.lastVisibleItemIndex
    get() = layoutInfo.visibleItemsInfo.maxOf { it.index }

val LazyListState.lastVisibleItemInfo
    get() = layoutInfo.visibleItemsInfo.maxByOrNull { it.index }!!

fun LazyListState.themeColor(
    start: Color,
    end: Color,
): Color {
    val last = lastVisibleItemInfo
    if (last.index < layoutInfo.totalItemsCount - 1) {
        return end
    } else {
        val percent =
            1 - (layoutInfo.viewportEndOffset - last.offset) / last.size.toFloat()
        return calculateFractionColor(start, end, percent)
    }
}

fun ScrollState.themeColor(
    start: Color,
    end: Color,
    distance: Float = 100f,
): Color {
    if (value >= maxValue) {
        return end
    }
    return calculateFractionColor(start, end, value / distance)
}