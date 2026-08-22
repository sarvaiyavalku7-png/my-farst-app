package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserProfile
import com.example.ui.theme.ValkuAccentBlue
import com.example.ui.theme.ValkuAccentEmerald
import com.example.ui.theme.ValkuBackground
import com.example.ui.theme.ValkuCardBorder
import com.example.ui.theme.ValkuPrimary
import com.example.ui.theme.ValkuSecondary
import com.example.ui.theme.ValkuSurface
import com.example.ui.theme.ValkuSurfaceVariant
import com.example.ui.theme.ValkuTextMuted
import com.example.ui.theme.ValkuTextPrimary
import com.example.ui.theme.ValkuTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthModal(
    currentUser: UserProfile,
    onDismiss: () -> Unit,
    onLoginWithPhone: (phone: String, name: String) -> Unit,
    onLoginWithGoogle: (email: String, name: String) -> Unit,
    onLoginWithEmail: (email: String, name: String) -> Unit,
    onUpdateProfile: (name: String, handle: String, bio: String, phone: String, email: String) -> Unit,
    onLogout: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Phone, 1: Google, 2: Email, 3: Profile Edit
    var isEditingProfile by remember { mutableStateOf(false) }

    // Phone Login States
    var phoneNumber by remember { mutableStateOf(currentUser.phoneNumber.replace("+91 ", "")) }
    var userNameInput by remember { mutableStateOf(currentUser.name) }
    var otpCode by remember { mutableStateOf("") }
    var otpSent by remember { mutableStateOf(false) }

    // Email Login States
    var emailInput by remember { mutableStateOf(currentUser.email) }
    var passwordInput by remember { mutableStateOf("") }

    // Profile Edit States
    var editName by remember { mutableStateOf(currentUser.name) }
    var editHandle by remember { mutableStateOf(currentUser.handle) }
    var editBio by remember { mutableStateOf(currentUser.bio) }
    var editPhone by remember { mutableStateOf(currentUser.phoneNumber) }
    var editEmail by remember { mutableStateOf(currentUser.email) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = ValkuSurface,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .navigationBarsPadding()
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
                .testTag("auth_modal_content")
        ) {
            // Header with Close Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF3B82F6), Color(0xFFA855F7))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "User Auth",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (currentUser.isLoggedIn && !isEditingProfile) "Account & Profile" else "Valku Sarvaiya Login",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = ValkuTextPrimary
                        )
                        Text(
                            text = if (currentUser.isLoggedIn) "Logged in as ${currentUser.name}" else "Sign in to sync chats, feed & AI",
                            fontSize = 12.sp,
                            color = ValkuTextSecondary
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(ValkuSurfaceVariant)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = ValkuTextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // If user is currently logged in and not editing, show active profile card with Switch / Edit options
            if (currentUser.isLoggedIn && !isEditingProfile) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = ValkuSurfaceVariant),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.linearGradient(listOf(Color(0xFF3B82F6), Color(0xFFA855F7)))
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            listOf(Color(0xFF2563EB), Color(0xFF9333EA))
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = currentUser.name.take(2).uppercase(),
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = currentUser.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp,
                                        color = ValkuTextPrimary
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Verified User",
                                        tint = Color(0xFF38BDF8),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Text(
                                    text = currentUser.handle,
                                    fontSize = 13.sp,
                                    color = Color(0xFF60A5FA),
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "Auth method: ${currentUser.loginMethod}",
                                    fontSize = 11.sp,
                                    color = ValkuTextMuted
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = ValkuCardBorder)
                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Bio: ${currentUser.bio}",
                            fontSize = 13.sp,
                            color = ValkuTextSecondary
                        )

                        if (currentUser.phoneNumber.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "📱 Phone: ${currentUser.phoneNumber}",
                                fontSize = 12.sp,
                                color = ValkuTextMuted
                            )
                        }

                        if (currentUser.email.isNotBlank()) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "✉️ Email: ${currentUser.email}",
                                fontSize = 12.sp,
                                color = ValkuTextMuted
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { isEditingProfile = true },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("edit_profile_btn"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF1E293B)
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit Profile",
                                    tint = ValkuPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Edit Profile", color = ValkuPrimary, fontSize = 13.sp)
                            }

                            Button(
                                onClick = { onLogout() },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("logout_btn"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF3F1515)
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Logout,
                                    contentDescription = "Logout",
                                    tint = Color(0xFFFF6B6B),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Sign Out", color = Color(0xFFFF6B6B), fontSize = 13.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Switch Account / Connect Another Method",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ValkuTextSecondary
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            // If editing profile
            if (isEditingProfile) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = ValkuSurfaceVariant),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(Color(0xFF3B82F6), Color(0xFFA855F7))))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Edit Profile Information",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = ValkuTextPrimary
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = editName,
                            onValueChange = { editName = it },
                            label = { Text("Display Name") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = ValkuTextPrimary,
                                unfocusedTextColor = ValkuTextPrimary,
                                focusedBorderColor = ValkuPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = editHandle,
                            onValueChange = { editHandle = it },
                            label = { Text("Username Handle (@)") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = ValkuTextPrimary,
                                unfocusedTextColor = ValkuTextPrimary,
                                focusedBorderColor = ValkuPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = editBio,
                            onValueChange = { editBio = it },
                            label = { Text("Bio / Status") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = ValkuTextPrimary,
                                unfocusedTextColor = ValkuTextPrimary,
                                focusedBorderColor = ValkuPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = editPhone,
                            onValueChange = { editPhone = it },
                            label = { Text("Phone Number") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = ValkuTextPrimary,
                                unfocusedTextColor = ValkuTextPrimary,
                                focusedBorderColor = ValkuPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = editEmail,
                            onValueChange = { editEmail = it },
                            label = { Text("Email Address") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = ValkuTextPrimary,
                                unfocusedTextColor = ValkuTextPrimary,
                                focusedBorderColor = ValkuPrimary
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            TextButton(
                                onClick = { isEditingProfile = false },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Cancel", color = ValkuTextMuted)
                            }

                            Button(
                                onClick = {
                                    onUpdateProfile(editName, editHandle, editBio, editPhone, editEmail)
                                    isEditingProfile = false
                                },
                                modifier = Modifier.weight(1.5f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                            ) {
                                Text("Save Profile", color = Color(0xFF090814), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else {
                // Auth Tab Selection
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = ValkuSurfaceVariant,
                    contentColor = ValkuPrimary,
                    indicator = { tabPositions ->
                        SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = ValkuPrimary
                        )
                    },
                    modifier = Modifier.clip(RoundedCornerShape(12.dp))
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Phone OTP", fontSize = 13.sp, fontWeight = FontWeight.SemiBold) },
                        icon = { Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Google", fontSize = 13.sp, fontWeight = FontWeight.SemiBold) },
                        icon = { Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("Email", fontSize = 13.sp, fontWeight = FontWeight.SemiBold) },
                        icon = { Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                when (selectedTab) {
                    0 -> {
                        // Phone OTP Login Section
                        Column {
                            OutlinedTextField(
                                value = userNameInput,
                                onValueChange = { userNameInput = it },
                                label = { Text("Your Full Name") },
                                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = ValkuPrimary) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = ValkuTextPrimary,
                                    unfocusedTextColor = ValkuTextPrimary,
                                    focusedBorderColor = ValkuPrimary
                                )
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(ValkuSurfaceVariant)
                                        .border(1.dp, ValkuCardBorder, RoundedCornerShape(12.dp))
                                        .padding(horizontal = 12.dp, vertical = 15.dp)
                                ) {
                                    Text(
                                        text = "🇮🇳 +91",
                                        fontWeight = FontWeight.Bold,
                                        color = ValkuTextPrimary,
                                        fontSize = 14.sp
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                OutlinedTextField(
                                    value = phoneNumber,
                                    onValueChange = { if (it.length <= 10) phoneNumber = it },
                                    label = { Text("10-digit Mobile Number") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = ValkuTextPrimary,
                                        unfocusedTextColor = ValkuTextPrimary,
                                        focusedBorderColor = ValkuPrimary
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            if (!otpSent) {
                                Button(
                                    onClick = {
                                        otpSent = true
                                        otpCode = "4826" // Simulated instant auto-code
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .testTag("send_otp_btn"),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = ValkuAccentBlue
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Send,
                                        contentDescription = "Send OTP",
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Send 4-Digit OTP Code", fontWeight = FontWeight.Bold)
                                }
                            } else {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            text = "OTP sent to +91 $phoneNumber. Auto-filled: 4826 📲",
                                            color = ValkuAccentEmerald,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        OutlinedTextField(
                                            value = otpCode,
                                            onValueChange = { if (it.length <= 4) otpCode = it },
                                            label = { Text("Enter OTP Code") },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(10.dp),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedTextColor = ValkuTextPrimary,
                                                unfocusedTextColor = ValkuTextPrimary,
                                                focusedBorderColor = ValkuAccentEmerald
                                            )
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Button(
                                    onClick = {
                                        onLoginWithPhone("+91 $phoneNumber", userNameInput)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .testTag("verify_otp_btn"),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = ValkuAccentEmerald
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Verify",
                                        tint = Color.Black,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Verify & Continue", color = Color.Black, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    1 -> {
                        // Google One-Tap Sign In
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Sign in with your Google Workspace or Gmail account for instant cloud backup and Gemini AI sync.",
                                fontSize = 13.sp,
                                color = ValkuTextSecondary,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onLoginWithGoogle("valku.creator@gmail.com", "Valku Sarvaiya")
                                    },
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = ValkuSurfaceVariant),
                                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(Color(0xFF4285F4), Color(0xFF34A853))))
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(CircleShape)
                                            .background(Color.White),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "G",
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 22.sp,
                                            color = Color(0xFF4285F4)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(14.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Continue as Valku Sarvaiya",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = ValkuTextPrimary
                                        )
                                        Text(
                                            text = "valku.creator@gmail.com",
                                            fontSize = 12.sp,
                                            color = ValkuTextSecondary
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onLoginWithGoogle("shaileshsarvaiya39@gmail.com", "Shailesh Sarvaiya")
                                    },
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = ValkuSurfaceVariant),
                                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(Color(0xFFFBBC05), Color(0xFFEA4335))))
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(CircleShape)
                                            .background(Color.White),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "S",
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 22.sp,
                                            color = Color(0xFFEA4335)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(14.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Continue as Shailesh Sarvaiya",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = ValkuTextPrimary
                                        )
                                        Text(
                                            text = "shaileshsarvaiya39@gmail.com",
                                            fontSize = 12.sp,
                                            color = ValkuTextSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    2 -> {
                        // Email & Password Sign In
                        Column {
                            OutlinedTextField(
                                value = emailInput,
                                onValueChange = { emailInput = it },
                                label = { Text("Email Address") },
                                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = ValkuPrimary) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = ValkuTextPrimary,
                                    unfocusedTextColor = ValkuTextPrimary,
                                    focusedBorderColor = ValkuPrimary
                                )
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = passwordInput,
                                onValueChange = { passwordInput = it },
                                label = { Text("Password") },
                                visualTransformation = PasswordVisualTransformation(),
                                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = ValkuPrimary) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = ValkuTextPrimary,
                                    unfocusedTextColor = ValkuTextPrimary,
                                    focusedBorderColor = ValkuPrimary
                                )
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    val name = emailInput.substringBefore("@").replace(".", " ").capitalize()
                                    onLoginWithEmail(emailInput, name)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("email_login_btn"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ValkuSecondary
                                )
                            ) {
                                Text("Sign In with Email", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
