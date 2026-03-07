package com.example.altintakipandroid.ui.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ShowChart
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.altintakipandroid.domain.OnboardingSlide
import com.example.altintakipandroid.ui.components.ThemedText
import com.example.altintakipandroid.ui.components.ThemedView
import com.example.altintakipandroid.ui.theme.TextSecondary
import kotlinx.coroutines.launch

private val iconMap = mapOf(
    "qrcode" to Icons.Outlined.QrCodeScanner,
    "show_chart" to Icons.AutoMirrored.Outlined.ShowChart,
    "payments" to Icons.Outlined.Payments
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    slides: List<OnboardingSlide> = OnboardingSlide.ONBOARDING_SLIDES,
    onComplete: () -> Unit,
    onSkip: () -> Unit = onComplete
) {
    val pagerState = rememberPagerState(pageCount = { slides.size })
    val scope = rememberCoroutineScope()

    ThemedView {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = { onComplete() },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Geç",
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            Box(modifier = Modifier.weight(1f)) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                    userScrollEnabled = true
                ) { page ->
                    OnboardingSlideContent(slide = slides[page])
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                PagerDots(
                    count = slides.size,
                    currentIndex = pagerState.currentPage
                )
                Button(
                    onClick = {
                        if (pagerState.currentPage < slides.size - 1) {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        } else {
                            onComplete()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    contentPadding = ButtonDefaults.ContentPadding
                ) {
                    Text(
                        text = if (pagerState.currentPage == slides.size - 1) "Devam Et" else "İleri",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                }
                if (pagerState.currentPage == slides.size - 1) {
                    ThemedText(
                        text = "Bilgilendirme amaçlıdır.",
                        style = MaterialTheme.typography.labelSmall,
                        isSecondary = true
                    )
                }
            }
        }
    }
}

@Composable
private fun OnboardingSlideContent(slide: OnboardingSlide) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.weight(0.5f))
        val icon = slide.iconName?.let { iconMap[it] }
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(150.dp),
                tint = MaterialTheme.colorScheme.secondary
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
        ThemedText(
            text = slide.title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        ThemedText(
            text = slide.description,
            isSecondary = true,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun PagerDots(
    count: Int,
    currentIndex: Int
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(count) { index ->
            val width = if (index == currentIndex) 24.dp else 8.dp
            Box(
                modifier = Modifier
                    .size(width = width, height = 8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        if (index == currentIndex) MaterialTheme.colorScheme.secondary
                        else TextSecondary.copy(alpha = 0.3f)
                    )
            )
        }
    }
}
