package com.example.swimpal.ui.screens.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.swimpal.ui.components.NetworkVideoPlayer

/**
 * Swimming technique video library organized by stroke categories.
 *
 * Displays expandable category cards containing technique videos for different swimming strokes
 * (Kraul, Żaba, Grzbiet, Delfin) and exercises. Uses [NetworkVideoPlayer] for streaming video playback.
 * Videos hosted on external GitHub Pages server with predefined URL structure.
 */
@Composable
fun VideoSection() {
    val baseUrl = "https://pawelproit.github.io/swimpal-videos/videos"

    data class VideoItem(
        val key: String,
        val title: String,
        val url: String
    )

    val videoCategories: Map<String, List<VideoItem>> = mapOf(
        "Kraul" to listOf(
            VideoItem("kraul_pelny", "Kraul pełny", "$baseUrl/kraul/pelnyKraul.mp4"),
            VideoItem("kraul_nogi_deska", "Nogi kraul z deską", "$baseUrl/kraul/nogiKraulZDeska.mp4"),
            VideoItem("kraul_nogi_bez_deski", "Nogi kraul bez deski", "$baseUrl/kraul/nogiKraulBezDeski.mp4"),
            VideoItem("kraul_dokladanka_deska", "Dokładanka kraul z deską", "$baseUrl/kraul/dokladankaKraulDeskaUGory.mp4"),
            VideoItem("kraul_dokladanka_bez_deski", "Dokładanka kraul bez deski", "$baseUrl/kraul/dokladankaKraulBezDeski.mp4"),
            VideoItem("kraul_dokladanka_deska_w_nogach", "Dokładanka kraul z deską między nogami", "$baseUrl/kraul/dokladankaKraulZDeskaWNogach.mp4"),
            VideoItem("kraul_rece", "Ręce kraul", "$baseUrl/kraul/ramionaKraul.mp4")
        ),
        "Żaba" to listOf(
            VideoItem("zaba_pelna", "Żaba pełna", "$baseUrl/zaba/pelnaZaba.mp4"),
            VideoItem("zaba_nogi_deska", "Nogi żaba z deską", "$baseUrl/zaba/nogiZabaDeska.mp4"),
            VideoItem("zaba_nogi_bez_deski", "Nogi żaba bez deski", "$baseUrl/zaba/nogiZabaBezDeski.mp4"),
            VideoItem("zaba_piaty_styl", "Żaba i 5 styl", "$baseUrl/zaba/zabaZPiatymStylem.mp4")
        ),
        "Grzbiet" to listOf(
            VideoItem("grzbiet_pelny", "Grzbiet pełny", "$baseUrl/grzbiet/pelnyGrzbiet.mp4"),
            VideoItem("grzbiet_nogi", "Nogi grzbiet bez deski", "$baseUrl/grzbiet/nogiGrzbietTorpeda.mp4"),
            VideoItem("grzbiet_nogi_deska_gora", "Nogi grzbiet z deską u góry", "$baseUrl/grzbiet/nogiGrzbietDeskaUGory.mp4"),
            VideoItem("grzbiet_nogi_deska_dol", "Nogi grzbiet z deską na dole", "$baseUrl/grzbiet/nogiGrzbietDeskaNaDole.mp4"),
            VideoItem("grzbiet_dokladanka", "Dokładanka do grzbietu", "$baseUrl/grzbiet/dokladankaGrzbietZStrzalka.mp4"),
            VideoItem("grzbiet_dokladanka_deska_dol", "Dokładanka do grzbietu z deską na dole", "$baseUrl/grzbiet/dokladankaGrzbietDeskaNaDole.mp4"),
            VideoItem("grzbiet_dokladanka_deska_gora", "Dokładanka do grzbietu z deską u góry", "$baseUrl/grzbiet/dokladankaGrzbietDeskaUGory.mp4"),
            VideoItem("grzbiet_rece", "Ręce grzbiet", "$baseUrl/grzbiet/Kopia ramiona_grzbiet.mp4"),
            VideoItem("grzbiet_pelny_piaty_sty", "Grzbiet i 5 styl", "$baseUrl/grzbiet/pelnyGrzbiet5Styl.mp4")
        ),
        "Delfin" to listOf(
            VideoItem("delfin_pelny", "Delfin pełny", "$baseUrl/delfin/pelnyDelfin.mp4"),
            VideoItem("delfin_nogi_deska", "Nogi delfin z deską", "$baseUrl/delfin/nogiDelfinZDeska.mp4"),
            VideoItem("delfin_nogi_bez_deski", "Nogi delfin bez deski", "$baseUrl/delfin/nogiDelfinBezDeski.mp4"),
            VideoItem("delfin_rece", "Ręce delfin", "$baseUrl/delfin/ramionaDelfin.mp4")
        ),
        "Ćwiczenia" to listOf(
            VideoItem("cw_czucie_wody", "Czucie wody", "$baseUrl/cwiczenia/czucieWodyZDeska.mp4"),
            VideoItem("cw_glajch", "Glajch", "$baseUrl/cwiczenia/glajchWP.mp4"),
            VideoItem("cw_kraul_palce", "Ćwiczenie do kraula 1 (koniuszki palców)", "$baseUrl/cwiczenia/cwiczenieKoniuszkiPalcow.mp4"),
            VideoItem("cw_kraul_pod_woda", "Ćwiczenie do kraula 2 (kraul pod wodą)", "$baseUrl/cwiczenia/kraulPodWoda.mp4")
        )
    )

    var expandedCategories by remember { mutableStateOf(setOf<String>()) }
    var expandedVideos by remember { mutableStateOf(setOf<String>()) }

    Column(modifier = Modifier.padding(top = 4.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        videoCategories.forEach { (categoryName, variants) ->
            Card(
                modifier = Modifier.fillMaxWidth().clickable {
                    expandedCategories = if (categoryName in expandedCategories) expandedCategories - categoryName else expandedCategories + categoryName
                },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(categoryName, fontSize = 18.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, modifier = Modifier.weight(1f))
                    Text(if (categoryName in expandedCategories) "▲" else "▼", fontSize = 18.sp, color = Color(0xFF2196F3))
                }
            }

            if (categoryName in expandedCategories) {
                Spacer(modifier = Modifier.height(4.dp))
                variants.forEach { item ->
                    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
                        Card(
                            modifier = Modifier.fillMaxWidth().clickable {
                                expandedVideos = if (item.key in expandedVideos) expandedVideos - item.key else expandedVideos + item.key
                            },
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text(item.title, fontSize = 16.sp, modifier = Modifier.weight(1f))
                                Text(if (item.key in expandedVideos) "▲" else "▶", fontSize = 18.sp, color = Color(0xFF2196F3))
                            }
                        }

                        AnimatedVisibility(visible = item.key in expandedVideos) {
                            Card(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(item.title, fontSize = 18.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    NetworkVideoPlayer(videoUrl = item.url, title = item.title, modifier = Modifier.fillMaxWidth())
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}
