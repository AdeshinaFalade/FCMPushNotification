package com.example.fcmpushnotification

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.example.fcmpushnotification.ui.theme.FCMPushNotificationTheme

class MainActivity : ComponentActivity() {
    private val viewModel: ChatViewModel by viewModels()


    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)

        super.onCreate(savedInstanceState)
        requestNotificationPermission()
        setContent {
            FCMPushNotificationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val state = viewModel.state

                    Scaffold(topBar = {
                        CustomTitleBar(
                            onBackNav = { this@MainActivity.moveTaskToBack(true) },
                            title = "Home"
                        )
                    }) {
                        if (state.isEnteringToken) {
                            EnterTokenDialog(
                                token = state.remoteToken,
                                onTokenChange = viewModel::onRemoteTokenChange,
                                onSubmit = viewModel::onSubmitToken
                            )
                        } else {
                            ChatScreen(
                                messageText = state.messageText,
                                onMessageChange = viewModel::onMessageChange,
                                onMessageSend = {
                                    viewModel.sendMessage(false)
                                },
                                onMessageBroadcast = {
                                    viewModel.sendMessage(true)
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            val hasPermission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if(!hasPermission){
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    0
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTitleBar(
    modifier: Modifier = Modifier,
    title: String,
    onBackNav: NullableFunction? = null,
    icon: @Composable NullableFunction? = null,
) {
    CenterAlignedTopAppBar(
        modifier = modifier
            .height(96.dp)
            .paint(
                painter = painterResource(id = R.drawable.pattern_bg),
                contentScale = ContentScale.FillBounds,
            ),
        title = {
            Text(
                text = title,
                style = TextStyle(
                    fontWeight = FontWeight.W500,
                    fontSize = 18.sp,
                    lineHeight = 21.78.sp,
                    color = Color.White
                ),
                modifier = Modifier
                    .padding(top = 5.dp)
                    .testTag("title-text")
                    .semantics { contentDescription = "Title" }
            )
        },
        navigationIcon = {
            IconButton(onClick = {
                onBackNav?.let {
                    it()
                }
            }) {
                if (icon == null) {
                    onBackNav?.let {
                        Image(
                            painter = painterResource(id = R.drawable.ic_back_new),
                            contentDescription = "back Btn",
                            modifier = Modifier.padding(bottom = 10.dp)
                        )
                    }
                } else {
                    icon()
                }
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Black.copy(alpha = 0.75f)
        )
    )

}

