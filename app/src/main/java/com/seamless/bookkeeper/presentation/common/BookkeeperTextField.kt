package com.seamless.bookkeeper.presentation.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.seamless.bookkeeper.presentation.theme.Dimens

@Composable
fun BookkeeperTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    maxLines: Int = 1,
    maxLength: Int = Int.MAX_VALUE
) {
    OutlinedTextField(
        value = value,
        onValueChange = { if (it.length <= maxLength) onValueChange(it) },
        label = { Text(label) },
        placeholder = if (placeholder.isNotBlank()) ({ Text(placeholder) }) else null,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimens.shapeSmall),
        singleLine = maxLines == 1,
        maxLines = maxLines
    )
}
