package com.samadtch.inspired

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.samadtch.inspired.domain.models.AssetFile
import com.samadtch.inspired.feature.boarding.BOARDING_ROUTE
import com.samadtch.inspired.feature.boarding.BoardingRoot
import com.samadtch.inspired.feature.boarding.BoardingViewModel
import com.samadtch.inspired.feature.home.HOME_ROUTE
import com.samadtch.inspired.feature.home.HomeRoute
import com.samadtch.inspired.feature.home.HomeViewModel
import org.koin.compose.koinInject

@Composable
fun Nav(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier,
    onSplashScreenDone: () -> Unit,
    onDirectionChanges: (String?) -> Unit,
    loggedOut: Unit?,
    onShowSnackbar: suspend (Boolean, String, String?) -> Unit,
    authorize: () -> Unit,
    authorizationCode: Pair<String, String>?,
    onLogin: () -> Unit,
    onLogout: () -> Unit,
    onDrawerMenuClick: () -> Unit,
    onFilePick: () -> Unit,
    assetFile: AssetFile?
) {
    //------------------------------- Declarations
    navController.addOnDestinationChangedListener { _, destination, _ ->
        onDirectionChanges(destination.route)
    }

    LaunchedEffect(loggedOut) {
        if (loggedOut != null) {
            navController.navigate(
                BOARDING_ROUTE,
                navOptions {
                    launchSingleTop = true
                    popUpTo(HOME_ROUTE) { this.inclusive = true }
                }
            )
        }
    }

    //------------------------------- UI
    NavHost(
        navController = navController,
        modifier = modifier,
        startDestination = BOARDING_ROUTE,
    ) {
        //Boarding
        composable(route = BOARDING_ROUTE) {
            BoardingRoot(
                modifier = modifier,
                viewModel = koinInject<BoardingViewModel>(),
                onShowSnackbar = onShowSnackbar,
                onSplashScreenDone = onSplashScreenDone,
                authorize = authorize,
                authorizationCode = authorizationCode,
                goHome = {
                    onLogin()
                    navController.navigate(
                        HOME_ROUTE,
                        navOptions {
                            launchSingleTop = true
                            popUpTo(BOARDING_ROUTE) { this.inclusive = true }
                        }
                    )
                }
            )
        }

        //Home
        composable(route = HOME_ROUTE) {
            HomeRoute(
                modifier = modifier,
                viewModel = koinInject<HomeViewModel>(),
                onShowSnackbar = onShowSnackbar,
                onLogout = {
                    onLogout() //Logout on MainActivity
                    navController.navigate(
                        BOARDING_ROUTE,
                        navOptions {
                            launchSingleTop = true
                            popUpTo(HOME_ROUTE) { this.inclusive = true }
                        }
                    )
                },
                onDrawerMenuClick = onDrawerMenuClick,
                onFilePick = onFilePick,
                assetFile = assetFile
            )
        }
    }
}