package md.ipltbriceni.edubankjunior.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import md.ipltbriceni.edubankjunior.data.model.AuthViewModel
import md.ipltbriceni.edubankjunior.ui.theme.EduBankJuniorTheme

@Composable
fun AuthScreen(
    viewModel: AuthViewModel, 
    snackbarHostState: SnackbarHostState,
    onAuthSuccess: () -> Unit
) {
    // Collect error messages from the ViewModel and show them in a Snackbar
    LaunchedEffect(viewModel.error) {
        viewModel.error?.let { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    AuthContent(
        isLoading = viewModel.isLoading,
        snackbarHostState = snackbarHostState,
        onAuthClick = { isLogin, email, password ->
            viewModel.performAuth(isLogin, email, password, onAuthSuccess)
        }
    )
}

@Composable
fun AuthContent(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    snackbarHostState: SnackbarHostState? = null,
    onAuthClick: (isLogin: Boolean, email: String, password: String) -> Unit
) {
    var isLogin by remember { mutableStateOf(true) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Scaffold(
        modifier = modifier,
        snackbarHost = { snackbarHostState?.let { SnackbarHost(it) } }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = if (isLogin) "EduBank Junior" else "Creează Cont",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(40.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Parolă") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true
            )

            Spacer(Modifier.height(32.dp))

            if (isLoading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            } else {
                Button(
                    onClick = { onAuthClick(isLogin, email, password) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(if (isLogin) "Conectare" else "Înregistrare")
                }

                TextButton(onClick = { isLogin = !isLogin }) {
                    Text(if (isLogin) "Nu ai cont? Înregistrează-te" else "Ai deja cont? Loghează-te")
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Screen: Light")
@Composable
fun AuthContentPreview() {
    EduBankJuniorTheme(darkTheme = false) {
        AuthContent(
            isLoading = false,
            onAuthClick = { _, _, _ -> }
        )
    }
}

@Preview(showBackground = true, name = "Screen: Dark")
@Composable
fun AuthContentDarkPreview() {
    EduBankJuniorTheme(darkTheme = true) {
        AuthContent(
            isLoading = false,
            onAuthClick = { _, _, _ -> }
        )
    }
}

@Preview(showBackground = true, name = "Screen: Loading")
@Composable
fun AuthContentLoadingPreview() {
    EduBankJuniorTheme {
        AuthContent(
            isLoading = true,
            onAuthClick = { _, _, _ -> }
        )
    }
}
