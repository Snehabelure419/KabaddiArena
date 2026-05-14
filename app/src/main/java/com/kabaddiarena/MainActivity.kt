package com.kabaddiarena

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContent {

            var darkMode by remember {
                mutableStateOf(false)
            }

            MaterialTheme(
                colorScheme =
                    if (darkMode)
                        darkColorScheme()
                    else
                        lightColorScheme()
            ) {

                var currentScreen by remember {
                    mutableStateOf("splash")
                }

                AnimatedContent(
                    targetState = currentScreen,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(500)) togetherWith
                                fadeOut(animationSpec = tween(500))
                    },
                    label = "screen_transition"
                ) { screen ->
                    when (screen) {
                        "splash" -> SplashScreen(
                            onTimeout = {
                                currentScreen = "login"
                            }
                        )

                        "login" -> LoginScreen(
                            onLoginSuccess = {
                                currentScreen = "main"
                            }
                        )

                        "main" -> KabaddiApp(
                            darkMode = darkMode,

                            onToggleDarkMode = {
                                darkMode = !darkMode
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun KabaddiApp(
    darkMode: Boolean,
    onToggleDarkMode: () -> Unit
) {
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val scope = rememberCoroutineScope()
    
    // Using the MatchViewModel for Advanced State Management
    val viewModel: MatchViewModel = androidx.lifecycle.viewmodel.compose.viewModel()

    // ================= STATES =================

    var currentSubScreen by remember {
        mutableStateOf("home")
    }

    var selectedVideoUri by remember {
        mutableStateOf<android.net.Uri?>(null)
    }

    // ================= ANIMATED STATES =================

    val animatedTeamAScore by animateIntAsState(targetValue = viewModel.teamAScore, label = "team_a_score")
    val animatedTeamBScore by animateIntAsState(targetValue = viewModel.teamBScore, label = "team_b_score")

    AnimatedContent(
        targetState = currentSubScreen,
        transitionSpec = {
            if (targetState != "home") {
                slideInHorizontally { it } + fadeIn() togetherWith
                        slideOutHorizontally { -it } + fadeOut()
            } else {
                slideInHorizontally { -it } + fadeIn() togetherWith
                        slideOutHorizontally { it } + fadeOut()
            }
        },
        label = "sub_screen_transition"
    ) { screen ->
        when (screen) {
            "players" -> {
                PlayerScreen(
                    onBackClicked = {
                        currentSubScreen = "home"
                    }
                )
            }

            "analytics" -> {
                ChartScreen(
                    raidSuccess = viewModel.raidSuccess,
                    tacklePoints = viewModel.tacklePoints,
                    teamAScore = viewModel.teamAScore,
                    teamBScore = viewModel.teamBScore,

                    onBackClicked = {
                        currentSubScreen = "home"
                    }
                )
            }

            "history" -> {
                HistoryScreen(
                    db = db,

                    onBackClicked = {
                        currentSubScreen = "home"
                    }
                )
            }

            "result" -> {
                ResultScreen(
                    teamAScore = viewModel.teamAScore,
                    teamBScore = viewModel.teamBScore,
                    mvpPlayer = if (viewModel.raidSuccess > viewModel.tacklePoints) "Best Raider" else "Best Defender",
                    matchSummary = if (viewModel.teamAScore > viewModel.teamBScore) "Dominant performance by Team A!" else if (viewModel.teamBScore > viewModel.teamAScore) "Team B took control of the match!" else "A tough fought draw!",
                    onBackClicked = {
                        currentSubScreen = "home"
                    }
                )
            }

            "court" -> {
                CourtScreen(
                    onBackClicked = { currentSubScreen = "home" }
                )
            }

            "home" -> {
                HomeScreenContent(
                    paddingValues = PaddingValues(0.dp),
                    timeLeft = viewModel.timeLeft,
                    isRunning = viewModel.isRunning,
                    raidAttempts = viewModel.raidAttempts,
                    raidSuccess = viewModel.raidSuccess,
                    tacklePoints = viewModel.tacklePoints,
                    teamAScore = viewModel.teamAScore,
                    teamBScore = viewModel.teamBScore,
                    animatedTeamAScore = animatedTeamAScore,
                    animatedTeamBScore = animatedTeamBScore,
                    selectedVideoUri = selectedVideoUri,
                    timeline = viewModel.timeline,
                    onStart = { viewModel.startMatch() },
                    onPause = { viewModel.pauseMatch() },
                    onReset = { viewModel.resetMatch() },
                    onRaidSuccess = { viewModel.onRaidSuccess() },
                    onRaidFail = { viewModel.onRaidFail() },
                    onTackle = { viewModel.onTackle() },
                    onShowPlayers = { currentSubScreen = "players" },
                    onShowCourt = { currentSubScreen = "court" },
                    onAttachVideo = { selectedVideoUri = it },
                    onSaveMatch = {
                        scope.launch {
                            db.matchDao().insertMatch(
                                MatchEntity(
                                    raidAttempts = viewModel.raidAttempts,
                                    raidSuccess = viewModel.raidSuccess,
                                    tacklePoints = viewModel.tacklePoints,
                                    totalPoints = viewModel.raidSuccess + viewModel.tacklePoints,
                                    successRate = if (viewModel.raidAttempts == 0) 0 else (viewModel.raidSuccess * 100 / viewModel.raidAttempts)
                                )
                            )
                        }
                    },
                    onFinishMatch = { currentSubScreen = "result" },
                    onToggleDarkMode = onToggleDarkMode,
                    reportText = """Match Summary
Score: ${viewModel.teamAScore} - ${viewModel.teamBScore}
Success Rate: ${if (viewModel.raidAttempts == 0) 0 else (viewModel.raidSuccess * 100 / viewModel.raidAttempts)}%""",
                    onShowAnalytics = { currentSubScreen = "analytics" },
                    onShowHistory = { currentSubScreen = "history" }
                )
            }
        }
    }
}

@Composable
fun HomeScreenContent(
    paddingValues: PaddingValues,
    timeLeft: Int,
    isRunning: Boolean,
    raidAttempts: Int,
    raidSuccess: Int,
    tacklePoints: Int,
    teamAScore: Int,
    teamBScore: Int,
    animatedTeamAScore: Int,
    animatedTeamBScore: Int,
    selectedVideoUri: android.net.Uri?,
    timeline: List<MatchEvent>,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onReset: () -> Unit,
    onRaidSuccess: () -> Unit,
    onRaidFail: () -> Unit,
    onTackle: () -> Unit,
    onShowPlayers: () -> Unit,
    onShowCourt: () -> Unit,
    onAttachVideo: (android.net.Uri?) -> Unit,
    onSaveMatch: () -> Unit,
    onFinishMatch: () -> Unit,
    onToggleDarkMode: () -> Unit,
    reportText: String,
    onShowAnalytics: () -> Unit,
    onShowHistory: () -> Unit
) {
    val context = LocalContext.current
    val totalPoints = raidSuccess + tacklePoints
    val successRate = if (raidAttempts == 0) 0 else (raidSuccess * 100 / raidAttempts)

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onShowAnalytics,
                    icon = { Icon(Icons.Default.Analytics, contentDescription = "Analytics") },
                    label = { Text("Analytics") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onShowHistory,
                    icon = { Icon(Icons.Default.History, contentDescription = "History") },
                    label = { Text("History") }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF6A1B9A),
                            Color(0xFF8E24AA),
                            Color(0xFFCE93D8)
                        )
                    )
                )
                .padding(innerPadding)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "🏆 Kabaddi Arena",
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ================= TIMER =================

            Text(
                text = "⏱ Timer : $timeLeft s",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Yellow
            )

            if (isRunning && timeLeft > 0) {
                val infiniteTransition = rememberInfiniteTransition(label = "raid")
                val alpha by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(500, easing = LinearEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "alpha"
                )

                Text(
                    text = "🏃 RAIDING... RAIDING...",
                    color = Color.Red,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .alpha(alpha)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ================= PROGRESS BAR =================

            val animatedProgress by animateFloatAsState(
                targetValue = timeLeft / 30f,
                animationSpec = tween(1000, easing = LinearEasing),
                label = "timer_progress"
            )
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(10.dp)),
                color = Color.Yellow,
                trackColor = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = onStart) { Text("Start") }
                Button(onClick = onPause) { Text("Pause") }
                Button(onClick = onReset) { Text("Reset") }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ================= SCOREBOARD =================

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(text = "🏆 Team Scoreboard", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(110.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF6A1B9A))
                                    .padding(4.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.team_a),
                                    contentDescription = "Team A",
                                    modifier = Modifier.fillMaxSize().clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "TEAM A", fontWeight = FontWeight.ExtraBold, color = Color(0xFF6A1B9A))
                            Text(text = "$animatedTeamAScore", fontSize = 36.sp, fontWeight = FontWeight.Black)
                        }

                        Text(text = "VS", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.LightGray)

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(110.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE65100))
                                    .padding(4.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.team_b),
                                    contentDescription = "Team B",
                                    modifier = Modifier.fillMaxSize().clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "TEAM B", fontWeight = FontWeight.ExtraBold, color = Color(0xFFE65100))
                            Text(text = "$animatedTeamBScore", fontSize = 36.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ================= LIVE TIMELINE (ADVANCED FEATURE) =================

            if (timeline.isNotEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
                    modifier = Modifier.fillMaxWidth().heightIn(max = 200.dp),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "📜 Live Match Timeline", fontWeight = FontWeight.Bold, color = Color(0xFF4A148C))
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                            timeline.forEach { event ->
                                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(text = "${event.team}: ${event.action}", fontSize = 14.sp)
                                    Text(text = "${event.timeRemaining}s", fontSize = 14.sp, color = Color.Gray)
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // ================= MATCH STATS =================

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(text = "📊 Match Statistics", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "Raid Attempts : $raidAttempts", fontSize = 20.sp)
                    Text(text = "Successful Raids : $raidSuccess", fontSize = 20.sp)
                    Text(text = "Tackle Points : $tacklePoints", fontSize = 20.sp)
                    Text(text = "Total Points : $totalPoints", fontSize = 20.sp)
                    Text(text = "Success Rate : $successRate%", fontSize = 20.sp)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ================= ACTION BUTTONS =================

            Button(
                onClick = onRaidSuccess,
                modifier = Modifier.fillMaxWidth().height(60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF43A047))
            ) {
                Text(text = "Raid Success ✅", fontSize = 20.sp, color = Color.White)
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onRaidFail,
                modifier = Modifier.fillMaxWidth().height(60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
            ) {
                Text(text = "Raid Fail ❌", fontSize = 20.sp, color = Color.White)
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onTackle,
                modifier = Modifier.fillMaxWidth().height(60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))
            ) {
                Text(text = "Tackle + 💥", fontSize = 20.sp, color = Color.White)
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onShowPlayers,
                modifier = Modifier.fillMaxWidth().height(60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5))
            ) {
                Text(text = "Player Profiles 👤", fontSize = 20.sp, color = Color.White)
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onShowCourt,
                modifier = Modifier.fillMaxWidth().height(65.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20))
            ) {
                Icon(Icons.Default.Place, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = "Court Layout Visual 🏟", fontSize = 20.sp, color = Color.White)
            }

            Spacer(modifier = Modifier.height(14.dp))

            val videoPickerLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
                contract = androidx.activity.result.contract.ActivityResultContracts.GetContent()
            ) { uri -> onAttachVideo(uri) }

            Button(
                onClick = { videoPickerLauncher.launch("video/*") },
                modifier = Modifier.fillMaxWidth().height(65.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedVideoUri == null) Color(0xFF00796B) else Color(0xFF2E7D32)
                )
            ) {
                Icon(
                    if (selectedVideoUri == null) Icons.Default.VideoCall else Icons.Default.CheckCircle,
                    contentDescription = null, tint = Color.White
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = if (selectedVideoUri == null) "Attach Best Raid Video 🎥" else "Best Raid Video Attached ✅",
                    fontSize = 19.sp, color = Color.White
                )
            }

            if (selectedVideoUri != null) {
                Text(
                    text = "Video URI: ${selectedVideoUri.toString().takeLast(30)}...",
                    fontSize = 12.sp, color = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(onClick = onSaveMatch, modifier = Modifier.fillMaxWidth().height(60.dp)) {
                Text(text = "Save Match 💾", fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onFinishMatch,
                modifier = Modifier.fillMaxWidth().height(60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
            ) {
                Text(text = "Finish Match 🏁", fontSize = 20.sp, color = Color.White)
            }

            Spacer(modifier = Modifier.height(14.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E5F5)),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "🤖 AI Smart Prediction", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Color(0xFF4A148C))
                    Divider(modifier = Modifier.padding(vertical = 8.dp), thickness = 1.dp, color = Color.LightGray)
                    val winPrediction = when {
                        teamAScore > teamBScore + 3 -> "Team A is dominating (85% win probability)"
                        teamBScore > teamAScore + 3 -> "Team B is in control (80% win probability)"
                        else -> "Balanced match (50-50 chance)"
                    }
                    val predictedMVP = if (raidSuccess >= tacklePoints) "Best Raider" else "Best Defender"
                    val raidChance = if (successRate > 70) "High (90%)" else if (successRate > 40) "Moderate (60%)" else "Low (30%)"
                    Text(text = "🏁 Winner: $winPrediction", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "🥇 Predicted MVP: $predictedMVP", fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "⚡ Raid Success Chance: $raidChance", fontSize = 16.sp)
                    if (timeLeft < 10 && isRunning) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "⚠️ CRITICAL: Final moments approaching!", color = Color.Red, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(onClick = onToggleDarkMode, modifier = Modifier.fillMaxWidth().height(60.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)) {
                Text(text = "Dark Mode 🌙", fontSize = 20.sp, color = Color.White)
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = {
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, reportText)
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(sendIntent, "Share Match Report")
                    context.startActivity(shareIntent)
                },
                modifier = Modifier.fillMaxWidth().height(60.dp)
            ) {
                Text(text = "Share Match Report 📤", fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
