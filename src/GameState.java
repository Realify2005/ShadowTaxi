/**
 * GameState indicates 4 possible states of the game.
 * HOME_SCREEN is shown first when the program is run.
 * PLAYER_INFO comes next where player can enter their name.
 * GAME_ONGOING handles the main game.
 * GAME_END is when the game finishes or the player has won.
 */
public enum GameState {
    HOME_SCREEN,
    PLAYER_INFO,
    GAME_ONGOING,
    GAME_END
}
