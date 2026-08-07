package com.openamr.ros2course.features.course.presentation.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.openamr.ros2course.core.theme.AmberPrimary
import com.openamr.ros2course.core.theme.DeepBlack
import com.openamr.ros2course.core.theme.SurfaceVariantDark
import com.openamr.ros2course.core.theme.TextGrey
import com.openamr.ros2course.features.course.domain.entity.CourseModule
import com.openamr.ros2course.features.course.presentation.detail.ModuleDetailScreen

object ModuleListScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<ModuleListScreenModel>()
        val navigator = LocalNavigator.currentOrThrow
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

        Scaffold(
            containerColor = DeepBlack,
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TopAppBar(
                    title = { Text("ROS 2 Complete Course", color = Color.White, fontWeight = FontWeight.Bold) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = DeepBlack),
                    scrollBehavior = scrollBehavior
                )
            },
        ) { padding ->
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize().padding(padding),
            ) {
                items(screenModel.modules, key = { it.id }) { module ->
                    ModuleRow(module) { navigator.push(ModuleDetailScreen(module.id)) }
                }
            }
        }
    }
}

@Composable
private fun ModuleRow(module: CourseModule, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceVariantDark),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            OrderBadge(module.order)

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = module.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.size(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Tag(module.languages)
                    module.estimatedHours?.let { hours -> Tag(formatHours(hours)) }
                }
            }

            Text(
                text = "›",
                style = MaterialTheme.typography.headlineSmall,
                color = AmberPrimary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun OrderBadge(order: Int) {
    Surface(
        shape = CircleShape,
        color = AmberPrimary.copy(alpha = 0.2f),
        modifier = Modifier.size(40.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, AmberPrimary)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Text(
                text = if (order == 0) "•" else order.toString(),
                color = AmberPrimary,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun Tag(text: String) {
    Surface(
        shape = RoundedCornerShape(4.dp),
        color = Color.White.copy(alpha = 0.05f),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = TextGrey,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        )
    }
}

private fun formatHours(hours: Double): String {
    val whole = hours.toInt()
    return if (hours == whole.toDouble()) "${whole}h" else "${hours}h"
}
