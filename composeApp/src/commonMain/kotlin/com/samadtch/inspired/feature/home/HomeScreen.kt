package com.samadtch.inspired.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Colorize
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Pattern
import androidx.compose.material.icons.filled.PlusOne
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.samadtch.inspired.common.exceptions.AuthException.Companion.AUTH_TOKEN_MISSING
import com.samadtch.inspired.common.exceptions.DataException.Companion.API_ERROR_NETWORK
import com.samadtch.inspired.common.exceptions.DataException.Companion.API_ERROR_RATE_LIMIT
import com.samadtch.inspired.domain.models.Asset
import com.samadtch.inspired.domain.models.Folder
import com.samadtch.inspired.ui.components.SortDropdown
import inspired.composeapp.generated.resources.Res
import inspired.composeapp.generated.resources.*
import moe.tlaster.precompose.flow.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.stringResource

const val HOME_ROUTE = "/home"

@Composable
internal fun HomeRoute(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel,
    onShowSnackbar: suspend (Boolean, String, String?) -> Unit,
    onLogout: () -> Unit,
    onDrawerMenuClick: () -> Unit,
) {
    //------------------------------- Declarations
    val homeUiState by viewModel.homeUiState.collectAsStateWithLifecycle()

    //------------------------------- UI
    HomeScreen(
        modifier = modifier,
        onShowSnackbar = onShowSnackbar,
        homeUiState = homeUiState,
        onLogout = onLogout,
        onDrawerMenuClick = onDrawerMenuClick,
        onAssetClick = { },//TODO: Handle Asset Click
        onAssetAddClick = { },//TODO: Handle Asset Adding Click
        onFolderClick = { },//TODO: Handle Folder Click
        onFolderAddClick = { },//TODO: Handle Folder Adding Click
    )
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onShowSnackbar: suspend (Boolean, String, String?) -> Unit,
    homeUiState: HomeViewModel.HomeUiState,
    onLogout: () -> Unit,
    onDrawerMenuClick: () -> Unit,
    onAssetClick: (Asset) -> Unit,
    onAssetAddClick: () -> Unit,
    onFolderClick: (Folder) -> Unit,
    onFolderAddClick: (String) -> Unit,
) {
    //------------------------------- Declarations
    var isLoading by rememberSaveable { mutableStateOf(true) }
    var errorCaught by rememberSaveable { mutableStateOf(false) }
    var assets by rememberSaveable { mutableStateOf(listOf<Asset>()) }
    var folders by rememberSaveable { mutableStateOf(listOf<Folder>()) }

    //Errors
    val networkError = stringResource(Res.string.error_network)
    val rateLimitError = stringResource(Res.string.error_rate_limit)
    val authOtherError = stringResource(Res.string.error_auth_other)
    val dataOtherError = stringResource(Res.string.error_data_other)

    //------------------------------- Side Effects
    LaunchedEffect(homeUiState) {
        when (homeUiState) {
            HomeViewModel.HomeUiState.Loading -> isLoading = true
            is HomeViewModel.HomeUiState.Error -> {
                //Handle Errors
                when (homeUiState.type) {
                    "AUTH" -> {
                        when (homeUiState.code) {
                            AUTH_TOKEN_MISSING -> onLogout()
                            else -> onShowSnackbar(false, authOtherError, null)
                        }
                    }

                    "DATA" -> {
                        when (homeUiState.code) {
                            API_ERROR_NETWORK -> onShowSnackbar(false, networkError, null)
                            API_ERROR_RATE_LIMIT -> onShowSnackbar(false, rateLimitError, null)
                            else -> onShowSnackbar(false, dataOtherError, null)
                        }
                    }
                }

                //Returns
                isLoading = false
                errorCaught = true
            }

            is HomeViewModel.HomeUiState.Success -> {
                isLoading = false
                assets = homeUiState.assets
                folders = homeUiState.folders
            }
        }
    }

    //------------------------------- UI
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(start = 16.dp, end = 16.dp, top = 32.dp, bottom = 24.dp)
    ) {
        //Top Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            //TODO: Use Logo Instead
            Text(
                text = stringResource(Res.string.app_name),
                style = MaterialTheme.typography.displaySmall
            )
            FilledTonalIconButton(onClick = { onDrawerMenuClick() }) {
                Icon(Icons.Default.Menu, null)
            }
        }
        Spacer(Modifier.height(24.dp))

        //Slogan
        Text(
            text = stringResource(Res.string.slogan),
            style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(Modifier.height(16.dp))

        //Inspirations
        Inspirations(
            loading = isLoading,
            errorCaught = errorCaught,
            assets = assets,
            onAssetClick = onAssetClick,
            onAssetAddClick = onAssetAddClick
        )

        //Folders
        Folders(
            loading = isLoading,
            errorCaught = errorCaught,
            folders = folders,
            onFolderClick = onFolderClick,
            onFolderAddClick = onFolderAddClick
        )
    }
}

