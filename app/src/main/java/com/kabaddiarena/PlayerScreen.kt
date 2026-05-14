package com.kabaddiarena

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class PlayerData(
    val name: String,
    val role: String,
    val raidPoints: Int,
    val tacklePoints: Int,
    val matchesPlayed: Int,
    val imageRes: Int,
    val team: String,
    val teamColor: Color
) {
    val performanceIndex: Int get() = raidPoints + (tacklePoints * 2)
}

@Composable
fun PlayerScreen(
    onBackClicked: () -> Unit
) {
    var sortByRank by remember { mutableStateOf(false) }

    val basePlayers = listOf(
        PlayerData("Pawan Sehrawat", "Raider", 150, 8, 48, R.drawable.team_b, "TEAM B", Color(0xFFE65100)),
        PlayerData("Fazel Atrachali", "Defender", 5, 80, 60, R.drawable.team_b, "TEAM B", Color(0xFFE65100)),
        PlayerData("Arjun Deshwal", "Raider", 120, 5, 45, R.drawable.player1, "TEAM A", Color(0xFF6A1B9A)),
        PlayerData("Rahul Chaudhari", "Raider", 100, 10, 50, R.drawable.player1, "TEAM A", Color(0xFF6A1B9A)),
        PlayerData("Mohit Chhillar", "All Rounder", 40, 50, 55, R.drawable.team_b, "TEAM B", Color(0xFFE65100)),
        PlayerData("Siddharth Desai", "Raider", 95, 2, 40, R.drawable.player1, "TEAM A", Color(0xFF6A1B9A))
    )

    val players = if (sortByRank) basePlayers.sortedByDescending { it.performanceIndex } else basePlayers

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "👤 Players",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
            
            FilterChip(
                selected = sortByRank,
                onClick = { sortByRank = !sortByRank },
                label = { Text("Rank by Index 🏆") }
            )
        }
        
        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            itemsIndexed(players) { index, player ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(player.teamColor)
                                .padding(vertical = 4.dp, horizontal = 16.dp)
                        ) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = player.team, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Black)
                                if (sortByRank) {
                                    Text(text = "#${index + 1} Global", color = Color.Yellow, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(70.dp)
                                    .clip(CircleShape)
                                    .background(player.teamColor.copy(alpha = 0.1f))
                            ) {
                                Image(
                                    painter = painterResource(id = player.imageRes),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column {
                                Text(text = player.name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                Text(text = player.role, color = player.teamColor, fontSize = 14.sp)
                                
                                Spacer(modifier = Modifier.height(4.dp))
                                
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    StatItem("Raids", player.raidPoints.toString())
                                    StatItem("Tackles", player.tacklePoints.toString())
                                    StatItem("Index", player.performanceIndex.toString())
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onBackClicked,
            modifier = Modifier.fillMaxWidth().height(55.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = "Back to Match", fontSize = 18.sp)
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 11.sp, color = Color.Gray)
        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}
