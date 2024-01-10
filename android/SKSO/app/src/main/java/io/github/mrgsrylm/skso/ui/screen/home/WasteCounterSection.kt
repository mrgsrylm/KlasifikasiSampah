package io.github.mrgsrylm.skso.ui.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.mrgsrylm.skso.R
import io.github.mrgsrylm.skso.common.ScreenStatus

@Composable
fun WasteCounterSection(
    organikState: ScreenStatus<Int>,
    anorganikState: ScreenStatus<Int>,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.waste_counter),
            style = MaterialTheme.typography.bodyMedium
                .copy(fontWeight = FontWeight.Bold, fontSize = 16.sp),
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.Start)
        )
        Spacer(modifier = Modifier.size(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            ElevatedCard(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 4.dp),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 6.dp
                ),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            ) {
                when {
                    organikState is ScreenStatus.Loading -> {
                        Text(text = "Loading..")
                    }

                    organikState is ScreenStatus.Success -> {
                        WasteCounter(
                            title = R.string.organik,
                            icon = R.drawable.baseline_smartphone_24,
                            count = organikState.uiData
                        )
                    }

                    organikState is ScreenStatus.Error -> {
                        Text(text = "Error")
                    }
                }
            }

            ElevatedCard(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 6.dp
                ),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            ) {
                when {
                    anorganikState is ScreenStatus.Loading -> {
                        Text(text = "Loading..")
                    }

                    anorganikState is ScreenStatus.Success -> {
                        WasteCounter(
                            title = R.string.organik,
                            icon = R.drawable.baseline_smartphone_24,
                            count = anorganikState.uiData
                        )
                    }

                    anorganikState is ScreenStatus.Error -> {
                        Text(text = "Error")
                    }
                }
            }
        }
    }
}

@Composable
fun WasteCounter(
    title: Int,
    icon: Int,
    count: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = stringResource(id = title),
                modifier = Modifier.padding(end = 4.dp),
            )
            Text(
                text = stringResource(id = title),
                style = MaterialTheme.typography.bodyMedium

            )
        }
        Spacer(modifier = Modifier.size(2.dp))
        Text(
            text = "$count",
            style = MaterialTheme.typography.bodyMedium
                .copy(fontWeight = FontWeight.Bold, fontSize = 24.sp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .wrapContentWidth(Alignment.CenterHorizontally)
        )
    }
}