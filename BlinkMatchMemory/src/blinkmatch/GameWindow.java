package blinkmatch;

import blinkmatch.panels.BasePanel;
import blinkmatch.panels.GamePanel;
import blinkmatch.panels.HelpPanel;
import blinkmatch.panels.StartPanel;
import java.awt.CardLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class GameWindow {

    private final JFrame     frame;
    private final CardLayout cardLayout = new CardLayout();
    public static JPanel  container;
    public static int highScore = 0;

    private final BasePanel startPanel;
    private final GamePanel gamePanel;   
    private final BasePanel helpPanel;

    private BasePanel currentPanel;

    public GameWindow() {
        frame     = new JFrame("Blink Match Memory");
        frame.setResizable(false);


        container  = new JPanel(cardLayout);
        
        //Builds Panels
        startPanel = new StartPanel(cardLayout);
        gamePanel  = new GamePanel(cardLayout);
        helpPanel  = new HelpPanel(cardLayout);

        container.add(startPanel, "START");
        container.add(gamePanel,  "GAME");
        container.add(helpPanel,  "HELP");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setUndecorated(true);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setLocationRelativeTo(null);
        frame.add(container);

        currentPanel = startPanel;
        cardLayout.show(container, "START");
        currentPanel.onEnter();
    }


    public void show() { 
        frame.setVisible(true); 
    }

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
