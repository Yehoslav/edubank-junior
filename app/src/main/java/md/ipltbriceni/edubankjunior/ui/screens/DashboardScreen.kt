package md.ipltbriceni.edubankjunior.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import md.ipltbriceni.edubankjunior.R
import md.ipltbriceni.edubankjunior.data.model.Ticket
import md.ipltbriceni.edubankjunior.data.model.TicketsViewModel
import md.ipltbriceni.edubankjunior.ui.theme.EduHeroGradient

@Composable
fun DashboardScreen(
    viewModel: TicketsViewModel,
    onStartTest: (Int) -> Unit,
    onAllTicketsClick: () -> Unit,
    onQuickTestClick: (Int) -> Unit,
    onProgressClick: () -> Unit,
    onTicketClick: (Int) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val tickets by viewModel.tickets.collectAsState()

    // Listen for error events from the ViewModel
    LaunchedEffect(Unit) {
        viewModel.errorEvents.collect { message ->
            val result = snackbarHostState.showSnackbar(
                message = message,
                actionLabel = "Reîncearcă",
                duration = SnackbarDuration.Indefinite
            )

            if (result == SnackbarResult.ActionPerformed) {
                viewModel.loadTickets()
            }
        }
    }


    Scaffold(
        topBar = { DashboardHeader() }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            if (viewModel.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally).padding(top=20.dp))
            } else {
                val randomTicketId = if (viewModel.isLoading) {0} else {tickets.random().id}
                HeroProgressCard({onStartTest(randomTicketId)})

                ActionButtonsRow(
                    onQuickTest = {onQuickTestClick(randomTicketId)},
                    onAllTickets = onAllTicketsClick,
                    onProgress = onProgressClick
                )

                NextTicketsSection(tickets.take(4), onTicketClick)
            }
        }

    }
}

@Composable
fun DashboardHeader(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .statusBarsPadding() // Dynamically adds margin for the status bar/notch
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = "EduBank Junior",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Învață despre bani",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview
@Composable
fun HeroProgressCard(onStartClick: () -> Unit = {}) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(EduHeroGradient)
                .padding(24.dp)
        ) {
            Column {
                Text(
                    text = "Progresul tău",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = "Ai completat X din Y bilete",
                    color = Color.White.copy(alpha = 0.8f),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onStartClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Începe testul", color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

@Composable
fun ActionButtonCard(
    text: String,
    painter: Painter,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painter,
                contentDescription = null,
                modifier = Modifier.padding(bottom = 8.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(text, textAlign = TextAlign.Center)
        }
    }
}

@Preview
@Composable
fun ActionButtonsRow(
    onQuickTest: () -> Unit = {},
    onAllTickets: () -> Unit = {},
    onProgress: () -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val buttonModifier = Modifier.weight(1f)
        ActionButtonCard(
            "Test Rapid",
            painterResource(id = R.drawable.ic_electric_bolt),
            onQuickTest,
            buttonModifier
        )
        ActionButtonCard(
            "Bilete",
            painterResource(id = R.drawable.ic_list),
            onAllTickets,
            buttonModifier
        )
        ActionButtonCard(
            "Progres",
            painterResource(id = R.drawable.ic_chart_data),
            onProgress,
            buttonModifier
        )
    }
}

@Composable
fun NextTicketsSection(tickets: List<Ticket>, onTicketClick: (Int) -> Unit) {
    Column {
        Text(
            text = "Continuă aventura",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            tickets.forEach { ticket ->
                TicketCard(ticket, onTicketClick)
            }
        }
    }
}