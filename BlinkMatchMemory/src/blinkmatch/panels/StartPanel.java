package blinkmatch.panels;

import blinkmatch.*;
import java.awt.*;
import javax.swing.*;




/**
 * The opening/title screen.
 *
 * INHERITANCE:  extends BasePanel — reuses makeTitle(), makeButton(), bgColor().
 * POLYMORPHISM: overrides onEnter() / onExit() with its own behaviour.
 */
public class StartPanel extends BasePanel{


    private Image bg = null;
    

    public StartPanel(CardLayout cardLayout) {
        super (cardLayout);
        startPanel();
    }

    private  void startPanel() {
        initComponents();
        javax.swing.Timer animationTimer = new javax.swing.Timer(16, e -> repaint());
    animationTimer.start();
    }

   

    @Override
    public  void initComponents() {
        
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setBackground(bgColor());

        setBorder(BorderFactory.createEmptyBorder(450, 60, 0, 60));
        // panel.setImage(new Image(new File("src/resources/gifs/landscape.gif")));
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

        //startBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        //helpBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

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
            public void actionPerformed(java.awt.event.ActionEvent e) {
                onExit();
                SoundPlayer.playClickEffect();
                cardLayout.show(GameWindow.container, "GAME");
            }
        });

        // --- layout ---
        //add(Box.createVerticalGlue());
        //add(title);
        //add(Box.createVerticalStrut(8));
        //add(subtitle);
        //add(Box.createVerticalStrut(40));
        // add(helpBtn);
        // add(Box.createVerticalStrut(0));
        // // add(Box.createVerticalStrut(0));
        // add(startBtn);
        // add(Box.createVerticalStrut(0));
        // // add(Box.createVerticalStrut(0));
        // add(quitButton);
        // add(Box.createVerticalGlue());

        // repaint();

        add(Box.createHorizontalGlue());
        
        // 2. Add Help Button
        add(helpBtn);
        
        // 3. Add a horizontal strut to create a gap between Help and Play
        add(Box.createHorizontalStrut(30)); 
        
        // 4. Add Play (Start) Button
        add(startBtn);
        
        // 5. Add a horizontal strut to create a gap between Play and Quit
        add(Box.createHorizontalStrut(30)); 
        
        // 6. Add Quit Button
        add(quitButton);
        
        // 7. Add horizontal glue to push everything to the left (towards the center)
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

    @Override public void onEnter() { /* nothing special needed */ }
    @Override public void onExit()  { /* nothing special needed */ }
}
