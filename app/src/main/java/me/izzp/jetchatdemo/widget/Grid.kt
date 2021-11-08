package me.izzp.jetchatdemo.widget

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import kotlin.math.ceil
import kotlin.math.min


@Composable
fun Grid(
    count: Int,
    cells: Int,
    itemContent: @Composable (maxWidth: Dp, index: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val rows = ceil(count.toFloat() / cells).toInt()
    BoxWithConstraints {
        val width = maxWidth
        LazyColumn(modifier) {
            items(rows) { row ->
                Row {
                    val start = row * cells
                    val size = min(cells, count - start)
                    repeat(size) {
                        itemContent(width, start + it)
                    }
                }
            }
        }
    }
}