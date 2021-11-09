package me.izzp.jetchatdemo.conversation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.insets.navigationBarsWithImePadding
import kotlinx.coroutines.launch
import me.izzp.jetchatdemo.*
import me.izzp.jetchatdemo.ui.theme.*
import me.izzp.jetchatdemo.widget.Grid


enum class BottomAction(
    val icon: ImageVector,
    val selectedIcon: ImageVector,
) {
    emoji(Icons.Outlined.EmojiEmotions, Icons.Filled.EmojiEmotions),
    mention(Icons.Outlined.AlternateEmail, Icons.Filled.AlternateEmail),
    image(Icons.Outlined.Image, Icons.Filled.Image),
    place(Icons.Outlined.Place, Icons.Filled.Place),
    video(Icons.Outlined.Duo, Icons.Filled.Duo),
}

private val MessageItemShape = RoundedCornerShape(
    topStart = 2.dp,
    topEnd = 12.dp,
    bottomStart = 12.dp,
    bottomEnd = 12.dp
)

@Composable
fun ConversationPage(
    messages: List<Message>,
    onPeopleClick: (name: String) -> Unit,
    onUrlClick: (url: String) -> Unit,
    onConversationClick: () -> Unit,
    onSend: (content: String) -> Unit,
) {
    JetChatTheme {
        val isLight = mtColors.isLight
        val def = mtColors.surface
        val blue1 = mtColors.BLUE_1
        var topBarColor by remember { mutableStateOf(def) }
        val lazyListState = rememberLazyListState()
        val nestedScrollConnection = remember {
            object : NestedScrollConnection {
                override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                    if (isLight) {
                        topBarColor = lazyListState.themeColor(def, blue1)
                    }
                    return super.onPreScroll(available, source)
                }
            }
        }
        JetChatScaffold(
            topBarColor = topBarColor,
            topBarTitle = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = exampleUiState.channelName,
                        style = mtTypography.h6,
                    )
                    Text(
                        text = "${exampleUiState.channelMembers} members",
                        style = mtTypography.caption,
                    )
                }
            },
            topBarActions = {
                IconButton({}) { Icon(Icons.Default.Search, null) }
                IconButton({}) { Icon(Icons.Default.Info, null) }
            },
            onConversationClick = onConversationClick,
            onPeopleClick = onPeopleClick,
            modifier = Modifier.nestedScroll(nestedScrollConnection)
        ) {
            Column(
                modifier = Modifier.navigationBarsWithImePadding()
            ) {
                MessageList(
                    messages = messages,
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    onPeopleClick = onPeopleClick,
                    onUrlClick = onUrlClick,
                    lazyListState = lazyListState,
                )
                BottomBar(onSend = onSend)
            }
        }
    }
}

@Composable
private fun MessageList(
    messages: List<Message>,
    onPeopleClick: (name: String) -> Unit,
    onUrlClick: (url: String) -> Unit,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
) {
    val coroutineScope = rememberCoroutineScope()
    Box(
        modifier = modifier,
    ) {
        LazyColumn(
            state = lazyListState,
            reverseLayout = true,
        ) {
            items(messages) {
                MessageItem(
                    message = it,
                    onPeopleClick = onPeopleClick,
                    onUrlClick = onUrlClick,
                )
            }
        }
        val colors = ButtonDefaults.buttonColors(
            backgroundColor = mtColors.BLUE_1,
            contentColor = mtColors.primary
        )
        if (lazyListState.firstVisibleItemIndex != 0 || lazyListState.firstVisibleItemScrollOffset != 0) {
            Button(
                onClick = {
                    coroutineScope.launch {
                        lazyListState.animateScrollToItem(0)
                    }
                },
                shape = CircleShape,
                colors = colors,
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp)
            ) {
                Icon(Icons.Default.ArrowDownward, null)
                Text("Jump to bottom")
            }
        }
    }
}

