package com.samadtch.inspired.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Colorize
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Pattern
import androidx.compose.material.icons.filled.PlusOne
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SuggestionChip
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
import com.samadtch.inspired.common.LOADING_STATE
import com.samadtch.inspired.common.SUCCESS_STATE
import com.samadtch.inspired.common.exceptions.DataException.Companion.API_ERROR_FILE_TOO_BIG
import com.samadtch.inspired.common.exceptions.DataException.Companion.API_ERROR_IMPORT_FAILED
import com.samadtch.inspired.domain.models.Asset
import com.samadtch.inspired.domain.models.AssetFile
import com.samadtch.inspired.domain.models.Folder
import inspired.composeapp.generated.resources.Res
import inspired.composeapp.generated.resources.*
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeComponents
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource

/* **************************************************************************
 * ************************************* Folders
 */
@Composable
fun FolderDialog(
    folder: Folder,
    onFolderUpdate: (Folder) -> Unit,
    onFolderDelete: (String) -> Unit,
    folderDeleteState: Int?,
    onDismiss: () -> Unit
) {
    //------------------------------- Side Effect
    LaunchedEffect(folderDeleteState) {
        if (folderDeleteState == SUCCESS_STATE) onDismiss()
    }

    //------------------------------- UI
    AlertDialog(
        onDismissRequest = { onDismiss() },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    tint = MaterialTheme.colorScheme.secondaryContainer,
                    imageVector = Icons.Default.Folder,
                    contentDescription = null
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = folder.name,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontFamily = FontFamily(Font(Res.font.font_medium)),
                    )
                )
                Text(
                    text = folder.createdAt!!.format((DateTimeComponents.Formats.RFC_1123)),
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontFamily = FontFamily(Font(Res.font.font_medium)),
                    )
                )
            }
        },
        confirmButton = {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                FilledTonalButton(onClick = { onFolderDelete(folder.folderId!!) }) {
                    //Loading State
                    if (folderDeleteState == LOADING_STATE) CircularProgressIndicator(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .size(28.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                    Text(
                        text = stringResource(Res.string.delete), //TODO: Add Confirmation (double click)
                        style = MaterialTheme.typography.labelLarge
                    )
                }
                Spacer(Modifier.width(8.dp))
                FilledTonalButton(onClick = { onFolderUpdate(folder) }) {
                    Text(
                        text = stringResource(Res.string.update),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        },
    )
}

@Composable
fun FolderEditorDialog(
    folder: Folder? = null,
    parentId: String? = null,
    onFolderUpdate: (Folder, String?) -> Unit,
    folderSaveState: Int?,
    onDismiss: () -> Unit
) {
    //------------------------------- Declarations
    var name by remember { mutableStateOf(folder?.name ?: "") }
    var error by remember { mutableStateOf<StringResource?>(null) }

    //------------------------------- Side Effect
    LaunchedEffect(folderSaveState) {
        if (folderSaveState == SUCCESS_STATE) {
            name = "" //Reset Input
            onDismiss()
        }
    }

    //------------------------------- UI
    AlertDialog(
        onDismissRequest = {
            name = "" //Reset Input
            onDismiss()
        },
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (folder == null) stringResource(Res.string.add_folder)
                    else stringResource(Res.string.update_folder),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontFamily = FontFamily(Font(Res.font.font_medium)),
                    )
                )
                IconButton(onClick = { onDismiss() }) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = null)
                }
            }
        },
        text = {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                enabled = folderSaveState != LOADING_STATE,
                shape = MaterialTheme.shapes.extraLarge,
                textStyle = MaterialTheme.typography.labelMedium,
                placeholder = {
                    Text(
                        text = stringResource(Res.string.folder_name_placeholder),
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                isError = error != null,
                supportingText = {
                    if (error != null) Text(
                        text = stringResource(error!!),
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                value = name,
                onValueChange = { name = it }
            )
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                FilledTonalButton(
                    onClick = {
                        error = null
                        //Save
                        if (name == "") error = Res.string.error_name_required
                        else if (folder != null && name == folder.name) error =
                            Res.string.error_name_similar
                        else {
                            if (folder == null) onFolderUpdate(Folder(name = name), parentId!!)
                            else onFolderUpdate(folder.copy(name = name), null)
                        }
                    },
                ) {
                    //Loading State
                    if (folderSaveState == LOADING_STATE) CircularProgressIndicator(
                        modifier = Modifier
                            .padding(0.dp, 0.dp, 16.dp, 0.dp)
                            .size(28.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                    Text(
                        text = if (folder == null) stringResource(Res.string.add)
                        else stringResource(Res.string.update),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    )
}

/* **************************************************************************
 * ************************************* Assets
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AssetDialog(
    asset: Asset,
    onAssetDelete: (String) -> Unit,
    assetDeleteState: Int?,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    tint = MaterialTheme.colorScheme.secondaryContainer,
                    imageVector = Icons.Default.Inventory2,
                    contentDescription = null
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = asset.name,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontFamily = FontFamily(Font(Res.font.font_medium)),
                    )
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = asset.createdAt!!.format((DateTimeComponents.Formats.RFC_1123)),
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontFamily = FontFamily(Font(Res.font.font_medium)),
                    )
                )
                Spacer(Modifier.height(4.dp))
                FlowRow {
                    asset.tags.forEach {
                        SuggestionChip(
                            onClick = {},//Do Nothing
                            label = {
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        )
                        Spacer(Modifier.width(4.dp))
                    }
                }
            }
        },
        confirmButton = {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                FilledTonalButton(onClick = { onAssetDelete(asset.assetId!!) }) {
                    //Loading State
                    if (assetDeleteState == LOADING_STATE) CircularProgressIndicator(
                        modifier = Modifier
                            .padding(0.dp, 0.dp, 16.dp, 0.dp)
                            .size(28.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                    Text(
                        text = stringResource(Res.string.delete),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        },
    )
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AssetEditorDialog(
    assetFile: AssetFile? = null,
    folders: List<Folder>,
    onFilePickClick: () -> Unit,
    onAssetAdd: (Asset) -> Unit,
    assetCreatedState: Int?,
    onDismiss: () -> Unit
) {
    //------------------------------- Declarations
    //Name
    var name by remember { mutableStateOf("") }
    var errorName by remember { mutableStateOf<StringResource?>(null) }

    //Tags
    var tag by remember { mutableStateOf("") }
    val tags = remember { mutableStateListOf<String>() }
    var errorTag by remember { mutableStateOf<StringResource?>(null) }

    //Types
    val types = stringArrayResource(Res.array.types).filter { it != "All" && it != "Other" }
    var type by remember { mutableStateOf<String?>(null) }

    //Other
    var folderId by remember { mutableStateOf("root") }
    var fileError by remember { mutableStateOf<StringResource?>(null) }

    //------------------------------- Side Effects
    LaunchedEffect(assetCreatedState) {
        //Reset State
        if (assetCreatedState == SUCCESS_STATE) {
            //Dismiss
            onDismiss()

            //Reset
            name = ""
            folderId = "root"
            type = null
            tag = ""; tags.clear()
        } else if (assetCreatedState == API_ERROR_FILE_TOO_BIG) fileError = Res.string.file_too_big_error
        else if (assetCreatedState == API_ERROR_IMPORT_FAILED) fileError = Res.string.import_failed_error
    }

    //------------------------------- UI
    AlertDialog(
        onDismissRequest = {
            onDismiss()
            //Reset
            name = ""
            tag = ""; tags.clear()
        },
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(Res.string.add_asset),
                    style = MaterialTheme.typography.headlineSmall
                )
                IconButton(onClick = { onDismiss() }) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = null)
                }
            }
        },
        text = {
            Column(Modifier.fillMaxWidth()) {
                //File Upload Button
                Box(
                    modifier = Modifier.fillMaxWidth().height(144.dp)
                        .padding(bottom = 4.dp)
                        .clickable { onFilePickClick() }
                        .background(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = MaterialTheme.shapes.large
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        imageVector = Icons.Default.UploadFile,
                        contentDescription = null
                    )
                }
                Text(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    text = assetFile?.fileName ?: if (fileError != null) stringResource(fileError!!)
                    else stringResource(Res.string.pick_image),
                    textAlign = TextAlign.Center,
                    color = if (fileError != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary
                )

                //Asset Name
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    enabled = assetCreatedState != LOADING_STATE,
                    shape = MaterialTheme.shapes.extraLarge,
                    textStyle = MaterialTheme.typography.labelLarge,
                    placeholder = {
                        Text(
                            text = stringResource(Res.string.asset_name_placeholder),
                            style = MaterialTheme.typography.labelLarge
                        )
                    },
                    isError = errorName != null,
                    supportingText = {
                        if (errorName != null) Text(
                            text = stringResource(errorName!!),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    value = name,
                    onValueChange = { name = it }
                )

                //Pick Type
                Dropdown(
                    modifier = Modifier,
                    hint = stringResource(Res.string.pick_type),
                    options = types,
                    onItemPicked = { type = types[it] },
                    enabled = assetCreatedState != LOADING_STATE
                )
                Spacer(Modifier.height(16.dp))

                //Pick Folder
                FoldersDropdown(
                    modifier = Modifier,
                    hint = stringResource(Res.string.pick_folder),
                    folders = folders,
                    onFolderPicked = { folderId = it },
                    enabled = assetCreatedState != LOADING_STATE
                )
                Spacer(Modifier.height(16.dp))

                //Tags
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                            .wrapContentHeight(),
                        enabled = assetCreatedState != LOADING_STATE,
                        shape = MaterialTheme.shapes.extraLarge,
                        textStyle = MaterialTheme.typography.labelLarge,
                        placeholder = {
                            Text(
                                text = stringResource(Res.string.tag_placeholder),
                                style = MaterialTheme.typography.labelLarge
                            )
                        },
                        isError = errorTag != null,
                        value = tag,
                        onValueChange = { tag = it }
                    )
                    OutlinedIconButton(
                        onClick = {
                            if (tag == "") errorTag = Res.string.error_tag_required
                            else {
                                tags.add(tag)
                                tag = ""
                            }
                        }
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null)
                    }
                    if (errorTag != null) Text(
                        text = stringResource(errorTag!!),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                FlowRow {
                    tags.filter { it != "inspiration" && it != type?.lowercase() }.forEach {
                        InputChip(
                            selected = false,
                            onClick = { tags.remove(it) },
                            label = { Text(it) }
                        )
                        Spacer(Modifier.width(4.dp))
                    }
                }
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                FilledTonalButton(
                    onClick = {
                        errorName = null
                        fileError = null

                        //Save
                        if (name == "") errorName = Res.string.error_name_required
                        else if (assetFile == null) fileError = Res.string.error_file_required
                        else onAssetAdd(
                            Asset(
                                name = name,
                                tags = tags.apply {
                                    add("inspiration")
                                    if (type == null) add("other") else add(type!!.lowercase())
                                }.toList(),
                                folderId = folderId,
                                assetFile = assetFile
                            )
                        )
                    },
                ) {
                    //Loading State
                    if (assetCreatedState == LOADING_STATE) CircularProgressIndicator(
                        modifier = Modifier
                            .padding(0.dp, 0.dp, 16.dp, 0.dp)
                            .size(28.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                    Text(
                        text = stringResource(Res.string.add),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    )
}