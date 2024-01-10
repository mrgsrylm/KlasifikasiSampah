package io.github.mrgsrylm.skso.ui.screen.log

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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