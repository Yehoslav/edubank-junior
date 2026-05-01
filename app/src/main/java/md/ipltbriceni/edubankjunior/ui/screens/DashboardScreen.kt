package md.ipltbriceni.edubankjunior.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.ripple
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import md.ipltbriceni.edubankjunior.R
import md.ipltbriceni.edubankjunior.data.model.Ticket
import md.ipltbriceni.edubankjunior.data.model.TicketsViewModel
import md.ipltbriceni.edubankjunior.data.model.TicketProgress
import md.ipltbriceni.edubankjunior.ui.theme.EduBankJuniorTheme
import androidx.compose.ui.graphics.Brush

@Composable
fun DashboardScreen(
    viewModel: TicketsViewModel,
    onAllTicketsClick: () -> Unit,
    onTicketSelected: (Int) -> Unit,
    onLogout: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val ticketsWithProgress by viewModel.tickets.collectAsState()
    val showProgressDialog = remember { mutableStateOf(false) }

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

    DashboardContent(
        isLoading = viewModel.isLoading,
        ticketsWithProgress = ticketsWithProgress,
        showProgressDialog = showProgressDialog.value,
        onShowProgressChange = { showProgressDialog.value = it },
        onAllTicketsClick = onAllTicketsClick,
        onTicketSelected = onTicketSelected,
        onLogout = onLogout
    )
}

@Composable
fun DashboardContent(
    isLoading: Boolean,
    ticketsWithProgress: List<Pair<Ticket, TicketProgress?>>,
    showProgressDialog: Boolean,
    onShowProgressChange: (Boolean) -> Unit,
    onAllTicketsClick: () -> Unit,
    onTicketSelected: (Int) -> Unit,
    onLogout: () -> Unit
) {
    if (showProgressDialog) {
        ProgressDialog(
            ticketsWithProgress = ticketsWithProgress,
            onDismiss = { onShowProgressChange(false) }
        )
    }

    Scaffold(
        topBar = { DashboardHeader(onLogout = onLogout) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 20.dp)
                )
            } else {
                val tickets = ticketsWithProgress.map { it.first }
                val completedCount = ticketsWithProgress.count { it.second != null }
                val totalCount = ticketsWithProgress.size
                
                // Find the first unanswered ticket, or a random one if all are answered
                val nextTicketId = ticketsWithProgress.firstOrNull { it.second == null }?.first?.id 
                    ?: (if (tickets.isEmpty()) 0 else tickets.random().id)

                HeroProgressCard(
                    completed = completedCount,
                    total = totalCount,
                    onStartClick = { onTicketSelected(nextTicketId) }
                )

                ActionButtonsRow(
                    onQuickTest = { onTicketSelected(nextTicketId) },
                    onAllTickets = onAllTicketsClick,
                    onShowProgress = { onShowProgressChange(true) }
                )

                val sortedTickets = ticketsWithProgress.sortedWith(
                    compareBy<Pair<Ticket, TicketProgress?>> { it.second == null }
                        .reversed()
                        .thenBy { it.second?.correct ?: 0 }
                ).take(4)

                NextTicketsSection(sortedTickets, onTicketSelected)
            }
        }
    }
}

@Composable
fun DashboardHeader(modifier: Modifier = Modifier, onLogout: () -> Unit = {}) {
    var showMenu by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .statusBarsPadding()
            .padding(horizontal = 18.dp, vertical = 12.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
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

        Box {
            IconButton(onClick = { showMenu = true }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_account_circle),
                    contentDescription = "Cont",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Log out") },
                    onClick = {
                        showMenu = false
                        onLogout()
                    }
                )
            }
        }
    }
}

