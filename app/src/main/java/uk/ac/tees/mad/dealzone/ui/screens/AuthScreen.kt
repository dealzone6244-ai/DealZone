package uk.ac.tees.mad.dealzone.ui.screens

import android.content.res.Configuration
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uk.ac.tees.mad.dealzone.ui.theme.*

@Composable
fun AuthScreen(
    uiState: AuthUiState       = AuthUiState.Idle,
    onSignIn: (String, String) -> Unit  = { _, _ -> },
    onRegister: (String, String) -> Unit = { _, _ -> },
    onAuthSuccess: () -> Unit           = {}
) {
    var isSignIn       by remember { mutableStateOf(true) }
    var email          by remember { mutableStateOf("") }
    var password       by remember { mutableStateOf("") }
    var confirmPwd     by remember { mutableStateOf("") }
    var showPassword   by remember { mutableStateOf(false) }
    var showConfirmPwd by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val scrollState  = rememberScrollState()

    val emailError    = if (email.isNotEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
        "Enter a valid email address" else null
    val passwordError = if (password.isNotEmpty() && password.length < 6)
        "Password must be at least 6 characters" else null
    val confirmError  = if (!isSignIn && confirmPwd.isNotEmpty() && confirmPwd != password)
        "Passwords do not match" else null

    val isFormValid = email.isNotEmpty() && emailError == null &&
            password.isNotEmpty() && passwordError == null &&
            (isSignIn || (confirmPwd.isNotEmpty() && confirmError == null))

    val isLoading = uiState is AuthUiState.Loading

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) onAuthSuccess()
    }

    val cardEnter = remember { Animatable(0f) }
    LaunchedEffect(Unit) { cardEnter.animateTo(1f, tween(700, easing = FastOutSlowInEasing)) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.background
                    )
                )
            )
    ) {
        Box(
            modifier = Modifier
                .size(260.dp)
                .offset(x = 160.dp, y = (-60).dp)
                .alpha(0.08f)
                .clip(CircleShape)
                .background(Orange500)
        )
        Box(
            modifier = Modifier
                .size(180.dp)
                .offset(x = (-60).dp, y = 500.dp)
                .alpha(0.06f)
                .clip(CircleShape)
                .background(Orange600)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .alpha(cardEnter.value),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Orange500, Orange600),
                            start  = Offset(0f, 0f),
                            end    = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text("⚡", fontSize = 34.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text  = "DealZone",
                style = MaterialTheme.typography.displayMedium.copy(
                    color        = MaterialTheme.colorScheme.onBackground,
                    fontWeight   = FontWeight.ExtraBold,
                    letterSpacing = (-0.5).sp
                )
            )
            Text(
                text  = "Best deals, every single day",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            )

            Spacer(modifier = Modifier.height(36.dp))

            Card(
                modifier  = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape     = RoundedCornerShape(24.dp),
                colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
            ) {
                Column(
                    modifier            = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AuthTabRow(
                        isSignIn    = isSignIn,
                        onTabChange = {
                            isSignIn   = it
                            confirmPwd = ""
                        }
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    AnimatedContent(
                        targetState  = isSignIn,
                        transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(200)) },
                        label        = "title"
                    ) { signIn ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text  = if (signIn) "Welcome back!" else "Create account",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    color      = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text      = if (signIn) "Sign in to access your saved deals"
                                else "Join to start saving exclusive deals",
                                style     = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                ),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    DealZoneTextField(
                        value         = email,
                        onValueChange = { email = it },
                        label         = "Email address",
                        leadingIcon   = Icons.Outlined.Email,
                        keyboardType  = KeyboardType.Email,
                        imeAction     = ImeAction.Next,
                        onImeAction   = { focusManager.moveFocus(FocusDirection.Down) },
                        isError       = emailError != null,
                        errorText     = emailError
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    DealZoneTextField(
                        value            = password,
                        onValueChange    = { password = it },
                        label            = "Password",
                        leadingIcon      = Icons.Outlined.Lock,
                        isPassword       = true,
                        showPassword     = showPassword,
                        onTogglePassword = { showPassword = !showPassword },
                        imeAction        = if (isSignIn) ImeAction.Done else ImeAction.Next,
                        onImeAction      = {
                            if (isSignIn) focusManager.clearFocus()
                            else focusManager.moveFocus(FocusDirection.Down)
                        },
                        isError          = passwordError != null,
                        errorText        = passwordError
                    )

                    AnimatedVisibility(
                        visible = !isSignIn,
                        enter   = expandVertically() + fadeIn(),
                        exit    = shrinkVertically() + fadeOut()
                    ) {
                        Column {
                            Spacer(modifier = Modifier.height(14.dp))
                            DealZoneTextField(
                                value            = confirmPwd,
                                onValueChange    = { confirmPwd = it },
                                label            = "Confirm password",
                                leadingIcon      = Icons.Outlined.Lock,
                                isPassword       = true,
                                showPassword     = showConfirmPwd,
                                onTogglePassword = { showConfirmPwd = !showConfirmPwd },
                                imeAction        = ImeAction.Done,
                                onImeAction      = { focusManager.clearFocus() },
                                isError          = confirmError != null,
                                errorText        = confirmError
                            )
                        }
                    }

                    AnimatedVisibility(
                        visible = uiState is AuthUiState.Error,
                        enter   = expandVertically() + fadeIn(),
                        exit    = shrinkVertically() + fadeOut()
                    ) {
                        if (uiState is AuthUiState.Error) {
                            Column {
                                Spacer(modifier = Modifier.height(14.dp))
                                ErrorBanner(message = uiState.message)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick  = {
                            if (isSignIn) onSignIn(email.trim(), password)
                            else onRegister(email.trim(), password)
                        },
                        enabled  = isFormValid && !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape    = RoundedCornerShape(14.dp),
                        colors   = ButtonDefaults.buttonColors(
                            containerColor         = Orange500,
                            disabledContainerColor = Orange500.copy(alpha = 0.4f),
                            contentColor           = Color.White
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color       = Color.White,
                                modifier    = Modifier.size(22.dp),
                                strokeWidth = 2.5.dp
                            )
                        } else {
                            Text(
                                text  = if (isSignIn) "Sign In" else "Create Account",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontSize = 16.sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text  = if (isSignIn) "Don't have an account? " else "Already have an account? ",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        )
                        Text(
                            text  = if (isSignIn) "Register" else "Sign In",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color      = Orange500,
                                fontWeight = FontWeight.SemiBold
                            ),
                            modifier = Modifier.clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication        = null
                            ) { isSignIn = !isSignIn; confirmPwd = "" }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text      = "By continuing, you agree to our Terms & Privacy Policy",
                style     = MaterialTheme.typography.labelMedium.copy(
                    color     = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                    textAlign = TextAlign.Center
                ),
                textAlign = TextAlign.Center,
                modifier  = Modifier.padding(horizontal = 40.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun AuthTabRow(isSignIn: Boolean, onTabChange: (Boolean) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        val indicatorOffset by animateFloatAsState(
            targetValue   = if (isSignIn) 0f else 1f,
            animationSpec = tween(300, easing = FastOutSlowInEasing),
            label         = "tabIndicator"
        )
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val halfWidth = maxWidth / 2
            Box(
                modifier = Modifier
                    .width(halfWidth)
                    .fillMaxHeight()
                    .offset(x = halfWidth * indicatorOffset)
                    .padding(4.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Brush.horizontalGradient(listOf(Orange500, Orange600)))
            )
        }
        Row(modifier = Modifier.fillMaxSize()) {
            listOf("Sign In", "Register").forEachIndexed { idx, label ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication        = null
                        ) { onTabChange(idx == 0) },
                    contentAlignment = Alignment.Center
                ) {
                    val selected = (idx == 0) == isSignIn
                    Text(
                        text  = label,
                        style = MaterialTheme.typography.labelLarge.copy(
                            color      = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun DealZoneTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    isPassword: Boolean          = false,
    showPassword: Boolean        = false,
    onTogglePassword: () -> Unit = {},
    keyboardType: KeyboardType   = KeyboardType.Text,
    imeAction: ImeAction         = ImeAction.Next,
    onImeAction: () -> Unit      = {},
    isError: Boolean             = false,
    errorText: String?           = null
) {
    Column {
        OutlinedTextField(
            value         = value,
            onValueChange = onValueChange,
            label         = {
                Text(
                    label,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = if (isError) ErrorRed else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                )
            },
            leadingIcon = {
                Icon(
                    imageVector        = leadingIcon,
                    contentDescription = null,
                    tint               = if (isError) ErrorRed else Orange500.copy(alpha = 0.8f),
                    modifier           = Modifier.size(20.dp)
                )
            },
            trailingIcon = if (isPassword) ({
                IconButton(onClick = onTogglePassword) {
                    Icon(
                        imageVector        = if (showPassword) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = if (showPassword) "Hide password" else "Show password",
                        tint               = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        modifier           = Modifier.size(20.dp)
                    )
                }
            }) else null,
            isError              = isError,
            visualTransformation = if (isPassword && !showPassword) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions      = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
            keyboardActions      = KeyboardActions(
                onNext = { onImeAction() },
                onDone = { onImeAction() }
            ),
            modifier   = Modifier.fillMaxWidth(),
            singleLine = true,
            shape      = RoundedCornerShape(12.dp),
            colors     = OutlinedTextFieldDefaults.colors(
                focusedBorderColor      = Orange500,
                unfocusedBorderColor    = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                errorBorderColor        = ErrorRed,
                focusedTextColor        = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor      = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                errorTextColor          = MaterialTheme.colorScheme.onSurface,
                cursorColor             = Orange500,
                focusedContainerColor   = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.04f),
                unfocusedContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.02f),
                errorContainerColor     = ErrorRed.copy(alpha = 0.05f)
            )
        )
        if (isError && errorText != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text     = errorText,
                style    = MaterialTheme.typography.labelMedium.copy(color = ErrorRed),
                modifier = Modifier.padding(start = 12.dp)
            )
        }
    }
}

@Composable
private fun ErrorBanner(message: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(ErrorRed.copy(alpha = 0.12f))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector        = Icons.Filled.ErrorOutline,
            contentDescription = null,
            tint               = ErrorRed,
            modifier           = Modifier.size(18.dp)
        )
        Text(
            text  = message,
            style = MaterialTheme.typography.bodyMedium.copy(color = ErrorRed)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Auth - Sign In (Light)", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(showBackground = true, showSystemUi = true, name = "Auth - Sign In (Dark)", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun AuthScreenSignInPreview() {
    DealZoneTheme {
        AuthScreen(uiState = AuthUiState.Idle)
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Auth - Error State (Light)", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(showBackground = true, showSystemUi = true, name = "Auth - Error State (Dark)", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun AuthScreenErrorPreview() {
    DealZoneTheme {
        AuthScreen(uiState = AuthUiState.Error("Incorrect password. Please try again."))
    }
}