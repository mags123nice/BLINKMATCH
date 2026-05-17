package blinkmatch.panels;

import java.util.Properties;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import blinkmatch.GameWindow;
import blinkmatch.SoundPlayer;


public class CreditPanel extends BasePanel {
    Properties props = new Properties();

    public CreditPanel(CardLayout cardLayout) {
        super(cardLayout);
        initComponents();
    }

    @Override
    public void initComponents(){
        setLayout(new BorderLayout(10, 10));
        setBackground(bgColor());
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JPanel credit = new JPanel(new GridLayout(1, 2, 16, 0));
        credit.setBackground(bgColor());

        // JLabel devCredit = new JLabel(printDeveloperCredits(null););

        ImageIcon backIcon = new ImageIcon("resources/images/backIcon.png");

        JButton backBtn = makeButton(backIcon, 150, 75);
        backBtn.setBackground(new Color(0,0,0,0));

        backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        backBtn.addActionListener(e -> {
            SoundPlayer.playClickEffect();
            onExit();
            cardLayout.show(GameWindow.container, "START");
        });

        JPanel south = new JPanel();
        south.setBackground(new Color(0,0,0,0));
        south.add(backBtn);
        add(south, BorderLayout.SOUTH);

    }
    
    public void printDeveloperCredits(Properties props) {
        String gameName = props.getProperty("app.name", "Memory Match"); // Fallback default if key is missing
        String gameVersion = props.getProperty("app.version", "1.0.0");
        
        
        System.out.println(gameName + " (v" + gameVersion + ")");

        int i = 1;
        
        while (true) {
            // Check if the next developer name exists
            String name = props.getProperty("app." + i + ".name");
            
            // If it returns null, we've reached the end of our developer list
            if (name == null) {
                break; 
            }
            
            // Grab the rest of the data for this specific developer index
            String github = props.getProperty("app." + i + ".github");
            String contribution = props.getProperty("app." + i + ".contribution");
            
            // Display the details (Perfect for printing to console or a credits JTextArea!)
            System.out.println("--- Developer #" + i + " ---");
            System.out.println("Name:         " + name);
            System.out.println("GitHub:       " + github);
            System.out.println("Contribution: " + contribution);
            System.out.println();
            
            i++; 
        }
    }
     @Override public void onEnter() {}
    @Override public void onExit()  {}

}