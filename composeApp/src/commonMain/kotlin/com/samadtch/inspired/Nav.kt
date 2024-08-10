package com.samadtch.inspired

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.rememberNavigator
import moe.tlaster.precompose.navigation.transition.NavTransition

@Composable
fun Nav(
    modifier : Modifier,
    onSplashScreenDone: () -> Unit,
    onShowSnackbar: (Boolean, String, String?) -> Unit,
    onDrawerOpenClick: () -> Unit,
) {
    //------------------------------- Declarations
    val navigator = rememberNavigator()

    //------------------------------- UI
    NavHost(
        modifier = modifier,
        navigator = navigator,
        navTransition = NavTransition(),
        initialRoute = "/boarding",
    ) {

    }
}