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
import java.awt.GridBagLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
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

    setLayout(new GridBagLayout());
    setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
    
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.BOTH;


    JPanel topSpacer = new JPanel();
    topSpacer.setOpaque(false);
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 2.0; //adjusts to push dev credits down
    add(topSpacer, gbc);

    JPanel horizontalLeftSpacer = new JPanel();
    horizontalLeftSpacer.setOpaque(false);
    gbc.gridx = 0;     
    gbc.gridy = 1;     
    gbc.weightx = 0.20; // adjust to push dev credits horizontally
    gbc.weighty = 0.0;
    add(horizontalLeftSpacer, gbc);

    // ---- STEP B: THE DEVELOPER GRID (Stays small and responsive) ----
    JPanel developerGrid = new JPanel(new GridLayout(2, 2, 40, 50)) {
        @Override
        public Dimension getPreferredSize() {
            if (getParent() != null) {
                int dynamicWidth = (int) (getParent().getWidth() * 0.80);
                int dynamicHeight = (int) (getParent().getHeight() * 0.45);
                
                dynamicWidth = Math.max(dynamicWidth, 900);
                dynamicHeight = Math.max(dynamicHeight, 280);
                
                return new Dimension(dynamicWidth, dynamicHeight);
            }
            return new Dimension(900, 450);
        }
    };
    developerGrid.setOpaque(false);
    buildDeveloperUiCards(developerGrid);
    
    gbc.gridx = 1;    
    gbc.gridy = 1;      
    gbc.weightx = 0.80; // Claims the remaining 80% of window space
    gbc.weighty = 0.0;
    add(developerGrid, gbc);

    //Adjust location of button button
    JPanel midSpacer = new JPanel();
    midSpacer.setOpaque(false);
    gbc.gridx = 1;      
    gbc.gridy = 2;
    gbc.weightx = 1.0;
    gbc.weighty = 0.20; 
    add(midSpacer, gbc);


    JPanel southPanel = new JPanel();
    southPanel.setOpaque(false);
    southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));

    String gameName = props.getProperty("app.name", "BLINK MATCH MEMORY");
    String gameVersion = props.getProperty("app.version", "1.0.0");
    JLabel versionLabel = new JLabel(gameName + " (v" + gameVersion + ")", JLabel.CENTER);
    versionLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
    versionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    versionLabel.setForeground(Color.BLACK); 
    southPanel.add(versionLabel);
    

    ImageIcon backIcon = new ImageIcon("resources/images/backIcon.png");
    JButton backBtn = makeButton(backIcon, 175, 75);
    backBtn.setContentAreaFilled(false);
    backBtn.setBorderPainted(false);
    backBtn.setOpaque(false);
    backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
    backBtn.addActionListener(e -> {
        SoundPlayer.playClickEffect();
        onExit();
        cardLayout.show(GameWindow.container, "START");
    });
    southPanel.add(backBtn);

    gbc.gridx = 1;      // CHANGED from 0 to 1
    gbc.gridy = 3;
    gbc.weightx = 1.0;
    gbc.weighty = 0.15; 
    add(southPanel, gbc);
}

    private void buildDeveloperUiCards(JPanel targetGrid) {
        int i = 1;
        while (true) {
            String name = props.getProperty("dev." + i + ".name");
            if (name == null) {
                break; 
            }

            String github = props.getProperty("dev." + i + ".github", "");
            String contribution = props.getProperty("dev." + i + ".contribution", "");
            String imagePath = props.getProperty("dev." + i + ".image", "resources/images/defaultAvatar.png");

            //Creates developer cards
            JPanel card = new JPanel(new BorderLayout(15, 0));
            card.setOpaque(false);

            // Adjust Developer images
            ImageIcon devRaw = new ImageIcon(imagePath);
            Image scaledImg = devRaw.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
            JLabel devLabel = new JLabel(new ImageIcon(scaledImg));
            card.add(devLabel, BorderLayout.WEST);


            String htmlText = "<html>"
                    + "<font face='Monospaced' size='6' color='#333333'>" + github + "</font><br/>"
                    + "<u><font face='Monospaced' size='6' color='#000000'><b>" + name + "</b></font></u><br/>"
                    + "<font face='Monospaced' size='6' color='#555555'>" + contribution + "</font>"
                    + "</html>";

            JLabel infoLabel = new JLabel(htmlText);
            card.add(infoLabel, BorderLayout.CENTER);


            targetGrid.add(card);
            i++;
        }
        repaint();
    }

    

    @Override public void onEnter() {}
    @Override public void onExit()  {}
    
}