package com.ag_apps.voicecallingapp

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.FragmentActivity
import com.ag_apps.voicecallingapp.ui.theme.VoiceCallingAppTheme
import com.permissionx.guolindev.PermissionX
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallService
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationConfig
import com.zegocloud.uikit.prebuilt.call.invite.widget.ZegoSendCallInvitationButton
import com.zegocloud.uikit.service.defines.ZegoUIKitUser

class MainActivity : FragmentActivity() {

    private var username by mutableStateOf("")
    private var isLoggedIn by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VoiceCallingAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    Box(modifier = Modifier.padding(innerPadding)) {
                        if (isLoggedIn) {
                            CallingScreen()
                        } else {
                            LoginScreen()
                        }
                    }

                }
            }
        }
    }

    @Composable
    fun LoginScreen(modifier: Modifier = Modifier) {
        Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(text = "Please Login")

            Spacer(modifier = Modifier.height(40.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it }
            )

            Spacer(modifier = Modifier.height(40.dp))

            if (username.isNotBlank()) {
                Button(onClick = {
                    login()
                }) {
                    Text(text = "Login")
                }
            }

        }
    }

    @Composable
    fun CallingScreen(modifier: Modifier = Modifier) {
        Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(text = "Hello $username")

            Spacer(modifier = Modifier.height(12.dp))

            Text(text = "Enter a username to call")

            Spacer(modifier = Modifier.height(40.dp))

            var target by remember { mutableStateOf("") }
            var showCallButton by remember { mutableStateOf(false) }

            OutlinedTextField(
                value = target,
                onValueChange = { target = it },
                label = { Text(text = "Enter a username to call") },
                trailingIcon = {
                    if (target.isNotBlank()) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier
                                .clickable {
                                    showCallButton = false
                                    showCallButton = true
                                }
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(40.dp))

            if (showCallButton) {
                AndroidView(
                    modifier = Modifier.size(50.dp),
                    factory = {
                        ZegoSendCallInvitationButton(it).apply {
                            setIsVideoCall(false)
                            resourceID = "zego_uikit_call"
                            setInvitees(
                                listOf(
                                    ZegoUIKitUser(target, target)
                                )
                            )
                        }
                    }
                )
            }
        }
    }

    private fun login() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permission {
                initLogin()
            }
        } else {
            initLogin()
        }
    }

    private fun initLogin() {
        val appID = 0L
        val appSign = "xxxx"
        val userID = username
        val userName = username

        val callInvitationConfig = ZegoUIKitPrebuiltCallInvitationConfig()

        ZegoUIKitPrebuiltCallService.init(
            application, appID, appSign, userID, userName, callInvitationConfig
        )

        isLoggedIn = true
    }

    private fun permission(
        onGranted: () -> Unit,
    ) {
        PermissionX.init(this).permissions(Manifest.permission.SYSTEM_ALERT_WINDOW)
            .onExplainRequestReason { scope, deniedList ->
                val message =
                    "We need your consent for the following " +
                            "permissions in order to use the offline voice call function properly"
                scope.showRequestReasonDialog(
                    deniedList, message, "Allow", "Deny"
                )
            }.request { allGranted, grantedList, deniedList ->
                if (allGranted) {
                    onGranted()
                }
            }

    }
}


