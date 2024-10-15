import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.github.vinceglb.autolaunch.AutoLaunch
import kotlinx.coroutines.launch
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "AutoLaunch Sample") {
        App()
    }
}

@Composable
fun App() {
    val scope = rememberCoroutineScope()
    val autoLaunch = remember { AutoLaunch(appPackageName = "com.autolaunch.sample") }
    var isEnabled by remember { mutableStateOf(false) }
    val isStartedViaAutostart = autoLaunch.isStartedViaAutostart()


    LaunchedEffect(Unit) {
        isEnabled = autoLaunch.isEnabled()
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = when (isEnabled) {
                    true -> "AutoLaunch is enabled"
                    else -> "AutoLaunch is disabled"
                },
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )


            Text(
                text = "The application was ${if (!isStartedViaAutostart) "not" else ""} started via autostart.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp)
            )


            if (!AutoLaunch.isRunningFromDistributable) {
                Text(
                    text = "You are in development.\nAutoLaunch works only when app is distributed.\nRun the app using `./gradlew runDistributable`.",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.error,
                )
            } else {
                Text(text = "App resolved executable path:", modifier = Modifier.padding(bottom = 2.dp))
                TextButton(onClick = { copyToClipboard(AutoLaunch.resolvedExecutable.path) }) {
                    Text(
                        text = AutoLaunch.resolvedExecutable.path,
                        textAlign = TextAlign.Center,
                    )
                }
            }

            Button(
                enabled = AutoLaunch.isRunningFromDistributable,
                onClick = {
                    scope.launch {
                        when (isEnabled) {
                            true -> autoLaunch.disable()
                            else -> autoLaunch.enable()
                        }
                        isEnabled = autoLaunch.isEnabled()
                    }
                },
                modifier = Modifier.padding(top = 24.dp)
            ) {
                Text(
                    text = when (isEnabled) {
                        true -> "Disable AutoLaunch"
                        else -> "Enable AutoLaunch"
                    }
                )
            }
        }
    }
}

private fun copyToClipboard(text: String) {
    val selection = StringSelection(text)
    val clipboard = Toolkit.getDefaultToolkit().systemClipboard
    clipboard.setContents(selection, selection)
}
