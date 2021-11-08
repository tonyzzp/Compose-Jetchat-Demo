package me.izzp.jetchatdemo.conversation

import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import me.izzp.jetchatdemo.ui.theme.mtColors


internal enum class MessageSpanType {
    mention,
    link,
    code,
}

internal class MessageSpan(
    val range: TextRange,
    val type: MessageSpanType,
    val tag: String,
)

internal fun processMessage(text: String): Pair<String, List<MessageSpan>> {
    val list = mutableListOf<MessageSpan>()
    var string = text

    // 查找 @
    var index = 0
    while (index < string.length) {
        index = string.indexOf("@", index)
        if (index == -1) {
            break
        }
        var index2 = string.indexOf(" ", index)
        if (index2 == -1) {
            index2 = string.length
        }
        list.add(
            MessageSpan(
                range = TextRange(index, index2),
                type = MessageSpanType.mention,
                tag = string.substring(index + 1, index2)
            )
        )
        index = index2 + 1
    }

    //查找链接
    index = 0
    while (index < string.length) {
        index = string.indexOf("http", index)
        if (index == -1) {
            break
        }
        var index2 = string.indexOf(" ", index)
        if (index2 == -1) {
            index2 = string.length
        }
        list.add(
            MessageSpan(
                range = TextRange(index, index2),
                type = MessageSpanType.link,
                tag = string.substring(index, index2)
            )
        )
        index = index2 + 1
    }

    //查找代码
    index = 0
    while (index < string.length) {
        index = string.indexOf("`", index)
        if (index == -1) {
            break
        }
        val index2 = string.indexOf("`", index + 1)
        if (index2 == -1) {
            break
        }
        string =
            string.substring(0, index) + string.substring(index + 1, index2) + string.substring(
                index2 + 1
            )
        list.add(
            MessageSpan(
                range = TextRange(index, index2 - 1),
                type = MessageSpanType.code,
                tag = string.substring(index, index2 - 1)
            )
        )
        index = index2 + 1 - 2
    }
    return string to list.toList()
}

@Composable
internal fun MessageText(
    text: String,
    style: TextStyle,
    onSpanClick: (type: MessageSpanType, tag: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val data = remember(text) { processMessage(text) }
    val txt = buildAnnotatedString {
        append(data.first)
        data.second.forEach { span ->
            when (span.type) {
                MessageSpanType.mention -> {
                    addStyle(
                        SpanStyle(color = mtColors.primary),
                        span.range.start,
                        span.range.end,
                    )
                }
                MessageSpanType.link -> {
                    addStyle(
                        SpanStyle(color = mtColors.primary),
                        span.range.start,
                        span.range.end,
                    )
                }
                MessageSpanType.code -> {
                    addStyle(
                        SpanStyle(background = Color.White),
                        span.range.start,
                        span.range.end,
                    )
                }
            }
            addStringAnnotation(span.type.name, span.tag, span.range.start, span.range.end)
        }
    }
    ClickableText(
        text = txt,
        style = style,
        modifier = modifier,
    ) { index ->
        txt.getStringAnnotations(index, index + 1).firstOrNull()?.also { range ->
            onSpanClick(MessageSpanType.valueOf(range.tag), range.item)
        }
    }
}