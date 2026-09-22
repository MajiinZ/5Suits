package com.fivesuits.app

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fivesuits.app.game.*
import com.fivesuits.app.di.sharedModule
import com.fivesuits.app.navigation.Screen
import com.fivesuits.app.presentation.*
import com.fivesuits.app.ui.components.*
import com.fivesuits.app.ui.theme.*
import org.koin.compose.KoinApplication
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App() {
    KoinApplication(application = { modules(sharedModule) }) {
        FiveSuitsContent()
    }
}

@Composable
internal fun FiveSuitsContent(model: GameViewModel = koinViewModel()) {
    val state = model.state
    var rulesVisible by remember { mutableStateOf(false) }
    var scoresVisible by remember { mutableStateOf(false) }
    LaunchedEffect(state.game, state.screen, state.busy) { model.playBotIfNeeded() }
    FiveSuitsTheme(darkTheme = state.darkTheme) {
        Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Column(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
                AppHeader(state, model, onRules = { rulesVisible = true }, onScores = { scoresVisible = true })
                Box(Modifier.weight(1f).fillMaxWidth()) {
                    when (state.screen) {
                        Screen.HOME -> HomeScreen(state, model)
                        Screen.SETUP -> SetupScreen(state, model)
                        Screen.TABLE -> state.game?.let { GameScreen(it, state, model) }
                        Screen.RULES -> RulesContent(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp))
                        Screen.SETTINGS -> SettingsScreen(state, model)
                    }
                }
                if (state.screen in listOf(Screen.HOME, Screen.RULES, Screen.SETTINGS)) {
                    NavigationBar(containerColor = MaterialTheme.colorScheme.surface, tonalElevation = 0.dp) {
                        NavItem("Play", Icons.Outlined.Casino, state.screen == Screen.HOME) { model.navigate(Screen.HOME) }
                        NavItem("How to play", Icons.Outlined.MenuBook, state.screen == Screen.RULES) { model.navigate(Screen.RULES) }
                        NavItem("Settings", Icons.Outlined.Settings, state.screen == Screen.SETTINGS) { model.navigate(Screen.SETTINGS) }
                    }
                }
            }
        }
        if (rulesVisible) AlertDialog(
            onDismissRequest = { rulesVisible = false },
            title = { Text("A little strategy. A lot of fun.") },
            text = { RulesContent(Modifier.heightIn(max = 460.dp).verticalScroll(rememberScrollState())) },
            confirmButton = { TextButton(onClick = { rulesVisible = false }) { Text("Got it") } },
        )
        if (scoresVisible && state.game != null) AlertDialog(
            onDismissRequest = { scoresVisible = false }, title = { Text("The scorecard") },
            text = { Scorecard(state.game, Modifier.verticalScroll(rememberScrollState())) },
            confirmButton = { TextButton(onClick = { scoresVisible = false }) { Text("Back to the table") } },
        )
        state.message?.let { message -> AlertDialog(
            onDismissRequest = model::dismissMessage, title = { Text("Table update") }, text = { Text(message) },
            confirmButton = { TextButton(onClick = model::dismissMessage) { Text("OK") } },
        ) }
    }
}

@Composable
private fun RowScope.NavItem(label: String, icon: ImageVector, selected: Boolean, onClick: () -> Unit) {
    NavigationBarItem(selected = selected, onClick = onClick, icon = { Icon(icon, contentDescription = null) }, label = { Text(label) })
}

@Composable
private fun AppHeader(state: GameUiState, model: GameViewModel, onRules: () -> Unit, onScores: () -> Unit) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        if (state.screen in listOf(Screen.SETUP, Screen.TABLE)) {
            IconButton(onClick = { model.navigate(Screen.HOME) }, enabled = !state.busy) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Save and return home")
            }
        } else BrandMark(Modifier.size(38.dp))
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Text(WhiteLabel.appName, fontSize = 23.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif)
            Text("A GOOD HAND. A GREAT TIME.", fontSize = 9.sp, letterSpacing = 1.3.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        if (state.screen == Screen.TABLE) IconButton(onClick = onScores) { Icon(Icons.Outlined.Leaderboard, "View scores") }
        IconButton(onClick = onRules) { Icon(Icons.Outlined.HelpOutline, "Game rules") }
    }
}

