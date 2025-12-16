package com.deniz.durumu.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.deniz.durumu.data.ConditionDescriptions
import com.deniz.durumu.data.SeaConditions
import com.deniz.durumu.ui.components.HorizontalWeatherCard
import com.deniz.durumu.ui.components.SwimmingQualityBar
import com.deniz.durumu.ui.theme.*
import com.deniz.durumu.viewmodel.SeaConditionsViewModel
import com.deniz.durumu.viewmodel.UiState

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SeaConditionScreen(
    viewModel: SeaConditionsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isRefreshing = uiState is UiState.Loading
    
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = { viewModel.loadSeaConditions() }
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LightGray)
            .pullRefresh(pullRefreshState)
    ) {
        when (val state = uiState) {
            is UiState.Loading -> LoadingContent()
            is UiState.Success -> SeaConditionContent(conditions = state.data)
            is UiState.Error -> ErrorContent(
                message = state.message,
                onRetry = { viewModel.loadSeaConditions() }
            )
        }
        
        PullRefreshIndicator(
            refreshing = isRefreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter),
            contentColor = TextPrimary
        )
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color = TextPrimary,
                modifier = Modifier.size(40.dp),
                strokeWidth = 3.dp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Yükleniyor...",
                color = TextSecondary,
                fontSize = 15.sp
            )
        }
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.CloudOff,
                contentDescription = "Hata",
                tint = TextSecondary,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Bağlantı hatası",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                color = TextSecondary,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = TextPrimary
                )
            ) {
                Text("Tekrar Dene", color = White)
            }
        }
    }
}

@Composable
private fun SeaConditionContent(
    conditions: SeaConditions
) {
    val scrollState = rememberScrollState()
    
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .statusBarsPadding()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            
            // Header - Location Icon + Name
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.LocationOn,
                    contentDescription = "Konum",
                    tint = TextPrimary,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Sorgun Boğaz",
                    color = TextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Text(
                text = "Son Güncelleme: ${conditions.lastUpdated}",
                color = TextSecondary,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Swimming Quality Bar with Score
            SwimmingQualityBar(
                quality = conditions.swimmingQuality,
                score = conditions.swimmingScore
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Horizontal Weather Cards with Contextual Descriptions
            HorizontalWeatherCard(
                icon = Icons.Outlined.WbSunny,
                label = "Hava Sıcaklığı",
                value = "${conditions.airTemperature.toInt()}",
                unit = "°C",
                description = ConditionDescriptions.getAirTempDescription(conditions.airTemperature)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            HorizontalWeatherCard(
                icon = Icons.Outlined.Pool,
                label = "Deniz Suyu",
                value = "${conditions.seaTemperature.toInt()}",
                unit = "°C",
                description = ConditionDescriptions.getSeaTempDescription(conditions.seaTemperature)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            HorizontalWeatherCard(
                icon = Icons.Outlined.Waves,
                label = "Dalga Yüksekliği",
                value = String.format("%.1f", conditions.waveHeight),
                unit = "m",
                description = ConditionDescriptions.getWaveDescription(conditions.waveHeight)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            HorizontalWeatherCard(
                icon = Icons.Outlined.Air,
                label = "Rüzgar Hızı",
                value = "${conditions.windSpeed.toInt()}",
                unit = "km/h",
                description = ConditionDescriptions.getWindDescription(conditions.windSpeed)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            HorizontalWeatherCard(
                icon = Icons.Outlined.WbSunny,
                label = "UV Endeksi",
                value = String.format("%.1f", conditions.uvIndex),
                unit = "",
                description = ConditionDescriptions.getUvDescription(conditions.uvIndex)
            )
            
            // Spacer to push content up, leave room for hint
            Spacer(modifier = Modifier.height(60.dp))
        }
        
        // Pull hint fixed at bottom, above navigation bar
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(bottom = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "↓ Yenilemek İçin Aşağı Çekin",
                color = TextSecondary,
                fontSize = 12.sp
            )
        }
    }
}
