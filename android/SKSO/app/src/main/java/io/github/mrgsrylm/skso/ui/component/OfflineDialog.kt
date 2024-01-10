package io.github.mrgsrylm.skso.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.mrgsrylm.skso.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OfflineDialog(
    onRetry: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(size = 16.dp) // shape of the dialog
                )
                .padding(all = 16.dp), // inner padding
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(R.drawable.baseline_signal_wifi_off_24),
                contentDescription = stringResource(id = R.string.no_internet),
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.error
            )
            Text(
                text = stringResource(id = R.string.no_internet),
                color = MaterialTheme.colorScheme.error,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = stringResource(id = R.string.no_internet_desc),
                color = MaterialTheme.colorScheme.primary,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            TextButton(onClick = onRetry) {
                Text(stringResource(R.string.retry))
            }
        }
    }
}

//class OfflineDialogPreviewParameter : PreviewParameterProvider<Unit> {
//    override val values: Sequence<Unit>
//        get() = sequenceOf(Unit)
//}
//
//@Preview(showBackground = true)
//@Composable
//fun OfflineDialogPreview(
//    @PreviewParameter(OfflineDialogPreviewParameter::class) parameter: Unit
//) {
//    SKSOTheme {
//        OfflineDialog {}
//    }
//}