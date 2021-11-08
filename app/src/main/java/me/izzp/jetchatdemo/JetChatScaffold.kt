package me.izzp.jetchatdemo

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch
import me.izzp.jetchatdemo.ui.theme.BLUE_1
import me.izzp.jetchatdemo.ui.theme.BLUE_2
import me.izzp.jetchatdemo.ui.theme.mtColors
import me.izzp.jetchatdemo.ui.theme.mtTypography


private val MENUS = listOf("composers", "droidcon-nyc")

@Composable
fun JetChatScaffold(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    topBarColor: Color = mtColors.surface,
    topBarTitle: @Composable () -> Unit = {},
    topBarActions: @Composable RowScope.() -> Unit = {},
    onPeopleClick: (name: String) -> Unit = {},
    onConversationClick: () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val systemUiController = rememberSystemUiController()
    systemUiController.setNavigationBarColor(Color.Transparent, !isSystemInDarkTheme())
    systemUiController.setStatusBarColor(topBarColor, !isSystemInDarkTheme())
    Scaffold(
        scaffoldState = scaffoldState,
        drawerContent = {
            JetChatDrawer(
                onPeopleClick = {
                    coroutineScope.launch {
                        scaffoldState.drawerState.close()
                    }
                    onPeopleClick(it)
                },
                onConversationClick = {
                    coroutineScope.launch {
                        scaffoldState.drawerState.close()
                    }
                    onConversationClick()
                },
            )
        },
        drawerBackgroundColor = mtColors.BLUE_1,
        drawerContentColor = mtColors.onSurface,
        drawerShape = RectangleShape,
        topBar = {
            JetChatTopAppBar(
                backgroundColor = topBarColor,
                topBarTitle = topBarTitle,
                topBarActions = topBarActions,
                onNavIconClick = {
                    coroutineScope.launch {
                        scaffoldState.drawerState.open()
                    }
                },
            )
        },
        content = content,
        floatingActionButton = floatingActionButton
    )
}

@Composable
private fun JetChatTopAppBar(
    backgroundColor: Color,
    topBarTitle: @Composable () -> Unit,
    topBarActions: @Composable RowScope.() -> Unit,
    onNavIconClick: () -> Unit,
) {
    Box(
        Modifier.height(IntrinsicSize.Min)
    ) {
        TopAppBar(
            title = {},
            backgroundColor = backgroundColor,
            contentColor = mtColors.onSurface,
            actions = topBarActions,
            navigationIcon = {
                IconButton(onClick = onNavIconClick) {
                    Icon(
                        painterResource(R.drawable.ic_jetchat),
                        null,
                        tint = mtColors.primary,
                    )
                }
            },
            modifier = Modifier.statusBarsPadding(),
            elevation = 0.dp,
        )
        Box(
            modifier = Modifier.fillMaxSize().statusBarsPadding().wrapContentSize(Alignment.Center),
            content = { topBarTitle() },
        )
    }
}

@Composable
private fun ColumnScope.JetChatDrawer(
    onConversationClick: () -> Unit,
    onPeopleClick: (name: String) -> Unit,
) {
    Spacer(Modifier.statusBarsPadding())

    Row(
        modifier = Modifier.height(56.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(Modifier.width(12.dp))
        Icon(
            painter = painterResource(R.drawable.ic_jetchat_front),
            null,
            tint = mtColors.primary,
        )
        Spacer(Modifier.width(6.dp))
        Image(
            painterResource(R.drawable.jetchat_logo),
            null
        )
    }
    Divider()

    Caption("Chats")
    var currentMenu by remember { mutableStateOf(MENUS.first()) }
    MENUS.forEach { menu ->
        MenuItem(
            menu = menu,
            selected = currentMenu,
            onClick = {
                currentMenu = menu
                onConversationClick()
            },
        )
    }
    Divider()

    Caption("Recent Profiles")
    listOf(meProfile, colleagueProfile).forEach { profile ->
        ProfileItem(
            userId = profile.userId,
            icon = profile.photo ?: R.drawable.someone_else,
            name = profile.displayName,
            onClick = {
                onPeopleClick(profile.name)
            },
        )
    }
}

@Composable
private fun ProfileItem(
    userId: String,
    @DrawableRes icon: Int,
    name: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable(onClick = onClick)
            .padding(start = 28.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painterResource(icon),
            null,
            modifier = Modifier.size(36.dp).clip(CircleShape)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = name,
            style = mtTypography.subtitle1,
        )
    }
}

@Composable
private fun MenuItem(
    menu: String,
    selected: String,
    onClick: () -> Unit,
) {
    val isSelected = selected == menu
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp, 4.dp)
            .height(56.dp)
            .background(
                color = if (isSelected) mtColors.BLUE_2 else Color.Transparent,
                shape = CircleShape,
            )
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .padding(16.dp, 0.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val color =
            if (isSelected) LocalContentColor.current else LocalContentColor.current.copy(0.7f)
        Icon(
            painterResource(R.drawable.ic_jetchat),
            null,
            tint = color
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = menu,
            style = mtTypography.subtitle1,
            color = color,
        )
    }
}

@Composable
private fun Divider() {
    Divider(Modifier.padding(0.dp, 4.dp))
}

@Composable
private fun Caption(text: String) {
    Text(
        text = text,
        style = mtTypography.caption,
        modifier = Modifier
            .padding(start = 12.dp)
            .height(48.dp)
            .wrapContentHeight(Alignment.CenterVertically)
    )
}

@Composable
@Preview
private fun JetChatDrawerPreview() {
    Column(
        Modifier.fillMaxSize().background(mtColors.primary.copy(0.2f))
    ) {
        JetChatDrawer(
            onConversationClick = {},
            onPeopleClick = {}
        )
    }
}