package com.topicflow.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val topics = listOf("AI", "Coding", "Business", "Science", "Fitness", "Gaming", "Design", "Personal Finance")
private data class Video(val title: String, val channel: String, val topic: String, val level: String,
                         val description: String, val videoId: String, val relevance: Int,
                         val quality: Int, val value: Int, val freshness: Int, val duration: String)

private val catalogue = listOf(
    Video("Neural Networks Explained Clearly", "3Blue1Brown", "AI", "Beginner", "Build an intuition for how neural networks learn.", "aircAruvnKk", 98, 96, 98, 88, "19 min"),
    Video("The Attention Mechanism: A Visual Guide", "StatQuest", "AI", "Intermediate", "Understand attention and transformer architecture.", "hYdO9CscNes", 96, 94, 97, 90, "18 min"),
    Video("Build Your First AI Agent", "freeCodeCamp", "AI", "Intermediate", "A practical project-first introduction to AI agents.", "sal78ACtGTc", 94, 91, 95, 96, "2 hr"),
    Video("Python for Everybody - Full Course", "freeCodeCamp", "Coding", "Beginner", "Learn Python fundamentals with practical examples.", "8DvywoWv6fI", 97, 94, 96, 80, "4 hr"),
    Video("Data Structures Easy to Advanced", "William Fiset", "Coding", "Intermediate", "Visual explanations of core data structures.", "RBSGKlAvoiM", 91, 96, 94, 76, "1 hr"),
    Video("How Great Businesses Find Customers", "Y Combinator", "Business", "Beginner", "Customer discovery and startup fundamentals.", "MT4Ig2uqjTc", 95, 92, 96, 87, "32 min"),
    Video("Quantum Physics for Beginners", "PBS Space Time", "Science", "Beginner", "A grounded introduction to quantum ideas.", "Q1YqgPAtzho", 95, 94, 97, 85, "22 min"),
    Video("Science-Based Workout Fundamentals", "Jeff Nippard", "Fitness", "Beginner", "Training principles explained with evidence.", "eMjyvIQbn9M", 96, 94, 95, 86, "15 min"),
    Video("Game Design: The Invisible Hand", "Game Maker's Toolkit", "Gaming", "Intermediate", "Learn the design decisions behind compelling games.", "zhsLV2RD0W8", 94, 96, 97, 82, "17 min"),
    Video("Design Principles for Beginners", "The Futur", "Design", "Beginner", "Hierarchy, contrast, spacing and visual clarity.", "YqQx75OPra0", 94, 92, 95, 84, "21 min"),
    Video("Personal Finance Basics", "Two Cents", "Personal Finance", "Beginner", "Budgeting, saving and building financial habits.", "HQzoZfc3a6c", 96, 91, 96, 83, "12 min")
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences("topicflow", MODE_PRIVATE)
        setContent { TopicFlowApp(
            initialTopics = prefs.getStringSet("topics", setOf("AI", "Coding")) ?: setOf("AI", "Coding"),
            onTopicsChanged = { prefs.edit().putStringSet("topics", it).apply() },
            initialSaved = prefs.getStringSet("saved", emptySet()) ?: emptySet(),
            onSavedChanged = { prefs.edit().putStringSet("saved", it).apply() }
        ) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopicFlowApp(initialTopics: Set<String>, onTopicsChanged: (Set<String>) -> Unit,
                         initialSaved: Set<String>, onSavedChanged: (Set<String>) -> Unit) {
    var selected by remember { mutableStateOf(initialTopics) }
    var saved by remember { mutableStateOf(initialSaved) }
    var query by remember { mutableStateOf("") }
    var level by remember { mutableStateOf("All levels") }
    var tab by remember { mutableStateOf(0) }
    val context = LocalContext.current
    val ranked = remember(selected, query, level, tab) {
        val base = if (tab == 1) catalogue.filter { it.videoId in saved } else catalogue
        base.filter { (selected.isEmpty() || it.topic in selected) &&
                (level == "All levels" || it.level == level) &&
                (query.isBlank() || (it.title + it.topic + it.channel).contains(query, true)) }
            .sortedByDescending { score(it) }
    }

    MaterialTheme(colorScheme = darkColorScheme(
        primary = Color(0xFFB8F36B), secondary = Color(0xFF78D6C8),
        background = Color(0xFF0B0F0D), surface = Color(0xFF151B18),
        onBackground = Color(0xFFF2F5F0), onSurface = Color(0xFFF2F5F0)
    )) {
        Scaffold(containerColor = Color(0xFF0B0F0D), bottomBar = {
            NavigationBar(containerColor = Color(0xFF151B18)) {
                NavigationBarItem(selected = tab == 0, onClick = { tab = 0 }, icon = { Icon(Icons.Default.PlayArrow, null) }, label = { Text("Discover") })
                NavigationBarItem(selected = tab == 1, onClick = { tab = 1 }, icon = { Icon(Icons.Default.Bookmark, null) }, label = { Text("Saved") })
            }
        }) { padding ->
            LazyColumn(Modifier.fillMaxSize().padding(padding).padding(horizontal = 18.dp)) {
                item {
                    Spacer(Modifier.height(20.dp))
                    Text("TOPICFLOW", color = Color(0xFFB8F36B), fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 3.sp)
                    Text(if (tab == 0) "Your feed. Your rules." else "Your watchlist", fontSize = 28.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp, bottom = 6.dp))
                    Text(if (tab == 0) "Discover videos chosen for what matters to you." else "Videos you saved for later.", color = Color(0xFF9BA79F), fontSize = 14.sp)
                    Spacer(Modifier.height(20.dp))
                }
                if (tab == 0) {
                    item {
                        Text("CHOOSE YOUR TOPICS", color = Color(0xFF9BA79F), fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
                        Spacer(Modifier.height(10.dp))
                        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            topics.forEach { topic ->
                                FilterChip(selected = topic in selected, onClick = {
                                    selected = if (topic in selected) selected - topic else selected + topic
                                    onTopicsChanged(selected)
                                }, label = { Text(topic) })
                            }
                        }
                        Spacer(Modifier.height(18.dp))
                        OutlinedTextField(value = query, onValueChange = { query = it }, modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Search inside your interests") }, leadingIcon = { Icon(Icons.Default.Search, null) },
                            singleLine = true, shape = RoundedCornerShape(16.dp))
                        Spacer(Modifier.height(12.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Recommended for you", fontSize = 19.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                            var expanded by remember { mutableStateOf(false) }
                            Box {
                                TextButton(onClick = { expanded = true }) { Text(level, color = Color(0xFFB8F36B)) }
                                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                    listOf("All levels", "Beginner", "Intermediate").forEach { option ->
                                        DropdownMenuItem(text = { Text(option) }, onClick = { level = option; expanded = false })
                                    }
                                }
                            }
                        }
                        Text("Ranked by relevance, usefulness and quality signals — not views alone.", color = Color(0xFF9BA79F), fontSize = 12.sp)
                        Spacer(Modifier.height(12.dp))
                    }
                }
                if (ranked.isEmpty()) item {
                    Text("No matches yet. Select more topics or clear your search.", color = Color(0xFF9BA79F), modifier = Modifier.padding(vertical = 28.dp))
                }
                items(ranked, key = { it.videoId }) { video ->
                    VideoCard(video, video.videoId in saved,
                        onSave = {
                            saved = if (video.videoId in saved) saved - video.videoId else saved + video.videoId
                            onSavedChanged(saved)
                        },
                        onPlay = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=${video.videoId}"))
                            context.startActivity(intent)
                        })
                    Spacer(Modifier.height(12.dp))
                }
                item { Spacer(Modifier.height(20.dp)); Text("MVP catalogue • Connect a YouTube API key for live topic search and a larger catalogue.", color = Color(0xFF78847C), fontSize = 11.sp, modifier = Modifier.padding(bottom = 20.dp)) }
            }
        }
    }
}

