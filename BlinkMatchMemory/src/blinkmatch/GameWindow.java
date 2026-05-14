package blinkmatch;

import blinkmatch.panels.BasePanel;
import blinkmatch.panels.GamePanel;
import blinkmatch.panels.HelpPanel;
import blinkmatch.panels.StartPanel;
import java.awt.*;
import javax.swing.*;

/**
 * Top-level JFrame that owns the CardLayout container.
 *
 * Navigation is centralised here: it calls onExit() on the current panel
 * and onEnter() on the next — decoupling the panels from each other.
 *
 * POLYMORPHISM: panels are stored as BasePanel references; showPanel()
 *               calls the abstract lifecycle methods without knowing the
 *               concrete type.
 */
public class GameWindow {

    private final JFrame     frame;
    private final CardLayout cardLayout = new CardLayout();
    public static JPanel  container;
    public static int highScore = 0;

    private final BasePanel startPanel;
    private final GamePanel gamePanel;   // kept typed so we can call restartGame()
    private final BasePanel helpPanel;

    private BasePanel currentPanel;

    public GameWindow() {
        frame     = new JFrame("Blink Match Memory");
        frame.setResizable(false);


        container  = new JPanel(cardLayout);
        

        // build panels
        startPanel = new StartPanel(cardLayout);
        gamePanel  = new GamePanel(cardLayout);
        helpPanel  = new HelpPanel(cardLayout);

        container.add(startPanel, "START");
        container.add(gamePanel,  "GAME");
        container.add(helpPanel,  "HELP");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       // frame.setSize(650, 750);
        frame.setMinimumSize(new Dimension(750, 750));
        frame.setLocationRelativeTo(null);
        frame.add(container);

        // show start screen first
        currentPanel = startPanel;
        cardLayout.show(container, "START");
        currentPanel.onEnter();
    }


    public void show() { 
        frame.setVisible(true); 
    }

    /**
     * Transitions to a named screen, honouring the lifecycle contract.
     * POLYMORPHISM: currentPanel / next are BasePanel — concrete type unknown here.
     */

    public void showPanel(String name) {
        currentPanel.onExit();
        cardLayout.show(container, name);
        switch (name) {
            case "START":
                currentPanel = startPanel;
                break;
            case "GAME":
                currentPanel = gamePanel;
                break;
            case "HELP":
                currentPanel = helpPanel;
                break;
            default:
                throw new IllegalArgumentException("Unknown panel: " + name);
        }
        currentPanel.onEnter();
    }
}
