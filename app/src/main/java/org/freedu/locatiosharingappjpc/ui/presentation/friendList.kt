package org.freedu.locatiosharingappjpc.ui.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import org.freedu.locatiosharingappjpc.Screen
import org.freedu.locatiosharingappjpc.model.AppUsers
import org.freedu.locatiosharingappjpc.ui.theme.GreenError
import org.freedu.locatiosharingappjpc.ui.theme.GreenLight
import org.freedu.locatiosharingappjpc.ui.theme.GreenPrimary
import org.freedu.locatiosharingappjpc.ui.theme.GreenPrimaryLight
import org.freedu.locatiosharingappjpc.ui.theme.TextDark
import org.freedu.locatiosharingappjpc.ui.theme.White
import org.freedu.locatiosharingappjpc.ui.viewModel.FriendListViewModel

@Composable
fun FriendListScreen(
    viewModel: FriendListViewModel,
    navController: NavController,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val currentUser by viewModel.currentUser.collectAsState()
    val friends by viewModel.friends.collectAsState()
    var isMenuExpanded by remember { mutableStateOf(false) }

    // Permission Launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.values.all { it }) {
            viewModel.startLocationUpdates()
        } else {
            Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    Box(
        modifier = Modifier.fillMaxSize()
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(GreenLight) // Changed from Color(0xFFE8F5E9) to GreenLight
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // My Profile Card
            currentUser?.let { user ->
                UserCard(
                    user = user,
                    title = "My Profile",
                    containerColor = GreenPrimaryLight, // Changed from Color(0xFF4CAF50) to GreenPrimaryLight
                    onClick = {
                        val lat = user.latitude
                        val lng = user.longitude

                        // Check if location is actually valid
                        if (lat != null && lng != null && lat != 0.0 && lng != 0.0) {
                            navController.navigate(Screen.MapSingle.createRoute(lat, lng))
                        } else {
                            Toast.makeText(
                                context,
                                "Location not available for this user",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Friend List",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = GreenPrimary // Changed from Color(0xFF2E7D32) to GreenPrimary
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                items(friends) { friend ->
                    UserCard(
                        user = friend,
                        containerColor = White, // Changed from Color.White to White
                        contentColor = TextDark, // Changed from Color.Black to TextDark
                        onClick = {
                            val lat = friend.latitude
                            val lng = friend.longitude

                            // Block navigation if location is N/A or 0.0
                            if (lat != null && lng != null && lat != 0.0 && lng != 0.0) {
                                navController.navigate(Screen.MapSingle.createRoute(lat, lng))
                            } else {
                                Toast.makeText(context, "Location not set for this user (N/A)", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }
        }

        // FAB Menu
        Column(
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (isMenuExpanded) {
                // Inside FriendListScreen FAB Column
                SmallFloatingActionButton(
                    onClick = { navController.navigate(Screen.Profile.route) }, // Go to Profile
                    containerColor = GreenPrimaryLight // Changed from Color(0xFF4CAF50) to GreenPrimaryLight
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = White) // Changed to White
                }
                SmallFloatingActionButton(
                    onClick = { navController.navigate(Screen.MapAll.route) },
                    containerColor = GreenPrimaryLight // Changed from Color(0xFF4CAF50) to GreenPrimaryLight
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = White) // Changed to White
                }
                SmallFloatingActionButton(
                    onClick = { viewModel.logout(); onLogout() },
                    containerColor = GreenError // Changed from Color.Red to GreenError
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = null,
                        tint = White // Changed to White
                    )
                }
            }
            FloatingActionButton(
                onClick = { isMenuExpanded = !isMenuExpanded },
                containerColor = GreenPrimary, // Changed from Color(0xFF2E7D32) to GreenPrimary
                contentColor = White // Changed from Color.White to White
            ) {
                Icon(
                    if (isMenuExpanded) Icons.Default.Close else Icons.Default.Menu,
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
fun UserCard(
    user: AppUsers,
    title: String? = null,
    containerColor: Color,
    contentColor: Color = White, // Changed default from Color.White to White
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (title != null) {
                Text(
                    text = title,
                    fontWeight = FontWeight.ExtraBold,
                    color = contentColor,
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 4.dp)
                )
            }
            Text(user.username.ifEmpty { "Unknown User" }, fontWeight = FontWeight.Bold, color = contentColor)
            Text(user.email, fontSize = 12.sp, color = contentColor.copy(alpha = 0.8f))

            Row(
                modifier = Modifier.padding(top = 10.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Formatting logic: strictly N/A if null or 0.0
                @SuppressLint("DefaultLocale")
                fun formatLoc(value: Double?): String {
                    return if (value == null || value == 0.0) "N/A"
                    else String.format("%.4f", value)
                }

                Text("Lat: ${formatLoc(user.latitude)}", fontSize = 13.sp, color = contentColor)
                Text("Lng: ${formatLoc(user.longitude)}", fontSize = 13.sp, color = contentColor)
            }
        }
    }
}