@Composable
private fun MessageItem(
    message: Message,
    onPeopleClick: (name: String) -> Unit,
    onUrlClick: (url: String) -> Unit,
) {
    val isMe = message.author == meProfile.userId
    Row(
        modifier = Modifier.padding(12.dp),
    ) {
        val bgColor = if (isMe) {
            mtColors.primary
        } else {
            mtColors.primary.copy(0.5f)
        }
        Image(
            painterResource(message.authorImage),
            null,
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .border(1.dp, if (isMe) mtColors.secondary else mtColors.primary, CircleShape)
                .clickable { onPeopleClick(message.author) },
            contentScale = ContentScale.Crop,
        )
        Spacer(Modifier.width(8.dp))
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(0.dp, 4.dp)
            ) {
                Text(
                    text = message.author,
                    style = mtTypography.subtitle1,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(Modifier.width(4.dp))
                Text(text = message.timestamp, style = mtTypography.overline)
            }
            val textColor = if (isMe) Color.White else Color.Black.copy(0.7f)
            MessageText(
                text = message.content,
                style = mtTypography.body1.copy(color = textColor, lineHeight = 24.sp),
                onSpanClick = { type, tag ->
                    if (type == MessageSpanType.link) {
                        onUrlClick(tag)
                    } else if (type == MessageSpanType.mention) {
                        onPeopleClick(tag)
                    }
                },
                modifier = Modifier.background(
                    color = bgColor,
                    shape = MessageItemShape,
                ).padding(12.dp)
            )
            if (message.image != null) {
                Image(
                    painterResource(message.image),
                    null,
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .background(bgColor, MessageItemShape)
                        .padding(12.dp)
                        .widthIn(max = 200.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun BottomBar(
    onSend: (content: String) -> Unit,
) {
    Column(
        modifier = Modifier.background(mtColors.BLUE_1)
    ) {
        var action by remember { mutableStateOf<BottomAction?>(null) }
        BackHandler(
            enabled = action != null
        ) {
            action = null
        }
        var text by remember { mutableStateOf(TextFieldValue("")) }
        val focusRequester = remember { FocusRequester() }
        val keyboardController = LocalSoftwareKeyboardController.current
        val focusManager = LocalFocusManager.current
        BottomInputBar(
            text = text,
            onTextChanged = {
                text = it
            },
            focusRequester = focusRequester,
            onFocusChanged = {
                if (it) {
                    action = null
                }
            }
        )
        BottomActionBar(
            selectedAction = action,
            onActionClick = {
                if (action == it) {
                    action = null
                } else {
                    action = it
                }
                focusRequester.freeFocus()
                focusManager.clearFocus()
                keyboardController?.hide()
            },
            sendButtonEnabled = text.text.isNotBlank(),
            onSendClick = {
                onSend(text.text)
                text = TextFieldValue("")
                action = null
            }
        )
        if (action != BottomAction.emoji && action != null) {
            UnavailablePannel()
        } else if (action == BottomAction.emoji) {
            EmojiPannel(
                onEmojiClick = {
                    val str = text.text
                    val selection = text.selection
                    val newValue =
                        str.substring(0, selection.start) + it + str.substring(selection.end)
                    text = text.copy(
                        text = newValue,
                        selection = TextRange(
                            start = selection.start + it.length,
                            end = selection.end + it.length
                        ),
                    )
                }
            )
        }
    }
}

@Composable
private fun BottomInputBar(
    text: TextFieldValue,
    onTextChanged: (TextFieldValue) -> Unit,
    focusRequester: FocusRequester,
    onFocusChanged: (focus: Boolean) -> Unit,
) {
    val colors = TextFieldDefaults.textFieldColors(
        backgroundColor = Color.Transparent,
        textColor = mtColors.onSurface,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
        errorIndicatorColor = Color.Transparent,
    )
    TextField(
        value = text,
        onValueChange = onTextChanged,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 150.dp)
            .focusRequester(focusRequester)
            .animateContentSize(tween(easing = LinearEasing))
            .onFocusChanged {
                onFocusChanged(it.isFocused)
            },
        colors = colors,
        placeholder = { Text("input message here") }
    )
}

@Composable
private fun BottomActionBar(
    selectedAction: BottomAction?,
    onActionClick: (BottomAction) -> Unit,
    sendButtonEnabled: Boolean,
    onSendClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BottomAction.values().forEach { action ->
            val mod = if (selectedAction == action) {
                Modifier.background(mtColors.primary, mtShapes.medium)
            } else {
                Modifier
            }
            val iconColor = if (selectedAction == action) {
                Color.White
            } else {
                mtColors.primary
            }
            IconButton(
                onClick = {
                    onActionClick(action)
                },
                modifier = mod
            ) {
                Icon(
                    if (selectedAction == action) action.selectedIcon else action.icon,
                    null,
                    tint = iconColor
                )
            }
        }
        Spacer(Modifier.weight(1f))
        OutlinedButton(
            onClick = onSendClick,
            enabled = sendButtonEnabled,
            shape = CircleShape,
        ) {
            Text("Send")
        }
        Spacer(Modifier.width(6.dp))
    }
}

@Composable
private fun UnavailablePannel() {
    Column(
        modifier = Modifier.fillMaxWidth().height(200.dp).background(mtColors.BLUE_1),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
    ) {
        Text(
            text = "Function currently not available",
            style = mtTypography.h6,
        )
        Text(
            text = "Check back later",
            style = mtTypography.caption,
        )
    }
}

@Composable
private fun EmojiPannel(
    onEmojiClick: (emoji: String) -> Unit,
) {
    Grid(
        count = EMOJIS.count(),
        cells = 10,
        itemContent = { maxWidth, index ->
            val emoji = EMOJIS[index]
            Text(
                text = emoji,
                modifier = Modifier
                    .size(maxWidth / 10)
                    .clickable {
                        onEmojiClick(emoji)
                    }
                    .wrapContentSize(Alignment.Center)
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(mtColors.BLUE_1)
    )
}
