package blinkmatch;

import blinkmatch.panels.BasePanel;
import blinkmatch.panels.GamePanel;
import blinkmatch.panels.HelpPanel;
import blinkmatch.panels.StartPanel;
import blinkmatch.panels.CreditPanel;
import java.awt.CardLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

//File Reading
import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;
import java.io.IOException;

public class GameWindow extends JFrame{


    private final CardLayout cardLayout = new CardLayout();
    public static JPanel  container;
    public static int highScore = 0;
    private static final String HIGH_SCORE_FILE = "highscore.txt";
    private final BasePanel startPanel;
    private final GamePanel gamePanel;   
    private final BasePanel helpPanel;
    private final CreditPanel creditPanel;

    private BasePanel currentPanel;

    public GameWindow() {
        loadHighScore(); 
        super("Blink Match Memory");
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        container  = new JPanel(cardLayout);
        add(container);
        
        //Builds Panels
        startPanel = new StartPanel(cardLayout);
        gamePanel  = new GamePanel(cardLayout);
        helpPanel  = new HelpPanel(cardLayout);
        creditPanel= new CreditPanel(cardLayout);

        container.add(startPanel, "START");
        container.add(gamePanel,  "GAME");
        container.add(helpPanel,  "HELP");
        container.add(creditPanel, "CREDITS");

        

        currentPanel = startPanel;
        cardLayout.show(container, "START");
        currentPanel.onEnter();
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
            case "CREDITS":
                currentPanel=creditPanel;
            default:
                throw new IllegalArgumentException("Unknown panel: " + name);
        }
        currentPanel.onEnter();
    }

    //Save High Scores


public static void loadHighScore() {
    try {
        File file = new File(HIGH_SCORE_FILE);
        if (file.exists()) {
            Scanner scanner = new Scanner(file);
            if (scanner.hasNextInt()) {
                highScore = scanner.nextInt();
            }
            scanner.close();
        }
    } catch (IOException e) {
        System.err.println("Could not load high score.");
    }
}

public static void saveHighScore(int score) {
    try {
        PrintWriter writer = new PrintWriter(new File(HIGH_SCORE_FILE));
        writer.print(score);
        writer.close();
        highScore = score; // Update the static variable in memory too
    } catch (IOException e) {
        System.err.println("Could not save high score.");
    }
}
}
