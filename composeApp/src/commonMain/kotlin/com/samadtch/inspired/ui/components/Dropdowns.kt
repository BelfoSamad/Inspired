package com.samadtch.inspired.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import inspired.composeapp.generated.resources.Res
import inspired.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun SortDropdown(
    modifier: Modifier = Modifier,
    onSortPicked: (String) -> Unit
) {
    //------------------------------- Declarations
    var sortBy by remember { mutableStateOf("All") }
    var expanded by remember { mutableStateOf(false) }

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
                text = stringResource(Res.string.sort_by_val, sortBy),
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
            //Sort By
            Text(
                modifier = Modifier.padding(top = 16.dp, start = 24.dp),
                text = stringResource(Res.string.sort_by),
                style = MaterialTheme.typography.labelMedium.copy(
                    color = MaterialTheme.colorScheme.secondary
                )
            )
            DropdownMenuItem(
                text = {
                    Row(Modifier.fillMaxWidth()) {
                        Checkbox(
                            checked = sortBy == stringResource(Res.string.sort_all),
                            colors = CheckboxDefaults.colors(
                                uncheckedColor = MaterialTheme.colorScheme.secondary,
                                checkedColor = MaterialTheme.colorScheme.tertiary,
                                checkmarkColor = MaterialTheme.colorScheme.primary,
                            ),
                            onCheckedChange = {
                                sortBy = "All"
                                onSortPicked("All")
                                expanded = false
                            },
                        )
                        Text(
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .weight(1f)
                                .padding(end = 16.dp),
                            style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.secondary),
                            text = stringResource(Res.string.sort_all)
                        )
                    }
                },
                onClick = {
                    sortBy = "All"
                    onSortPicked("All")
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = {
                    Row(Modifier.fillMaxWidth()) {
                        Checkbox(
                            checked = sortBy == stringResource(Res.string.sort_palette),
                            colors = CheckboxDefaults.colors(
                                uncheckedColor = MaterialTheme.colorScheme.secondary,
                                checkedColor = MaterialTheme.colorScheme.tertiary,
                                checkmarkColor = MaterialTheme.colorScheme.primary,
                            ),
                            onCheckedChange = {
                                sortBy = "Palette"
                                onSortPicked("Palette")
                                expanded = false
                            },
                        )
                        Text(
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .weight(1f)
                                .padding(end = 16.dp),
                            style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.secondary),
                            text = stringResource(Res.string.sort_palette)
                        )
                    }
                },
                onClick = {
                    sortBy = "Palette"
                    onSortPicked("Palette")
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = {
                    Row(Modifier.fillMaxWidth()) {
                        Checkbox(
                            checked = sortBy == stringResource(Res.string.sort_composition),
                            colors = CheckboxDefaults.colors(
                                uncheckedColor = MaterialTheme.colorScheme.secondary,
                                checkedColor = MaterialTheme.colorScheme.tertiary,
                                checkmarkColor = MaterialTheme.colorScheme.primary,
                            ),
                            onCheckedChange = {
                                sortBy = "Composition"
                                onSortPicked("Composition")
                                expanded = false
                            },
                        )
                        Text(
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .weight(1f)
                                .padding(end = 16.dp),
                            style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.secondary),
                            text = stringResource(Res.string.sort_composition)
                        )
                    }
                },
                onClick = {
                    sortBy = "Composition"
                    onSortPicked("Composition")
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = {
                    Row(Modifier.fillMaxWidth()) {
                        Checkbox(
                            checked = sortBy == stringResource(Res.string.sort_typography),
                            colors = CheckboxDefaults.colors(
                                uncheckedColor = MaterialTheme.colorScheme.secondary,
                                checkedColor = MaterialTheme.colorScheme.tertiary,
                                checkmarkColor = MaterialTheme.colorScheme.primary,
                            ),
                            onCheckedChange = {
                                sortBy = "Typography"
                                onSortPicked("Typography")
                                expanded = false
                            },
                        )
                        Text(
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .weight(1f)
                                .padding(end = 16.dp),
                            style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.secondary),
                            text = stringResource(Res.string.sort_typography)
                        )
                    }
                },
                onClick = {
                    sortBy = "Typography"
                    onSortPicked("Typography")
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = {
                    Row(Modifier.fillMaxWidth()) {
                        Checkbox(
                            checked = sortBy == stringResource(Res.string.sort_pattern),
                            colors = CheckboxDefaults.colors(
                                uncheckedColor = MaterialTheme.colorScheme.secondary,
                                checkedColor = MaterialTheme.colorScheme.tertiary,
                                checkmarkColor = MaterialTheme.colorScheme.primary,
                            ),
                            onCheckedChange = {
                                sortBy = "Pattern"
                                onSortPicked("Pattern")
                                expanded = false
                            },
                        )
                        Text(
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .weight(1f)
                                .padding(end = 16.dp),
                            style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.secondary),
                            text = stringResource(Res.string.sort_pattern)
                        )
                    }
                },
                onClick = {
                    sortBy = "Pattern"
                    onSortPicked("Pattern")
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = {
                    Row(Modifier.fillMaxWidth()) {
                        Checkbox(
                            checked = sortBy == stringResource(Res.string.sort_other),
                            colors = CheckboxDefaults.colors(
                                uncheckedColor = MaterialTheme.colorScheme.secondary,
                                checkedColor = MaterialTheme.colorScheme.tertiary,
                                checkmarkColor = MaterialTheme.colorScheme.primary,
                            ),
                            onCheckedChange = {
                                sortBy = "Other"
                                onSortPicked("Other")
                                expanded = false
                            },
                        )
                        Text(
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .weight(1f)
                                .padding(end = 16.dp),
                            style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.secondary),
                            text = stringResource(Res.string.sort_other)
                        )
                    }
                },
                onClick = {
                    sortBy = "Other"
                    onSortPicked("Other")
                    expanded = false
                }
            )
        }
    }
}