@Composable
fun HeroProgressCard(completed: Int, total: Int, onStartClick: () -> Unit = {}) {
    val heroGradient = Brush.horizontalGradient(
        colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primaryContainer)
    )
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(heroGradient)
                .padding(24.dp)
        ) {
            Column {
                Text(
                    text = "Progresul tău",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = "Ai completat $completed din $total bilete",
                    color = Color.White.copy(alpha = 0.8f),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onStartClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Începe testul")
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
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .clickable(
                    interactionSource = null,
                    indication = ripple(color = MaterialTheme.colorScheme.primaryContainer)
                ) { onClick() }
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

@Composable
fun ActionButtonsRow(
    onQuickTest: () -> Unit = {},
    onAllTickets: () -> Unit = {},
    onShowProgress: () -> Unit = {}
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
            onShowProgress,
            buttonModifier
        )
    }
}

@Composable
fun ProgressDialog(
    ticketsWithProgress: List<Pair<Ticket, TicketProgress?>>,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Progresul tău",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val totalCorrect = ticketsWithProgress.sumOf { it.second?.correct ?: 0 }
                val totalQuestions = ticketsWithProgress.sumOf { it.second?.total ?: 0 }
                val averageScore = if (totalQuestions > 0) (totalCorrect.toFloat() / totalQuestions * 100).toInt() else 0
                val completedTickets = ticketsWithProgress.count { it.second != null }
                val totalTickets = ticketsWithProgress.size

                Text(
                    text = "Până acum ai finalizat $completedTickets din $totalTickets misiuni. Scorul tău mediu de performanță este de $averageScore%. Continuă să înveți!",
                    style = MaterialTheme.typography.bodyLarge
                )

                val answeredTickets = ticketsWithProgress.filter { it.second != null }
                if (answeredTickets.isNotEmpty()) {
                    Text(
                        text = "Istoric misiuni:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    answeredTickets.forEach { (ticket, progress) ->
                        Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = ticket.title,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = "Bilet ${ticket.id}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Text(
                                    text = "${progress?.correct ?: 0}/${progress?.total ?: 0}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                        }
                    }
                } else {
                    Text(
                        text = "Încă nu ai finalizat nicio misiune. Începe una pentru a-ți vedea progresul aici!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Închide")
            }
        }
    )
}

@Composable
fun NextTicketsSection(ticketsWithProgress: List<Pair<Ticket, TicketProgress?>>, onTicketClick: (Int) -> Unit) {
    Column {
        Text(
            text = "Continuă aventura",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ticketsWithProgress.forEach { (ticket, progress) ->
                TicketCard(ticket, progress, onTicketClick)
            }
        }
    }
}

// --- PREVIEWS ---

class DashboardPreviewProvider : PreviewParameterProvider<List<Pair<Ticket, TicketProgress?>>> {
    override val values = sequenceOf(
        List(5) { i ->
            Ticket(i, "Misiune de test $i") to if (i < 2) TicketProgress(8, 10) else null
        }
    )
}

@Preview(showBackground = true, name = "Dashboard Full, Dark")
@Composable
fun DashboardContentDarkPreview(
    @PreviewParameter(DashboardPreviewProvider::class) tickets: List<Pair<Ticket, TicketProgress?>>
) {
    EduBankJuniorTheme(darkTheme = true) {
        DashboardContent(
            isLoading = false,
            ticketsWithProgress = tickets,
            showProgressDialog = false,
            onShowProgressChange = {},
            onAllTicketsClick = {},
            onTicketSelected = {},
            onLogout = {}
        )
    }
}

@Preview(showBackground = true, name = "Dashboard Full")
@Composable
fun DashboardContentPreview(
    @PreviewParameter(DashboardPreviewProvider::class) tickets: List<Pair<Ticket, TicketProgress?>>
) {
    EduBankJuniorTheme {
        DashboardContent(
            isLoading = false,
            ticketsWithProgress = tickets,
            showProgressDialog = false,
            onShowProgressChange = {},
            onAllTicketsClick = {},
            onTicketSelected = {},
            onLogout = {}
        )
    }
}

@Preview(showBackground = true, name = "Dashboard Loading")
@Composable
fun DashboardLoadingPreview() {
    EduBankJuniorTheme {
        DashboardContent(
            isLoading = true,
            ticketsWithProgress = emptyList(),
            showProgressDialog = false,
            onShowProgressChange = {},
            onTicketSelected = {},
            onAllTicketsClick = {},
            onLogout = {}
        )
    }
}

@Preview(showBackground = true, name = "Hero Card")
@Composable
fun HeroProgressCardPreview() {
    EduBankJuniorTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            HeroProgressCard(completed = 3, total = 10)
        }
    }
}

@Preview(showBackground = true, name = "Action Buttons")
@Composable
fun ActionButtonsRowPreview() {
    EduBankJuniorTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            ActionButtonsRow()
        }
    }
}

@Preview(showBackground = true, name = "Progress Dialog")
@Composable
fun ProgressDialogPreview(
    @PreviewParameter(DashboardPreviewProvider::class) tickets: List<Pair<Ticket, TicketProgress?>>
) {
    EduBankJuniorTheme {
        ProgressDialog(
            ticketsWithProgress = tickets,
            onDismiss = {}
        )
    }
}
