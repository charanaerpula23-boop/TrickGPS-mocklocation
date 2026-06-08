package com.charanhyper.tech.greydailer.ui

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.GpsNotFixed
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.charanhyper.tech.greydailer.JoystickViewModel
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.util.GeoPoint
import org.osmdroid.util.MapTileIndex
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import kotlin.math.roundToInt

@Composable
fun JoystickScreen(
    viewModel: JoystickViewModel,
    onPickOnMap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // -- Map configuration (Google Satellites) --
    val servers = remember {
        arrayOf(
            "https://mt0.google.com/vt/lyrs=y",
            "https://mt1.google.com/vt/lyrs=y",
            "https://mt2.google.com/vt/lyrs=y",
            "https://mt3.google.com/vt/lyrs=y"
        )
    }

    val googleSatTileSource = remember {
        object : XYTileSource("GoogleSat", 0, 20, 256, ".png", servers) {
            override fun getTileURLString(pMapTileIndex: Long): String {
                val z = MapTileIndex.getZoom(pMapTileIndex)
                val x = MapTileIndex.getX(pMapTileIndex)
                val y = MapTileIndex.getY(pMapTileIndex)
                val s = servers[((x + y) % servers.size).toInt()]
                return "$s&x=$x&y=$y&z=$z"
            }
        }
    }

    val mapView = remember {
        MapView(context).apply {
            setTileSource(googleSatTileSource)
            setMultiTouchControls(true)
            controller.setZoom(16.0)
            val initLat = viewModel.latitude.toDoubleOrNull() ?: 37.7749
            val initLng = viewModel.longitude.toDoubleOrNull() ?: -122.4194
            controller.setCenter(GeoPoint(initLat, initLng))
        }
    }

    DisposableEffect(mapView) {
        onDispose { mapView.onDetach() }
    }

    val markerHolder = remember { arrayOf<Marker?>(null) }
    val redPinDrawable = remember(context) { BitmapDrawable(context.resources, createRedPinBitmap()) }

    var lockCamera by remember { mutableStateOf(true) }

    // Synchronize map center when mock location coordinates update from the service
    val latVal = viewModel.latitude.toDoubleOrNull() ?: 37.7749
    val lngVal = viewModel.longitude.toDoubleOrNull() ?: -122.4194

    LaunchedEffect(latVal, lngVal) {
        if (lockCamera) {
            mapView.controller.animateTo(GeoPoint(latVal, lngVal))
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        // ── Map View ──
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { mapView },
            update = { mv ->
                markerHolder[0]?.let { mv.overlays.remove(it) }
                val marker = Marker(mv).apply {
                    position = GeoPoint(latVal, lngVal)
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    icon = redPinDrawable
                }
                mv.overlays.add(marker)
                markerHolder[0] = marker
                mv.invalidate()
            }
        )

        // ── Search & Controls Overlay (Top) ──
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            LocationSearchBar(
                placeholder = "Search start location\u2026",
                onLocationSelected = { lat, lng, _ ->
                    viewModel.onLatLngPicked(lat, lng)
                    mapView.controller.animateTo(GeoPoint(lat, lng))
                }
            )

            if (viewModel.errorMessage.isNotEmpty()) {
                ErrorCard(message = viewModel.errorMessage, onDismiss = viewModel::clearError)
            }
        }

        // ── Control Card (Bottom Left / Bottom Full) ──
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth(if (viewModel.isMocking) 0.55f else 1f)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Stats & Lock camera control
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = if (viewModel.isMocking) "Joystick Mock Active" else "Setup Joystick",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (viewModel.isMocking) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${viewModel.latitude}, ${viewModel.longitude}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Lock Camera", style = MaterialTheme.typography.bodySmall)
                        IconButton(
                            onClick = { lockCamera = !lockCamera },
                            modifier = Modifier.size(32.dp),
                            colors = IconButtonDefaults.iconButtonColors(
                                contentColor = if (lockCamera) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        ) {
                            Icon(
                                imageVector = if (lockCamera) Icons.Default.GpsFixed else Icons.Default.GpsNotFixed,
                                contentDescription = "Lock Camera"
                            )
                        }
                    }
                }
            }

            // Speed multiplier selector
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    val speeds = listOf(
                        SpeedOption("5", Icons.Default.DirectionsWalk),
                        SpeedOption("15", Icons.Default.DirectionsRun),
                        SpeedOption("30", Icons.Default.DirectionsBike),
                        SpeedOption("60", Icons.Default.DirectionsCar)
                    )
                    speeds.forEach { opt ->
                        val isSelected = viewModel.speedKmh == opt.value
                        IconButton(
                            onClick = { viewModel.speedKmh = opt.value },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primaryContainer else androidx.compose.ui.graphics.Color.Transparent
                                )
                        ) {
                            Icon(
                                imageVector = opt.icon,
                                contentDescription = "${opt.value} km/h",
                                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Start / Stop Toggle Button
            Button(
                onClick = {
                    if (viewModel.isMocking) {
                        viewModel.stopJoystickMocking()
                    } else {
                        viewModel.startJoystickMocking()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (viewModel.isMocking) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = if (viewModel.isMocking) Icons.Default.PowerSettingsNew else Icons.Default.MyLocation,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = if (viewModel.isMocking) "Stop Mock" else "Start Joystick",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (!viewModel.isMocking) {
                Button(
                    onClick = onPickOnMap,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                ) {
                    Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Pick Start On Map")
                }
            }
        }

        // ── Joystick Thumb Overlay (Bottom Right - only when mocking) ──
        AnimatedVisibility(
            visible = viewModel.isMocking,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Joystick(
                onMove = { bearing, normalizedSpeed ->
                    val maxSpeedKmh = viewModel.speedKmh.toFloatOrNull() ?: 15f
                    val maxSpeedMps = maxSpeedKmh / 3.6f
                    val currentSpeedMps = normalizedSpeed * maxSpeedMps
                    viewModel.updateJoystickVector(bearing, currentSpeedMps)
                },
                onStop = {
                    viewModel.updateJoystickVector(0f, 0f)
                }
            )
        }
    }
}

private data class SpeedOption(val value: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

@Composable
fun Joystick(
    modifier: Modifier = Modifier,
    sizeDp: Dp = 150.dp,
    onMove: (bearing: Float, normalizedSpeed: Float) -> Unit,
    onStop: () -> Unit
) {
    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    val density = LocalDensity.current
    val sizePx = with(density) { sizeDp.toPx() }
    val maxRadiusPx = sizePx / 2f
    val knobRadiusPx = with(density) { 30.dp.toPx() }
    val limitPx = maxRadiusPx - knobRadiusPx

    Box(
        modifier = modifier
            .size(sizeDp)
            .clip(CircleShape)
            .shadow(4.dp, CircleShape)
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.85f))
            .border(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f), CircleShape)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        dragOffset = Offset.Zero
                        onStop()
                    },
                    onDragCancel = {
                        dragOffset = Offset.Zero
                        onStop()
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        val newOffset = dragOffset + dragAmount
                        val dist = newOffset.getDistance()
                        dragOffset = if (dist > limitPx) {
                            newOffset * (limitPx / dist)
                        } else {
                            newOffset
                        }

                        // Calculate direction and normalised speed
                        val x = dragOffset.x
                        val y = dragOffset.y
                        val currentDist = dragOffset.getDistance()
                        val normalizedSpeed = if (limitPx > 0f) (currentDist / limitPx).coerceIn(0f, 1f) else 0f
                        val angleRad = Math.atan2(x.toDouble(), -y.toDouble())
                        var bearing = Math.toDegrees(angleRad).toFloat()
                        if (bearing < 0f) bearing += 360f

                        onMove(bearing, normalizedSpeed)
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        // Outer directional indicators
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), CircleShape)
        )

        // Center dot
        Box(
            modifier = Modifier
                .offset {
                    IntOffset(
                        x = dragOffset.x.roundToInt(),
                        y = dragOffset.y.roundToInt()
                    )
                }
                .size(60.dp)
                .shadow(6.dp, CircleShape)
                .background(MaterialTheme.colorScheme.primary, CircleShape)
        )
    }
}

