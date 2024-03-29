package com.example.gratitudejar

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.CalendarView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import java.util.Calendar


class MainActivity : ComponentActivity(), SensorEventListener {
    private val viewModel: ShakeDetectionViewModel by viewModels()
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var lastUpdate: Long = 0
    private val shakeThreshold = 10
    private lateinit var navController: NavController
    private var screen4Opened = false // Flag to track if screen 4 has been opened

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize sensor manager and accelerometer sensor
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)


        setContent {
            navController = rememberNavController()
            NavHost(navController = navController as NavHostController, startDestination = "screen1") {
                composable("screen1") {
                    GratitudeJarScreen1(viewModel = viewModel, onAddGratitudeClick = {
                        navController.navigate("screen2")
                    }, onHomeClick = {
                        navController.navigate("screen1")
                    })
                }
                composable("screen2") {
                    GratitudeJarScreen2(onLocationSelected = {
                        navController.navigate("screen3")
                    }, onHomeClick = { navController.navigate("screen1")})
                }
                composable("screen3") {
                    GratitudeJarScreen3(onSubmitGratitude = {
                        navController.navigate("screen1")
                    }, onHomeClick = { navController.navigate("screen1")})
                }
                composable("screen4") {
                    GratitudeJarScreen4(viewModel = viewModel, onHomeClick = {
                            navController.navigate("screen1")
                            screen4Opened = false
                    })
                }
            }
        }
    }
    // SensorEventListener methods
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onSensorChanged(event: SensorEvent?) {
        // Check if navController is initialized
        if (::navController.isInitialized) {
            val currentRoute = navController.currentDestination?.route
            Log.d("CurrentRoute", "Current Route: $currentRoute")

            if (currentRoute == "screen1") {
                if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
                    val currentTime = System.currentTimeMillis()

                    if ((currentTime - lastUpdate) > shakeThreshold) {
                        val x = event.values[0]
                        val y = event.values[1]
                        val z = event.values[2]

                        val acceleration = Math.sqrt((x * x + y * y + z * z).toDouble()).toFloat()
                        if (acceleration > shakeThreshold && !screen4Opened) {
                            // Set shake detected flag in ViewModel
                            viewModel.shakeDetected = true
                            Log.d("ShakeDetection", "Shake detected!")
                            navController.navigate("screen4")
                            screen4Opened = true
                        }

                        lastUpdate = currentTime
                    }
                }
            }
        } else {
            Log.e("NavController", "NavController is not initialized yet")
        }
    }

    override fun onResume() {
        super.onResume()
        // Register the sensor listener onResume
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        // Unregister the sensor listener onPause to avoid battery drain
        sensorManager.unregisterListener(this)
    }
}

@Composable
fun GratitudeJarScreen1(viewModel: ShakeDetectionViewModel, onAddGratitudeClick: () -> Unit, onHomeClick: () -> Unit) {
    if (viewModel.shakeDetected) {
        viewModel.shakeDetected = false
    }
    else {
        Surface(color = Color.Black) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Header(dark = true)
                Spacer(modifier = Modifier.weight(0.05f)) // Pushes content to the top half
                WelcomeMessage()
                Spacer(modifier = Modifier.weight(0.05f)) // Pushes content to the top half
                GratitudeAndReminderChips()
                Spacer(modifier = Modifier.weight(0.05f)) // Pushes content to the top half
                GratitudePrompt()
                Spacer(modifier = Modifier.weight(0.05f)) // Pushes content to the top half
                AddGratitudeButton(onClick = onAddGratitudeClick)
                Spacer(modifier = Modifier.weight(0.05f)) // Pushes content to the top half
                ShakeIcon()
                SurpriseText()
                Spacer(modifier = Modifier.weight(0.05f)) // Pushes content to the bottom half
                Footer(onHomeClick = onHomeClick, dark = true)
            }
        }
    }
}

// Screen 2
@Composable
fun GratitudeJarScreen2(onLocationSelected: () -> Unit, onHomeClick: () -> Unit) {
    // Define your Google Maps preview here
    // Use third-party libraries like Accompanist for Google Maps integration
    // Placeholder for demonstration purposes
    Surface(color = Color.Black){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Header(dark = true)
            Spacer(modifier = Modifier.weight(0.05f)) // Pushes content to the top half
            GMaps(onLocationSelected = onLocationSelected)
            Spacer(modifier = Modifier.weight(0.05f)) // Pushes content to the top half
            Footer(onHomeClick = onHomeClick, dark = true)
        }
    }
}

