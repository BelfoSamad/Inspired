package com.samadtch.inspired

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.IntegrationInstructions
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.samadtch.inspired.domain.models.AssetFile
import com.samadtch.inspired.feature.home.HOME_ROUTE
import com.samadtch.inspired.ui.components.CustomSnackbar
import com.samadtch.inspired.ui.theme.InspiredTheme
import inspired.composeapp.generated.resources.Res
import inspired.composeapp.generated.resources.*
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview


data class AppUiState(
    val profileName: String = "",
    val links: Map<String, String> = mapOf()
)

@Composable
@Preview
fun App(
    appState: AppUiState,
    onSplashScreenDone: () -> Unit,
    openWebPage: (String) -> Unit,
    launchReview: () -> Unit,
    authorize: () -> Unit,
    authorizationCode: Pair<String, String>?,
    onFilePick: () -> Unit,
    assetFile: AssetFile?,
    logout: () -> Unit
) {
    //------------------------------- Declarations
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarSuccess by remember { mutableStateOf(false) }
    var current by remember { mutableStateOf<String?>(null) }

    //------------------------------- UI
    InspiredTheme {

        //------------------------------- UI
        ModalNavigationDrawer(
            drawerState = drawerState,
            gesturesEnabled = current == HOME_ROUTE,
            drawerContent = {
                ModalDrawerSheet {
                    Column(
                        modifier = Modifier
                            .padding(start = 24.dp, top = 64.dp)
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                modifier = Modifier.align(Alignment.CenterHorizontally),
                                text = stringResource(Res.string.app_name),
                                style = MaterialTheme.typography.displayMedium.copy(
                                    fontFamily = FontFamily(Font(Res.font.logo_font)),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                            Spacer(Modifier.height(24.dp))
                            Column {
                                NavigationDrawerItem(
                                    label = {
                                        Text(
                                            text = stringResource(Res.string.privacy_policy),
                                            style = MaterialTheme.typography.labelLarge
                                        )
                                    },
                                    selected = false,
                                    onClick = { openWebPage(appState.links["privacy"]!!) },
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Default.Lock,
                                            contentDescription = null
                                        )
                                    }
                                )//Privacy Policy

                                NavigationDrawerItem(
                                    label = {
                                        Text(
                                            text = stringResource(Res.string.tos),
                                            style = MaterialTheme.typography.labelLarge
                                        )
                                    },
                                    selected = false,
                                    onClick = { openWebPage(appState.links["tos"]!!) },
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Default.Description,
                                            contentDescription = null
                                        )
                                    }
                                )//Terms of Use

                                NavigationDrawerItem(
                                    label = {
                                        Text(
                                            text = stringResource(Res.string.check_developer),
                                            style = MaterialTheme.typography.labelLarge
                                        )
                                    },
                                    selected = false,
                                    onClick = { openWebPage("https://play.google.com/store/apps/developer?id=${appState.links["developer"]}") },
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Default.IntegrationInstructions,
                                            contentDescription = null,
                                        )
                                    }
                                )//Check Developer

                                NavigationDrawerItem(
                                    label = {
                                        Text(
                                            text = stringResource(Res.string.review_app),
                                            style = MaterialTheme.typography.labelLarge
                                        )
                                    },
                                    selected = false,
                                    onClick = { launchReview() },
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Default.RateReview,
                                            contentDescription = null
                                        )
                                    }
                                )//Review App

                            } //Mid Section End
                        }


                        //Bottom Section
                        Column(Modifier.padding(bottom = 16.dp)) {
                            NavigationDrawerItem(
                                label = {
                                    Text(
                                        text = stringResource(Res.string.logout),
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    )
                                },
                                selected = false,
                                onClick = {
                                    logout()
                                    scope.launch { drawerState.close() }
                                },
                                icon = {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Default.Logout,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            )//Logout
                            Spacer(Modifier.padding(16.dp))
                        } //Bottom Section
                    }
                }
            }
        ) {
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
                    onLogout = logout,
                    onDrawerMenuClick = { scope.launch { drawerState.open() } },
                    onFilePick = onFilePick,
                    assetFile = assetFile
                )
            }
        }
    }
}