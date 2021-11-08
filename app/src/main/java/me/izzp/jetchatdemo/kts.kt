package me.izzp.jetchatdemo

import android.os.Bundle
import androidx.compose.foundation.layout.PaddingValues
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
