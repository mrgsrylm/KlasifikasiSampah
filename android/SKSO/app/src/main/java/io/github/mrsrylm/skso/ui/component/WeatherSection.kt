package io.github.mrsrylm.skso.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.mrsrylm.skso.R
import io.github.mrsrylm.skso.ui.theme.SKSOTheme

@Composable
fun WeatherSection(
    weatherData: WeatherData
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_location_on_24),
                    contentDescription = stringResource(id = R.string.location),
                    modifier = Modifier.padding(end = 4.dp)
                )
                Text(
                    text = weatherData.location,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_today_24),
                    contentDescription = weatherData.dateTime,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = weatherData.dateTime,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = null,
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally),
                contentScale = ContentScale.Inside
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = weatherData.status, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Preview()
@Composable
fun WeatherSectionPreview() {
    SKSOTheme {
        val data = WeatherData()
        WeatherSection(data)
    }
}

data class WeatherData(
    val location: String = "Kuningan, Jawa Barat",
    val dateTime: String = "Senin, 05 Januari 2024",
    val value: String = "27C",
    val status: String = "Hujan"
) {}