@Composable
fun ShowDatePicker(onDateSelected: (Long) -> Unit) {
    var selectedDate by remember { mutableStateOf(System.currentTimeMillis()) }

    Surface(
        color = MaterialTheme.colorScheme.surface, // Set your desired color here
        modifier = Modifier.wrapContentWidth()
    ) {
        AndroidView(
            factory = { context ->
                CalendarView(context).apply {
                    setOnDateChangeListener { _, year, month, dayOfMonth ->
                        val calendar = Calendar.getInstance()
                        calendar.set(Calendar.YEAR, year)
                        calendar.set(Calendar.MONTH, month)
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                        selectedDate = calendar.timeInMillis
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun GratitudeJarScreen3(onSubmitGratitude: () -> Unit, onHomeClick: () -> Unit) {
    var gratitudeMessage by remember { mutableStateOf(TextFieldValue()) }
    var selectedDate by remember { mutableStateOf(System.currentTimeMillis()) }

    Surface(color = Color.Black) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Header(dark = true)
            Spacer(modifier = Modifier.weight(0.05f))

            Text(
                text = "What are you grateful for here?",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.weight(0.05f))

            OutlinedTextField(
                value = gratitudeMessage,
                onValueChange = { newValue ->
                    if (newValue.text.length <= 2000) {
                        gratitudeMessage = newValue
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Gratitude Message") },
                placeholder = { Text("I am grateful for...") },
                maxLines = 3,
                textStyle = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.onPrimary)
            )

            Spacer(modifier = Modifier.weight(0.05f))

            ShowDatePicker { date ->
                selectedDate = date
            }

            Spacer(modifier = Modifier.weight(0.05f))

            Button(
                onClick = onSubmitGratitude,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Submit gratitude",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(modifier = Modifier.weight(0.05f))

            Footer(onHomeClick = onHomeClick, dark = true)
        }
    }
}

// Screen 4
@Composable
fun GratitudeJarScreen4(viewModel: ShakeDetectionViewModel, onHomeClick: () -> Unit) {

    if (viewModel.shakeDetected) {
        viewModel.shakeDetected = false
    }

    var showCardWithMap by remember { mutableStateOf(false) }

    // Define animation values
    val jarRotation by animateFloatAsState(
        targetValue = if (showCardWithMap) 45f else 0f,
        animationSpec = tween(durationMillis = 1500)
    )

    // Animatable for pulsation scale
    val pulseScale = remember { Animatable(initialValue = 1f) }

    // Start pulsation animation
    LaunchedEffect(Unit) {
        if (!showCardWithMap) {
            pulseScale.animateTo(
                targetValue = 1.5f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 500, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )
        }
    }

    // Use LaunchedEffect to delay showing the card
    LaunchedEffect(Unit) {
        delay(1500) // Wait for 1.5 seconds before showing the card
        showCardWithMap = true
    }

    Surface(color = Color.White) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Header(dark = false)
            Spacer(modifier = Modifier.weight(0.05f))

            // Animate the jar
            Image(
                painter = painterResource(id = R.drawable.jar_foreground),
                contentDescription = "Jar",
                modifier = Modifier
                    .size(150.dp)
                    .graphicsLayer(
                        rotationZ = jarRotation,
                        scaleX = pulseScale.value,
                        scaleY = pulseScale.value
                    )
            )

            // Conditionally show the card with map
            if (showCardWithMap) {
                CardWithMap()
                LaunchedEffect(Unit) {
                    pulseScale.stop()
                }
            }

            Spacer(modifier = Modifier.weight(0.05f))
            Footer(onHomeClick = onHomeClick, dark = false)
        }
    }
}

@Composable
fun CardWithMap() {
    Surface(
        color = MaterialTheme.colorScheme.onSecondaryContainer,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(42.dp,42.dp)
        ) {
            // Add GMaps preview here
            
            // Gratitude Date
            Text(
                text = "On July 12, 2024", // You can display the actual date here
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.padding(bottom = 20.dp)
            )
            // Gratitude Message
            Text(
                text = "I feel grateful for the meeting I had with a Walmart greeter today. He was very kind. He said I resembled his granddaughter and offered me a candy.", // You can display the actual message here
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.padding(bottom = 11.dp)
            )
        }
    }
}

@Composable
fun Header( dark: Boolean) {
    var color = Color.Unspecified
    if (dark) {
        color = Color.White
    }
    else {
        color = Color.Black
    }
    Text(
        text = "Gratitude Jar",
        style = MaterialTheme.typography.bodyLarge,
        fontWeight = FontWeight.Bold,
        color = color
    )
}

@Composable
fun WelcomeMessage() {
    Text(
        text = "Welcome to Gratitude Jar!",
        style = MaterialTheme.typography.headlineLarge,
        fontFamily = FontFamily.Monospace,
        fontSize = 50.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 70.sp,
        color = MaterialTheme.colorScheme.onPrimary
    )
}

@Composable
fun GratitudeAndReminderChips() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // get counts from database
        RoundedChip(number = "24", text="Gratitudes")
        RoundedChip(number = "3", text="Reminders")
    }
}

@Composable
fun RoundedChip(number: String, text: String) {
    Surface(
        shape = RoundedCornerShape(30.dp),
        color = MaterialTheme.colorScheme.surfaceTint, // Change the color here
        modifier = Modifier.size(200.dp, 150.dp),
    ) {
        Column(
            modifier = Modifier.padding(15.dp,15.dp)
        ) {
            Text(
                text = number,
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 60.sp,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 2.sp,
                    textAlign = TextAlign.Left
                ),
//                modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp)
            )
            Text(
                text = text,
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 29.sp,
                    textAlign = TextAlign.Left
                ),
//                modifier = Modifier.padding(horizontal = 18.dp, vertical = 20.dp)
            )
        }
    }
}

