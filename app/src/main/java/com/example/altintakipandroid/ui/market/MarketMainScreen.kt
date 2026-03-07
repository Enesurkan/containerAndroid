package com.example.altintakipandroid.ui.market

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.altintakipandroid.domain.CategoryTree
import com.example.altintakipandroid.ui.components.CustomHeader
import com.example.altintakipandroid.ui.components.ThemedText
import com.example.altintakipandroid.ui.components.ThemedView
import com.example.altintakipandroid.ui.theme.AccentOrange
import com.example.altintakipandroid.ui.theme.SurfaceCream
import com.example.altintakipandroid.ui.theme.SurfaceElevated
import com.example.altintakipandroid.ui.theme.TextPrimary
import com.example.altintakipandroid.ui.theme.TextSecondary

@Composable
fun MarketMainScreen(
    viewModel: MarketViewModel,
    onOpenCampaigns: () -> Unit,
    onOpenCampaignDetail: (Int) -> Unit,
    onOpenProductList: (categoryId: Int, subcategoryId: Int?, String) -> Unit
) {
    val state by viewModel.state.collectAsState()
    var selectedCategoryId by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) { viewModel.fetchDataIfNeeded() }
    LaunchedEffect(state.categories) {
        if (selectedCategoryId == null && state.categories.isNotEmpty()) {
            selectedCategoryId = state.categories.first().id
        }
    }

    ThemedView {
        Column(modifier = Modifier.fillMaxSize()) {
            CustomHeader(title = "Vitrin")
            when {
                state.isLoading && state.categories.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = AccentOrange)
                    }
                }
                state.errorMessage != null -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        ThemedText(text = state.errorMessage!!, color = com.example.altintakipandroid.ui.theme.Danger)
                    }
                }
                else -> {
                    if (state.bannerSlides.isNotEmpty()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .padding(top = 16.dp, bottom = 8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(TextPrimary)
                                    .clickable(onClick = onOpenCampaigns),
                                contentAlignment = Alignment.Center
                            ) {
                                ThemedText(
                                    text = "Kampanyalar",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                    color = SurfaceCream
                                )
                            }
                        }
                    }
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        LazyColumn(
                            modifier = Modifier.width(130.dp).padding(horizontal = 8.dp, vertical = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.categories) { category ->
                                val isSelected = selectedCategoryId == category.id
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(SurfaceCream)
                                        .clickable { selectedCategoryId = category.id }
                                        .padding(8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    ThemedText(
                                        text = category.name,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = if (isSelected) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Medium,
                                        color = if (isSelected) TextPrimary else TextSecondary
                                    )
                                }
                            }
                        }
                        androidx.compose.material3.VerticalDivider(
                            color = com.example.altintakipandroid.ui.theme.Separator,
                            modifier = Modifier.width(1.dp)
                        )
                        val selectedCat = state.categories.find { it.id == selectedCategoryId }
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .padding(16.dp)
                        ) {
                            if (selectedCat != null) {
                                item {
                                    ThemedText(
                                        text = "(TÜMÜ) ${selectedCat.name}",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = TextSecondary,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { onOpenProductList(selectedCat.id, null, "(TÜMÜ) ${selectedCat.name}") }
                                            .padding(vertical = 8.dp)
                                    )
                                }
                                items(selectedCat.children.orEmpty()) { subcat ->
                                    ThemedText(
                                        text = subcat.name,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = TextSecondary,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { onOpenProductList(selectedCat.id, subcat.id, subcat.name) }
                                            .padding(vertical = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
