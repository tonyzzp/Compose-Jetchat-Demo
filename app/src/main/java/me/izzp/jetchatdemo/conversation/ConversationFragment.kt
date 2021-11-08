package me.izzp.jetchatdemo.conversation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.ComposeView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.ViewWindowInsetObserver
import me.izzp.jetchatdemo.*

class ConversationFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val profiles = listOf(colleagueProfile, meProfile)
        val view = ComposeView(inflater.context)
        val insets = ViewWindowInsetObserver(view).start(false)
        view.setContent {
            CompositionLocalProvider(LocalWindowInsets provides insets) {
                ConversationPage(
                    messages = exampleUiState.messages,
                    onPeopleClick = { name ->
                        val profile = profiles.find { it.name == name }
                        if (profile != null) {
                            findNavController().navigateTo(
                                R.id.profileFragment,
                                bundleOf(
                                    "userId" to profile.userId
                                )
                            )
                        }
                    },
                    onUrlClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
                        startActivity(intent)
                    },
                    onConversationClick = {
                        findNavController().navigateTo(R.id.conversationFragment)
                    },
                    onSend = {
                        exampleUiState.addMessage(
                            Message(
                                author = meProfile.userId,
                                content = it,
                                timestamp = "justnow",
                            )
                        )
                    }
                )
            }
        }
        return view
    }

}