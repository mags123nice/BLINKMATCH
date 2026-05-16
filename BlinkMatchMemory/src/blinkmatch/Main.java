package blinkmatch;

import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameWindow window = new GameWindow();
            window.setVisible(true);
            }
        );
    }
}

// Things to Change for Code
// proper file placements
// Update gloss buttons to non-gloss
// Adjust the location of the pause button to be centered //DONE
// text clarity in the game over //PARTIALLY DONE
// stats size more clearly
// Adjust the space below or use it for something
// Use inheritance for frames //DONE
// Add a high score in the main menu