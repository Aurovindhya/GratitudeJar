package com.example.gratitudejar

import android.os.Build
import android.os.Bundle
import android.widget.CalendarView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.appcompat.view.ContextThemeWrapper
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "screen1") {
                composable("screen1") {
                    GratitudeJarScreen1(onAddGratitudeClick = {
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
                        // Handle submission of gratitude
                    }, onHomeClick = { navController.navigate("screen1")})
                }
            }
        }
    }
}

@Composable
fun GratitudeJarScreen1(onAddGratitudeClick: () -> Unit, onHomeClick: () -> Unit) {
    Surface(color = Color.Black) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Header()
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
            Footer(onHomeClick = onHomeClick)
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
            Header()
            Spacer(modifier = Modifier.weight(0.05f)) // Pushes content to the top half
            GMaps(onLocationSelected = onLocationSelected)
            Spacer(modifier = Modifier.weight(0.05f)) // Pushes content to the top half
            Footer(onHomeClick = onHomeClick)
        }
    }
}

// Screen 3
@RequiresApi(Build.VERSION_CODES.O)

@Composable
fun GratitudeJarScreen3(onSubmitGratitude: () -> Unit, onHomeClick: () -> Unit) {
    var gratitudeMessage by remember { mutableStateOf(TextFieldValue()) }

    Surface(color = Color.Black) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Header()
            Spacer(modifier = Modifier.weight(0.05f)) // Pushes content to the top half
            Text(
                text = "Enter your gratitude message",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.weight(0.05f)) // Pushes content to the top half
            // Add input box with character limit of 2000
            OutlinedTextField(
                value = gratitudeMessage,
                onValueChange = { newValue ->
                    if (newValue.text.length <= 2000) {
                        gratitudeMessage = newValue
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("I am grateful for...") },
                maxLines = 3,
                textStyle = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.onPrimary)
            )

            Spacer(modifier = Modifier.weight(0.05f)) // Pushes content to the top half
            // Add date-time picker
            ShowDatePicker()
            Spacer(modifier = Modifier.weight(0.05f)) // Pushes content to the top half
            Button(
                onClick = onSubmitGratitude,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Submit gratitude")
            }
            Spacer(modifier = Modifier.weight(0.05f)) // Pushes content to the top half
            Footer(onHomeClick = onHomeClick)
        }
    }
}


@Composable
fun Header() {
    Text(
        text = "Gratitude Jar",
        style = MaterialTheme.typography.bodyLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.background
    )
}

@Composable
fun WelcomeMessage() {
    Text(
        text = "Welcome to Gratitude Jar!",
        style = MaterialTheme.typography.bodyLarge,
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
        RoundedChip(text = "24\nGratitudes")
        RoundedChip(text = "3\nReminders")
    }
}

@Composable
fun RoundedChip(text: String) {
    Surface(
        shape = RoundedCornerShape(30.dp),
        color = MaterialTheme.colorScheme.surfaceTint, // Change the color here
        modifier = Modifier.size(200.dp, 100.dp),
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 25.sp, fontWeight = FontWeight.ExtraBold, lineHeight = 50.sp),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun GratitudePrompt() {
    Text(
        text = "What are you grateful for today?",
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.inverseOnSurface
    )

}

@Composable
fun AddGratitudeButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = "Add a gratitude")
    }
}

@Composable
fun ShakeIcon() {
    Icon(imageVector = ImageVector.vectorResource(id = R.drawable.shake),
        contentDescription = null,
        modifier = Modifier.size(48.dp),
        tint = Color.Yellow)
}

@Composable
fun SurpriseText() {
    Text(
        text = "Shake for a surprise gratitude boost",
        color = Color.Yellow,
        fontStyle = FontStyle.Italic
    )
}

