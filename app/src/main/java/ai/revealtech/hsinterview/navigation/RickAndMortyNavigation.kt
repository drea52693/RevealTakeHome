package ai.revealtech.hsinterview.navigation

import ai.revealtech.hsinterview.ui.characterdetail.CharacterDetailScreen
import ai.revealtech.hsinterview.ui.characterdetail.CharacterDetailViewModel
import ai.revealtech.hsinterview.ui.characterlist.CharacterListScreen
import ai.revealtech.hsinterview.ui.characterlist.CharacterListViewModel
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun RickAndMortyNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.CharacterList.route
    ) {
        composable(Screen.CharacterList.route) {
            val viewModel: CharacterListViewModel = hiltViewModel()
            CharacterListScreen(
                onCharacterClick = { characterId ->
                    navController.navigate(Screen.CharacterDetail.createRoute(characterId))
                },
                viewModel = viewModel
            )
        }
        
        composable(
            route = Screen.CharacterDetail.route,
            arguments = Screen.CharacterDetail.arguments
        ) { backStackEntry ->
            val characterId = backStackEntry.arguments?.getInt(Screen.CharacterDetail.characterIdArg) ?: 0
            val viewModel: CharacterDetailViewModel = hiltViewModel()
            CharacterDetailScreen(
                characterId = characterId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                viewModel = viewModel
            )
        }
    }
}