@Composable
private fun HomeScreen(state: GameUiState, model: GameViewModel) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp, vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Eyebrow("YOUR SEAT IS WAITING")
            Text("Make time\nfor a good hand.", fontFamily = FontFamily.Serif, fontSize = 38.sp, lineHeight = 42.sp, fontWeight = FontWeight.Medium)
            Text("Five suits. Eleven rounds. Endless possibilities.", color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 23.sp)
        }
        Box(Modifier.fillMaxWidth().height(235.dp).background(Brush.linearGradient(listOf(Color(0xFF194E40), Color(0xFF0D332B))), RoundedCornerShape(28.dp)), contentAlignment = Alignment.Center) {
            Text("★   ♠   ♥   ♣   ♦", Modifier.align(Alignment.TopCenter).padding(top = 19.dp), color = Color(0xFF85A89C), letterSpacing = 8.sp, fontSize = 13.sp)
            Row(Modifier.padding(top = 18.dp), horizontalArrangement = Arrangement.spacedBy((-14).dp), verticalAlignment = Alignment.CenterVertically) {
                PlayingCard(Card(901, Suit.HEARTS, 7), 3, Modifier.rotate(-13f).width(80.dp))
                PlayingCard(Card(902, Suit.SPADES, 7), 3, Modifier.offset(y = (-9).dp).rotate(-5f).width(80.dp))
                PlayingCard(Card(903, Suit.STARS, 7), 3, Modifier.offset(y = (-9).dp).rotate(5f).width(80.dp))
                PlayingCard(Card(904, Suit.CLUBS, 7), 3, Modifier.rotate(13f).width(80.dp))
            }
            Text("A CLASSIC CARD NIGHT, ANYWHERE.", Modifier.align(Alignment.BottomCenter).padding(bottom = 17.dp), color = Color(0xFFCBDAD3), fontSize = 9.sp, letterSpacing = 1.5.sp)
        }
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            if (state.game != null && state.game.phase != TurnPhase.GAME_COMPLETE) {
                ActionButton("Continue · round ${state.game.round}", Icons.AutoMirrored.Outlined.ArrowForward) { model.navigate(Screen.TABLE) }
                OutlinedButton(onClick = { model.navigate(Screen.SETUP) }, modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp)) { Text("Start a new game") }
            } else ActionButton("Let’s play", Icons.AutoMirrored.Outlined.ArrowForward) { model.navigate(Screen.SETUP) }
            Text("Play with friends nearby or take on the computer.", Modifier.fillMaxWidth(), textAlign = TextAlign.Center, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            FactCard("5", "distinct suits", Modifier.weight(1f))
            FactCard("11", "fresh rounds", Modifier.weight(1f))
            FactCard("2–4", "at your table", Modifier.weight(1f))
        }
        Surface(shape = RoundedCornerShape(18.dp), color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = .5f)) {
            Row(Modifier.padding(18.dp), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                Icon(Icons.Outlined.AutoAwesome, null, tint = MaterialTheme.colorScheme.primary)
                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text("A different wild card every round", fontWeight = FontWeight.SemiBold)
                    Text("Build books and runs. Keep your score low. There’s always another way to play your hand.", fontSize = 13.sp, lineHeight = 20.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun SetupScreen(state: GameUiState, model: GameViewModel) {
    var bots by rememberSaveable { mutableStateOf(true) }
    var count by rememberSaveable { mutableIntStateOf(2) }
    var name0 by rememberSaveable { mutableStateOf("You") }
    var name1 by rememberSaveable { mutableStateOf("Player 2") }
    var name2 by rememberSaveable { mutableStateOf("Player 3") }
    var name3 by rememberSaveable { mutableStateOf("Player 4") }
    var replace by remember { mutableStateOf(false) }
    val names = listOf(name0, name1, name2, name3)
    val start = { model.startGame(if (bots) listOf(name0, "Robin", "Sage", "Jules").take(count) else names.take(count), bots) }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp), verticalArrangement = Arrangement.spacedBy(22.dp)) {
        Eyebrow("SET THE TABLE")
        Text("Who’s playing?", fontFamily = FontFamily.Serif, fontSize = 34.sp)
        Text("All you need is a little time and a good hand.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ModeCard("Vs. computer", "A table, just for you", Icons.Outlined.SmartToy, bots, Modifier.weight(1f)) { bots = true }
            ModeCard("Pass & play", "Friends, one device", Icons.Outlined.Group, !bots, Modifier.weight(1f)) { bots = false }
        }
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Players at the table", fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                (2..4).forEach { n -> FilterChip(selected = count == n, onClick = { count = n }, label = { Text("$n players") }) }
            }
        }
        repeat(if (bots) 1 else count) { index ->
            OutlinedTextField(
                value = names[index], onValueChange = { value ->
                    val clipped = value.take(20)
                    when (index) { 0 -> name0 = clipped; 1 -> name1 = clipped; 2 -> name2 = clipped; else -> name3 = clipped }
                }, label = { Text(if (bots) "Your name" else "Player ${index + 1}") }, singleLine = true,
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp),
            )
        }
        Text(if (bots) "Your opponents plan their moves using books, runs, and the current wild cards." else "Hands stay hidden between turns. Pass the device, then reveal your cards.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp, lineHeight = 20.sp)
        ActionButton(if (state.busy) "Dealing…" else "Deal me in", Icons.Outlined.Casino, !state.busy) {
            if (state.game != null && state.game.phase != TurnPhase.GAME_COMPLETE) replace = true else start()
        }
        Text("11 rounds · lowest score wins · saved automatically", Modifier.fillMaxWidth(), textAlign = TextAlign.Center, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
    if (replace) AlertDialog(onDismissRequest = { replace = false }, title = { Text("Start a fresh table?") }, text = { Text("This replaces your current saved game.") }, confirmButton = { TextButton(onClick = { replace = false; start() }) { Text("Start new game") } }, dismissButton = { TextButton(onClick = { replace = false }) { Text("Keep playing") } })
}

@Composable
private fun GameScreen(game: GameState, state: GameUiState, model: GameViewModel) {
    val complete = game.phase == TurnPhase.ROUND_COMPLETE || game.phase == TurnPhase.GAME_COMPLETE
    val humanCount = game.players.count { !it.isBot }
    val concealed = !complete && !game.currentPlayer.isBot && humanCount > 1 && !state.handRevealed
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Eyebrow("THE TABLE")
                Text("Round ${game.round} of 11", fontSize = 28.sp, fontFamily = FontFamily.Serif)
            }
            Surface(color = MaterialTheme.colorScheme.secondaryContainer, shape = RoundedCornerShape(14.dp)) {
                Column(Modifier.padding(horizontal = 16.dp, vertical = 9.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("${rankLabel(game.wildRank)}s", fontSize = 23.sp, fontWeight = FontWeight.Bold)
                    Text("ARE WILD", fontSize = 9.sp, letterSpacing = 1.sp)
                }
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            (1..11).forEach { round -> Box(Modifier.weight(1f).height(5.dp).background(if (round <= game.round) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant, CircleShape)) }
        }
        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(game.players.indices.toList()) { index ->
                val player = game.players[index]
                Surface(color = if (game.currentPlayerIndex == index) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(16.dp)) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Icon(if (player.isBot) Icons.Outlined.SmartToy else Icons.Outlined.Person, null, Modifier.size(21.dp))
                        Column { Text(player.name, fontWeight = FontWeight.SemiBold, fontSize = 13.sp); Text("${player.totalScore} pts · ${player.hand.size} cards", fontSize = 11.sp) }
                    }
                }
            }
        }
        when {
            complete -> RoundResult(game, model, state.busy)
            concealed -> Surface(shape = RoundedCornerShape(24.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
                Column(Modifier.fillMaxWidth().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(18.dp)) {
                    Icon(Icons.Outlined.Lock, null, Modifier.size(36.dp))
                    Text("Over to ${game.currentPlayer.name}", fontFamily = FontFamily.Serif, fontSize = 27.sp, textAlign = TextAlign.Center)
                    Text("Pass the device before revealing your hand.", textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    ActionButton("Show my hand", Icons.Outlined.Visibility, !state.busy, model::revealHand)
                }
            }
            else -> {
                if (game.outPlayerIndex != null) Surface(color = MaterialTheme.colorScheme.tertiaryContainer, shape = RoundedCornerShape(12.dp)) {
                    Text("${game.players[game.outPlayerIndex].name} went out. One last turn for everyone else.", Modifier.padding(14.dp), fontSize = 13.sp)
                }
                DrawTable(game, state, model)
                if (game.currentPlayer.isBot) {
                    Row(Modifier.fillMaxWidth().padding(24.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                        Spacer(Modifier.width(12.dp)); Text("${game.currentPlayer.name} is thinking…")
                    }
                } else HandSection(game, state, model)
                if (game.lastAction.isNotBlank()) Text(game.lastAction, Modifier.fillMaxWidth(), textAlign = TextAlign.Center, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun DrawTable(game: GameState, state: GameUiState, model: GameViewModel) {
    val canDraw = game.phase == TurnPhase.DRAW && !state.busy && !game.currentPlayer.isBot
    Box(Modifier.fillMaxWidth().background(Brush.verticalGradient(listOf(Color(0xFF225B4A), Color(0xFF153E33))), RoundedCornerShape(24.dp))) {
        Column(Modifier.fillMaxWidth().padding(22.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(if (canDraw) "YOUR TURN · DRAW A CARD" else if (game.currentPlayer.isBot) "AT THE TABLE" else "CHOOSE A CARD TO DISCARD", color = Color(0xFFD2E3D9), fontSize = 10.sp, letterSpacing = 1.5.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(35.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(7.dp)) {
                    CardBack(Modifier.width(82.dp), onClick = if (canDraw) ({ model.draw(DrawSource.STOCK) }) else null)
                    Text("DRAW PILE", color = Color.White, fontSize = 10.sp, letterSpacing = .8.sp)
                    Text("${game.stock.size} cards", color = Color(0xFFBDD2C7), fontSize = 10.sp)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(7.dp)) {
                    game.topDiscard?.let { card -> PlayingCard(card, game.wildRank, Modifier.width(82.dp), onClick = if (canDraw) ({ model.draw(DrawSource.DISCARD) }) else null) }
                    Text("DISCARD", color = Color.White, fontSize = 10.sp, letterSpacing = .8.sp)
                    Text("Pick up the top card", color = Color(0xFFBDD2C7), fontSize = 10.sp)
                }
            }
        }
    }
}

@Composable
private fun HandSection(game: GameState, state: GameUiState, model: GameViewModel) {
    val hand = game.currentPlayer.hand
    val sorted = if (state.handOrder == HandOrder.RANK) hand.sortedWith(compareBy<Card> { it.rank }.thenBy { it.suit?.ordinal ?: -1 }) else hand.sortedWith(compareBy<Card> { it.suit?.ordinal ?: -1 }.thenBy { it.rank })
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("${game.currentPlayer.name}’s hand", fontFamily = FontFamily.Serif, fontSize = 24.sp)
                Text(if (game.phase == TurnPhase.DRAW) "Pick up a card to begin your turn." else "Tap a card, then discard to finish your turn.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            TextButton(onClick = model::toggleOrder) { Icon(Icons.Outlined.SwapVert, null, Modifier.size(18.dp)); Text(if (state.handOrder == HandOrder.RANK) "Rank" else "Suit") }
        }
        LazyRow(horizontalArrangement = Arrangement.spacedBy(9.dp), contentPadding = PaddingValues(vertical = 6.dp)) {
            items(sorted, key = { it.id }) { card ->
                PlayingCard(card, game.wildRank, Modifier.width(74.dp), selected = state.selectedCardId == card.id, onClick = if (game.phase == TurnPhase.DISCARD && !state.busy) ({ model.selectCard(card.id) }) else null)
            }
        }
        state.analysis?.let { analysis ->
            Surface(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = .6f), shape = RoundedCornerShape(14.dp)) {
                Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text("${analysis.melds.size} ${if (analysis.melds.size == 1) "group" else "groups"} ready · ${analysis.score} unused points", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Text(if (analysis.melds.isEmpty()) "Look for three matching ranks or a sequence in one suit." else analysis.melds.joinToString("  ·  ") { meld -> "${if (meld.type == MeldType.BOOK) "Book" else "Run"}: ${meld.cards.joinToString(" ") { cardShort(it) }}" }, fontSize = 11.sp, lineHeight = 18.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        val selected = hand.find { it.id == state.selectedCardId }
        ActionButton(if (state.busy) "Playing…" else selected?.let { "Discard ${cardShort(it)}" } ?: if (game.phase == TurnPhase.DRAW) "Draw a card above" else "Select a card to discard", Icons.AutoMirrored.Outlined.ArrowForward, !state.busy && game.phase == TurnPhase.DISCARD && selected != null, model::discard)
        Text("Books and runs are found automatically. Cover your hand after discarding to go out.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 17.sp)
    }
}

@Composable
private fun RoundResult(game: GameState, model: GameViewModel, busy: Boolean) {
    val finished = game.phase == TurnPhase.GAME_COMPLETE
    val lowest = game.players.minOf { it.totalScore }
    val winners = game.players.filter { it.totalScore == lowest }.joinToString(" & ") { it.name }
    Surface(color = MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(24.dp), border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)) {
        Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Icon(Icons.Outlined.EmojiEvents, null, Modifier.size(36.dp), tint = MaterialTheme.colorScheme.primary)
            Text(if (finished) "$winners ${if (game.players.count { it.totalScore == lowest } > 1) "win" else "wins"}!" else "Round ${game.round}, in the books.", fontFamily = FontFamily.Serif, fontSize = 30.sp)
            Text(if (finished) "Eleven rounds well played. The lowest score takes the table." else "A fresh hand is coming. Next round, ${rankLabel(game.wildRank + 1)}s are wild.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Scorecard(game)
            ActionButton(if (finished) "Back to the lounge" else "Deal round ${game.round + 1}", Icons.AutoMirrored.Outlined.ArrowForward, !busy) { if (finished) model.navigate(Screen.HOME) else model.nextRound() }
        }
    }
}

@Composable
private fun Scorecard(game: GameState, modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(14.dp)) {
        game.players.sortedBy { it.totalScore }.forEachIndexed { index, player ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("${index + 1}", Modifier.width(28.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                Column(Modifier.weight(1f)) { Text(player.name, fontWeight = FontWeight.SemiBold); Text(if (player.scores.isEmpty()) "No rounds scored yet" else "Rounds: ${player.scores.joinToString(" · ")}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                Text("${player.totalScore}", fontWeight = FontWeight.Bold, fontSize = 24.sp)
            }
            if (index < game.players.lastIndex) HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        }
        Text("Lower is better. Tied lowest scores share the win.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun RulesContent(modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Eyebrow("HOW TO PLAY")
        Text("The art of a good hand.", fontFamily = FontFamily.Serif, fontSize = 29.sp)
        Rule("01", "Aim low", "Finish eleven rounds with the fewest points. Round one starts with three cards; each new round adds one, ending with thirteen.")
        Rule("02", "Draw, then discard", "Take one card from the draw pile or the top of the discard pile. Choose one card from your hand to discard. Empty draw piles refill from earlier discards.")
        Rule("03", "Build books and runs", "A book has at least three equal ranks, in any suits. A run has at least three consecutive ranks in one suit. Ranks run from 3 through King. Each card belongs to only one group.")
        Rule("04", "Let the wilds help", "Jokers are always wild. The rank matching this round’s hand size is also wild: 3s first, then 4s, up to Kings. Wild cards can fill any position in a group.")
        Rule("05", "Go out", "After discarding, if every card in your hand fits a book or run, you go out automatically. Everyone else gets one final turn. The app finds the grouping that leaves the fewest points.")
        Rule("06", "Count what’s left", "Only unused cards score: number cards at face value, Jacks 11, Queens 12, Kings 13, rotating wilds 20, and Jokers 50. You cannot add cards to another player’s groups.")
        Text("This table supports 2–4 players, against the computer or passing one device. All play is offline.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 19.sp)
    }
}

@Composable
private fun SettingsScreen(state: GameUiState, model: GameViewModel) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
        Eyebrow("MAKE YOURSELF AT HOME")
        Text("Your kind of table.", fontSize = 32.sp, fontFamily = FontFamily.Serif)
        Surface(shape = RoundedCornerShape(18.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
            Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) { Text("Evening mode", fontWeight = FontWeight.SemiBold); Text("A softer table after dark", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                Switch(checked = state.darkTheme, onCheckedChange = model::setDarkTheme, modifier = Modifier.semantics { contentDescription = "Evening mode" })
            }
        }
        Rule("✓", "Pick up where you left off", "Every move saves on this device. Return to the home screen and choose Continue to resume.")
        Rule("♡", "Good company, anywhere", "Play offline with computer opponents or friends nearby. No account or internet connection needed.")
        Text("${WhiteLabel.appName} · Version 1.0", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun Eyebrow(text: String) = Text(text, color = MaterialTheme.colorScheme.primary, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.8.sp)

@Composable
private fun ActionButton(text: String, icon: ImageVector, enabled: Boolean = true, onClick: () -> Unit) {
    Button(onClick = onClick, enabled = enabled, modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp), shape = RoundedCornerShape(16.dp), contentPadding = PaddingValues(16.dp)) {
        Text(text, Modifier.weight(1f), fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        Icon(icon, null, Modifier.size(20.dp))
    }
}

@Composable
private fun FactCard(value: String, label: String, modifier: Modifier) {
    Column(modifier.padding(vertical = 8.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(value, fontFamily = FontFamily.Serif, fontSize = 28.sp, color = MaterialTheme.colorScheme.primary)
        Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun ModeCard(title: String, detail: String, icon: ImageVector, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Surface(onClick = onClick, modifier = modifier, shape = RoundedCornerShape(18.dp), color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface, border = androidx.compose.foundation.BorderStroke(if (selected) 2.dp else 1.dp, if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
            Icon(icon, null, Modifier.size(27.dp), tint = MaterialTheme.colorScheme.primary)
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Text(detail, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun Rule(number: String, title: String, description: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(number, Modifier.width(26.dp), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) { Text(title, fontWeight = FontWeight.SemiBold); Text(description, fontSize = 14.sp, lineHeight = 22.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) }
    }
}

private fun rankLabel(rank: Int): String = when (rank) { 11 -> "J"; 12 -> "Q"; 13 -> "K"; else -> rank.toString() }
private fun cardShort(card: Card): String = if (card.isJoker) "Joker" else rankLabel(card.rank) + when (card.suit) { Suit.CLUBS -> "♣"; Suit.DIAMONDS -> "♦"; Suit.HEARTS -> "♥"; Suit.SPADES -> "♠"; Suit.STARS -> "★"; null -> "" }
