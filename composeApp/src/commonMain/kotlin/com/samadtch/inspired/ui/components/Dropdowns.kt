package com.samadtch.inspired.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.samadtch.inspired.domain.models.Folder
import inspired.composeapp.generated.resources.Res
import inspired.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun Dropdown(
    modifier: Modifier,
    hint: String,
    options: List<String>,
    onItemPicked: (Int) -> Unit,
    isError: Boolean = false,
    enabled: Boolean = true
) {
    //------------------------------- Declarations
    val interactionSource = remember { MutableInteractionSource() }
    var value by remember { mutableIntStateOf(-1) }
    var expanded by remember { mutableStateOf(false) }

    //------------------------------- UI
    Column(modifier) {
        Row(
            modifier = modifier
                .padding(top = 4.dp)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) { if (enabled) expanded = true }
                .border(
                    BorderStroke(
                        width = 1.dp,
                        color = if (isError) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.secondary
                    ), CircleShape
                )
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
                    .align(Alignment.CenterVertically),
                text = if (value == -1) hint else options[value],
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            )
            Icon(
                modifier = Modifier.align(Alignment.CenterVertically),
                imageVector = Icons.Default.ExpandMore,
                tint = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                contentDescription = null
            )
        }
        DropdownMenu(
            modifier = modifier,
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEachIndexed { index, s ->
                DropdownMenuItem(
                    text = {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 4.dp, end = 16.dp),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.primary
                            ),
                            text = s
                        )
                    },
                    onClick = {
                        value = index
                        onItemPicked(index)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun FilterDropdown(
    modifier: Modifier = Modifier,
    onFilterPicked: (String) -> Unit
) {
    //------------------------------- Declarations
    var filterBy by remember { mutableStateOf("All") }
    var expanded by remember { mutableStateOf(false) }

    val types = stringArrayResource(Res.array.types)

    //------------------------------- UI
    Column(modifier) {
        Row(
            modifier = Modifier
                .clickable(onClick = { expanded = true })
                .border(BorderStroke(1.dp, MaterialTheme.colorScheme.secondary), CircleShape)
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .align(Alignment.CenterVertically),
                text = stringResource(Res.string.filter_by_val, filterBy),
                style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.secondary)
            )
            Icon(
                modifier = Modifier.align(Alignment.CenterVertically),
                imageVector = Icons.Default.ExpandMore,
                tint = MaterialTheme.colorScheme.secondary,
                contentDescription = "Dropdown"
            )
        }
        DropdownMenu(
            modifier = Modifier.width(164.dp),
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            //Filter By
            Text(
                modifier = Modifier.padding(top = 16.dp, start = 24.dp),
                text = stringResource(Res.string.filter_by),
                style = MaterialTheme.typography.labelMedium.copy(
                    color = MaterialTheme.colorScheme.secondary
                )
            )
            types.forEach {
                DropdownMenuItem(
                    text = {
                        Row(Modifier.fillMaxWidth()) {
                            Checkbox(
                                checked = filterBy == it,
                                colors = CheckboxDefaults.colors(
                                    uncheckedColor = MaterialTheme.colorScheme.secondary,
                                    checkedColor = MaterialTheme.colorScheme.tertiary,
                                    checkmarkColor = MaterialTheme.colorScheme.onTertiary,
                                ),
                                onCheckedChange = {
                                    filterBy = "All"
                                    onFilterPicked("All")
                                    expanded = false
                                },
                            )
                            Text(
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .weight(1f)
                                    .padding(end = 16.dp),
                                style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.secondary),
                                text = it
                            )
                        }
                    },
                    onClick = {
                        filterBy = it
                        onFilterPicked(it)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun FoldersDropdown(
    modifier: Modifier,
    hint: String,
    folders: List<Folder>,
    onFolderPicked: (String) -> Unit,
    isError: Boolean = false,
    enabled: Boolean = true
) {
    //------------------------------- Declarations
    val interactionSource = remember { MutableInteractionSource() }
    var pickedFolder by remember { mutableStateOf<String?>(null) }
    var expanded by remember { mutableStateOf(false) }

    //------------------------------- UI
    Column(modifier) {
        Row(
            modifier = modifier
                .padding(top = 4.dp)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) { if(enabled) expanded = true }
                .border(
                    border = BorderStroke(
                        width = 1.dp,
                        color = if (isError) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.secondary
                    ),
                    shape = CircleShape
                ).padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
                    .align(Alignment.CenterVertically),
                text = if (pickedFolder == null) hint else pickedFolder!!,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            )
            Icon(
                modifier = Modifier.align(Alignment.CenterVertically),
                imageVector = Icons.Default.ExpandMore,
                tint = if (isError) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.primary,
                contentDescription = "Dropdown"
            )
        }
        DropdownMenu(
            modifier = modifier,
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            folders.forEach {
                FolderItemPicker(folder = it) { id, name ->
                    pickedFolder = name
                    onFolderPicked(id)
                    expanded = false
                }
            }
        }
    }
}

@Composable
fun FolderItemPicker(
    folder: Folder,
    onFolderPick: (String, String) -> Unit
) {
    Column {
        DropdownMenuItem(
            text = {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 4.dp, end = 16.dp),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.secondary
                    ),
                    text = folder.name
                )
            },
            onClick = { onFolderPick(folder.folderId!!, folder.name) }
        )
        Column(Modifier.padding(start = 16.dp)) {
            folder.children!!.forEach {
                FolderItemPicker(it, onFolderPick)
                Spacer(modifier = Modifier.width(4.dp))
            }
        }
    }
}