@Composable
fun Footer(onHomeClick: () -> Unit) {
    IconButton(onClick = onHomeClick) {
        Icon(
            imageVector = ImageVector.vectorResource(id = R.drawable.home), // Provide your home icon resource here
            contentDescription = "Home",
            tint = MaterialTheme.colorScheme.onPrimary
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
        Text(text = "Select Location")
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ShowDatePicker() {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    Surface(
        color = MaterialTheme.colorScheme.surface, // Set your desired color here
        modifier = Modifier.wrapContentWidth()
    ) {
        AndroidView(
            factory = { context ->
                CalendarView(context).apply {
                    setOnDateChangeListener { _, year, month, dayOfMonth ->
                        selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}


//@RequiresApi(Build.VERSION_CODES.O)
//@Composable
//fun DatePicker(onDismissRequest: () -> Unit) {
//    val selDate = remember { mutableStateOf(LocalDate.now()) }
//
//    //todo - add strings to resource after POC
//    Dialog(onDismissRequest = { onDismissRequest() }, properties = DialogProperties()) {
//        Column(
//            modifier = Modifier
//                .wrapContentSize()
//                .background(
//                    color = MaterialTheme.colorScheme.surface,
//                    shape = RoundedCornerShape(size = 16.dp)
//                )
//        ) {
//            Column(
//                Modifier
//                    .defaultMinSize(minHeight = 72.dp)
//                    .fillMaxWidth()
//                    .background(
//                        color = MaterialTheme.colorScheme.primary,
//                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
//                    )
//                    .padding(16.dp)
//            ) {
//                Text(
//                    text = "Select date".toUpperCase(Locale.current),
//                    style = MaterialTheme.typography.titleMedium,
//                    color = MaterialTheme.colorScheme.onPrimary
//                )
//
//                Spacer(modifier = Modifier.size(24.dp))
//
//                Text(
//                    text = selDate.value.format(DateTimeFormatter.ofPattern("dd-mm-yyyy")),
//                    style = MaterialTheme.typography.headlineLarge,
//                    color = MaterialTheme.colorScheme.onPrimary
//                )
//
//                Spacer(modifier = Modifier.size(16.dp))
//            }
//
////            CustomCalendarView(onDateSelected = {
////                selDate.value = it
////            })
//
//            Spacer(modifier = Modifier.size(8.dp))
//
//            Row(
//                modifier = Modifier
//                    .align(Alignment.End)
//                    .padding(bottom = 16.dp, end = 16.dp)
//            ) {
//                TextButton(
//                    onClick = onDismissRequest
//                ) {
//                    //TODO - hardcode string
//                    Text(
//                        text = "Cancel",
//                        style = MaterialTheme.typography.labelMedium,
//                        color = MaterialTheme.colorScheme.onPrimary
//                    )
//                }
//
//                TextButton(
//                    onClick = {
//                        onDismissRequest()
//                    }
//                ) {
//                    //TODO - hardcode string
//                    Text(
//                        text = "OK",
//                        style = MaterialTheme.typography.labelMedium,
//                        color = MaterialTheme.colorScheme.onPrimary
//                    )
//                }
//
//            }
//        }
//    }
//}

//@RequiresApi(Build.VERSION_CODES.O)
//@Composable
//fun CustomCalendarView(onDateSelected: (LocalDate) -> Unit) {
//    // Adds view to Compose
//    AndroidView(
//        modifier = Modifier.wrapContentSize(),
//        factory = { context ->
//            CalendarView(ContextThemeWrapper(context, R.style.CalenderViewCustom))
//        },
//        update = { view ->
//            view.minDate = Long.MIN_VALUE
//            view.maxDate = Long.MAX_VALUE
//
//            view.setOnDateChangeListener { _, year, month, dayOfMonth ->
//                onDateSelected(
//                    LocalDate
//                        .now()
//                        .withMonth(month + 1)
//                        .withYear(year)
//                        .withDayOfMonth(dayOfMonth)
//                )
//            }
//        }
//    )
//}

// Preview for Screen 1
@Preview(showBackground = true)
@Composable
fun GratitudeJarScreen1Preview() {
    GratitudeJarScreen1(onAddGratitudeClick = {}, onHomeClick = {})
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
