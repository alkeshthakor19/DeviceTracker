package com.devicetracker.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.devicetracker.R

@Composable
fun AssetStatusRadioButtons(
    assetWorkingStatus: Boolean,
    onStatusChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = stringResource(id = R.string.str_working_status) )

        Spacer(modifier = Modifier.width(16.dp))

        RadioButton(
            selected = assetWorkingStatus,
            onClick = { onStatusChange(true) }
        )
        Text(text = stringResource(id = R.string.str_yes), modifier = Modifier.padding(start = 4.dp))

        Spacer(modifier = Modifier.width(16.dp))

        RadioButton(
            selected = !assetWorkingStatus,
            onClick = { onStatusChange(false) }
        )
        Text(text = stringResource(id = R.string.str_no), modifier = Modifier.padding(start = 4.dp))
    }
}
