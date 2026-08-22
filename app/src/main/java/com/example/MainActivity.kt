package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.AppBottomNavigation
import com.example.ui.components.AppTopBar
import com.example.ui.components.AuthModal
import com.example.ui.screens.ArcadeGamesScreen
import com.example.ui.screens.CallScreen
import com.example.ui.screens.ChatsScreen
import com.example.ui.screens.FeedScreen
import com.example.ui.screens.ReelsAndWatchScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.StoryViewerScreen
import com.example.ui.screens.ValkuAiScreen
import com.example.ui.screens.WebPortalAndInviteScreen
import com.example.ui.theme.ValkuBackground
import com.example.ui.theme.ValkuSarvaiyaTheme
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.CallType
import com.example.ui.viewmodel.ValkuViewModel
import com.example.ui.viewmodel.ValkuViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ValkuSarvaiyaTheme {
                val valkuViewModel: ValkuViewModel = viewModel(factory = ValkuViewModelFactory)
                ValkuSuperApp(viewModel = valkuViewModel)
            }
        }
    }
}

@Composable
fun ValkuSuperApp(viewModel: ValkuViewModel) {
    val currentTab by viewModel.currentTab.collectAsState()
    val activeCall by viewModel.activeCall.collectAsState()
    val storyState by viewModel.storyState.collectAsState()
    val activeThread by viewModel.activeThread.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val isSettingsOpen by viewModel.isSettingsOpen.collectAsState()
    val isAuthModalOpen by viewModel.isAuthModalOpen.collectAsState()
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ValkuBackground)
    ) {
        if (isSettingsOpen) {
            // Fullscreen Settings View
            SettingsScreen(
                viewModel = viewModel,
                onBack = { viewModel.closeSettings() }
            )
        } else {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    // TopBar hidden during active 1-on-1 chat or fullscreen overlays
                    if (activeThread == null && activeCall == null && !storyState.isOpen) {
                        Box(modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)) {
                            AppTopBar(
                                currentUser = currentUser,
                                onSearchClick = { /* Quick search */ },
                                onAiSparkClick = { viewModel.switchTab(AppTab.AI) },
                                onInviteClick = { viewModel.shareInviteLink(context) },
                                onQuickCallClick = {
                                    viewModel.startCall("Valku Sarvaiya", "@valku.creator", CallType.AUDIO)
                                },
                                onSettingsClick = { viewModel.openSettings() },
                                onProfileClick = { viewModel.openAuthModal() }
                            )
                        }
                    }
                },
                bottomBar = {
                    // BottomBar hidden during active 1-on-1 chat or fullscreen overlays
                    if (activeThread == null && activeCall == null && !storyState.isOpen) {
                        AppBottomNavigation(
                            selectedTab = currentTab,
                            onTabSelected = { tab -> viewModel.switchTab(tab) }
                        )
                    }
                },
                containerColor = ValkuBackground
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    Crossfade(targetState = currentTab, label = "tab_crossfade") { tab ->
                        when (tab) {
                            AppTab.CHATS -> ChatsScreen(viewModel = viewModel)
                            AppTab.FEED -> FeedScreen(viewModel = viewModel)
                            AppTab.REELS -> ReelsAndWatchScreen(viewModel = viewModel)
                            AppTab.AI -> ValkuAiScreen(viewModel = viewModel)
                            AppTab.ARCADE -> ArcadeGamesScreen(viewModel = viewModel)
                            AppTab.WEB_PORTAL -> WebPortalAndInviteScreen(viewModel = viewModel)
                        }
                    }
                }
            }
        }

        // Fullscreen Active Call Overlay
        if (activeCall != null) {
            CallScreen(
                callState = activeCall!!,
                onMuteToggle = { viewModel.toggleCallMute() },
                onCameraToggle = { viewModel.toggleCallCamera() },
                onFlipCamera = { viewModel.flipCallCamera() },
                onSpeakerToggle = { viewModel.toggleCallSpeaker() },
                onEndCall = { viewModel.endCall() }
            )
        }

        // Fullscreen Story Viewer Overlay
        if (storyState.isOpen) {
            StoryViewerScreen(
                storyState = storyState,
                onNext = { viewModel.nextStory() },
                onPrev = { viewModel.previousStory() },
                onClose = { viewModel.closeStoryViewer() },
                onReply = { reply -> viewModel.replyToStory(reply) }
            )
        }

        // Auth / Login Modal
        if (isAuthModalOpen) {
            AuthModal(
                currentUser = currentUser,
                onDismiss = { viewModel.closeAuthModal() },
                onLoginWithPhone = { phone, name -> viewModel.loginWithPhone(phone, name) },
                onLoginWithGoogle = { email, name -> viewModel.loginWithGoogle(email, name) },
                onLoginWithEmail = { email, name -> viewModel.loginWithEmail(email, name) },
                onUpdateProfile = { name, handle, bio, phone, email ->
                    viewModel.updateProfile(name, handle, bio, phone, email)
                },
                onLogout = { viewModel.logout() }
            )
        }
    }
}
