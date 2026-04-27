package md.ipltbriceni.edubankjunior.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketListScreen(viewModel: TicketsViewModel, onTicketClick: (Int) -> Unit, onNavigateBack: () -> Unit) {
    val snackbarHostState = remember { SnackbarHostState() }
    val ticketsWithProgress by viewModel.tickets.collectAsState()

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
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    containerColor = MaterialTheme.colorScheme.background,
                    actionColor = MaterialTheme.colorScheme.primary,
                    snackbarData = data
                )
            }
        },
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
        }
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentAlignment = Alignment.Center
        ) {
            if (viewModel.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                TicketList(ticketsWithProgress, onTicketClick)
            }
        }
    }
}

class TicketProvider : PreviewParameterProvider<List<Pair<Ticket, TicketProgress?>>> {
    override val values = sequenceOf(
        emptyList(), // Test empty state
        List(5) { 
            Ticket(it, "Bilet Repetat $it") to if (it % 2 == 0) TicketProgress(8, 10) else null
        } 
    )
}

@Preview(showBackground = true)
@Composable
fun TicketList(
    @PreviewParameter(TicketProvider::class) tickets: List<Pair<Ticket, TicketProgress?>>,
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
    val containerColor = if (isCompleted) Color(0xFFE8F5E9) else Color.White
    val iconColor = if (isCompleted) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary

    ElevatedCard(
        onClick = { onTicketClick(ticket.id) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Bilet ${ticket.id}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = ticket.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                if (isCompleted) {
                    Text(
                        text = "Scor: ${progress?.correct}/${progress?.total}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF2E7D32),
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
