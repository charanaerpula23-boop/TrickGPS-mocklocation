package com.charanhyper.tech.greydailer.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisclaimerScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Disclaimer & Info", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Setup Guide
            SectionCard(
                title = "Setup Guide",
                titleColor = MaterialTheme.colorScheme.primary,
                content = "1. Enable Developer Options\n" +
                    "   Go to Settings \u2192 About phone \u2192 Tap \"Build number\" 7 times.\n\n" +
                    "2. Select Mock Location App\n" +
                    "   Go to Developer Options \u2192 Select mock location app \u2192 Select \"trick-gps\".\n\n" +
                    "3. Start Spoofing\n" +
                    "   Configure coordinates in Static Mock, Travel, or Joystick mode, and tap the Start button."
            )

            // Terms & Conditions
            SectionCard(
                title = "Terms & Conditions",
                titleColor = MaterialTheme.colorScheme.primary,
                content = "By using trick-gps, you acknowledge and agree that this software is provided strictly for development, testing, and educational purposes. The emulation of GPS signals may violate the terms of service of third-party platforms or services. You assume all responsibility and liability for compliance with applicable local laws and regulations."
            )

            // Disclaimer of Liability
            SectionCard(
                title = "Disclaimer of Liability",
                titleColor = MaterialTheme.colorScheme.primary,
                content = "This application is provided \"as is\" under the MIT License without warranties of any kind. The developer shall not be held liable for any direct, indirect, incidental, or consequential damages (including, but not limited to, account bans, service suspensions, device issues, or legal consequences) resulting from the use or misuse of this software."
            )

            // Technical Notes
            SectionCard(
                title = "Technical Notes",
                titleColor = MaterialTheme.colorScheme.primary,
                content = "\u2022 Route generation depends on the public OSRM endpoint " +
                    "(router.project-osrm.org). Availability is not guaranteed.\n\n" +
                    "\u2022 Location search uses Android's built-in Geocoder " +
                    "(Google Play Services). Falls back to Nominatim if unavailable.\n\n" +
                    "\u2022 Active mock sessions are persisted and automatically restored " +
                    "by the foreground service.\n\n" +
                    "\u2022 The internal package namespace is " +
                    "com.charanhyper.tech.greydailer (original project name retained).\n\n" +
                    "\u2022 Map tiles are loaded from public tile servers. Data usage " +
                    "depends on zoom level and area viewed."
            )

            // Usage Guidelines
            SectionCard(
                title = "Usage Guidelines",
                titleColor = MaterialTheme.colorScheme.error,
                content = "\u2022 Do NOT use this app to cheat in location-based games, deceive " +
                    "ride-sharing/delivery services, or bypass geolocation security.\n\n" +
                    "\u2022 Sensitive banking, payment, or enterprise security apps may " +
                    "detect that Developer Options and Mock Location apps are enabled and restrict functionality.\n\n" +
                    "\u2022 Always disable mock locations in Developer Options when you are done testing to restore normal GPS behaviour.\n\n" +
                    "\u2022 Avoid using mock location services while active navigation apps are running to prevent system conflicts."
            )

            // License
            SectionCard(
                title = "License",
                titleColor = MaterialTheme.colorScheme.primary,
                content = "MIT License\n\n" +
                    "Copyright \u00a9 2026 Charan\n\n" +
                    "Permission is hereby granted, free of charge, to any person obtaining " +
                    "a copy of this software to deal in the Software without restriction, " +
                    "including without limitation the rights to use, copy, modify, merge, " +
                    "publish, distribute, sublicense, and/or sell copies of the Software.\n\n" +
                    "THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND."
            )

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    titleColor: androidx.compose.ui.graphics.Color,
    content: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = titleColor
            )
            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 22.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