private fun createRedPinBitmap(): Bitmap {
    val w = 96
    val h = 128
    val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bmp)

    val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#E53935")
        style = Paint.Style.FILL
    }
    val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#B71C1C")
        style = Paint.Style.FILL
    }
    val highlightPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FFCDD2")
        style = Paint.Style.FILL
        alpha = 180
    }

    val cx = w / 2f
    val radius = w / 2f - 4f

    canvas.drawCircle(cx, radius + 4f, radius, fillPaint)
    val path = Path().apply {
        moveTo(cx - radius * 0.55f, radius + 4f + radius * 0.6f)
        lineTo(cx + radius * 0.55f, radius + 4f + radius * 0.6f)
        lineTo(cx, h.toFloat() - 2f)
        close()
    }
    canvas.drawPath(path, fillPaint)

    val tipPath = Path().apply {
        moveTo(cx - radius * 0.25f, radius + 4f + radius * 0.8f)
        lineTo(cx + radius * 0.25f, radius + 4f + radius * 0.8f)
        lineTo(cx, h.toFloat() - 2f)
        close()
    }
    canvas.drawPath(tipPath, shadowPaint)

    canvas.drawCircle(cx, radius + 4f, radius * 0.38f, Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.FILL
    })

    canvas.drawOval(
        RectF(cx - radius * 0.28f, 8f, cx + radius * 0.05f, 8f + radius * 0.42f),
        highlightPaint
    )

    return bmp
}
