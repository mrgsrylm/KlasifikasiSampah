package io.github.mrsrylm.skso.ui.screen.log

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.mrsrylm.skso.R
import io.github.mrsrylm.skso.ui.theme.SKSOTheme

@Composable
fun LogScreen() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            Text(
                text = stringResource(id = R.string.log_date),
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp)
            )
            Text(
                text = stringResource(id = R.string.log_time),
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp)
            )
            Text(
                text = stringResource(id = R.string.log_waste),
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp)
            )
        }

        LazyColumn {
            items(50) { index ->
                LogItem(date = "$index", time = "$index", waste = "$index")
            }
        }
    }

}

@Composable
fun LogItem(
    date: String,
    time: String,
    waste: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        Text(
            text = date,
            modifier = Modifier
                .weight(1f)
                .padding(4.dp)
        )
        Text(
            text = time,
            modifier = Modifier
                .weight(1f)
                .padding(4.dp)
        )
        Text(
            text = waste,
            modifier = Modifier
                .weight(1f)
                .padding(4.dp)
        )
    }
}

@Preview
@Composable
fun LogScreenPreview() {
    SKSOTheme {
        LogScreen()
    }
}