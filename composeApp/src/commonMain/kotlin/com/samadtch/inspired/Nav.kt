package com.samadtch.inspired

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.samadtch.inspired.domain.models.AssetFile
import com.samadtch.inspired.feature.home.HOME_ROUTE
import com.samadtch.inspired.feature.home.HomeRoute
import com.samadtch.inspired.feature.home.HomeViewModel
import moe.tlaster.precompose.koin.koinViewModel
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.rememberNavigator
import moe.tlaster.precompose.navigation.transition.NavTransition

@Composable
fun Nav(
    modifier : Modifier,
    onSplashScreenDone: () -> Unit,
    onShowSnackbar: suspend (Boolean, String, String?) -> Unit,
    onLogout: () -> Unit,
    onDrawerMenuClick: () -> Unit,
    onFilePick: () -> Unit,
    assetFile: AssetFile?
) {
    //------------------------------- Declarations
    val navigator = rememberNavigator()

    //------------------------------- UI
    NavHost(
        modifier = modifier,
        navigator = navigator,
        navTransition = NavTransition(),
        initialRoute = HOME_ROUTE,
    ) {
        //Home
        scene(
            route = HOME_ROUTE,
            navTransition = NavTransition()
        ) {
            val viewModel = koinViewModel(HomeViewModel::class)
            HomeRoute(
                modifier = modifier,
                viewModel = viewModel,
                onShowSnackbar = onShowSnackbar,
                onLogout = onLogout,
                onDrawerMenuClick = onDrawerMenuClick,
                onFilePick = onFilePick,
                assetFile = assetFile
            )
        }
    }
}