@Composable
fun GratitudePrompt() {
    Text(
        text = "What are you grateful for today?",
        style = MaterialTheme.typography.titleMedium.copy(
            fontSize = 15.sp,
            fontFamily = FontFamily.Monospace
        ),
        color = MaterialTheme.colorScheme.inverseOnSurface,
    )

}

@Composable
fun AddGratitudeButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Add a gratitude",
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Composable
fun ShakeIcon() {
    Icon(imageVector = ImageVector.vectorResource(id = R.drawable.shake),
        contentDescription = null,
        modifier = Modifier.size(70.dp),
        tint = Color.Yellow)
}

@Composable
fun SurpriseText() {
    Text(
        text = "\nShake for a surprise gratitude boost",
        style = MaterialTheme.typography.titleMedium.copy(
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Italic
        ),
        color = Color.Yellow
    )
}

@Composable
fun Footer(onHomeClick: () -> Unit, dark: Boolean) {
    var color = Color.Unspecified
    if (dark) {
        color = Color.White
    }
    else {
        color = Color.Black
    }
    IconButton(onClick = onHomeClick) {
        Icon(
            imageVector = ImageVector.vectorResource(id = R.drawable.home), // Provide your home icon resource here
            contentDescription = "Home",
            tint = color
        )
    }
}

@Composable
fun GMaps(onLocationSelected: () -> Unit) {
//    Text(
//        text = "Google Maps Preview",
//        style = MaterialTheme.typography.bodyLarge,
//        fontWeight = FontWeight.Bold,
//        color = MaterialTheme.colorScheme.onPrimary
//    )
    Button(
        onClick = onLocationSelected,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Select Location",
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

// Preview for Screen 1
@Preview(showBackground = true)
@Composable
fun GratitudeJarScreen1Preview() {
    GratitudeJarScreen1(viewModel = ShakeDetectionViewModel(), onAddGratitudeClick = {}, onHomeClick = {})
}

// Preview for Screen 2
@Preview(showBackground = true)
@Composable
fun GratitudeJarScreen2Preview() {
    GratitudeJarScreen2(onLocationSelected = {}, onHomeClick = {})
}

// Preview for Screen 3
@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun GratitudeJarScreen3Preview() {
    GratitudeJarScreen3(onSubmitGratitude = {}, onHomeClick = {})
}

// Preview for Screen 4
@Preview(showBackground = true)
@Composable
fun GratitudeJarScreen4Preview() {
    GratitudeJarScreen4(viewModel = ShakeDetectionViewModel(), onHomeClick = {})
}
