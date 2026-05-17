package blinkmatch.panels;

import blinkmatch.*;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Color;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import java.awt.Font;

public class StartPanel extends BasePanel{


    private Image bg = null;
    

    public StartPanel(CardLayout cardLayout) {
        super (cardLayout);
        startPanel();
    }

    private  void startPanel() {
        initComponents();
        Timer animationTimer = new Timer(16, e -> repaint());
    animationTimer.start();
    }

   

    @Override
    public  void initComponents() {
        
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(bgColor());

        setBorder(BorderFactory.createEmptyBorder(600, 60, 20, 60));
        bg = new ImageIcon(
                        "resources/gifs/landscape.gif"
                ).getImage();

        //button imageicon
        ImageIcon startIcon = new ImageIcon("resources/images/startIcon.png");
        ImageIcon helpIcon = new ImageIcon("resources/images/helpIcon.png");
        ImageIcon quitIcon = new ImageIcon("resources/images/exitIcon.png");
        ImageIcon creditIcon = new ImageIcon("resources/images/credits.png");
        

        // --- title ---
        // JLabel title = makeTitle("Blink Match Memory");
        // title.setAlignmentX(Component.CENTER_ALIGNMENT);

        // JLabel subtitle = makeSubtitle("Match all pairs before the storm hits!");
        // subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        GameWindow.loadHighScore();
        JLabel highScore = new JLabel("Current Highscore: " + GameWindow.highScore + "!" );
        highScore.setFont(new Font("Comic Sans MS", Font.BOLD, 50));
        highScore.setForeground(Color.BLACK);
        
        highScore.setAlignmentX(Component.CENTER_ALIGNMENT);

        // --- buttons ---
        int smolSizeX = 250;
        int smolSizeY = 100;
        // int beegSizX = 425;
        // int beegSizY = 375;

        JButton startBtn =  makeButton(startIcon, smolSizeX, smolSizeY);
        JButton quitButton =  makeButton(quitIcon,  smolSizeX, smolSizeY);
        JButton helpBtn  = makeButton(helpIcon,  smolSizeX, smolSizeY);
        JButton creditBtn  = makeButton(creditIcon,  smolSizeX, smolSizeY);

        startBtn.addActionListener(e -> {
            SoundPlayer.playClickEffect();
            onExit();
            cardLayout.show(GameWindow.container, "GAME");
        });
        helpBtn.addActionListener(e -> {
            SoundPlayer.playClickEffect();
            onExit();
            cardLayout.show(GameWindow.container, "HELP");
        });

        quitButton.addActionListener(e -> {
            SoundPlayer.playClickEffect();
            System.exit(0);
        });
        creditBtn.addActionListener(e -> {
            SoundPlayer.playClickEffect();
            cardLayout.show(GameWindow.container, "CREDITS");
        });

        // keyboard: S = start
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('S'), "start");
        getActionMap().put("start", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                onExit();
                SoundPlayer.playClickEffect();
                cardLayout.show(GameWindow.container, "GAME");
            }
        });
        JPanel buttonRow = new JPanel();
        buttonRow.setLayout(new BoxLayout(buttonRow, BoxLayout.X_AXIS));
        buttonRow.setOpaque(false);
        buttonRow.add(Box.createHorizontalGlue());
        
        buttonRow.add(helpBtn);
        
        buttonRow.add(Box.createHorizontalStrut(30)); 
        
        buttonRow.add(startBtn);
        
        buttonRow.add(Box.createHorizontalStrut(30)); 
        
        buttonRow.add(quitButton);

        buttonRow.add(Box.createHorizontalStrut(30)); 
        
        buttonRow.add(creditBtn);
        
        buttonRow.add(Box.createHorizontalGlue());

        add(Box.createVerticalGlue());
        add(buttonRow);              
        add(Box.createVerticalStrut(4));
        add(highScore);
        add(Box.createVerticalGlue());

        

        repaint();
    }

    @Override
    public void paintComponent(Graphics g)
    {
        g.drawImage(
            bg,
            0,
            0,
            getWidth(),
            getHeight(),
            null
        );

    }

    @Override public void onEnter() {  }
    @Override public void onExit()  {  }
}
