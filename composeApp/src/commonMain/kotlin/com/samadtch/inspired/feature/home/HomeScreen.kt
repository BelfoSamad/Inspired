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
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
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
import org.jetbrains.compose.resources.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.samadtch.inspired.common.exceptions.AuthException.Companion.AUTH_TOKEN_MISSING
import com.samadtch.inspired.common.exceptions.DataException.Companion.API_ERROR_NETWORK
import com.samadtch.inspired.common.exceptions.DataException.Companion.API_ERROR_RATE_LIMIT
import com.samadtch.inspired.domain.models.Asset
import com.samadtch.inspired.domain.models.AssetFile
import com.samadtch.inspired.domain.models.Folder
import com.samadtch.inspired.ui.components.AssetDialog
import com.samadtch.inspired.ui.components.AssetEditorDialog
import com.samadtch.inspired.ui.components.FolderDialog
import com.samadtch.inspired.ui.components.FolderEditorDialog
import com.samadtch.inspired.ui.components.SortDropdown
import com.samadtch.inspired.ui.components.shimmerEffect
import inspired.composeapp.generated.resources.Res
import inspired.composeapp.generated.resources.*
import kotlinx.coroutines.flow.StateFlow
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
    onFilePick: () -> Unit,
    assetFile: StateFlow<AssetFile?>
) {
    //------------------------------- Declarations
    val homeUiState by viewModel.homeUiState.collectAsStateWithLifecycle()
    val deleteFolderState by viewModel.deleteFolderState.collectAsStateWithLifecycle()
    val saveFolderState by viewModel.saveFolderState.collectAsStateWithLifecycle()
    val createAssetState by viewModel.createAssetState.collectAsStateWithLifecycle()
    val deleteAssetState by viewModel.deleteAssetState.collectAsStateWithLifecycle()

    val file by assetFile.collectAsStateWithLifecycle() //TODO: Fix
    //------------------------------- Side Effects
    //TODO: Handle Action Error States
    //TODO: Handle Returns! [Created Folder, Updated Folder Name, Created Asset]

    //------------------------------- Dialogs
    //Folder Editor
    var folderAddDialog by remember { mutableStateOf<String?>(null) }
    var folderUpdateDialog by remember { mutableStateOf<Folder?>(null) }
    if (folderAddDialog != null || folderUpdateDialog != null) FolderEditorDialog(
        folder = folderUpdateDialog,
        parentId = folderAddDialog,
        onFolderUpdate = viewModel::saveFolder,
        folderSaveState = saveFolderState,
        onDismiss = {
            folderAddDialog = null
            folderUpdateDialog = null
        }
    )

    //Folder Dialog
    var folderDialog by remember { mutableStateOf<Folder?>(null) }
    if (folderDialog != null) FolderDialog(
        folder = folderDialog!!,
        onFolderUpdate = {
            folderDialog = null
            folderUpdateDialog = it
        },
        onFolderDelete = viewModel::deleteFolder,
        folderDeleteState = deleteFolderState,
        onDismiss = { folderDialog = null }
    )

    //Asset Dialog
    var assetDialog by remember { mutableStateOf<Asset?>(null) }
    if (assetDialog != null) AssetDialog(
        asset = assetDialog!!,
        onAssetDelete = viewModel::deleteAsset,
        assetDeleteState = deleteAssetState,
        onDismiss = { assetDialog = null }
    )

    //Asset Editor Dialog
    var showAddAssetDialog by remember { mutableStateOf(false) }
    if (showAddAssetDialog || file != null) AssetEditorDialog(
        assetFile = file,
        folders = (homeUiState as? HomeViewModel.HomeUiState.Success)?.folders ?: listOf(),
        onFilePickClick = onFilePick,
        onAssetAdd = viewModel::createAsset,
        assetCreatedState = createAssetState,
        onDismiss = { showAddAssetDialog = false }
    )

    //------------------------------- UI
    HomeScreen(
        modifier = modifier,
        onShowSnackbar = onShowSnackbar,
        homeUiState = homeUiState,
        onLogout = onLogout,
        onDrawerMenuClick = onDrawerMenuClick,
        onAssetClick = { assetDialog = it },
        onAssetAddClick = { showAddAssetDialog = true },
        onFolderClick = { folderDialog = it },
        onFolderAddClick = { folderAddDialog = it }
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
        modifier = modifier.verticalScroll(rememberScrollState())
            .padding(start = 16.dp, end = 16.dp, top = 32.dp, bottom = 24.dp)
    ) {
        //Top Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(Res.string.app_name),
                style = MaterialTheme.typography.displayMedium.copy(
                    fontFamily = FontFamily(Font(Res.font.display_bold))
                )
            )
            OutlinedIconButton(onClick = { onDrawerMenuClick() }) {
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
            modifier = Modifier.fillMaxWidth(),
            loading = isLoading,
            errorCaught = errorCaught,
            assets = assets,
            onAssetClick = onAssetClick,
            onAssetAddClick = onAssetAddClick
        )
        Spacer(Modifier.height(16.dp))

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
    modifier: Modifier,
    loading: Boolean = true,
    errorCaught: Boolean = false,
    assets: List<Asset>,
    onAssetClick: (Asset) -> Unit,
    onAssetAddClick: () -> Unit,
) {
    //------------------------------- Declarations
    var sortBy by remember { mutableStateOf("All") }
    val sortedAssets = remember { mutableListOf<Asset>() }

    //------------------------------- Side Effects
    //TODO: Properly handle Assets Sorting
    //Init Sorted Assets
    LaunchedEffect(assets) {
        sortedAssets.addAll(
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

    LaunchedEffect(sortBy) {
        //Update Sorted List
        sortedAssets.clear()
        sortedAssets.addAll(
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

    //------------------------------- UI
    Column(modifier) {
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
        Spacer(Modifier.height(16.dp))

        //Inspirations (Assets)
        if (loading) {
            InspirationLoader()
        } else if (errorCaught) {
            //TODO: Handle Error
        } else if (sortedAssets.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().height(144.dp).border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = MaterialTheme.shapes.large
                ).clickable { onAssetAddClick() }, contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.size(48.dp),
                    imageVector = Icons.Default.Add,
                    tint = MaterialTheme.colorScheme.secondaryContainer,
                    contentDescription = null
                )
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
                            modifier = Modifier.width(96.dp).height(144.dp).border(
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
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InspirationLoader() {
    Row(Modifier.fillMaxWidth()) {
        repeat(3) {
            Box(
                Modifier.weight(1f).height(144.dp).background(
                    shape = MaterialTheme.shapes.large, color = MaterialTheme.colorScheme.surface
                ).shimmerEffect()
            )
            Spacer(Modifier.width(8.dp))
        }
    }
}

@Composable
fun InspirationItem(
    asset: Asset, onAssetClick: (Asset) -> Unit
) {
    Column(Modifier.width(96.dp)) {
        Box(
            modifier = Modifier.fillMaxWidth().height(144.dp).background(
                color = MaterialTheme.colorScheme.tertiaryContainer,
                shape = MaterialTheme.shapes.large
            ).clickable { onAssetClick(asset) }, contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier.align(Alignment.Center), imageVector = when {
                    asset.tags.contains("palette") -> Icons.Default.Colorize
                    asset.tags.contains("composition") -> Icons.Default.Casino
                    asset.tags.contains("typography") -> Icons.Default.TextFields
                    asset.tags.contains("pattern") -> Icons.Default.Pattern
                    else -> Icons.Default.PlusOne
                }, contentDescription = null
            )
        }
        Text(
            modifier = Modifier.padding(top = 4.dp).fillMaxWidth(),
            text = asset.name,
            textAlign = TextAlign.Center,
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
            FolderLoader()
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
fun FolderLoader() {
    repeat(5) {
        Box(Modifier.fillMaxWidth().height(48.dp).padding(bottom = 8.dp).shimmerEffect())
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
            modifier = Modifier.clickable { onFolderClick(folder) }.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.padding(end = 8.dp),
                tint = MaterialTheme.colorScheme.secondaryContainer,
                imageVector = Icons.Default.Folder,
                contentDescription = null
            )
            Text(
                modifier = Modifier.padding(4.dp).weight(1f),
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