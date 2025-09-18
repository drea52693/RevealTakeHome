package ai.revealtech.hsinterview.ui.characterlist

import ai.revealtech.hsinterview.data.model.Character
import ai.revealtech.hsinterview.ui.state.UiState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import ai.revealtech.hsinterview.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterListScreen(
    onCharacterClick: (Int) -> Unit,
    viewModel: CharacterListViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val isLoadingMore by viewModel.isLoadingMore.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = { Text(stringResource(R.string.rick_and_morty_characters)) },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary
            )
        )

        val currentUiState = uiState
        when (currentUiState) {
            is UiState.Loading -> {
                LoadingScreen()
            }

            is UiState.Success -> {
                CharacterList(
                    characters = currentUiState.data,
                    onCharacterClick = onCharacterClick,
                    isLoadingMore = isLoadingMore,
                    viewModel = viewModel
                )
            }

            is UiState.Error -> {
                ErrorScreen(
                    message = currentUiState.message,
                    onRetry = { viewModel.refresh() }
                )
            }
        }
    }
}

@Composable
private fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator()
            Text(
                text = stringResource(R.string.loading_characters),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun ErrorScreen(
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.error),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.error
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
            Button(onClick = onRetry) {
                Text(stringResource(R.string.retry))
            }
        }
    }
}

@Composable
private fun CharacterList(
    characters: List<Character>,
    onCharacterClick: (Int) -> Unit,
    isLoadingMore: Boolean,
    viewModel: CharacterListViewModel
) {
    val listState = rememberLazyListState()

    // Direct scroll-based pagination detection
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo }
            .collect { layoutInfo ->
                val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()

                if (lastVisibleItem != null &&
                    !isLoadingMore &&
                    lastVisibleItem.index >= characters.size - CharacterListConstants.PAGINATION_THRESHOLD
                ) {
                    viewModel.loadMoreCharacters()
                }
            }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(CharacterListConstants.SCREEN_PADDING.dp),
        verticalArrangement = Arrangement.spacedBy(CharacterListConstants.LIST_ITEM_SPACING.dp)
    ) {
        items(
            items = characters,
            key = { it.id }
        ) { character ->
            CharacterItem(
                character = character,
                onClick = { onCharacterClick(character.id) }
            )
        }

        if (isLoadingMore) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(CharacterListConstants.SCREEN_PADDING.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
private fun CharacterItem(
    character: Character,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = CharacterListConstants.CARD_ELEVATION.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(CharacterListConstants.CARD_PADDING.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(character.image)
                    .crossfade(true)
                    .build(),
                contentDescription = character.name,
                modifier = Modifier
                    .size(CharacterListConstants.CHARACTER_IMAGE_SIZE.dp)
                    .clip(RoundedCornerShape(CharacterListConstants.IMAGE_CORNER_RADIUS.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(CharacterListConstants.SPACER_WIDTH.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = character.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "${character.status} - ${character.species}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = character.location.name,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            StatusIndicator(status = character.status)
        }
    }
}

@Composable
private fun StatusIndicator(status: String) {
    val color = when (status.lowercase()) {
        "alive" -> MaterialTheme.colorScheme.primary
        "dead" -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.outline
    }

    Box(
        modifier = Modifier
            .size(CharacterListConstants.STATUS_INDICATOR_SIZE.dp)
            .background(
                color = color,
                shape = RoundedCornerShape(CharacterListConstants.STATUS_INDICATOR_CORNER_RADIUS)
            )
    )
}

private object CharacterListConstants {
    const val PAGINATION_THRESHOLD = 2
    const val SCREEN_PADDING = 16
    const val LIST_ITEM_SPACING = 8
    const val CARD_ELEVATION = 4
    const val CARD_PADDING = 12
    const val CHARACTER_IMAGE_SIZE = 60
    const val IMAGE_CORNER_RADIUS = 8
    const val SPACER_WIDTH = 12
    const val STATUS_INDICATOR_SIZE = 12
    const val STATUS_INDICATOR_CORNER_RADIUS = 50
}
