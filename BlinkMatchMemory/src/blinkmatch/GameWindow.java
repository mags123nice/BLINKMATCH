package blinkmatch;

import blinkmatch.panels.BasePanel;
import blinkmatch.panels.GamePanel;
import blinkmatch.panels.HelpPanel;
import blinkmatch.panels.StartPanel;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.CardLayout;
import java.awt.Dimension;

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
    private final CardLayout cardLayout;
    private final JPanel     container;

    private final BasePanel startPanel;
    private final GamePanel gamePanel;   // kept typed so we can call restartGame()
    private final BasePanel helpPanel;

    private BasePanel currentPanel;

    public GameWindow() {
        frame     = new JFrame("Blink Match Memory");
        cardLayout = new CardLayout();
        container  = new JPanel(cardLayout);

        // build panels
        startPanel = new StartPanel(cardLayout, container);
        gamePanel  = new GamePanel(cardLayout, container);
        helpPanel  = new HelpPanel(cardLayout, container);

        container.add(startPanel.getPanel(), "START");
        container.add(gamePanel.getPanel(),  "GAME");
        container.add(helpPanel.getPanel(),  "HELP");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(520, 560);
        frame.setMinimumSize(new Dimension(460, 500));
        frame.setLocationRelativeTo(null);
        frame.add(container);

        // show start screen first
        currentPanel = startPanel;
        cardLayout.show(container, "START");
        currentPanel.onEnter();
    }

    public void show() { frame.setVisible(true); }

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
