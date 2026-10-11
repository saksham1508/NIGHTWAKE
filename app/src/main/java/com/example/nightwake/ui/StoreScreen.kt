package com.example.nightwake.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nightwake.services.IPurchaseService
import com.example.nightwake.services.ServiceLocator

/**
 * Store & Season Pass Catalog (Strictly No-P2W: Convenience & Cosmetics Only).
 */
@Composable
fun StoreScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val catalog = remember { ServiceLocator.purchases.getCatalog() }
    var saveState by remember { mutableStateOf(ServiceLocator.save.loadSave()) }
    var purchaseFeedback by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF090D14))
    ) {
        // Header
        Surface(
            color = Color(0xFF10151E),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("store_back_button")) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Text(
                    text = "QUARTERMASTER & PASS",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "💎 ${saveState.premiumCurrencyNanites} Nanites",
                    color = Color(0xFF00E5FF),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(end = 12.dp)
                )
            }
        }

        // Anti-P2W Guarantee Banner
        Surface(
            color = Color(0xFF14241B),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "🛡️ FAIR PLAY PROMISE: Premium currency never sells raw combat power or skips gameplay tiers.",
                color = Color(0xFF69F0AE),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
            )
        }

        purchaseFeedback?.let { msg ->
            Text(
                text = msg,
                color = Color(0xFFFFD54F),
                fontSize = 12.sp,
                modifier = Modifier.padding(14.dp)
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Text(
                    text = "NIGHTWATCH SEASON 1 BATTLE PASS",
                    color = Color(0xFFFF9100),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            items(catalog) { item ->
                StoreItemCard(
                    item = item,
                    onBuy = {
                        ServiceLocator.purchases.purchaseProduct(item.productId) { success, _ ->
                            if (success) {
                                if (item.nanitesReward > 0) {
                                    saveState.premiumCurrencyNanites += item.nanitesReward
                                    ServiceLocator.save.saveGame(saveState)
                                }
                                purchaseFeedback = "Unlocked: ${item.title}!"
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun StoreItemCard(
    item: IPurchaseService.ProductCatalogItem,
    onBuy: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF141A24), RoundedCornerShape(12.dp))
            .border(1.dp, Color(0xFF263238), RoundedCornerShape(12.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = item.description,
                color = Color(0xFF90A4AE),
                fontSize = 11.sp
            )
        }

        Button(
            onClick = onBuy,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF3D00)),
            modifier = Modifier.testTag("buy_${item.productId}")
        ) {
            Text(item.priceFormatted, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
    }
}