@Composable
fun Inspirations(
    loading: Boolean = true,
    errorCaught: Boolean = false,
    assets: List<Asset>,
    onAssetClick: (Asset) -> Unit,
    onAssetAddClick: () -> Unit,
) {
    //------------------------------- Declarations
    var sortBy by remember { mutableStateOf("All") }
    val sortedAssets by remember {
        mutableStateOf(
            when (sortBy) {
                "All" -> assets
                "Palette" -> assets.filter { it.tags.contains("palette") }
                "Composition" -> assets.filter { it.tags.contains("composition") }
                "Typography" -> assets.filter { it.tags.contains("typography") }
                "Pattern" -> assets.filter { it.tags.contains("pattern") }
                else -> assets.filter { it.tags.contains("other") }
            }
        )
    }

    //------------------------------- Side Effects
    LaunchedEffect(sortBy) {
        //TODO: Maybe needed
    }

    //------------------------------- UI
    Column(Modifier.fillMaxWidth()) {
        //Top Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(Res.string.inspirations),
                style = MaterialTheme.typography.headlineMedium
            )
            SortDropdown { sortBy = it } //TODO: Handle empty data when filtered
        }
        Spacer(Modifier.height(8.dp))

        //Inspirations (Assets)
        if (loading) {
            //TODO: Handle Loading
        } else if (errorCaught) {
            //TODO: Handle Error
        } else if (assets.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth()
                    .height(256.dp)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        shape = MaterialTheme.shapes.large
                    ).clickable { onAssetAddClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Add, null)
            }
        } else {
            LazyRow {
                items(sortedAssets) {
                    InspirationItem(it, onAssetClick)
                    Spacer(modifier = Modifier.width(4.dp))
                }
                item {
                    Column(Modifier.width(96.dp)) {
                        Box(
                            modifier = Modifier.width(96.dp).height(144.dp)
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.tertiaryContainer,
                                    shape = MaterialTheme.shapes.large
                                ).clickable { onAssetAddClick() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Add, null)
                        }
                        Text(
                            modifier = Modifier.padding(top = 4.dp).fillMaxWidth(),
                            text = stringResource(Res.string.add_asset),
                            textAlign = TextAlign.Center,//TODO: Add textAlign for Asset name
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InspirationItem(
    asset: Asset,
    onAssetClick: (Asset) -> Unit
) {
    Column(Modifier.width(96.dp)) {
        Box(
            modifier = Modifier.fillMaxWidth().height(144.dp).background(
                color = MaterialTheme.colorScheme.tertiaryContainer,
                shape = MaterialTheme.shapes.large
            ).clickable { onAssetClick(asset) },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier.align(Alignment.Center),
                imageVector = when {
                    asset.tags.contains("palette") -> Icons.Default.Colorize
                    asset.tags.contains("composition") -> Icons.Default.Casino
                    asset.tags.contains("typography") -> Icons.Default.TextFields
                    asset.tags.contains("pattern") -> Icons.Default.Pattern
                    else -> Icons.Default.PlusOne
                },
                contentDescription = null
            )
        }
        Text(
            modifier = Modifier.padding(top = 4.dp).fillMaxWidth(),
            text = asset.name,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
fun Folders(
    loading: Boolean = true,
    errorCaught: Boolean = false,
    folders: List<Folder>,
    onFolderClick: (Folder) -> Unit,
    onFolderAddClick: (String) -> Unit
) {
    //------------------------------- UI
    Column(Modifier.fillMaxWidth()) {
        //Top Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(Res.string.folders),
                style = MaterialTheme.typography.headlineMedium
            )
            IconButton(onClick = { onFolderAddClick("root") }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
        }

        if (loading) {
            //TODO: Handle Loading
        } else if (errorCaught || folders.isEmpty()) {
            //TODO: Handle Error
        } else {
            folders.forEach {
                FolderItem(it, onFolderClick, onFolderAddClick)
                Spacer(modifier = Modifier.width(4.dp))
            }
        }
    }
}

@Composable
fun FolderItem(
    folder: Folder,
    onFolderClick: (Folder) -> Unit,
    onFolderAddClick: (String) -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .clickable { onFolderClick(folder) }
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.padding(end = 8.dp),
                tint = MaterialTheme.colorScheme.tertiaryContainer,
                imageVector = Icons.Default.Folder,
                contentDescription = null
            )
            Text(
                modifier = Modifier
                    .padding(4.dp)
                    .weight(1f),
                text = folder.name,
                style = MaterialTheme.typography.titleMedium
            )
            IconButton(onClick = { onFolderAddClick(folder.folderId!!) }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
        }
        folder.children!!.forEach {
            Column(Modifier.padding(start = 24.dp)) {
                FolderItem(it, onFolderClick, onFolderAddClick)
                Spacer(modifier = Modifier.width(4.dp))
            }
        }
    }
}