package com.example.ui.screens

import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp

import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.MeetUpXViewModel
import com.example.ui.viewmodel.PopupState
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.math.roundToInt
import androidx.compose.ui.composed
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.Dp
import com.example.ui.viewmodel.AuthState

val SketchCardShape = RoundedCornerShape(topStart = 24.dp, topEnd = 30.dp, bottomStart = 28.dp, bottomEnd = 22.dp)
val SketchButtonShape = RoundedCornerShape(topStart = 16.dp, topEnd = 20.dp, bottomStart = 18.dp, bottomEnd = 14.dp)
val SketchSmallShape = RoundedCornerShape(topStart = 12.dp, topEnd = 16.dp, bottomStart = 14.dp, bottomEnd = 10.dp)


fun Modifier.bounceClickable(
    interactionSource: MutableInteractionSource = MutableInteractionSource(),
    onClick: () -> Unit
): Modifier = composed {
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "bounce_scale"
    )
    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .clickable(
            interactionSource = interactionSource,
            indication = androidx.compose.foundation.LocalIndication.current,
            onClick = onClick
        )
}

data class CategoryData(
    val name: String,
    val icon: ImageVector,
    val color: Color
)

val categoryList = listOf(
    CategoryData("All", Icons.Rounded.Home, Color(0xFF4F46E5)),
    CategoryData("Music", Icons.Rounded.MusicNote, Color(0xFFE11D48)),
    CategoryData("Food & Dining", Icons.Rounded.Restaurant, Color(0xFFF59E0B)),
    CategoryData("Sports", Icons.Rounded.SportsSoccer, Color(0xFF10B981)),
    CategoryData("Fitness", Icons.Rounded.Favorite, Color(0xFFEC4899)),
    CategoryData("Travel", Icons.Rounded.Place, Color(0xFF06B6D4)),
    CategoryData("Education", Icons.Rounded.School, Color(0xFF3B82F6)),
    CategoryData("Technology", Icons.Rounded.Computer, Color(0xFF6366F1)),
    CategoryData("Gaming", Icons.Rounded.Gamepad, Color(0xFF8B5CF6)),
    CategoryData("Business & Networking", Icons.Rounded.BusinessCenter, Color(0xFF14B8A6)),
    CategoryData("Art & Design", Icons.Rounded.Brush, Color(0xFFF97316)),
    CategoryData("Movies & Cinema", Icons.Rounded.Movie, Color(0xFFEF4444)),
    CategoryData("Nightlife", Icons.Rounded.Nightlife, Color(0xFF9333EA)),
    CategoryData("Outdoor Activities", Icons.Rounded.Terrain, Color(0xFF84CC16)),
    CategoryData("Workshops", Icons.Rounded.Construction, Color(0xFF059669)),
    CategoryData("Community Events", Icons.Rounded.Groups, Color(0xFF3B82F6)),
    CategoryData("Family Friendly", Icons.Rounded.ChildCare, Color(0xFF10B981)),
    CategoryData("Culture & Festivals", Icons.Rounded.Festival, Color(0xFFD97706))
)

