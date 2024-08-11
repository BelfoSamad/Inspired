package com.samadtch.inspired

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.samadtch.inspired.domain.models.AssetFile
import com.samadtch.inspired.ui.components.CustomSnackbar
import com.samadtch.inspired.ui.theme.InspiredTheme
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import moe.tlaster.precompose.PreComposeApp
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App(
    onSplashScreenDone: () -> Unit,
    authorize: () -> Unit,
    authorizationCode: Pair<String, StateFlow<String?>>,
    onFilePick: () -> Unit,
    assetFile: StateFlow<AssetFile?>
) {
    PreComposeApp {
        //------------------------------- Declarations
        val scope = rememberCoroutineScope()
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val snackbarHostState = remember { SnackbarHostState() }
        var snackbarSuccess by remember { mutableStateOf(false) }

        //------------------------------- Effects

        //------------------------------- UI
        InspiredTheme {
            //------------------------------- Dialogs

            //------------------------------- UI
            Scaffold(
                snackbarHost = {
                    SnackbarHost(
                        hostState = snackbarHostState,
                        snackbar = {
                            CustomSnackbar(
                                isSuccess = snackbarSuccess,
                                content = it.visuals.message
                            )
                        }
                    )
                }
            ) {
                Nav(
                    modifier = Modifier.fillMaxSize().padding(it),
                    onSplashScreenDone = onSplashScreenDone,
                    onShowSnackbar = { success, message, action ->
                        snackbarSuccess = success
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = message,
                                actionLabel = action,
                                duration = SnackbarDuration.Short,
                            )
                        }
                    },
                    authorize = authorize,
                    authorizationCode = authorizationCode,
                    onLogout = {
                        //TODO: Handle State when Logging Out
                    },
                    onDrawerMenuClick = { scope.launch { drawerState.open() } },
                    onFilePick = onFilePick,
                    assetFile = assetFile
                )
            }
        }
    }
}