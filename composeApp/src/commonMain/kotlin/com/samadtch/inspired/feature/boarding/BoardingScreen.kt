package com.samadtch.inspired.feature.boarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.samadtch.inspired.common.LOADING_STATE
import com.samadtch.inspired.common.SUCCESS_STATE
import com.samadtch.inspired.common.exceptions.AuthException.Companion.AUTH_TOKEN_CONFLICT_ERROR
import com.samadtch.inspired.common.exceptions.AuthException.Companion.AUTH_TOKEN_REQUEST_ERROR
import com.samadtch.inspired.common.exceptions.AuthException.Companion.AUTH_TOKEN_REQUEST_ERROR_OTHER
import com.samadtch.inspired.common.exceptions.AuthException.Companion.AUTH_TOKEN_SERVER_ERROR_OTHER
import inspired.composeapp.generated.resources.*
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource

const val BOARDING_ROUTE = "/boarding"

@Composable
internal fun BoardingRoot(
    modifier: Modifier = Modifier,
    viewModel: BoardingViewModel,
    onShowSnackbar: suspend (Boolean, String, String?) -> Unit,
    onSplashScreenDone: () -> Unit,
    authorize: () -> Unit,
    authorizationCode: Pair<String, String>?,
    goHome: () -> Unit,
) {
    //------------------------------- Declarations
    var goLast by rememberSaveable { mutableStateOf(false) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val loginState by viewModel.loginState.collectAsStateWithLifecycle()

    //Texts
    val errorAuthConflict = stringResource(Res.string.error_auth_conflict)
    val errorAuthRequest = stringResource(Res.string.error_auth_request)
    val errorAuthRequestOther = stringResource(Res.string.error_auth_request_other)
    val errorAuthServer = stringResource(Res.string.error_auth_server)
    val errorAuthOther = stringResource(Res.string.error_auth_other)

    //------------------------------- Effects
    //Catch Code sent from MainActivity (Caught from Intent)
    LaunchedEffect(authorizationCode) {
        if (authorizationCode != null) viewModel.authenticate(
            authorizationCode.first,
            authorizationCode.second
        )
    }

    LaunchedEffect(uiState) {
        if (uiState != null) {
            if (uiState?.isLoggedIn == true) goHome()
            else if (uiState?.isFirstOpen == false) goLast = true
            onSplashScreenDone()
        }
    }

    //Catch Token Generation State
    LaunchedEffect(loginState) {
        if (loginState != null && loginState != LOADING_STATE) when (loginState) {
            SUCCESS_STATE -> goHome()
            AUTH_TOKEN_CONFLICT_ERROR -> onShowSnackbar(false, errorAuthConflict, null)
            AUTH_TOKEN_REQUEST_ERROR -> onShowSnackbar(false, errorAuthRequest, null)
            AUTH_TOKEN_REQUEST_ERROR_OTHER -> onShowSnackbar(false, errorAuthRequestOther, null)
            AUTH_TOKEN_SERVER_ERROR_OTHER -> onShowSnackbar(false, errorAuthServer, null)
            else -> onShowSnackbar(false, errorAuthOther, null)
        }
    }

    //------------------------------- UI
    BoardingScreen(
        modifier = modifier,
        goLast = goLast,
        onAuthClick = {
            viewModel.setFirstTimeOpened()
            authorize()
        },
        loginState = loginState
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BoardingScreen(
    modifier: Modifier = Modifier,
    onAuthClick: () -> Unit,
    goLast: Boolean,
    loginState: Int?
) {
    //------------------------------- Declarations
    //Boarding Content
    val titles = stringArrayResource(Res.array.boarding_titles)
    val descriptions = stringArrayResource(Res.array.boarding_descriptions)
    //Others
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { titles.size })

    //------------------------------- Side Effects
    LaunchedEffect(goLast) { if (goLast) pagerState.scrollToPage(pagerState.pageCount - 1) }

    //------------------------------- UI
    Column(modifier) {
        //Top Section
        Box(modifier = Modifier.weight(1f))

        Column(Modifier.weight(1f)) {
            Text(
                modifier = Modifier.padding(top = 32.dp, start = 24.dp),
                text = stringResource(Res.string.app_name),
                style = MaterialTheme.typography.displayLarge.copy(
                    fontFamily = FontFamily(Font(Res.font.logo_font))
                )
            )

            //Boarding Messages
            HorizontalPager(
                modifier = Modifier
                    .padding(top = 32.dp)
                    .fillMaxWidth(), state = pagerState
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = titles[pagerState.currentPage],
                        style = MaterialTheme.typography.headlineLarge
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = descriptions[pagerState.currentPage],
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            }
        }

        //Bottom Section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 32.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            //Button
            FilledTonalButton(modifier = Modifier.align(Alignment.CenterVertically), onClick = {
                if (pagerState.currentPage < pagerState.pageCount - 1) coroutineScope.launch {
                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                } else onAuthClick()
            }) {
                if (loginState == LOADING_STATE) CircularProgressIndicator(
                    modifier = Modifier
                        .padding(0.dp, 0.dp, 16.dp, 0.dp)
                        .size(28.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    text = if (pagerState.currentPage < titles.size - 1) stringResource(Res.string.next)
                    else stringResource(Res.string.authenticate),
                    style = MaterialTheme.typography.labelLarge
                )
            }

            //Indicators
            Row(
                Modifier
                    .height(32.dp)
                    .padding(end = 16.dp)
                    .align(Alignment.CenterVertically)
            ) {
                repeat(pagerState.pageCount) { iteration ->
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(2.dp)
                            .clip(CircleShape)
                            .border(
                                color = MaterialTheme.colorScheme.secondary,
                                width = 1.dp,
                                shape = CircleShape
                            )
                            .background(
                                if (pagerState.currentPage == iteration) MaterialTheme.colorScheme.secondary
                                else Color.Transparent
                            )
                            .size(12.dp)
                    )
                }
            }
        }
    }
}