@Composable
fun QRCodeTicket(reference: String, modifier: Modifier = Modifier, color: Color = BrandIndigo) {
    Canvas(modifier = modifier) {
        val sizePx = size.minDimension
        val numBlocks = 15
        val blockSize = sizePx / numBlocks

        fun drawAnchor(x: Float, y: Float) {
            // Outer Square
            drawRect(
                color = color,
                topLeft = androidx.compose.ui.geometry.Offset(x, y),
                size = androidx.compose.ui.geometry.Size(blockSize * 4, blockSize * 4)
            )
            // Inner White block
            drawRect(
                color = Color.White,
                topLeft = androidx.compose.ui.geometry.Offset(x + blockSize, y + blockSize),
                size = androidx.compose.ui.geometry.Size(blockSize * 2, blockSize * 2)
            )
            // Center Solid square
            drawRect(
                color = color,
                topLeft = androidx.compose.ui.geometry.Offset(x + blockSize * 1.25f, y + blockSize * 1.25f),
                size = androidx.compose.ui.geometry.Size(blockSize * 1.5f, blockSize * 1.5f)
            )
        }

        // 1. Clear background
        drawRect(Color.White)

        // 2. Draw 3 main corner anchors
        drawAnchor(0f, 0f) // Top Left
        drawAnchor(sizePx - blockSize * 4, 0f) // Top Right
        drawAnchor(0f, sizePx - blockSize * 4) // Bottom Left

        // 3. Draw deterministic blocks based on reference hashcode
        val hash = reference.hashCode()
        for (row in 0 until numBlocks) {
            for (col in 0 until numBlocks) {
                val isAnchor = (row < 4 && col < 4) || (row < 4 && col >= numBlocks - 4) || (row >= numBlocks - 4 && col < 4)
                if (!isAnchor) {
                    val seed = row * 31 + col * 17 + hash
                    if (seed % 3 == 0 || seed % 7 == 0) {
                        drawRect(
                            color = color,
                            topLeft = androidx.compose.ui.geometry.Offset(col * blockSize, row * blockSize),
                            size = androidx.compose.ui.geometry.Size(blockSize, blockSize)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MeetUpXApp(viewModel: MeetUpXViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val activeTab by viewModel.activeTab.collectAsState()
    val popup by viewModel.popupMessage.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AnimatedContent(
            targetState = currentScreen,
            transitionSpec = {
                (fadeIn(animationSpec = tween(350, easing = FastOutSlowInEasing)) + scaleIn(initialScale = 0.95f, animationSpec = tween(350, easing = FastOutSlowInEasing)))
                    .togetherWith(fadeOut(animationSpec = tween(250, easing = FastOutSlowInEasing)) + scaleOut(targetScale = 0.95f, animationSpec = tween(250, easing = FastOutSlowInEasing)))
            },
            label = "screen_transition"
        ) { screen ->
            when (screen) {
                "splash" -> SplashScreen(
                    onNavigateNext = {
                        // Restore session: if Firebase already has a signed-in user, skip welcome
                        val isSignedIn = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser != null
                        viewModel.navigateTo(if (isSignedIn) "main" else "welcome")
                    }
                )
                "welcome" -> WelcomeScreen(
                    onGoToLogin = {
                        viewModel.resetAuthState()
                        viewModel.navigateTo("login")
                    },
                    onGoToRegister = {
                        viewModel.resetAuthState()
                        viewModel.navigateTo("register")
                    }
                )
                "login" -> LoginScreen(
                    viewModel = viewModel,
                    onBack = {
                        viewModel.resetAuthState()
                        viewModel.navigateTo("welcome")
                    }
                )
                "register" -> RegisterScreen(
                    viewModel = viewModel,
                    onBack = {
                        viewModel.resetAuthState()
                        viewModel.navigateTo("welcome")
                    }
                )
                "main" -> MainLayout(viewModel = viewModel)
                "details" -> EventDetailsScreen(
                    viewModel = viewModel,
                    onBack = { viewModel.navigateTo("main") }
                )
                "settings" -> SettingsScreen(
                    viewModel = viewModel,
                    onBack = { viewModel.navigateTo("main") }
                )
            }
        }

        // Custom Floating Spring Animated Popups / Notifications
        popup?.let {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                PopupNotificationCard(
                    popupState = it,
                    onDismiss = { viewModel.dismissPopup() }
                )
            }
        }
    }
}

@Composable
fun SplashScreen(onNavigateNext: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "splash")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(3000)
        onNavigateNext()
    }

    val isDark = MaterialTheme.colorScheme.background == DarkBackground
    val startBg = if (isDark) DarkBackground else CreamBackground
    val endBg = if (isDark) Color(0xFF1E1B4B) else Color(0xFFFEE2E2)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(startBg, endBg)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(180.dp)
                    .scale(pulseScale)
            ) {
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .background(BrandIndigo.copy(alpha = 0.1f), CircleShape)
                )
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .shadow(24.dp, CircleShape, spotColor = BrandIndigo)
                        .background(if (isDark) DarkSurface else Color.White, CircleShape)
                        .clip(CircleShape)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_app_icon),
                        contentDescription = "MeetUpX App Icon",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "MeetUpX",
                fontSize = 42.sp,
                fontWeight = FontWeight.ExtraBold,
                color = BrandIndigo,
                letterSpacing = (-1).sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Discover Local Events Differently",
                fontSize = 16.sp,
                color = appTextSecondary(),
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(128.dp))

            // Tag
            Surface(
                color = BrandSecondary.copy(alpha = 0.1f),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.border(1.dp, BrandSecondary.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
            ) {
                Text(
                    text = "SOCIAL & COMMUNITY",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandSecondary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
fun WelcomeScreen(onGoToLogin: () -> Unit, onGoToRegister: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Branding Top
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_app_icon),
                        contentDescription = "Logo",
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "MeetUpX",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = BrandIndigo,
                    letterSpacing = (-0.5).sp
                )
            }

            // Beautiful Central Illustration (Matching Duolingo/Headspace style)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = SketchCardShape,
                    color = appSurface(),
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .aspectRatio(1.1f)
                        .shadow(16.dp, SketchCardShape, spotColor = Color(0xFF64748B))
                        .border(borderStroke(), SketchCardShape)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_onboarding),
                        contentDescription = "MeetUpX Onboarding Illustration",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                    )
                }
            }

            // Marketing copy
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Swipe. Discover. Meet!",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary,
                    textAlign = TextAlign.Center,
                    lineHeight = 38.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "A modern social discovery platform to find local meetups, concerts, workshops, and communities via playful swipe cards.",
                    fontSize = 15.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Action CTAs
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onGoToRegister,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandIndigo),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Get Started",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                OutlinedButton(
                    onClick = onGoToLogin,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = Brush.linearGradient(listOf(BrandIndigo, BrandSecondary))
                    )
                ) {
                    Text(
                        text = "I Already Have an Account",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandIndigo
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(viewModel: MeetUpXViewModel, onBack: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            viewModel.navigateTo("main")
            viewModel.resetAuthState()
        }
    }

    val isLoading = authState is AuthState.Loading
    val errorMsg = (authState as? AuthState.Error)?.message

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // Header Bar
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .size(48.dp)
                    .background(Color.White, CircleShape)
                    .border(1.dp, Color(0xFFE2E8F0), CircleShape)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = BrandIndigo)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Welcome Back! 👋",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary
            )

            Text(
                text = "Log in to continue your journey.",
                fontSize = 15.sp,
                color = TextSecondary,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Form inputs
            Text(
                text = "EMAIL ADDRESS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = BrandIndigo,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            AuthTextField(
                value = email,
                onValueChange = { email = it },
                placeholderText = "Enter your email"
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "PASSWORD",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = BrandIndigo,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            AuthTextField(
                value = password,
                onValueChange = { password = it },
                placeholderText = "Enter your password",
                visualTransformation = PasswordVisualTransformation()
            )

            errorMsg?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = it,
                    color = BrandSecondary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = {
                    viewModel.login(
                        email = email,
                        password = password
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandIndigo),
                shape = RoundedCornerShape(16.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Log In Securely", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Forgot password? Contact platform help support.",
                fontSize = 13.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(viewModel: MeetUpXViewModel, onBack: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var university by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            viewModel.navigateTo("main")
            viewModel.resetAuthState()
        }
    }

    val isLoading = authState is AuthState.Loading
    val errorMsg = (authState as? AuthState.Error)?.message

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // Header Bar
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .size(48.dp)
                    .background(Color.White, CircleShape)
                    .border(1.dp, Color(0xFFE2E8F0), CircleShape)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = BrandIndigo)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Join the Club! 🚀",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary
            )

            Text(
                text = "Create an account to start exploring.",
                fontSize = 15.sp,
                color = TextSecondary,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Form inputs
            Text(
                text = "YOUR FULL NAME",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = BrandIndigo,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            AuthTextField(
                value = name,
                onValueChange = { name = it },
                placeholderText = "Enter your name"
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "EMAIL ADDRESS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = BrandIndigo,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            AuthTextField(
                value = email,
                onValueChange = { email = it },
                placeholderText = "Enter your email"
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "YOUR CITY / LOCATION",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = BrandIndigo,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            AuthTextField(
                value = university,
                onValueChange = { university = it },
                placeholderText = "Enter your city"
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "PASSWORD",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = BrandIndigo,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            AuthTextField(
                value = password,
                onValueChange = { password = it },
                placeholderText = "Enter your password",
                visualTransformation = PasswordVisualTransformation()
            )

            errorMsg?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = it,
                    color = BrandSecondary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = {
                    viewModel.register(
                        name = name,
                        email = email,
                        university = university,
                        password = password
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandIndigo),
                shape = RoundedCornerShape(16.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Create My Account", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}


@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholderText: String,
    visualTransformation: androidx.compose.ui.text.input.VisualTransformation = androidx.compose.ui.text.input.VisualTransformation.None
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.02f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "auth_textfield_scale"
    )

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholderText, color = Color(0xFF94A3B8), fontSize = 15.sp) },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .scale(scale),
        shape = RoundedCornerShape(16.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            focusedBorderColor = BrandIndigo,
            unfocusedBorderColor = Color(0xFFE2E8F0),
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
        ),
        visualTransformation = visualTransformation,
        interactionSource = interactionSource
    )
}

@Composable
fun MainLayout(viewModel: MeetUpXViewModel) {
    val activeTab by viewModel.activeTab.collectAsState()

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = CreamSurface,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .border(1.dp, CardBorder.copy(alpha = 0.5f), RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            ) {
                val items = listOf(
                    NavigationItem("home", "Discover", Icons.Rounded.Home),
                    NavigationItem("my_events", "My Events", Icons.Rounded.DateRange),
                    NavigationItem("create", "Create", Icons.Rounded.AddCircle),
                    NavigationItem("notifications", "Alerts", Icons.Rounded.Notifications),
                    NavigationItem("profile", "Profile", Icons.Rounded.Person)
                )

                items.forEach { item ->
                    val isSelected = activeTab == item.id
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.setActiveTab(item.id) },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                tint = if (isSelected) BrandIndigo else TextSecondary,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = {
                            Text(
                                text = item.label,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 11.sp,
                                color = if (isSelected) BrandIndigo else TextSecondary
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = BrandIndigo.copy(alpha = 0.1f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = activeTab,
                transitionSpec = {
                    (fadeIn(animationSpec = tween(250, easing = FastOutSlowInEasing)) + scaleIn(initialScale = 0.97f, animationSpec = tween(250, easing = FastOutSlowInEasing)))
                        .togetherWith(fadeOut(animationSpec = tween(150, easing = FastOutSlowInEasing)) + scaleOut(targetScale = 0.97f, animationSpec = tween(150, easing = FastOutSlowInEasing)))
                },
                label = "tab_transition"
            ) { tab ->
                when (tab) {
                    "home" -> HomeScreen(viewModel = viewModel)
                    "my_events" -> MyEventsTab(viewModel = viewModel)
                    "create" -> CreateEventScreen(viewModel = viewModel)
                    "notifications" -> NotificationsScreen(viewModel = viewModel)
                    "profile" -> ProfileScreenRedesigned(viewModel = viewModel)
                }
            }
        }
    }
}

data class NavigationItem(val id: String, val label: String, val icon: ImageVector)

@Composable
fun HomeScreen(viewModel: MeetUpXViewModel) {
    val discoverEvents by viewModel.discoverEvents.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(appBackground())
    ) {
        // TOP LOGO BAR
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_app_icon),
                        contentDescription = "Logo",
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "MeetUpX",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = BrandIndigo,
                    letterSpacing = (-0.5).sp
                )
            }

            IconButton(
                onClick = { viewModel.setActiveTab("notifications") },
                modifier = Modifier
                    .background(appSurface(), CircleShape)
                    .border(borderStroke(), CircleShape)
            ) {
                Icon(Icons.Default.Notifications, contentDescription = "Alerts", tint = BrandIndigo)
            }
        }

        // CATEGORY PILLED CHIPS - SMOOTH HORIZONTAL SCROLLING
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(categoryList) { categoryData ->
                val category = categoryData.name
                val isSelected = selectedCategory == category
                
                // Animated scale and shadow glow
                val chipScale by animateFloatAsState(
                    targetValue = if (isSelected) 1.05f else 1.0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    ),
                    label = "chip_scale"
                )
                
                val backgroundBrush = if (isSelected) {
                    Brush.linearGradient(listOf(categoryData.color, categoryData.color.copy(alpha = 0.85f)))
                } else {
                    Brush.linearGradient(listOf(appSurface(), appSurface()))
                }
                val textColor = if (isSelected) Color.White else appTextPrimary()
                val iconColor = if (isSelected) Color.White else categoryData.color
                val borderModifier = if (isSelected) Modifier else Modifier.border(borderStroke(), SketchSmallShape)

                Box(
                    modifier = Modifier
                        .scale(chipScale)
                        .shadow(
                            elevation = if (isSelected) 8.dp else 1.dp,
                            shape = SketchSmallShape,
                            spotColor = categoryData.color
                        )
                        .clip(SketchSmallShape)
                        .background(backgroundBrush)
                        .bounceClickable { viewModel.selectCategory(category) }
                        .then(borderModifier)
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = categoryData.icon,
                            contentDescription = category,
                            tint = iconColor,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = category,
                            color = textColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // CENTRAL SWIPE STACK CARDS DECK
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 24.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            if (discoverEvents.isEmpty()) {
                // Deck Empty State with Floating bounce animation
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    val infiniteTransition = rememberInfiniteTransition(label = "empty_state")
                    val bounceY by infiniteTransition.animateFloat(
                        initialValue = -10f,
                        targetValue = 10f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1800, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "bounce"
                    )
                    Box(
                        modifier = Modifier
                            .graphicsLayer { translationY = bounceY }
                            .size(120.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = appSurface(),
                            shadowElevation = 8.dp,
                            modifier = Modifier
                                .size(72.dp)
                                .border(borderStroke(), CircleShape)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Rounded.Star,
                                    contentDescription = "Cleared",
                                    tint = BrandAmber,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "You've Cleared the Deck! 🌟",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = appTextPrimary(),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No more events match this category right now. Swipe back later or tap '+' to publish your own!",
                        fontSize = 14.sp,
                        color = appTextSecondary(),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            } else {
                // Show 3 Stacked Cards (Backwards order for proper painter layering)
                val totalCards = discoverEvents.size
                val cardsToShow = discoverEvents.take(3)

                cardsToShow.reversed().forEachIndexed { indexInRender, event ->
                    // Correct logical card index (0 = top, 1 = middle, 2 = bottom)
                    val logicalIndex = cardsToShow.indexOf(event)
                    
                    TinderSwipeCard(
                        event = event,
                        cardIndex = logicalIndex,
                        onSwipeLeft = { viewModel.handleSwipeLeft(event.id) },
                        onSwipeRight = { viewModel.handleSwipeRight(event.id) },
                        onSwipeUp = { viewModel.handleSwipeUp(event.id) },
                        onCardClicked = { 
                            viewModel.selectEvent(event.id)
                            viewModel.navigateTo("details")
                        }
                    )
                }
            }
        }

        // PHYSICAL SWIPE CONTROL BUTTONS UNDERNEATH DECK
        if (discoverEvents.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // SKIP BUTTON (Coral/Rose)
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .shadow(4.dp, CircleShape)
                        .clip(CircleShape)
                        .background(appSurface())
                        .bounceClickable {
                            val topEvent = discoverEvents.firstOrNull()
                            if (topEvent != null) {
                                viewModel.handleSwipeLeft(topEvent.id)
                            }
                        }
                        .border(borderStroke(), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Skip",
                        tint = BrandSecondary,
                        modifier = Modifier.size(28.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(24.dp))
                
                // SAVE BUTTON (Gold/Amber)
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .shadow(4.dp, CircleShape)
                        .clip(CircleShape)
                        .background(appSurface())
                        .bounceClickable {
                            val topEvent = discoverEvents.firstOrNull()
                            if (topEvent != null) {
                                viewModel.handleSwipeUp(topEvent.id)
                            }
                        }
                        .border(borderStroke(), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Star,
                        contentDescription = "Save",
                        tint = BrandAmber,
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(24.dp))
                
                // JOIN BUTTON (Emerald Green)
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .shadow(4.dp, CircleShape)
                        .clip(CircleShape)
                        .background(appSurface())
                        .bounceClickable {
                            val topEvent = discoverEvents.firstOrNull()
                            if (topEvent != null) {
                                viewModel.handleSwipeRight(topEvent.id)
                            }
                        }
                        .border(borderStroke(), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Favorite,
                        contentDescription = "Join",
                        tint = BrandTertiary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun TinderSwipeCard(
    event: EventEntity,
    cardIndex: Int,
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit,
    onSwipeUp: () -> Unit,
    onCardClicked: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    // Interactive Physics variables for drag
    var draggingX by remember(event.id) { mutableStateOf(0f) }
    var draggingY by remember(event.id) { mutableStateOf(0f) }

    val animX = remember(event.id) { Animatable(0f) }
    val animY = remember(event.id) { Animatable(0f) }

    // Synchronize interactive dragging with springy releases
    val currentX = if (animX.isRunning) animX.value else draggingX
    val currentY = if (animY.isRunning) animY.value else draggingY

    // Render parameters based on stack hierarchy (0 = Top card, 1 = Middle card, 2 = Bottom card)
    val scale = when (cardIndex) {
        0 -> 1.0f
        1 -> 0.94f
        else -> 0.88f
    }
    val offsetTranslationY = when (cardIndex) {
        0 -> 0.dp
        1 -> 16.dp
        else -> 32.dp
    }
    val opacity = when (cardIndex) {
        0 -> 1.0f
        1 -> 0.85f
        else -> 0.60f
    }

    // Interactive tactile scaling and direction-aware glow feedback
    val swipeProgress = (currentX / 400f).coerceIn(-1f, 1f)
    val draggingScale = (1.0f - (kotlin.math.abs(swipeProgress) * 0.04f)).coerceIn(0.96f, 1.0f)
    val finalScale = scale * draggingScale

    val glowColor = when {
        currentX > 50 -> BrandTertiary // Join (Emerald Green)
        currentX < -50 -> BrandSecondary // Skip (Coral/Rose)
        currentY < -50 -> BrandAmber // Save (Amber)
        else -> Color.Transparent
    }
    val glowIntensity = when {
        kotlin.math.abs(currentX) > 50 -> (kotlin.math.abs(currentX) / 200f).coerceIn(0f, 0.4f)
        currentY < -50 -> (kotlin.math.abs(currentY) / 150f).coerceIn(0f, 0.4f)
        else -> 0f
    }

    // Interactive visual overlay badges while swiping left/right/up
    val rotation = (currentX / 20f).coerceIn(-15f, 15f)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .offset {
                if (cardIndex == 0) {
                    IntOffset(currentX.roundToInt(), currentY.roundToInt())
                } else {
                    IntOffset(0, 0)
                }
            }
            .offset(y = offsetTranslationY)
            .scale(finalScale)
            .rotate(if (cardIndex == 0) rotation else 0f)
            .shadow(
                elevation = if (cardIndex == 0) 12.dp else 4.dp,
                shape = SketchCardShape,
                spotColor = if (cardIndex == 0 && glowIntensity > 0f) glowColor else Color(0xFF64748B)
            )
            .clip(SketchCardShape)
            .background(appSurface())
            .clickable(enabled = cardIndex == 0) { onCardClicked() }
            .handDrawnBorder(
                color = if (cardIndex == 0 && glowIntensity > 0f) glowColor.copy(alpha = glowIntensity) else Color.Unspecified,
                shape = SketchCardShape
            )
            .then(
                if (cardIndex == 0) {
                    Modifier.pointerInput(event.id) {
                        detectDragGestures(
                            onDrag = { change, dragAmount ->
                                change.consume()
                                draggingX += dragAmount.x
                                draggingY += dragAmount.y
                            },
                            onDragEnd = {
                                coroutineScope.launch {
                                    if (draggingX > 400) {
                                        // SWIPE RIGHT -> JOIN
                                        animX.animateTo(1200f, tween(300))
                                        onSwipeRight()
                                    } else if (draggingX < -400) {
                                        // SWIPE LEFT -> SKIP
                                        animX.animateTo(-1200f, tween(300))
                                        onSwipeLeft()
                                    } else if (draggingY < -300) {
                                        // SWIPE UP -> SAVE
                                        animY.animateTo(-1200f, tween(300))
                                        onSwipeUp()
                                    } else {
                                        // RESET
                                        launch {
                                            animX.animateTo(
                                                0f,
                                                spring(
                                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                                    stiffness = Spring.StiffnessMedium
                                                )
                                            )
                                        }
                                        launch {
                                            animY.animateTo(
                                                0f,
                                                spring(
                                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                                    stiffness = Spring.StiffnessMedium
                                                )
                                            )
                                        }
                                        draggingX = 0f
                                        draggingY = 0f
                                    }
                                }
                            }
                        )
                    }
                } else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Event Illustration Header (Cover)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.1f)
                    .background(Color(0xFFFFF7ED))
            ) {
                val drawableId = when (event.imageName) {
                    "img_event_music" -> R.drawable.img_event_music
                    "img_event_food" -> R.drawable.img_event_food
                    "img_event_sport" -> R.drawable.img_event_sport
                    else -> R.drawable.img_onboarding
                }

                Image(
                    painter = painterResource(id = drawableId),
                    contentDescription = event.title,
                    modifier = Modifier.fillMaxSize()
                )

                // Category tag pill with custom category color tint
                val categoryColor = categoryList.find { it.name.equals(event.category, ignoreCase = true) }?.color ?: BrandIndigo
                Surface(
                    color = categoryColor,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.TopStart)
                ) {
                    Text(
                        text = event.category.uppercase(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            // Card Metadata Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.9f)
                    .padding(24.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = event.title,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = appTextPrimary(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = event.description,
                        fontSize = 14.sp,
                        color = appTextSecondary(),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 20.sp
                    )
                }

                HorizontalDivider(color = appBorderColor().copy(alpha = 0.5f), thickness = 2.dp)

                // Time, Location, details
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.DateRange, contentDescription = "Date", tint = BrandSecondary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = event.date, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = appTextPrimary())
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Place, contentDescription = "Location", tint = BrandIndigo, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = event.location.substringBefore(","),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = appTextPrimary(),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        // SWIPE INTERACTIVE OVERLAYS
        if (cardIndex == 0 && currentX > 100) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BrandTertiary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    color = BrandTertiary,
                    shape = RoundedCornerShape(16.dp),
                    shadowElevation = 8.dp
                ) {
                    Text(
                        text = "JOIN EVENT 🎉",
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
                    )
                }
            }
        }

        if (cardIndex == 0 && currentX < -100) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BrandSecondary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    color = BrandSecondary,
                    shape = RoundedCornerShape(16.dp),
                    shadowElevation = 8.dp
                ) {
                    Text(
                        text = "SKIP 💨",
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
                    )
                }
            }
        }

        if (cardIndex == 0 && currentY < -100) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BrandAmber.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    color = BrandAmber,
                    shape = RoundedCornerShape(16.dp),
                    shadowElevation = 8.dp
                ) {
                    Text(
                        text = "SAVE ⭐",
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun EventDetailsScreen(viewModel: MeetUpXViewModel, onBack: () -> Unit) {
    val selectedEventId by viewModel.selectedEventId.collectAsState()
    val allEvents by viewModel.allEvents.collectAsState(initial = emptyList())
    val event = allEvents.find { it.id == selectedEventId } ?: return

    val isJoined by viewModel.isJoinedFlow(event.id).collectAsState(initial = false)
    val isSaved by viewModel.isSavedFlow(event.id).collectAsState(initial = false)
    val ticketQrRef by viewModel.getQrCodeReferenceForEvent(event.id).collectAsState()

    val context = LocalContext.current
    var showShareSuccessDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(appBackground())
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            // Illustration Cover Header
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .background(if (MaterialTheme.colorScheme.background == DarkBackground) DarkBackground else Color(0xFFFEF3C7))
                ) {
                    val drawableId = when (event.imageName) {
                        "img_event_music" -> R.drawable.img_event_music
                        "img_event_food" -> R.drawable.img_event_food
                        "img_event_sport" -> R.drawable.img_event_sport
                        else -> R.drawable.img_onboarding
                    }

                    Image(
                        painter = painterResource(id = drawableId),
                        contentDescription = event.title,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Control Buttons Top
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                            .align(Alignment.TopStart),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier
                                .background(appSurface(), CircleShape)
                                .border(borderStroke(), CircleShape)
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = BrandIndigo)
                        }

                        IconButton(
                            onClick = {
                                if (isSaved) {
                                    viewModel.unsaveEvent(event.id)
                                } else {
                                    viewModel.handleSwipeUp(event.id)
                                }
                            },
                            modifier = Modifier
                                .background(appSurface(), CircleShape)
                                .border(borderStroke(), CircleShape)
                        ) {
                            Icon(
                                imageVector = if (isSaved) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = BrandSecondary
                            )
                        }
                    }
                }
            }

            // Meta Details card
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = BrandIndigo.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = event.category.uppercase(),
                                fontSize = 11.sp,
                                color = BrandIndigo,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = event.title,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = appTextPrimary(),
                        lineHeight = 34.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Date Place Card details
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = SketchCardShape,
                        color = appSurface(),
                        border = borderStroke()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(BrandSecondary.copy(alpha = 0.15f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Rounded.DateRange, contentDescription = "Date", tint = BrandSecondary, modifier = Modifier.size(20.dp))
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(text = "Date & Time", fontSize = 12.sp, color = appTextSecondary())
                                    Text(text = "${event.date} • ${event.time}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = appTextPrimary())
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(BrandIndigo.copy(alpha = 0.15f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Rounded.Place, contentDescription = "Place", tint = BrandIndigo, modifier = Modifier.size(20.dp))
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(text = "Event Location", fontSize = 12.sp, color = appTextSecondary())
                                    Text(text = event.location, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = appTextPrimary())
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "About this Event",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = appTextPrimary()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = event.description,
                        fontSize = 15.sp,
                        color = appTextSecondary(),
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // ADVANCED FEATURE - DYNAMIC QR TICKET VIEW
                    if (isJoined) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(8.dp, SketchCardShape, spotColor = BrandIndigo),
                            shape = SketchCardShape,
                            colors = CardDefaults.cardColors(containerColor = appSurface()),
                            border = borderStroke()
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Your Digital QR Ticket",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = BrandIndigo
                                )
                                Text(
                                    text = "Present this QR ticket at the event entrance.",
                                    fontSize = 12.sp,
                                    color = appTextSecondary(),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                                )

                                // Custom QR code canvas element
                                QRCodeTicket(
                                    reference = ticketQrRef,
                                    modifier = Modifier
                                        .size(160.dp)
                                        .border(2.5.dp, BrandIndigo, RoundedCornerShape(8.dp))
                                        .padding(8.dp)
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = "TICKET REF: $ticketQrRef",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = appTextPrimary()
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            // Simulated standard share action
                                            val shareIntent = Intent().apply {
                                                action = Intent.ACTION_SEND
                                                putExtra(Intent.EXTRA_TEXT, "Hey! I'm attending '${event.title}' on MeetUpX. Join me! My ticket ref: $ticketQrRef")
                                                type = "text/plain"
                                            }
                                            context.startActivity(Intent.createChooser(shareIntent, "Share Event Ticket"))
                                            showShareSuccessDialog = true
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = BrandIndigo),
                                        shape = SketchButtonShape,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(Icons.Rounded.Share, contentDescription = "Share", tint = Color.White)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Share Ticket")
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            viewModel.cancelJoinedEvent(event.id)
                                        },
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = BrandSecondary),
                                        border = ButtonDefaults.outlinedButtonBorder.copy(
                                            brush = Brush.linearGradient(listOf(BrandSecondary, BrandSecondary))
                                        ),
                                        shape = SketchButtonShape
                                    ) {
                                        Text("Cancel Booking")
                                    }
                                }
                            }
                        }
                    } else {
                        // User has not joined -> Display swipe-to-join action card
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = appSurface(),
                            shape = SketchCardShape,
                            border = borderStroke()
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Ready to attend this meetup? 👋",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BrandIndigo
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = {
                                        viewModel.handleSwipeRight(event.id)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = BrandIndigo),
                                    shape = SketchButtonShape
                                ) {
                                    Text("Join Event & Get Ticket", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(48.dp))
                }
            }
        }
    }

    if (showShareSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showShareSuccessDialog = false },
            confirmButton = {
                TextButton(onClick = { showShareSuccessDialog = false }) {
                    Text("Awesome!", color = BrandIndigo)
                }
            },
            title = { Text("Ticket Reference Shared! 📨") },
            text = { Text("Your QR ticket reference $ticketQrRef was successfully formatted for sharing. Bring your friends along!") },
            shape = RoundedCornerShape(24.dp),
            containerColor = Color.White
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEventScreen(viewModel: MeetUpXViewModel) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("July 30, 2026") }
    var time by remember { mutableStateOf("6:00 PM") }
    var category by remember { mutableStateOf("Music") }
    var imageSelection by remember { mutableStateOf("img_event_music") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(appBackground())
            .padding(24.dp)
    ) {
        item {
            Text(
                text = "Host a Meetup 📢",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = appTextPrimary()
            )
            Text(
                text = "Bring people together in your community. Fill details below.",
                fontSize = 15.sp,
                color = appTextSecondary(),
                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
            )
        }

        item {
            Card(
                shape = SketchCardShape,
                colors = CardDefaults.cardColors(containerColor = appSurface()),
                border = borderStroke(),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    // Category Picker
                    Text(
                        text = "EVENT CATEGORY",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandIndigo
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Filter out "All" category since you can't create an "All" category event
                        val selectableCategories = categoryList.filter { it.name != "All" }
                        items(selectableCategories) { catData ->
                            val cat = catData.name
                            val isSelected = category == cat
                            Box(
                                modifier = Modifier
                                    .bounceClickable {
                                        category = cat
                                        imageSelection = when (cat) {
                                            "Music" -> "img_event_music"
                                            "Food & Dining" -> "img_event_food"
                                            "Sports" -> "img_event_sport"
                                            else -> "img_onboarding"
                                        }
                                    }
                                    .shadow(if (isSelected) 6.dp else 0.dp, SketchSmallShape, spotColor = catData.color)
                                    .clip(SketchSmallShape)
                                    .background(if (isSelected) catData.color else appBackground())
                                    .then(if (isSelected) Modifier else Modifier.border(borderStroke(), SketchSmallShape))
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = catData.icon,
                                        contentDescription = cat,
                                        tint = if (isSelected) Color.White else catData.color,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = cat,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else appTextPrimary()
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Title
                    Text(text = "EVENT TITLE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BrandIndigo)
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = SketchButtonShape,
                        placeholder = { Text("e.g. Sourdough Pizza Making") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = appTextPrimary(),
                            unfocusedTextColor = appTextPrimary(),
                            focusedPlaceholderColor = appTextSecondary(),
                            unfocusedPlaceholderColor = appTextSecondary(),
                            focusedBorderColor = BrandIndigo,
                            unfocusedBorderColor = appBorderColor(),
                            focusedContainerColor = appBackground(),
                            unfocusedContainerColor = appBackground()
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Description
                    Text(text = "DESCRIPTION", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BrandIndigo)
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = desc,
                        onValueChange = { desc = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = SketchButtonShape,
                        placeholder = { Text("Describe your event and why people should join...") },
                        maxLines = 4,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = appTextPrimary(),
                            unfocusedTextColor = appTextPrimary(),
                            focusedPlaceholderColor = appTextSecondary(),
                            unfocusedPlaceholderColor = appTextSecondary(),
                            focusedBorderColor = BrandIndigo,
                            unfocusedBorderColor = appBorderColor(),
                            focusedContainerColor = appBackground(),
                            unfocusedContainerColor = appBackground()
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Location
                    Text(text = "EVENT LOCATION", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BrandIndigo)
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = SketchButtonShape,
                        placeholder = { Text("e.g. Central Arts Square") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = appTextPrimary(),
                            unfocusedTextColor = appTextPrimary(),
                            focusedPlaceholderColor = appTextSecondary(),
                            unfocusedPlaceholderColor = appTextSecondary(),
                            focusedBorderColor = BrandIndigo,
                            unfocusedBorderColor = appBorderColor(),
                            focusedContainerColor = appBackground(),
                            unfocusedContainerColor = appBackground()
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Date & Time Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "DATE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BrandIndigo)
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = date,
                                onValueChange = { date = it },
                                shape = SketchButtonShape,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = appTextPrimary(),
                                    unfocusedTextColor = appTextPrimary(),
                                    focusedPlaceholderColor = appTextSecondary(),
                                    unfocusedPlaceholderColor = appTextSecondary(),
                                    focusedBorderColor = BrandIndigo,
                                    unfocusedBorderColor = appBorderColor(),
                                    focusedContainerColor = appBackground(),
                                    unfocusedContainerColor = appBackground()
                                )
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "TIME", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BrandIndigo)
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = time,
                                onValueChange = { time = it },
                                shape = SketchButtonShape,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = appTextPrimary(),
                                    unfocusedTextColor = appTextPrimary(),
                                    focusedPlaceholderColor = appTextSecondary(),
                                    unfocusedPlaceholderColor = appTextSecondary(),
                                    focusedBorderColor = BrandIndigo,
                                    unfocusedBorderColor = appBorderColor(),
                                    focusedContainerColor = appBackground(),
                                    unfocusedContainerColor = appBackground()
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            if (title.isNotEmpty() && desc.isNotEmpty() && location.isNotEmpty()) {
                                viewModel.createEvent(
                                    title = title,
                                    description = desc,
                                    category = category,
                                    date = date,
                                    time = time,
                                    location = location,
                                    imageName = imageSelection
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandIndigo),
                        shape = SketchButtonShape
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "Publish", tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Publish to Discovery", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun MyEventsTab(viewModel: MeetUpXViewModel) {
    var selectedSubTab by remember { mutableStateOf("Joined") }
    val joinedEvents by viewModel.joinedEventsDetails.collectAsState()
    val savedEvents by viewModel.savedEventsDetails.collectAsState()
    val myCreated by viewModel.myCreatedEventsDetails.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(appBackground())
    ) {
        // Tab Header Titles
        Text(
            text = "My Bookings 🎫",
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            color = appTextPrimary(),
            modifier = Modifier.padding(start = 24.dp, top = 24.dp, end = 24.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Custom segmented sub tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val subTabs = listOf("Joined", "Saved", "Created")
            subTabs.forEach { tab ->
                val isSelected = selectedSubTab == tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(SketchButtonShape)
                        .background(if (isSelected) BrandIndigo else appSurface())
                        .border(borderStroke(), SketchButtonShape)
                        .clickable { selectedSubTab = tab }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tab,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White else appTextPrimary()
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // List requirement rendering depending on selected booking deck
        when (selectedSubTab) {
            "Joined" -> {
                if (joinedEvents.isEmpty()) {
                    EmptyListPlaceholder(
                        title = "No Joined Events Yet",
                        body = "Swipe right on discovery cards to join events. Once joined, your ticket appears here!"
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(joinedEvents) { event ->
                            InteractiveTicketCard(
                                event = event,
                                actionLabel = "View QR Ticket",
                                onAction = { 
                                    viewModel.selectEvent(event.id)
                                    viewModel.navigateTo("details")
                                },
                                onCancel = { viewModel.cancelJoinedEvent(event.id) }
                            )
                        }
                    }
                }
            }
            "Saved" -> {
                if (savedEvents.isEmpty()) {
                    EmptyListPlaceholder(
                        title = "No Saved Bookmarks",
                        body = "Swipe up on cards to bookmark listings for later review."
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(savedEvents) { event ->
                            InteractiveTicketCard(
                                event = event,
                                actionLabel = "Attend Event",
                                onAction = { 
                                    viewModel.selectEvent(event.id)
                                    viewModel.navigateTo("details")
                                },
                                onCancel = { viewModel.unsaveEvent(event.id) },
                                cancelIcon = Icons.Rounded.Delete
                            )
                        }
                    }
                }
            }
            "Created" -> {
                if (myCreated.isEmpty()) {
                    EmptyListPlaceholder(
                        title = "No Created Meetups",
                        body = "Hosting events is simple. Tap the '+' create tab to create your own community meetup!"
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(myCreated) { event ->
                            InteractiveTicketCard(
                                event = event,
                                actionLabel = "Manage Meetup",
                                onAction = { 
                                    viewModel.selectEvent(event.id)
                                    viewModel.navigateTo("details")
                                },
                                onCancel = { viewModel.deleteCreatedEvent(event) },
                                cancelIcon = Icons.Rounded.Delete
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InteractiveTicketCard(
    event: EventEntity,
    actionLabel: String,
    onAction: () -> Unit,
    onCancel: () -> Unit,
    cancelIcon: ImageVector = Icons.Rounded.Close
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, SketchCardShape, spotColor = Color(0xFF64748B)),
        shape = SketchCardShape,
        colors = CardDefaults.cardColors(containerColor = appSurface()),
        border = borderStroke()
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category mini icon
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(BrandIndigo.copy(alpha = 0.15f), SketchSmallShape),
                    contentAlignment = Alignment.Center
                ) {
                    val icon = when (event.category) {
                        "Music" -> Icons.Rounded.PlayArrow
                        "Food" -> Icons.Rounded.ShoppingCart
                        else -> Icons.Rounded.Star
                    }
                    Icon(icon, contentDescription = "Category", tint = BrandIndigo)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = event.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = appTextPrimary(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(text = "${event.date} • ${event.time}", fontSize = 12.sp, color = appTextSecondary())
                    Text(text = event.location.substringBefore(","), fontSize = 12.sp, color = BrandIndigo, fontWeight = FontWeight.Bold)
                }

                IconButton(
                    onClick = onCancel,
                    modifier = Modifier
                        .background(Color(0xFFFEF2F2), CircleShape)
                        .size(32.dp)
                ) {
                    Icon(cancelIcon, contentDescription = "Cancel", tint = BrandSecondary, modifier = Modifier.size(16.dp))
                }
            }

            // CTA Button divider
            HorizontalDivider(color = appBorderColor().copy(alpha = 0.3f), thickness = 1.dp)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAction() }
                    .background(BrandIndigo.copy(alpha = 0.05f))
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = actionLabel, color = BrandIndigo, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Rounded.ArrowForward, contentDescription = null, tint = BrandIndigo, modifier = Modifier.size(14.dp))
                }
            }
        }
    }
}

@Composable
fun EmptyListPlaceholder(title: String, body: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(BrandIndigo.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Rounded.Info, contentDescription = "Empty", tint = BrandIndigo, modifier = Modifier.size(32.dp))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = appTextPrimary())
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = body, fontSize = 14.sp, color = appTextSecondary(), textAlign = TextAlign.Center)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(viewModel: MeetUpXViewModel) {
    val user by viewModel.currentUser.collectAsState()
    val joined by viewModel.joinedEvents.collectAsState(initial = emptyList())
    val saved by viewModel.savedEvents.collectAsState(initial = emptyList())
    val allEvents by viewModel.allEvents.collectAsState(initial = emptyList())
    val myCreated = allEvents.filter { it.isCustom }

    var isEditing by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf("") }
    var editBio by remember { mutableStateOf("") }
    var editUni by remember { mutableStateOf("") }

    // Init fields on editing toggle
    LaunchedEffect(isEditing) {
        if (isEditing && user != null) {
            editName = user?.name ?: ""
            editBio = user?.bio ?: ""
            editUni = user?.university ?: ""
        }
    }

    // Staggered entry animation triggers
    var startAnimation by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        startAnimation = true
    }
    
    val profileAlpha by animateFloatAsState(targetValue = if (startAnimation) 1f else 0f, animationSpec = tween(400, delayMillis = 50), label = "profile_alpha")
    val statsAlpha by animateFloatAsState(targetValue = if (startAnimation) 1f else 0f, animationSpec = tween(400, delayMillis = 150), label = "stats_alpha")
    val interestsAlpha by animateFloatAsState(targetValue = if (startAnimation) 1f else 0f, animationSpec = tween(400, delayMillis = 250), label = "interests_alpha")
    val timelineAlpha by animateFloatAsState(targetValue = if (startAnimation) 1f else 0f, animationSpec = tween(400, delayMillis = 350), label = "timeline_alpha")
    val shortcutsAlpha by animateFloatAsState(targetValue = if (startAnimation) 1f else 0f, animationSpec = tween(400, delayMillis = 450), label = "shortcuts_alpha")

    val animJoinedOffset = remember { Animatable(120f) }
    val animJoinedAlpha = remember { Animatable(0f) }
    val animSavedOffset = remember { Animatable(120f) }
    val animSavedAlpha = remember { Animatable(0f) }
    val animCreatedOffset = remember { Animatable(120f) }
    val animCreatedAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        launch {
            kotlinx.coroutines.delay(100)
            animJoinedOffset.animateTo(
                targetValue = 0f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }
        launch {
            kotlinx.coroutines.delay(100)
            animJoinedAlpha.animateTo(1f, tween(300))
        }

        launch {
            kotlinx.coroutines.delay(220)
            animSavedOffset.animateTo(
                targetValue = 0f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }
        launch {
            kotlinx.coroutines.delay(220)
            animSavedAlpha.animateTo(1f, tween(300))
        }

        launch {
            kotlinx.coroutines.delay(340)
            animCreatedOffset.animateTo(
                targetValue = 0f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }
        launch {
            kotlinx.coroutines.delay(340)
            animCreatedAlpha.animateTo(1f, tween(300))
        }
    }

    // Subtle floating animation for avatar
    val infiniteTransition = rememberInfiniteTransition(label = "avatar_transition")
    val avatarOffsetY by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "avatar_y"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(appBackground())
            .padding(horizontal = 24.dp),
        contentPadding = PaddingValues(top = 24.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // TOP HEADER BAR
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "My Profile 👤",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = appTextPrimary()
                )

                IconButton(
                    onClick = {
                        viewModel.logout()
                        viewModel.navigateTo("welcome")
                    },
                    modifier = Modifier
                        .background(appSurface(), CircleShape)
                        .border(borderStroke(), CircleShape)
                ) {
                    Icon(Icons.Rounded.ExitToApp, contentDescription = "Log out", tint = BrandSecondary)
                }
            }
        }

        // AVATAR & BIO SECTION (CARD WITH FLOAT MOTION ON AVATAR)
        item {
            Card(
                shape = SketchCardShape,
                colors = CardDefaults.cardColors(containerColor = appSurface()),
                border = borderStroke(),
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer { alpha = profileAlpha }
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Profile Circle with Floating Motion
                    Box(
                        modifier = Modifier
                            .graphicsLayer { translationY = avatarOffsetY }
                            .size(105.dp)
                            .background(
                                Brush.linearGradient(listOf(BrandIndigo.copy(alpha = 0.15f), BrandSecondary.copy(alpha = 0.15f))),
                                CircleShape
                            )
                            .border(androidx.compose.foundation.BorderStroke(2.dp, BrandIndigo.copy(alpha = 0.4f)), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (user?.name ?: "S").take(1).uppercase(),
                            fontSize = 40.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = BrandIndigo
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (!isEditing) {
                        Text(
                            text = user?.name ?: "Georgian Explorer",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = appTextPrimary()
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 6.dp)
                        ) {
                            Icon(Icons.Rounded.Place, contentDescription = null, tint = BrandSecondary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = user?.university ?: "Tbilisi, Georgia",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandSecondary
                            )
                        }

                        Text(
                            text = user?.bio ?: "Discovering events across Tbilisi, Batumi, and the beautiful regions of Georgia! 🏔️✨",
                            fontSize = 14.sp,
                            color = appTextSecondary(),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 12.dp, bottom = 16.dp)
                        )

                        Button(
                            onClick = { isEditing = true },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandIndigo.copy(alpha = 0.08f)),
                            elevation = null,
                            shape = SketchButtonShape
                        ) {
                            Icon(Icons.Rounded.Edit, contentDescription = "Edit", tint = BrandIndigo, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Edit Bio & Details", color = BrandIndigo, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    } else {
                        // Editable text fields (Dynamic border + scale transitions built-in)
                        AnimatedOutlinedTextField(
                            value = editName,
                            onValueChange = { editName = it },
                            label = { Text("Display Name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        AnimatedOutlinedTextField(
                            value = editUni,
                            onValueChange = { editUni = it },
                            label = { Text("City / Location") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        AnimatedOutlinedTextField(
                            value = editBio,
                            onValueChange = { editBio = it },
                            label = { Text("Bio description") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = false
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    viewModel.updateProfile(editName, editBio, editUni)
                                    isEditing = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = BrandIndigo),
                                modifier = Modifier.weight(1f),
                                shape = SketchButtonShape
                            ) {
                                Text("Save details")
                            }

                            OutlinedButton(
                                onClick = { isEditing = false },
                                modifier = Modifier.weight(1f),
                                shape = SketchButtonShape
                            ) {
                                Text("Cancel")
                            }
                        }
                    }
                }
            }
        }

        // STATS ROW COUNTER CARD WITH ANIMATED COUNTERS (STAGGERED AND BOUNCY WITH HAND-DRAWN JITTER BORDERS)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Joined Stats Card
                Surface(
                    shape = SketchCardShape,
                    color = appSurface(),
                    modifier = Modifier
                        .weight(1f)
                        .graphicsLayer {
                            translationY = animJoinedOffset.value
                            alpha = animJoinedAlpha.value
                        }
                        .handDrawnBorder(shape = SketchCardShape)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AnimatedCounter(targetValue = joined.size, color = BrandIndigo)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Joined",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = appTextSecondary()
                        )
                    }
                }

                // Saved Stats Card
                Surface(
                    shape = SketchCardShape,
                    color = appSurface(),
                    modifier = Modifier
                        .weight(1f)
                        .graphicsLayer {
                            translationY = animSavedOffset.value
                            alpha = animSavedAlpha.value
                        }
                        .handDrawnBorder(shape = SketchCardShape)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AnimatedCounter(targetValue = saved.size, color = BrandSecondary)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Saved",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = appTextSecondary()
                        )
                    }
                }

                // Created Stats Card
                Surface(
                    shape = SketchCardShape,
                    color = appSurface(),
                    modifier = Modifier
                        .weight(1f)
                        .graphicsLayer {
                            translationY = animCreatedOffset.value
                            alpha = animCreatedAlpha.value
                        }
                        .handDrawnBorder(shape = SketchCardShape)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AnimatedCounter(targetValue = myCreated.size, color = BrandAmber)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Created",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = appTextSecondary()
                        )
                    }
                }
            }
        }

        // MY INTERESTS SECTION (PILL-BASED MINI CHIPS)
        item {
            Card(
                shape = SketchCardShape,
                colors = CardDefaults.cardColors(containerColor = appSurface()),
                border = borderStroke(),
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer { alpha = interestsAlpha }
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "My Interests 🎯",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = appTextPrimary()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    val interests = listOf(
                        "Music 🎵", "Food & Dining 🍕", "Sports ⚽", 
                        "Travel ✈️", "Technology 💻", "Gaming 🎮"
                    )
                    
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        interests.forEach { tag ->
                            Surface(
                                shape = SketchSmallShape,
                                color = BrandIndigo.copy(alpha = 0.06f),
                                border = androidx.compose.foundation.BorderStroke(2.5.dp, BrandIndigo.copy(alpha = 0.4f)),
                                modifier = Modifier.bounceClickable {}
                            ) {
                                Text(
                                    text = tag,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BrandIndigo,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // MY ACTIVITY TIMELINE SECTION
        item {
            Card(
                shape = SketchCardShape,
                colors = CardDefaults.cardColors(containerColor = appSurface()),
                border = borderStroke(),
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer { alpha = timelineAlpha }
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "My Activity Timeline 📈",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = appTextPrimary()
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    val timelineActivities = listOf(
                        "Joined Tbilisi Rooftop Synth & Techno 🎵" to "Just now",
                        "Created Svaneti Expedition & Alpine Hike ⛰️" to "2 hours ago",
                        "Saved Traditional Supra & Khinkali Class 🍷" to "Yesterday"
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        timelineActivities.forEachIndexed { index, (activity, timeAgo) ->
                            Row(modifier = Modifier.fillMaxWidth()) {
                                // Timeline bullet & line column
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.width(20.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .background(
                                                if (index == 0) BrandIndigo else BrandSecondary,
                                                CircleShape
                                            )
                                    )
                                    if (index < timelineActivities.lastIndex) {
                                        Box(
                                            modifier = Modifier
                                                .width(2.dp)
                                                .height(35.dp)
                                                .background(appBorderColor())
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = activity,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = appTextPrimary()
                                    )
                                    Text(
                                        text = timeAgo,
                                        fontSize = 11.sp,
                                        color = appTextSecondary(),
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // SHORTCUT PANEL (SETTINGS LINK)
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer { alpha = shortcutsAlpha }
                    .bounceClickable { viewModel.navigateTo("settings") },
                shape = SketchCardShape,
                color = appSurface(),
                border = borderStroke()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Settings, contentDescription = "Settings", tint = BrandIndigo)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = "App Settings & Theme Options", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = appTextPrimary())
                    }
                    Icon(Icons.Rounded.ArrowForward, contentDescription = null, tint = appTextSecondary())
                }
            }
        }
    }
}

@Composable
fun AnimatedCounter(targetValue: Int, color: Color) {
    var count by remember { mutableIntStateOf(0) }
    LaunchedEffect(targetValue) {
        val steps = targetValue.coerceAtLeast(0)
        if (steps > 0) {
            for (i in 0..steps) {
                count = i
                kotlinx.coroutines.delay(60)
            }
        } else {
            count = 0
        }
    }
    Text(
        text = count.toString(),
        fontSize = 28.sp,
        fontWeight = FontWeight.ExtraBold,
        color = color
    )
}

@Composable
fun FlowRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier) {
        androidx.compose.ui.layout.Layout(
            content = content
        ) { measurables, constraints ->
            val placeables = measurables.map { it.measure(constraints) }
            val gapPx = horizontalArrangement.spacing.roundToPx()
            val lineGapPx = verticalArrangement.spacing.roundToPx()
            
            val lines = mutableListOf<List<androidx.compose.ui.layout.Placeable>>()
            var currentLine = mutableListOf<androidx.compose.ui.layout.Placeable>()
            var currentWidth = 0
            
            placeables.forEach { placeable ->
                if (currentWidth + placeable.width + gapPx > constraints.maxWidth && currentLine.isNotEmpty()) {
                    lines.add(currentLine)
                    currentLine = mutableListOf()
                    currentWidth = 0
                }
                currentLine.add(placeable)
                currentWidth += placeable.width + gapPx
            }
            if (currentLine.isNotEmpty()) {
                lines.add(currentLine)
            }
            
            val totalHeight = lines.sumOf { line -> line.maxOf { it.height } } + (lines.size - 1).coerceAtLeast(0) * lineGapPx
            
            layout(constraints.maxWidth, totalHeight) {
                var y = 0
                lines.forEach { line ->
                    val lineHeight = line.maxOf { it.height }
                    var x = 0
                    line.forEach { placeable ->
                        placeable.placeRelative(x, y + (lineHeight - placeable.height) / 2)
                        x += placeable.width + gapPx
                    }
                    y += lineHeight + lineGapPx
                }
            }
        }
    }
}


@Composable
fun NotificationsScreen(viewModel: MeetUpXViewModel) {
    val notifications by viewModel.notifications.collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(appBackground())
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "My Inbox 🔔",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = appTextPrimary()
            )

            if (notifications.isNotEmpty()) {
                TextButton(onClick = { viewModel.clearAllNotifications() }) {
                    Text("Clear All", color = BrandSecondary, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (notifications.isEmpty()) {
            EmptyListPlaceholder(
                title = "All caught up!",
                body = "Your inbox is currently empty. Notifications for event bookings will appear here."
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(notifications) { notification ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.markNotificationRead(notification.id) },
                        shape = SketchCardShape,
                        colors = CardDefaults.cardColors(
                            containerColor = if (notification.isRead) appSurface() else BrandIndigo.copy(alpha = 0.12f)
                        ),
                        border = borderStroke()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = notification.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = appTextPrimary()
                                )
                                if (!notification.isRead) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(BrandIndigo, CircleShape)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = notification.message,
                                fontSize = 13.sp,
                                color = appTextSecondary(),
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun borderStroke() = androidx.compose.foundation.BorderStroke(
    width = 2.5.dp,
    color = appBorderColor()
)

@Composable
fun Image(
    painter: androidx.compose.ui.graphics.painter.Painter,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    androidx.compose.foundation.Image(
        painter = painter,
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = androidx.compose.ui.layout.ContentScale.Crop
    )
}

@Composable
fun PopupNotificationCard(popupState: PopupState, onDismiss: () -> Unit) {
    val containerColor = when (popupState.type) {
        "success" -> BrandTertiary
        "save" -> BrandAmber
        else -> BrandIndigo
    }

    LaunchedEffect(popupState) {
        kotlinx.coroutines.delay(4000)
        onDismiss()
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(16.dp, SketchCardShape, spotColor = containerColor),
        shape = SketchCardShape,
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = popupState.title,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    color = Color.White
                )
                Text(
                    text = popupState.message,
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
            }
        }
    }
}

@Composable
fun SettingsScreen(viewModel: MeetUpXViewModel, onBack: () -> Unit) {
    val themePref by viewModel.themePreference.collectAsState()
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(appBackground())
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .background(appSurface(), CircleShape)
                        .border(borderStroke(), CircleShape)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = BrandIndigo)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "App Settings",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = appTextPrimary()
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Theme selection section
            Text(
                text = "APP THEME & APPEARANCE",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = BrandIndigo,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            val options = listOf(
                Triple("light", "Light Mode", Icons.Rounded.LightMode),
                Triple("dark", "Dark Mode", Icons.Rounded.DarkMode),
                Triple("system", "System Default", Icons.Rounded.SettingsSuggest)
            )
            
            options.forEach { (mode, label, icon) ->
                val isSelected = themePref == mode
                val scale by animateFloatAsState(
                    targetValue = if (isSelected) 1.02f else 1.0f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
                    label = "theme_scale"
                )
                
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .scale(scale)
                        .clickable { viewModel.setThemePreference(mode) },
                    shape = SketchCardShape,
                    color = if (isSelected) BrandIndigo.copy(alpha = 0.08f) else appSurface(),
                    border = androidx.compose.foundation.BorderStroke(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) BrandIndigo else appBorderColor()
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = if (isSelected) BrandIndigo else appTextSecondary()
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = label,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                                color = appTextPrimary(),
                                fontSize = 15.sp
                            )
                        }
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Selected",
                                tint = BrandIndigo
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Georgia Location Section
            Text(
                text = "GEORGIA REGIONAL ADAPTATION",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = BrandIndigo,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = SketchCardShape,
                color = appSurface(),
                border = borderStroke()
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Place,
                        contentDescription = null,
                        tint = BrandSecondary
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Regional Context Active",
                            fontWeight = FontWeight.Bold,
                            color = appTextPrimary(),
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Default city is Tbilisi, Georgia. Swipes and newly created events are customized for the Svaneti, Batumi, Kutaisi and Kakheti regions.",
                            color = appTextSecondary(),
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Logout Action
            Button(
                onClick = {
                    viewModel.logout()
                    viewModel.navigateTo("welcome")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandSecondary),
                shape = SketchButtonShape
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Logout, contentDescription = null, tint = Color.White)
                    Text("Log Out Account", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun appBackground(): Color = MaterialTheme.colorScheme.background

@Composable
fun appSurface(): Color = MaterialTheme.colorScheme.surface

@Composable
fun appTextPrimary(): Color = if (MaterialTheme.colorScheme.background == DarkBackground) Color.White else TextPrimary

@Composable
fun appTextSecondary(): Color = if (MaterialTheme.colorScheme.background == DarkBackground) Color(0xFF94A3B8) else TextSecondary

@Composable
fun appBorderColor(): Color = if (MaterialTheme.colorScheme.background == DarkBackground) DarkCardBorder else CardBorder

@Composable
fun AnimatedOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: @Composable (() -> Unit)? = null,
    label: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    visualTransformation: androidx.compose.ui.text.input.VisualTransformation = androidx.compose.ui.text.input.VisualTransformation.None
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.02f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "textfield_scale"
    )
    
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = placeholder,
        label = label,
        modifier = modifier
            .scale(scale)
            .shadow(if (isFocused) 6.dp else 0.dp, shape = SketchButtonShape),
        interactionSource = interactionSource,
        shape = SketchButtonShape,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = appTextPrimary(),
            unfocusedTextColor = appTextPrimary(),
            focusedPlaceholderColor = appTextSecondary(),
            unfocusedPlaceholderColor = appTextSecondary(),
            focusedBorderColor = BrandIndigo,
            unfocusedBorderColor = appBorderColor(),
            focusedContainerColor = appSurface(),
            unfocusedContainerColor = appSurface(),
            focusedLabelColor = BrandIndigo,
            unfocusedLabelColor = appTextSecondary()
        ),
        singleLine = singleLine,
        visualTransformation = visualTransformation
    )
}

fun Modifier.handDrawnBorder(
    width: Dp = 2.5.dp,
    color: Color = Color.Unspecified,
    shape: androidx.compose.foundation.shape.CornerBasedShape = SketchCardShape
): Modifier = this.composed {
    val borderColor = if (color == Color.Unspecified) appBorderColor() else color
    val infiniteTransition = rememberInfiniteTransition(label = "handDrawnJitter")
    val jitterFrame by infiniteTransition.animateValue(
        initialValue = 0,
        targetValue = 3,
        typeConverter = Int.VectorConverter,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 220, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "jitterFrame"
    )

    drawWithContent {
        drawContent()

        val strokeWidthPx = width.toPx()
        val w = size.width
        val h = size.height
        val overshoot = strokeWidthPx * 1.5f

        val offset1 = when (jitterFrame) {
            0 -> 1.5f
            1 -> -1.0f
            2 -> 0.5f
            else -> -0.5f
        }
        val offset2 = when (jitterFrame) {
            0 -> -1.0f
            1 -> 1.5f
            2 -> -0.5f
            else -> 0.5f
        }
        val offset3 = when (jitterFrame) {
            0 -> 0.8f
            1 -> -1.2f
            2 -> 1.2f
            else -> -0.8f
        }
        val offset4 = when (jitterFrame) {
            0 -> -1.2f
            1 -> 0.8f
            2 -> -0.8f
            else -> 1.2f
        }

        val topY1 = 0f + offset1
        val topY2 = 0f + offset2
        val rightX1 = w + offset2
        val rightX2 = w + offset3
        val bottomY1 = h + offset3
        val bottomY2 = h + offset4
        val leftX1 = 0f + offset4
        val leftX2 = 0f + offset1

        // Top border
        drawLine(
            color = borderColor,
            start = Offset(-overshoot, topY1),
            end = Offset(w + overshoot, topY2),
            strokeWidth = strokeWidthPx,
            cap = StrokeCap.Round
        )
        // Right border
        drawLine(
            color = borderColor,
            start = Offset(rightX1, -overshoot),
            end = Offset(rightX2, h + overshoot),
            strokeWidth = strokeWidthPx,
            cap = StrokeCap.Round
        )
        // Bottom border
        drawLine(
            color = borderColor,
            start = Offset(w + overshoot, bottomY1),
            end = Offset(-overshoot, bottomY2),
            strokeWidth = strokeWidthPx,
            cap = StrokeCap.Round
        )
        // Left border
        drawLine(
            color = borderColor,
            start = Offset(leftX1, h + overshoot),
            end = Offset(leftX2, -overshoot),
            strokeWidth = strokeWidthPx,
            cap = StrokeCap.Round
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreenRedesigned(viewModel: MeetUpXViewModel) {
    val user by viewModel.currentUser.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Profile Header / Details
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp)
            ) {
                // Circular frame with MeetUpX custom icon or user avatar
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .background(BrandIndigo.copy(alpha = 0.1f))
                        .border(3.dp, BrandIndigo, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (user != null) {
                        Text(
                            text = (user?.name ?: "S").take(1).uppercase(),
                            fontSize = 44.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = BrandIndigo
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Rounded.Person,
                            contentDescription = "Profile Loading",
                            tint = BrandIndigo,
                            modifier = Modifier.size(56.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                if (user != null) {
                    Text(
                        text = user?.name ?: "Georgian Explorer",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = user?.email ?: "",
                        fontSize = 14.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .background(Color(0xFFF1F5F9), RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Place,
                            contentDescription = "Location",
                            tint = BrandIndigo,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = user?.university ?: "Tbilisi, Georgia",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandIndigo
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = user?.bio ?: "",
                        fontSize = 13.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                } else {
                    Text(
                        text = "Loading profile...",
                        fontSize = 16.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Menu Items
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ProfileMenuItem(
                    icon = Icons.Rounded.Favorite,
                    title = "Favorites",
                    onClick = {
                        viewModel.setMyEventsSubTab("Saved")
                        viewModel.setActiveTab("my_events")
                    }
                )

                ProfileMenuItem(
                    icon = Icons.Rounded.Settings,
                    title = "Settings",
                    onClick = {
                        viewModel.navigateTo("settings")
                    }
                )
            }

            // Bottom Red Outline "Log Out" Button
            OutlinedButton(
                onClick = {
                    viewModel.logout()
                    viewModel.navigateTo("welcome")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFFEF4444)
                ),
                border = BorderStroke(1.dp, Color(0xFFFEE2E2))
            ) {
                Text(
                    text = "Log Out",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(Color.White, RoundedCornerShape(16.dp))
            .border(1.dp, Color(0xFFF1F5F9), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(BrandIndigo.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = BrandIndigo,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Rounded.ArrowForward,
            contentDescription = null,
            tint = Color(0xFF94A3B8),
            modifier = Modifier.size(16.dp)
        )
    }
}

