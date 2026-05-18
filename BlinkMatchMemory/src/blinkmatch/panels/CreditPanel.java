package blinkmatch.panels;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Graphics;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import blinkmatch.GameWindow;
import blinkmatch.SoundPlayer;

public class CreditPanel extends BasePanel {
    private final Properties props = new Properties();
    private Image backgroundImage;

    public CreditPanel(CardLayout cardLayout) {
        super(cardLayout);
        loadProperties();
        ImageIcon bgIcon = new ImageIcon("resources/images/creditsBackground.png");
        this.backgroundImage = bgIcon.getImage();
        initComponents();
    }

    private void loadProperties() {
        // Safe path parsing; adjust path if your config is straight under root or src
        try (FileInputStream fis = new FileInputStream("config/app.properties")) {
            props.load(fis);
        } catch (IOException e) {
            System.err.println("Could not load config: " + e.getMessage());
        }
    }

    @Override //Prints image to act as background
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    @Override
    public void initComponents() {
        setLayout(new BorderLayout(10, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        JPanel gridWrapper = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 20));
        gridWrapper.setOpaque(false);
        gridWrapper.setBorder(BorderFactory.createEmptyBorder(500, 0, 0, 0));

        

        // 2. CENTER SECTION: 2x2 Grid of Developers
        JPanel developerGrid = new JPanel(new GridLayout(2, 2, 40, 30));
        developerGrid.setOpaque(false);
        developerGrid.setPreferredSize(new java.awt.Dimension(800, 300));
        buildDeveloperUiCards(developerGrid);
        gridWrapper.add(developerGrid);
        add(gridWrapper, BorderLayout.CENTER);
        
        

        // 3. BOTTOM SECTION: Version Title & Navigation
        JPanel southPanel = new JPanel();
        southPanel.setOpaque(false);
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));
        southPanel.setBackground(bgColor());

        // App Name and Version Text Label
        String gameName = props.getProperty("app.name", "BLINK MATCH MEMORY");
        String gameVersion = props.getProperty("app.version", "1.0.0");
        JLabel versionLabel = new JLabel(gameName + " (v" + gameVersion + ")", JLabel.CENTER);
        versionLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
        versionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        versionLabel.setForeground(Color.BLACK); 
        southPanel.add(versionLabel);

        // Back Button Setup
        ImageIcon backIcon = new ImageIcon("resources/images/backIcon.png");

        JButton backBtn = makeButton(backIcon, 175, 75);
        backBtn.setBackground(new Color(0,0,0,0));

        backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        backBtn.addActionListener(e -> {
            SoundPlayer.playClickEffect();
            onExit();
            cardLayout.show(GameWindow.container, "START");
        });

        southPanel.add(backBtn);
        add(southPanel, BorderLayout.SOUTH);
    }

    private void buildDeveloperUiCards(JPanel targetGrid) {
        int i = 1;
        while (true) {
            String name = props.getProperty("dev." + i + ".name");
            if (name == null) {
                break; // Stop loop when out of developers
            }

            String github = props.getProperty("dev." + i + ".github", "");
            String contribution = props.getProperty("dev." + i + ".contribution", "");
            String imagePath = props.getProperty("dev." + i + ".image", "resources/images/defaultAvatar.png");

            // Create individual card container
            JPanel card = new JPanel(new BorderLayout(15, 0));
            card.setOpaque(false);

            // Handle Avatar Image (Scaled to 100x100 pixels)
            ImageIcon avatarRaw = new ImageIcon(imagePath);
            Image scaledImg = avatarRaw.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            JLabel avatarLabel = new JLabel(new ImageIcon(scaledImg));
            card.add(avatarLabel, BorderLayout.WEST);

            // Handle Text fields utilizing HTML styling for clean wrapping and underlines
            // Swing JLabels natively parse HTML, making multi-line styling incredibly simple
            String htmlText = "<html>"
                    + "<font face='Monospaced' size='4' color='#333333'>" + github + "</font><br/>"
                    + "<u><font face='Monospaced' size='5' color='#000000'><b>" + name + "</b></font></u><br/>"
                    + "<font face='Monospaced' size='4' color='#555555'>" + contribution + "</font>"
                    + "</html>";

            JLabel infoLabel = new JLabel(htmlText);
            card.add(infoLabel, BorderLayout.CENTER);

            // Add single card to main 2x2 grid
            targetGrid.add(card);
            i++;
        }
        repaint();
    }



    @Override public void onEnter() {}
    @Override public void onExit()  {}
    
}