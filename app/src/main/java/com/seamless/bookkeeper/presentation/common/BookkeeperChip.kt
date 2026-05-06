package com.seamless.bookkeeper.presentation.common

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.seamless.bookkeeper.presentation.theme.Dimens

@Composable
fun BookkeeperChip(
    text: String,
    selected: Boolean = false,
    onClick: () -> Unit = {}
) {
    SuggestionChip(
        onClick = onClick,
        label = { Text(text, style = MaterialTheme.typography.bodySmall) },
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.height(Dimens.chipHeight)
    )
}

@Composable
fun RemovableChip(
    text: String,
    onRemove: () -> Unit
) {
    SuggestionChip(
        onClick = onRemove,
        label = { Text(text, style = MaterialTheme.typography.bodySmall) },
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.height(Dimens.chipHeight)
    )
}