private fun score(v: Video): Int {
    // Transparent initial ranking: relevance 30%, quality proxy 25%, learning value 20%,
    // engagement-quality proxy 15%, freshness 10%. These are editorial seed scores.
    return (v.relevance * 30 + v.quality * 25 + v.value * 20 + 90 * 15 + v.freshness * 10) / 100
}

@Composable
private fun VideoCard(video: Video, isSaved: Boolean, onSave: () -> Unit, onPlay: () -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF151B18)), shape = RoundedCornerShape(20.dp)) {
        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(color = Color(0xFF26342B), shape = RoundedCornerShape(8.dp)) {
                    Text(video.topic.uppercase(), color = Color(0xFFB8F36B), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp))
                }
                Spacer(Modifier.weight(1f))
                Text(video.duration, color = Color(0xFF9BA79F), fontSize = 11.sp)
                IconButton(onClick = onSave) { Icon(if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder, contentDescription = "Save video", tint = Color(0xFFB8F36B)) }
            }
            Text(video.title, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 5.dp))
            Text(video.channel + "  •  " + video.level, color = Color(0xFF9BA79F), fontSize = 12.sp, modifier = Modifier.padding(top = 5.dp))
            Text(video.description, color = Color(0xFFD0D8D1), fontSize = 13.sp, modifier = Modifier.padding(top = 10.dp))
            Row(Modifier.fillMaxWidth().padding(top = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("TOPIC MATCH  ${video.relevance}%", color = Color(0xFFB8F36B), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Button(onClick = onPlay, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB8F36B), contentColor = Color(0xFF101510))) {
                    Icon(Icons.Default.PlayArrow, null); Spacer(Modifier.width(4.dp)); Text("Watch")
                }
            }
        }
    }
}
