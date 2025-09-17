package ai.revealtech.hsinterview.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class Screen(
    val route: String,
    val arguments: List<NamedNavArgument> = emptyList()
) {
    object CharacterList : Screen("character_list")
    
    object CharacterDetail : Screen(
        route = "character_detail/{characterId}",
        arguments = listOf(
            navArgument("characterId") {
                type = NavType.IntType
            }
        )
    ) {
        const val characterIdArg = "characterId"
        
        fun createRoute(characterId: Int) = "character_detail/$characterId"
    }
}
