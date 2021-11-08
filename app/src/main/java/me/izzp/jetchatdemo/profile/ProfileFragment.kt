package me.izzp.jetchatdemo.profile

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
import me.izzp.jetchatdemo.R
import me.izzp.jetchatdemo.colleagueProfile
import me.izzp.jetchatdemo.meProfile
import me.izzp.jetchatdemo.navigateTo

class ProfileFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = ComposeView(inflater.context)
        val insets = ViewWindowInsetObserver(view).start(false)
        view.setContent {
            CompositionLocalProvider(LocalWindowInsets provides insets) {
                val profiles = listOf(colleagueProfile, meProfile)
                val args = findNavController().currentBackStackEntry!!.arguments
                val profile = profiles.find { it.userId == args!!.getString("userId") }
                ProfilePage(
                    profile = profile!!,
                    onPeopleClick = { name ->
                        val profile = profiles.find { it.name == name }
                        findNavController().navigateTo(
                            R.id.profileFragment,
                            bundleOf("userId" to profile!!.userId),
                        )
                    },
                    onConversationClick = {
                        findNavController().popBackStack(R.id.conversationFragment, false)
                    },
                )
            }
        }
        return view
    }
}