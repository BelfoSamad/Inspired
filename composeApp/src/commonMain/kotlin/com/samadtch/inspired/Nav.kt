package com.samadtch.inspired

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.samadtch.inspired.domain.models.AssetFile
import com.samadtch.inspired.feature.boarding.BOARDING_ROUTE
import com.samadtch.inspired.feature.boarding.BoardingRoot
import com.samadtch.inspired.feature.boarding.BoardingViewModel
import com.samadtch.inspired.feature.home.HOME_ROUTE
import com.samadtch.inspired.feature.home.HomeRoute
import com.samadtch.inspired.feature.home.HomeViewModel
import kotlinx.coroutines.flow.StateFlow
import moe.tlaster.precompose.koin.koinViewModel
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.NavOptions
import moe.tlaster.precompose.navigation.PopUpTo
import moe.tlaster.precompose.navigation.rememberNavigator
import moe.tlaster.precompose.navigation.transition.NavTransition

@Composable
fun Nav(
    modifier: Modifier,
    onSplashScreenDone: () -> Unit,
    onShowSnackbar: suspend (Boolean, String, String?) -> Unit,
    authorize: () -> Unit,
    authorizationCode: Pair<String, StateFlow<String?>>,
    onLogout: () -> Unit,
    onDrawerMenuClick: () -> Unit,
    onFilePick: () -> Unit,
    assetFile: StateFlow<AssetFile?>
) {
    //------------------------------- Declarations
    val navigator = rememberNavigator()

    //------------------------------- UI
    NavHost(
        modifier = modifier,
        navigator = navigator,
        navTransition = NavTransition(),
        initialRoute = BOARDING_ROUTE,
    ) {
        //Boarding
        scene(
            route = BOARDING_ROUTE,
            navTransition = NavTransition()
        ) {
            val viewModel = koinViewModel(BoardingViewModel::class)
            BoardingRoot(
                modifier = modifier,
                viewModel = viewModel,
                onShowSnackbar = onShowSnackbar,
                onSplashScreenDone = onSplashScreenDone,
                authorize = authorize,
                authorizationCode = authorizationCode,
                goHome = {
                    navigator.navigate(
                        HOME_ROUTE,
                        NavOptions(popUpTo = PopUpTo(BOARDING_ROUTE, true))
                    )
                }
            )
        }

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