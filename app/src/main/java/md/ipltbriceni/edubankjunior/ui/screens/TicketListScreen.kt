package md.ipltbriceni.edubankjunior.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import md.ipltbriceni.edubankjunior.R
import md.ipltbriceni.edubankjunior.data.model.TicketsViewModel
import md.ipltbriceni.edubankjunior.data.model.Ticket
import md.ipltbriceni.edubankjunior.data.model.TicketProgress
import md.ipltbriceni.edubankjunior.ui.theme.EduBankJuniorTheme

@Composable
fun TicketListScreen(
    viewModel: TicketsViewModel,
    snackbarHostState: SnackbarHostState,
    onTicketClick: (Int) -> Unit,
    onNavigateBack: () -> Unit
) {
    val ticketsWithProgress by viewModel.tickets.collectAsState()

    LaunchedEffect(viewModel.errorEvents) {
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

    TicketListContent(
        isLoading = viewModel.isLoading,
        ticketsWithProgress = ticketsWithProgress,
        snackbarHostState = snackbarHostState,
        onTicketClick = onTicketClick,
        onNavigateBack = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketListContent(
    isLoading: Boolean,
    ticketsWithProgress: List<Pair<Ticket, TicketProgress?>>,
    snackbarHostState: SnackbarHostState,
    onTicketClick: (Int) -> Unit,
    onNavigateBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Bilete de Studiu", style = MaterialTheme.typography.titleLarge)
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_back),
                            contentDescription = "Înapoi",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
            ,
            contentAlignment = Alignment.Center
        ) {
            when {
                isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                ticketsWithProgress.isNotEmpty() -> TicketList(ticketsWithProgress, onTicketClick)
                else -> Text("Nu există bilete de studiu", modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
fun TicketList(
    tickets: List<Pair<Ticket, TicketProgress?>>,
    onTicketClick: (Int) -> Unit = {},
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(tickets) { (ticket, progress) ->
            TicketCard(ticket, progress, onTicketClick)
        }
    }
}

@Composable
fun TicketCard(ticket: Ticket, progress: TicketProgress?, onTicketClick: (Int) -> Unit) {
    val isCompleted = progress != null
    val containerColor = if (isCompleted) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.surface
    val iconColor = if (isCompleted) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = null,
                    indication = ripple(color = MaterialTheme.colorScheme.primaryContainer)
                ) { onTicketClick(ticket.id) }
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = ticket.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    if (isCompleted) {
                        Text(
                            text = "Scor: ${progress.correct}/${progress.total}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_forward),
                    contentDescription = null,
                    tint = iconColor
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Ticket Card: Not Completed")
@Composable
fun TicketCardPreview() {
    EduBankJuniorTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            TicketCard(
                ticket = Ticket(1, "Educație Financiară"),
                progress = null,
                onTicketClick = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Ticket Card: Completed")
@Composable
fun TicketCardCompletedPreview() {
    EduBankJuniorTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            TicketCard(
                ticket = Ticket(2, "Banii și Băncile"),
                progress = TicketProgress(8, 10),
                onTicketClick = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Screen: Loading")
@Composable
fun TicketListLoadingPreview() {
    EduBankJuniorTheme {
        TicketListContent(
            isLoading = true,
            ticketsWithProgress = emptyList(),
            snackbarHostState = remember { SnackbarHostState() },
            onTicketClick = {},
            onNavigateBack = {}
        )
    }
}

@Preview(showBackground = true, name = "Screen: Empty")
@Composable
fun TicketListEmptyPreview() {
    EduBankJuniorTheme {
        TicketListContent(
            isLoading = false,
            ticketsWithProgress = emptyList(),
            snackbarHostState = remember { SnackbarHostState() },
            onTicketClick = {},
            onNavigateBack = {}
        )
    }
}

@Preview(showBackground = true, name = "Screen: Light Theme")
@Composable
fun TicketListLightPreview(
    @PreviewParameter(TicketProvider::class) tickets: List<Pair<Ticket, TicketProgress?>>
) {
    EduBankJuniorTheme(darkTheme = false) {
        TicketListContent(
            isLoading = false,
            ticketsWithProgress = tickets,
            snackbarHostState = remember { SnackbarHostState() },
            onTicketClick = {},
            onNavigateBack = {}
        )
    }
}

@Preview(showBackground = true, name = "Screen: Dark Theme")
@Composable
fun TicketListDarkPreview(
    @PreviewParameter(TicketProvider::class) tickets: List<Pair<Ticket, TicketProgress?>>
) {
    EduBankJuniorTheme(darkTheme = true) {
        TicketListContent(
            isLoading = false,
            ticketsWithProgress = tickets,
            snackbarHostState = remember { SnackbarHostState() },
            onTicketClick = {},
            onNavigateBack = {}
        )
    }
}

class TicketProvider : PreviewParameterProvider<List<Pair<Ticket, TicketProgress?>>> {
    override val values = sequenceOf(
        List(5) {
            Ticket(it, "Bilet de Studiu $it") to if (it % 2 == 0) TicketProgress(8, 10) else null
        }
    )
}
