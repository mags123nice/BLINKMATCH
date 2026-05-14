package blinkmatch.panels;

import blinkmatch.*;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.KeyStroke;
import javax.swing.Timer;

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
        
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setBackground(bgColor());

        setBorder(BorderFactory.createEmptyBorder(450, 60, 0, 60));
        bg = new ImageIcon(
                        "src/resources/gifs/landscape.gif"
                ).getImage();

        //button imageicon
        ImageIcon startIcon = new ImageIcon("src/resources/images/startIcon.png");
        ImageIcon helpIcon = new ImageIcon("src/resources/images/helpIcon.png");
        ImageIcon quitIcon = new ImageIcon("src/resources/images/exitIcon.png");
        

        // --- title ---
        JLabel title = makeTitle("Blink Match Memory");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = makeSubtitle("Match all pairs before the storm hits!");
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // --- buttons ---
        int smolSizeX = 300;
        int smolSizeY = 200;
        int beegSizX = 400;
        int beegSizY = 375;

        JButton startBtn =  makeButton(startIcon, beegSizX, beegSizY);
        JButton quitButton =  makeButton(quitIcon,  smolSizeX, smolSizeY);
        JButton helpBtn  = makeButton(helpIcon,  smolSizeX, smolSizeY);

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

        // keyboard: S = start
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('S'), "start");
        getActionMap().put("start", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                onExit();
                SoundPlayer.playClickEffect();
                cardLayout.show(GameWindow.container, "GAME");
            }
        });
        add(Box.createHorizontalGlue());
        
        add(helpBtn);
        
        add(Box.createHorizontalStrut(30)); 
        
        add(startBtn);
        
        add(Box.createHorizontalStrut(30)); 
        
        add(quitButton);
        
        add(Box.createHorizontalGlue());

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
