package com.nischint.app.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nischint.app.data.api.StorageAnalyzer
import com.nischint.app.data.api.StorageAnalysis
import com.nischint.app.data.database.AppDatabase
import com.nischint.app.data.models.Transaction
import com.nischint.app.ui.components.NeomorphicCard
import com.nischint.app.ui.theme.*
import com.nischint.app.utils.rememberCurrentLanguage
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext

/**
 * Storage Analysis Card showing Gemini-powered financial insights
 */
@Composable
fun StorageAnalysisCard(
    transactions: List<Transaction>,
    modifier: Modifier = Modifier
) {
    val analyzer = remember { StorageAnalyzer() }
    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }
    val currentLanguage = rememberCurrentLanguage()
    
    var analysis by remember { mutableStateOf<StorageAnalysis?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    
    // Load transactions from database if empty
    val dbTransactions by database.transactionDao().getAllTransactions()
        .map { entities -> entities.map { it.toTransaction() } }
        .collectAsState(initial = emptyList())
    
    val effectiveTransactions = if (transactions.isNotEmpty()) transactions else dbTransactions
    
    // Load analysis when transactions change or language changes
    LaunchedEffect(effectiveTransactions.size, currentLanguage) {
        if (effectiveTransactions.isNotEmpty()) {
            isLoading = true
            try {
                analyzer.analyzeTransactions(effectiveTransactions, currentLanguage).collect { result ->
                    analysis = result
                    isLoading = false
                }
            } catch (e: Exception) {
                android.util.Log.e("StorageAnalysisCard", "Error analyzing transactions", e)
                isLoading = false
            }
        } else {
            analysis = null
            isLoading = false
        }
    }
    
    NeomorphicCard(modifier = modifier) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "💡 Financial Insights",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
                if (analyzer.isAvailable()) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = YellowPrimary.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "AI",
                            style = MaterialTheme.typography.labelSmall,
                            color = YellowPrimary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
            
            if (isLoading) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = YellowPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Analyzing transactions...",
                        color = TextGray,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else if (analysis != null) {
                val analysisData = analysis!!
                
                // Summary
                Text(
                    text = analysisData.summary,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextWhite
                )
                
                Divider(color = BorderSubtle)
                
                // Insights
                if (analysisData.insights.isNotEmpty()) {
                    Text(
                        text = "Key Insights:",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = YellowPrimary
                    )
                    analysisData.insights.forEach { insight ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = "• ",
                                color = YellowPrimary,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = insight,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextGray,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
                
                Divider(color = BorderSubtle)
                
                // Recommendations
                if (analysisData.recommendations.isNotEmpty()) {
                    Text(
                        text = "Recommendations:",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = GreenSafe
                    )
                    analysisData.recommendations.forEach { rec ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = "✓ ",
                                color = GreenSafe,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = rec,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextGray,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
                
                // Risk Level & Savings Potential
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Risk Level
                    Column {
                        Text(
                            text = "Risk Level",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextGray
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = when (analysisData.riskLevel.lowercase()) {
                                "high" -> RedDanger.copy(alpha = 0.2f)
                                "medium" -> OrangeWarning.copy(alpha = 0.2f)
                                else -> GreenSafe.copy(alpha = 0.2f)
                            }
                        ) {
                            Text(
                                text = analysisData.riskLevel.uppercase(),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = when (analysisData.riskLevel.lowercase()) {
                                    "high" -> RedDanger
                                    "medium" -> OrangeWarning
                                    else -> GreenSafe
                                },
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                    
                    // Savings Potential
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Savings Potential",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextGray
                        )
                        Text(
                            text = analysisData.savingsPotential,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = GreenSafe
                        )
                    }
                }
            } else {
                Text(
                    text = "No transactions to analyze yet. Add transactions to get insights!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextGray
                )
            }
        }
    }
}

