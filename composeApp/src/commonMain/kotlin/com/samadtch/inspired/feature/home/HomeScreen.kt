package com.samadtch.inspired.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.Card
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.samadtch.inspired.common.LOADING_STATE
import com.samadtch.inspired.common.SUCCESS_STATE
import com.samadtch.inspired.common.exceptions.AuthException.Companion.AUTH_TOKEN_SERVER_ERROR_OTHER
import com.samadtch.inspired.common.exceptions.DataException.Companion.API_ERROR_AUTH
import com.samadtch.inspired.common.exceptions.DataException.Companion.API_ERROR_NETWORK
import com.samadtch.inspired.common.exceptions.DataException.Companion.API_ERROR_NOT_FOUND
import com.samadtch.inspired.common.exceptions.DataException.Companion.API_ERROR_RATE_LIMIT
import com.samadtch.inspired.common.exceptions.DataException.Companion.API_ERROR_REQUEST_OTHER
import com.samadtch.inspired.common.exceptions.DataException.Companion.API_ERROR_SERVER_OTHER
import com.samadtch.inspired.domain.models.Asset
import com.samadtch.inspired.domain.models.AssetFile
import com.samadtch.inspired.domain.models.Folder
import com.samadtch.inspired.ui.components.AssetDialog
import com.samadtch.inspired.ui.components.AssetEditorDialog
import com.samadtch.inspired.ui.components.ErrorMessage
import com.samadtch.inspired.ui.components.FilterDropdown
import com.samadtch.inspired.ui.components.FolderDialog
import com.samadtch.inspired.ui.components.FolderEditorDialog
import com.samadtch.inspired.ui.components.shimmerEffect
import inspired.composeapp.generated.resources.Res
import inspired.composeapp.generated.resources.add_asset
import inspired.composeapp.generated.resources.app_name
import inspired.composeapp.generated.resources.data_error
import inspired.composeapp.generated.resources.empty_error
import inspired.composeapp.generated.resources.error_auth
import inspired.composeapp.generated.resources.error_auth_server
import inspired.composeapp.generated.resources.error_data_other
import inspired.composeapp.generated.resources.error_data_request
import inspired.composeapp.generated.resources.error_data_server
import inspired.composeapp.generated.resources.error_network
import inspired.composeapp.generated.resources.error_not_found
import inspired.composeapp.generated.resources.error_rate_limit
import inspired.composeapp.generated.resources.folders
import inspired.composeapp.generated.resources.font_bold
import inspired.composeapp.generated.resources.inspirations
import inspired.composeapp.generated.resources.logo_font
import inspired.composeapp.generated.resources.slogan
import org.jetbrains.compose.resources.Font
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
    assetFile: AssetFile?
) {
    //------------------------------- Declarations
    val homeUiState by viewModel.homeUiState.collectAsStateWithLifecycle()
    val deleteFolderState by viewModel.deleteFolderState.collectAsStateWithLifecycle()
    val saveFolderState by viewModel.saveFolderState.collectAsStateWithLifecycle()
    val createAssetState by viewModel.createAssetState.collectAsStateWithLifecycle()
    val deleteAssetState by viewModel.deleteAssetState.collectAsStateWithLifecycle()
    val actionStates = listOf(
        deleteFolderState?.first, saveFolderState?.first,
        createAssetState?.first, deleteAssetState?.first
    )

    //Data
    var receivedAssetFile by remember { mutableStateOf<AssetFile?>(null) }

    //Errors
    val errorAuth = stringResource(Res.string.error_auth)
    val errorAuthServer = stringResource(Res.string.error_auth_server)
    val errorNetwork = stringResource(Res.string.error_network)
    val errorRateLimit = stringResource(Res.string.error_rate_limit)
    val errorNotFound = stringResource(Res.string.error_not_found)
    val errorDataRequest = stringResource(Res.string.error_data_request)
    val errorDataServer = stringResource(Res.string.error_data_server)
    val errorDataOther = stringResource(Res.string.error_data_other)

    //------------------------------- Side Effects
    //catch picked Asset Files
    LaunchedEffect(assetFile) {
        receivedAssetFile = assetFile
    }

    //all actions have the same error codes returned, handle them all here
    LaunchedEffect(actionStates) {
        val actionCode = actionStates.filterNotNull().firstOrNull()
        if (actionCode !in listOf(null, LOADING_STATE, SUCCESS_STATE)) when (actionCode) {
            AUTH_TOKEN_SERVER_ERROR_OTHER -> onShowSnackbar(false, errorAuthServer, null)
            API_ERROR_AUTH -> {
                onShowSnackbar(false, errorAuth, null)
                onLogout()
            }

            API_ERROR_NETWORK -> onShowSnackbar(false, errorNetwork, null)
            API_ERROR_RATE_LIMIT -> onShowSnackbar(false, errorRateLimit, null)
            API_ERROR_NOT_FOUND -> onShowSnackbar(false, errorNotFound, null)
            API_ERROR_REQUEST_OTHER -> onShowSnackbar(false, errorDataRequest, null)
            API_ERROR_SERVER_OTHER -> onShowSnackbar(false, errorDataServer, null)
            else -> onShowSnackbar(false, errorDataOther, null)
        }
    }

    //------------------------------- Dialogs
    //Folder Editor
    var parentIdFolderDialog by remember { mutableStateOf<String?>(null) }
    var folderUpdateDialog by remember { mutableStateOf<Folder?>(null) }
    if (parentIdFolderDialog != null || folderUpdateDialog != null) FolderEditorDialog(
        folder = folderUpdateDialog,
        parentId = parentIdFolderDialog,
        onFolderUpdate = viewModel::saveFolder,
        folderSaveState = saveFolderState?.first,
        onDismiss = {
            parentIdFolderDialog = null
            folderUpdateDialog = null
            viewModel.resetStates()
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
        folderDeleteState = deleteFolderState?.first,
        onDismiss = {
            folderDialog = null
            viewModel.resetStates()
        }
    )

    //Asset Dialog
    var assetDialog by remember { mutableStateOf<Asset?>(null) }
    if (assetDialog != null) AssetDialog(
        asset = assetDialog!!,
        onAssetDelete = viewModel::deleteAsset,
        assetDeleteState = deleteAssetState?.first,
        onDismiss = {
            assetDialog = null
            viewModel.resetStates()
        }
    )

    //Asset Editor Dialog
    var showAddAssetDialog by remember { mutableStateOf(false) }
    if (showAddAssetDialog || receivedAssetFile != null) AssetEditorDialog(
        assetFile = receivedAssetFile,
        folders = (homeUiState as? HomeViewModel.HomeUiState.Success)?.folders ?: listOf(),
        onFilePickClick = onFilePick,
        onAssetAdd = viewModel::createAsset,
        assetCreatedState = createAssetState?.first,
        onDismiss = {
            receivedAssetFile = null
            showAddAssetDialog = false
            viewModel.resetStates()
        }
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
        onFolderAddClick = { parentIdFolderDialog = it },
        createAssetState = createAssetState,
        deleteAssetState = deleteAssetState,
        saveFolderState = saveFolderState,
        deleteFolderState = deleteFolderState
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
    createAssetState: Pair<Int?, Asset>?,
    deleteAssetState: Pair<Int?, String>?,
    saveFolderState: Pair<Int?, Folder>?,
    deleteFolderState: Pair<Int?, String>?
) {
    //------------------------------- Declarations
    var isLoading by remember { mutableStateOf(true) }
    var errorCaught by remember { mutableStateOf(false) }
    var assets by remember { mutableStateOf(listOf<Asset>()) }
    var folders by remember { mutableStateOf(listOf<Folder>()) }

    //Errors
    val networkError = stringResource(Res.string.error_network)
    val rateLimitError = stringResource(Res.string.error_rate_limit)
    val errorAuth = stringResource(Res.string.error_auth)
    val errorAuthServer = stringResource(Res.string.error_auth_server)
    val errorNotFound = stringResource(Res.string.error_not_found)
    val errorDataRequest = stringResource(Res.string.error_data_request)
    val errorDataServer = stringResource(Res.string.error_data_server)

    //------------------------------- Side Effects
    LaunchedEffect(homeUiState) {
        when (homeUiState) {
            HomeViewModel.HomeUiState.Loading -> isLoading = true
            is HomeViewModel.HomeUiState.Error -> {
                //Handle Errors
                when (homeUiState.type) {
                    "AUTH" -> {
                        when (homeUiState.code) {
                            AUTH_TOKEN_SERVER_ERROR_OTHER -> onShowSnackbar(
                                false,
                                errorAuthServer,
                                null
                            )

                            API_ERROR_AUTH -> {
                                onShowSnackbar(false, errorAuth, null)
                                onLogout()
                            }
                        }
                    }

                    "DATA" -> {
                        when (homeUiState.code) {
                            API_ERROR_NETWORK -> onShowSnackbar(false, networkError, null)
                            API_ERROR_RATE_LIMIT -> onShowSnackbar(false, rateLimitError, null)
                            API_ERROR_NOT_FOUND -> onShowSnackbar(false, errorNotFound, null)
                            API_ERROR_REQUEST_OTHER -> onShowSnackbar(false, errorDataRequest, null)
                            API_ERROR_SERVER_OTHER -> onShowSnackbar(false, errorDataServer, null)
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
            .padding(start = 16.dp, top = 24.dp, bottom = 24.dp)
    ) {
        //Top Section
        Row(
            modifier = Modifier.fillMaxWidth().padding(end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(Res.string.app_name),
                style = MaterialTheme.typography.displayMedium.copy(
                    fontFamily = FontFamily(Font(Res.font.logo_font)),
                    color = MaterialTheme.colorScheme.primary
                )
            )
            FilledTonalIconButton(onClick = { onDrawerMenuClick() }) {
                Icon(Icons.Default.Menu, null)
            }
        }
        Spacer(Modifier.height(24.dp))

        //Slogan
        Text(
            text = stringResource(Res.string.slogan),
            style = MaterialTheme.typography.displayMedium.copy(
                fontFamily = FontFamily(Font(Res.font.font_bold))
            )
        )
        Spacer(Modifier.height(24.dp))

        //Inspirations
        Inspirations(
            modifier = Modifier.fillMaxWidth(),
            loading = isLoading,
            errorCaught = errorCaught,
            assets = assets,
            onAssetClick = onAssetClick,
            onAssetAddClick = onAssetAddClick,
            createAssetState = createAssetState,
            deleteAssetState = deleteAssetState
        )
        Spacer(Modifier.height(16.dp))

        //Folders
        Folders(
            loading = isLoading,
            errorCaught = errorCaught,
            folders = folders,
            onFolderClick = onFolderClick,
            onFolderAddClick = onFolderAddClick,
            saveFolderState = saveFolderState,
            deleteFolderState = deleteFolderState
        )
    }
}

@Composable
fun Inspirations(
    modifier: Modifier,
    loading: Boolean = true,
    errorCaught: Boolean = true,
    assets: List<Asset>,
    onAssetClick: (Asset) -> Unit,
    onAssetAddClick: () -> Unit,
    createAssetState: Pair<Int?, Asset>?,
    deleteAssetState: Pair<Int?, String>?
) {
    //------------------------------- Declarations
    val receivedAssets = remember { mutableStateListOf<Asset>() }
    var filterBy by remember { mutableStateOf("All") }
    val filteredAssets = remember { mutableStateListOf<Asset>() }

    //------------------------------- Side Effects
    //Init Assets: put assets in mutable list for edits (adding, removing)
    LaunchedEffect(assets) {
        receivedAssets.addAll(assets)
    }

    //Handle Filtering
    LaunchedEffect(filterBy, receivedAssets.size) {
        //Update Filtered List
        filteredAssets.clear()
        filteredAssets.addAll(
            when (filterBy) {
                "All" -> receivedAssets
                "Palette" -> receivedAssets.filter { it.tags.contains("palette") }
                "Composition" -> receivedAssets.filter { it.tags.contains("composition") }
                "Typography" -> receivedAssets.filter { it.tags.contains("typography") }
                "Pattern" -> receivedAssets.filter { it.tags.contains("pattern") }
                else -> receivedAssets.filter { it.tags.contains("other") }
            }
        )
    }

    //Asset Actions
    LaunchedEffect(createAssetState) {
        if (createAssetState?.first == SUCCESS_STATE) {
            receivedAssets.add(createAssetState.second)
        }
    }
    LaunchedEffect(deleteAssetState) {
        if (deleteAssetState?.first == SUCCESS_STATE) {
            receivedAssets.removeAll { it.assetId == deleteAssetState.second }
        }
    }

    //------------------------------- UI
    Column(modifier) {
        //Top Section
        Row(
            modifier = Modifier.fillMaxWidth().padding(end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(Res.string.inspirations),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontFamily = FontFamily(Font(Res.font.font_bold))
                )
            )
            FilterDropdown { filterBy = it }
        }
        Spacer(Modifier.height(16.dp))

        //Inspirations (Assets)
        if (loading) InspirationLoader()
        else if (errorCaught) ErrorMessage(Res.string.data_error)
        else if (filteredAssets.isEmpty()) Box(
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
        else LazyRow {
            items(filteredAssets) {
                InspirationItem(it, onAssetClick)
                Spacer(modifier = Modifier.width(4.dp))
            }
            item {
                Column(Modifier.width(96.dp)) {
                    Box(
                        modifier = Modifier.width(96.dp).height(144.dp).border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.secondaryContainer,
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
    asset: Asset,
    onAssetClick: (Asset) -> Unit
) {
    Column(Modifier.width(96.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth().height(144.dp).clickable { onAssetClick(asset) },
        ) {
            Box(Modifier.fillMaxSize()) {
                Icon(
                    modifier = Modifier.align(Alignment.Center).size(32.dp),
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
    onFolderAddClick: (String) -> Unit,
    saveFolderState: Pair<Int?, Folder>?,
    deleteFolderState: Pair<Int?, String>?
) {
    //------------------------------- Declarations
    val receivedFolders = remember { mutableStateListOf<Folder>() }

    //------------------------------- LaunchedEffect
    LaunchedEffect(folders) {
        receivedFolders.clear()
        receivedFolders.addAll(folders)
    }
    LaunchedEffect(deleteFolderState) {
        if (deleteFolderState?.first == SUCCESS_STATE && receivedFolders.firstOrNull { it.folderId == deleteFolderState.second } != null)
            receivedFolders.removeAll { it.folderId == deleteFolderState.second }
    }
    LaunchedEffect(saveFolderState) {
        if (saveFolderState?.first == SUCCESS_STATE && saveFolderState.second.parentId == "root") {
            //if already exists, it is updated -> remove then re-add
            receivedFolders.removeAll { it.folderId == saveFolderState.second.folderId }
            receivedFolders.add(saveFolderState.second)
        }
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
                text = stringResource(Res.string.folders),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontFamily = FontFamily(Font(Res.font.font_bold))
                )
            )
            IconButton(onClick = { onFolderAddClick("root") }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
        }

        if (loading) FolderLoader()
        else if (errorCaught || receivedFolders.isEmpty()) ErrorMessage(
            if (folders.isEmpty()) Res.string.empty_error
            else Res.string.data_error
        )
        else receivedFolders.forEach {
            FolderItem(
                folder = it,
                onFolderClick = onFolderClick,
                onFolderAddClick = onFolderAddClick,
                saveFolderState = saveFolderState,
                deleteFolderState = deleteFolderState
            )
            Spacer(modifier = Modifier.width(4.dp))
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
    onFolderAddClick: (String) -> Unit,
    saveFolderState: Pair<Int?, Folder>?,
    deleteFolderState: Pair<Int?, String>?
) {
    //------------------------------- Declarations
    val childrenFolders = remember { mutableStateListOf<Folder>() }

    //------------------------------- LaunchedEffect
    LaunchedEffect(folder) {
        childrenFolders.clear()
        childrenFolders.addAll(folder.children ?: listOf())
    }
    LaunchedEffect(deleteFolderState) {
        if (deleteFolderState?.first == SUCCESS_STATE && childrenFolders.firstOrNull { it.folderId == deleteFolderState.second } != null)
            childrenFolders.removeAll { it.folderId == deleteFolderState.second }
    }
    LaunchedEffect(saveFolderState) {
        if (saveFolderState?.first == SUCCESS_STATE && saveFolderState.second.parentId == folder.folderId) {
            //if already exists, it is updated -> remove then re-add
            childrenFolders.removeAll { it.folderId == saveFolderState.second.folderId }
            childrenFolders.add(saveFolderState.second)
        }
    }

    //------------------------------- UI
    Column {
        Row(
            modifier = Modifier.clickable { onFolderClick(folder.copy(children = childrenFolders)) }
                .fillMaxWidth(),
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
        childrenFolders.forEach {
            Column(Modifier.padding(start = 24.dp)) {
                FolderItem(it, onFolderClick, onFolderAddClick, saveFolderState, deleteFolderState)
                Spacer(modifier = Modifier.width(4.dp))
            }
        }
    }
}