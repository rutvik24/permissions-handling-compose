package com.atliq.permissionmanagement

import android.Manifest.*
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.atliq.permissionmanagement.ui.theme.PermissionManagementTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.shouldShowRationale

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PermissionManagementTheme {
                val permissionsState = rememberMultiplePermissionsState(
                    permissions = listOf(
                        permission.RECORD_AUDIO,
                        permission.CAMERA
                    )
                )

                val lifecycleOwner = LocalLifecycleOwner.current
                DisposableEffect(
                    key1 = lifecycleOwner,
                    effect = {
                        val observer = LifecycleEventObserver { _, event ->
                            if (event == Lifecycle.Event.ON_RESUME) {
                                permissionsState.launchMultiplePermissionRequest()
                            }
                        }

                        lifecycleOwner.lifecycle.addObserver(observer)

                        onDispose {
                            lifecycleOwner.lifecycle.removeObserver(observer)
                        }
                    }
                )

                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    permissionsState.permissions.forEach { perm ->
                        when (perm.permission) {
                            permission.CAMERA -> {
                                when {
                                    perm.status.isGranted -> {
                                        Text(text = "Camera Permission accepted")
                                    }
                                    perm.status.shouldShowRationale -> {
                                        Text(
                                            text = "Camera Permission is needed" +
                                                    " to access the camera"
                                        )
                                    }
                                    perm.isPermanentlyDenied() -> {
                                        Text(
                                            text = "Camera Permission was permanently " +
                                                    "denied. Please go to settings to enable it"
                                        )
                                    }
                                }
                            }
                            permission.RECORD_AUDIO -> {
                                when {
                                    perm.status.isGranted -> {
                                        Text(text = "Record Audio Permission accepted")
                                    }
                                    perm.status.shouldShowRationale -> {
                                        Text(
                                            text = "Record Audio Permission is needed" +
                                                    " to record audio"
                                        )
                                    }
                                    perm.isPermanentlyDenied() -> {
                                        Text(
                                            text = "Record Audio Permission was permanently " +
                                                    "denied. Please go to settings to enable it"
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
