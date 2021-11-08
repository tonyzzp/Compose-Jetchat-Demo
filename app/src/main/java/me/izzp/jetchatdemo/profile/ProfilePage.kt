package me.izzp.jetchatdemo.profile

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.navigationBarsPadding
import me.izzp.jetchatdemo.JetChatScaffold
import me.izzp.jetchatdemo.ProfileScreenState
import me.izzp.jetchatdemo.R
import me.izzp.jetchatdemo.themeColor
import me.izzp.jetchatdemo.ui.theme.*

@Composable
fun ProfilePage(
    profile: ProfileScreenState,
    onPeopleClick: (name: String) -> Unit,
    onConversationClick: () -> Unit,
) {
    val def = mtColors.surface
    val blue1 = mtColors.BLUE_1
    var topBarColor by remember { mutableStateOf(def) }
    val scrollState = rememberScrollState()
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                topBarColor = scrollState.themeColor(def, blue1, 150f)
                return super.onPreScroll(available, source)
            }
        }
    }
    JetChatTheme {
        JetChatScaffold(
            modifier = Modifier.nestedScroll(nestedScrollConnection),
            topBarColor = topBarColor,
            topBarActions = {
                var open by remember { mutableStateOf(false) }
                IconButton(
                    onClick = {
                        open = true
                    }
                ) {
                    Icon(Icons.Default.Menu, null)
                }
                DropdownMenu(
                    expanded = open,
                    onDismissRequest = { open = false },
                ) {
                    DropdownMenuItem(onClick = {}) {
                        Icon(Icons.Default.Refresh, null)
                        Text("Refresh")
                    }
                    DropdownMenuItem(onClick = {}) {
                        Icon(Icons.Default.Share, null)
                        Text("Share")
                    }
                }
            },
            onPeopleClick = onPeopleClick,
            onConversationClick = onConversationClick,
            floatingActionButton = {
                val expand = scrollState.value > 0
                FloatingActionButton(
                    onClick = {},
                    shape = if (expand) mtShapes.large else CircleShape,
                    modifier = Modifier.navigationBarsPadding()
                ) {
                    Row(
                        modifier = Modifier.animateContentSize().padding(
                            if (expand) 8.dp else 0.dp,
                            0.dp,
                        ),
                    ) {
                        Icon(Icons.Default.Edit, null)
                        if (expand) {
                            Spacer(Modifier.width(4.dp))
                            Text("Edit Profile")
                        }
                    }
                }
            },
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .padding(18.dp)
                    .navigationBarsPadding()
            ) {
                Image(
                    painterResource(profile.photo ?: R.drawable.someone_else),
                    null,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                        .clip(
                            CircleShape
                        )
                )
                Text(
                    text = profile.displayName,
                    style = mtTypography.h6,
                    modifier = Modifier.padding(0.dp, 12.dp)
                )
                Text(
                    text = profile.position, style = mtTypography.body1,
                    modifier = Modifier.padding(0.dp, 4.dp)
                )

                Line("Name", profile.name)
                Line("Status", profile.status)
                Line("Twiier", profile.twitter, true)
                profile.timeZone?.also {
                    Line("Timezone", it)
                }
            }
        }
    }
}

@Composable
private fun Divider() {
    Divider(Modifier.padding(0.dp, 8.dp))
}

@Composable
private fun Line(
    title: String,
    content: String,
    isLink: Boolean = false
) {
    Divider()
    Text(text = title, style = mtTypography.caption)
    if (isLink) {
        ClickableText(
            text = buildAnnotatedString {
                pushStyle(
                    SpanStyle(
                        color = mtColors.primary,
                        textDecoration = TextDecoration.Underline,
                    )
                )
                append(content)
            },
            style = mtTypography.body1.copy(color = LocalContentColor.current),
        ) {

        }
    } else {
        Text(text = content, style = mtTypography.body1)
    }
}