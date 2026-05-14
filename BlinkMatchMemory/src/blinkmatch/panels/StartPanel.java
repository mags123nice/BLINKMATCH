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
    private JLabel highScoreLabel;

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
    public void initComponents() {
        setLayout(new BorderLayout());
        setBackground(bgColor());

        // Button icons
        ImageIcon startIcon = new ImageIcon("src/resources/images/startIcon.png");
        ImageIcon helpIcon = new ImageIcon("src/resources/images/helpIcon.png");
        ImageIcon quitIcon = new ImageIcon("src/resources/images/exitIcon.png");

        // Build buttons
        JButton startBtn = makeButton(startIcon, 170, 150);
        JButton quitButton = makeButton(quitIcon, 125, 95);
        JButton helpBtn = makeButton(helpIcon, 125, 95);

        startBtn.addActionListener(e -> {
            onExit();
            cardLayout.show(GameWindow.container, "GAME");
        });
        helpBtn.addActionListener(e -> {
            onExit();
            cardLayout.show(GameWindow.container, "HELP");
        });

        quitButton.addActionListener(e -> {
            System.exit(0);
        });

        // Keyboard: S = start
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('S'), "start");
        getActionMap().put("start", new AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                onExit();
                cardLayout.show(GameWindow.container, "GAME");
            }
        });

        // ─── BULLETPROOF VERTICAL LAYOUT ──────────────────────────
        
        // 1. Create an overlay that strictly stacks items Top-to-Bottom (Y_AXIS)
        JPanel overlayBox = new JPanel();
        overlayBox.setLayout(new BoxLayout(overlayBox, BoxLayout.Y_AXIS));
        overlayBox.setOpaque(false); // Keeps GIF visible

        // 2. Build the High Score Label
        highScoreLabel = new JLabel("HIGH SCORE: " + GameWindow.highScore);
        highScoreLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        highScoreLabel.setForeground(Color.WHITE);
        // Force the text to center horizontally
        highScoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT); 

        // 3. Build the Button Row (stacks buttons Left-to-Right)
        JPanel buttonRow = new JPanel();
        buttonRow.setLayout(new BoxLayout(buttonRow, BoxLayout.X_AXIS));
        buttonRow.setOpaque(false);
        
        buttonRow.add(Box.createHorizontalGlue());
        buttonRow.add(helpBtn);
        buttonRow.add(Box.createHorizontalStrut(15));
        buttonRow.add(startBtn);
        buttonRow.add(Box.createHorizontalStrut(15));
        buttonRow.add(quitButton);
        buttonRow.add(Box.createHorizontalGlue());

        // 4. Stack them together using our invisible "Spring"
        overlayBox.add(Box.createVerticalStrut(30)); // 30px gap from the top of window
        overlayBox.add(highScoreLabel);              // Snap High Score to top
        
        // --- THE MAGIC SPRING ---
        // This expands to fill ALL empty space, pushing the buttons down
        overlayBox.add(Box.createVerticalGlue());    
        
        overlayBox.add(buttonRow);                   // Snap buttons to bottom
        overlayBox.add(Box.createVerticalStrut(40)); // 40px gap from the bottom of window

        // Add the finished layout to the panel
        add(overlayBox, BorderLayout.CENTER);
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
