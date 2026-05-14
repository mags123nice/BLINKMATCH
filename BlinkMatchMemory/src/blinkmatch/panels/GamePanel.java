package blinkmatch.panels;

import blinkmatch.GameWindow;
import blinkmatch.SoundPlayer;
import blinkmatch.model.Card;
import blinkmatch.model.GameState;
import blinkmatch.model.Player;
import blinkmatch.weather.StormyWeather;
import blinkmatch.weather.SunnyWeather;
import blinkmatch.weather.Weather;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.sound.sampled.*;
import javax.swing.*;

/**
 * Main gameplay screen.
 *
 * All four OOP pillars are active here:
 * ENCAPSULATION  — Card / Player / GameState fields are private.
 * ABSTRACTION    — Weather used only via abstract type; BasePanel lifecycle used.
 * INHERITANCE    — extends BasePanel, inherits makeButton() / bgColor() etc.
 * POLYMORPHISM   — currentWeather variable is Weather; causesShuffle() /
 * getDisplayText() dispatch to the right subclass at runtime.
 */
public class GamePanel extends BasePanel {

    // ── Game data (encapsulated model objects) ───────────────────────────────
    private Player    player;
    private GameState gameState;
    private Card[]    cards;

    // ── Weather (polymorphism: type is abstract Weather) ─────────────────────
    private Weather currentWeather;

    // ── Swing components ─────────────────────────────────────────────────────
    private JLabel   timerLabel, weatherLabel, scoreLabel;
    private JPanel   gridPanel;
    private JButton[] cardButtons;
    
    // ── Layered Pane Components (For Pause Menu) ─────────────────────────────
    private JPanel pauseOverlay;
    private boolean isPaused = false;

    // ── Game loop ────────────────────────────────────────────────────────────
    private javax.swing.Timer countdownTimer;
    private final List<Integer> flippedIndices = new ArrayList<>();
    private boolean canFlip = true;
    private boolean stormTriggered = false;

    // ── Constants ────────────────────────────────────────────────────────────
    private static final ImageIcon[] SYMBOLS = {
        new ImageIcon("src/resources/images/Apple.png"),// 🍎
        new ImageIcon("src/resources/images/Banana.png"), // 🍌
        new ImageIcon("src/resources/images/Grapes.png"), // 🍇
        new ImageIcon("src/resources/images/StrawBerry.png"), // 🍓
        new ImageIcon("src/resources/images/Orange.png"), // 🍊
        new ImageIcon("src/resources/images/Lemon.png"), // 🍋
        new ImageIcon("src/resources/images/WaterMelon.png"), // 🍉
        new ImageIcon("src/resources/images/Peach.png"),  // 🍑
    };
    
    private static final int TOTAL_PAIRS = 8;
    private static final int GRID_SIZE   = TOTAL_PAIRS * 2;
    private static final int GAME_TIME   = 60;
    private static final int STORM_TIME  = 30;   // seconds remaining when storm hits

    // ── CARDS ───────────────────────────────────────────────────────────────
    int BACK_SIZE=225;
    private static final ImageIcon Card_Back  = new ImageIcon("src/resources/images/cardback.png");
    Image scaledCard = Card_Back.getImage().getScaledInstance(BACK_SIZE, BACK_SIZE, Image.SCALE_SMOOTH);
    ImageIcon ICON_FACE_DOWN = new ImageIcon(scaledCard);

    private static final Color COLOR_FACE_UP    = Color.WHITE;
    private static final Color COLOR_MATCHED    = new Color(144, 238, 144);

    private static final ImageIcon Storm_Back = new ImageIcon("src/resources/images/Avocado.png");
    Image scaledStorm = Storm_Back.getImage().getScaledInstance(BACK_SIZE, BACK_SIZE, Image.SCALE_SMOOTH);
    ImageIcon ICON_STORM_DOWN = new ImageIcon(scaledStorm);

    // ════════════════════════════════════════════════════════════════════════
    public GamePanel(CardLayout cardLayout) {
        super(cardLayout);
        
        this.player     = new Player("Player 1");
        this.gameState  = new GameState(GAME_TIME, TOTAL_PAIRS);
        initComponents();
    }

    // ── BasePanel lifecycle ──────────────────────────────────────────────────

    @Override
    public void initComponents() {
        setLayout(new BorderLayout()); // Root layout
        
        JLayeredPane layeredPane = new JLayeredPane();
        
        // 1. Create the Main Game Content (Bottom Layer)
        JPanel mainContent = new JPanel(new BorderLayout(8, 8));
        mainContent.setBackground(bgColor());
        mainContent.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        mainContent.add(buildHUD(),    BorderLayout.NORTH);
        mainContent.add(buildGrid(),   BorderLayout.CENTER);
        mainContent.add(buildButtons(), BorderLayout.SOUTH);
        
        // 2. Create the Pause Overlay (Top Layer)
        pauseOverlay = buildPauseMenu();
        
        // 3. Add both to the LayeredPane
        layeredPane.add(mainContent, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(pauseOverlay, JLayeredPane.MODAL_LAYER);
        
        // 4. Because JLayeredPane uses a null layout, we must dynamically resize 
        // our panels whenever the window size changes.
        layeredPane.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                int w = layeredPane.getWidth();
                int h = layeredPane.getHeight();
                mainContent.setBounds(0, 0, w, h);
                pauseOverlay.setBounds(0, 0, w, h);
            }
        });

        
        try {         
            Clip sound;
            AudioInputStream audio = AudioSystem.getAudioInputStream(
                    new File("src/resources/music/itty.wav"));
            sound = AudioSystem.getClip();
            
            sound.open(audio);
            sound.loop(Clip.LOOP_CONTINUOUSLY);
            sound.start();

        } catch (Exception e) {
        }

        add(layeredPane, BorderLayout.CENTER);
        bindKeys();

        // Native Swing fix: Start game ONLY when panel is shown
        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                if (!isPaused && !gameState.isRunning()) {
                    startGame();
                }
            }
            @Override
            public void componentHidden(java.awt.event.ComponentEvent e) {
                stopTimer();
            }
        });
    }

    @Override
    public void onEnter() { }

    @Override
    public void onExit()  { stopTimer(); }

    // ── UI builders ─────────────────────────────────────────────────────────

    private JPanel buildHUD() {
        JPanel hud = new JPanel(new GridLayout(1, 3, 4, 0));
        hud.setBackground(new Color(200, 225, 255));
        hud.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));

        Font f = new Font("SansSerif", Font.BOLD, 14);

        timerLabel   = new JLabel("Timer: " + GAME_TIME, SwingConstants.CENTER);
        weatherLabel = new JLabel("\u2600 Sunny",          SwingConstants.CENTER);
        scoreLabel   = new JLabel("Score: 0",              SwingConstants.CENTER);

        for (JLabel l : new JLabel[]{timerLabel, weatherLabel, scoreLabel}) {
            l.setFont(f);
            hud.add(l);
        }
        return hud;
    }

    private JPanel buildGrid() {
        gridPanel = new JPanel(new GridLayout(4, 4, 8, 8));
        gridPanel.setBackground(bgColor());
        cardButtons = new JButton[GRID_SIZE];

        for (int i = 0; i < GRID_SIZE; i++) {
            JButton btn = new JButton();
            btn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 26));
            btn.setIcon(ICON_FACE_DOWN);
            btn.setOpaque(true);
            btn.setBorderPainted(true);
            btn.setFocusPainted(false);
            btn.setCursor(customHoverCursor);

            final int idx = i;
            btn.addActionListener(e -> {
                SoundPlayer.playClickEffect();
                onCardClicked(idx);
            });
            cardButtons[i] = btn;
            gridPanel.add(btn);
        }
        return gridPanel;
    }

    private JPanel buildButtons() {
        // Reduced the horizontal gap from 30 to 10 fix pushed off screen
        JPanel south = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        south.setBackground(bgColor());

        // imageIcons 
        ImageIcon pauseIcon = new ImageIcon("src/resources/images/pauseIcon.png");
        

        // Scaled these down to atleast 130x50 so all fit
        
        JButton pauseBtn = makeButton(pauseIcon, 50, 50);

        pauseBtn.addActionListener(e -> {
            pauseGame();
            pauseOverlay.repaint();
        });

        

        // Add them in this exact order to force PAUSE into the middle
        south.add(pauseBtn);
        
        return south;
    }
    
    private JPanel buildPauseMenu() {
        // GridBagLayout automatically centers contents inside the panel
        JPanel overlay = new JPanel(new GridBagLayout()){
            @Override
            protected void paintComponent(Graphics g)
            {
                super.paintComponent(g);
                g.drawImage(
                    new ImageIcon("src/resources/images/PauseImage.png").getImage(),
                    0,
                    0,
                    750,
                    750,
                    null);
            }
        }; 
        overlay.repaint();
  
        overlay.setBackground(Color.blue);
        overlay.setBackground(new Color(0, 0, 0)); // Semi-transparent black
        overlay.setVisible(false); // Hidden by default

        JPanel menuBox = new JPanel();
        menuBox.setLayout(new BoxLayout(menuBox, BoxLayout.Y_AXIS));
        menuBox.setOpaque(false);

        JLabel pauseTitle = new JLabel("GAME PAUSED");
        pauseTitle.setFont(new Font("SansSerif", Font.BOLD, 40));
        pauseTitle.setForeground(Color.WHITE);
        pauseTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        ImageIcon resumeIcon = new ImageIcon("src/resources/images/resumeIcon.png");
        ImageIcon restartIcon = new ImageIcon("src/resources/images/restartIcon.png");
        ImageIcon exitIcon = new ImageIcon("src/resources/images/quitIcon.png");
        
        int buttonX = 150;
        int buttonY = 175;
        
        JButton resumeBtn = makeButton(resumeIcon, buttonX,buttonY);
        JButton restartBtn = makeButton(restartIcon, buttonX,buttonY);
        JButton quitBtn    = makeButton(exitIcon, buttonX,buttonY);

        for (JButton btn : new JButton[]{resumeBtn, restartBtn, quitBtn}) {
            btn.setFont(new Font("SansSerif", Font.BOLD, 18));
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setFocusPainted(false);
            btn.setMaximumSize(new Dimension(buttonX, buttonY));
        }

        resumeBtn.addActionListener(e -> resumeGame());
        
        restartBtn.addActionListener(e -> { 
            resumeGame(); 
            restartGame(); 
        });
        
        quitBtn.addActionListener(e -> {
            resumeGame();
            onExit();
            cardLayout.show(GameWindow.container, "START");
        });

        //menuBox.add(pauseTitle);

        menuBox.add(Box.createVerticalGlue());
        menuBox.add(resumeBtn);
        menuBox.add(Box.createRigidArea(new Dimension(10, 10)));
        menuBox.add(restartBtn);
        menuBox.add(Box.createRigidArea(new Dimension(10, 10)));
        menuBox.add(quitBtn);
        menuBox.add(Box.createVerticalGlue());
        
        overlay.add(menuBox);


        return overlay;
    }

    private void bindKeys() {
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('R'), "restart");
        getActionMap().put("restart", new AbstractAction() {
            @Override
            public void actionPerformed( java.awt.event.ActionEvent e) 
            { 
                restartGame();
                SoundPlayer.playClickEffect();
            }
        });

        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('E'), "exit");
        getActionMap().put("exit", new AbstractAction() {

            @Override
            public void actionPerformed( java.awt.event.ActionEvent e) 
            { 
                onExit();
                SoundPlayer.playClickEffect();
                cardLayout.show(GameWindow.container, "START");
            }
            
        });
        
        // Pressing Escape or 'P' pauses/unpauses
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "togglePause");
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('P'), "togglePause");
        getActionMap().put("togglePause", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                SoundPlayer.playClickEffect();
                if (isPaused) resumeGame();
                else pauseGame();
            }
        });
    }

    // ── Game flow & Pause Logic ──────────────────────────────────────────────

    public void startGame() {
        player.reset();
        gameState.reset(GAME_TIME);
        currentWeather  = new SunnyWeather();   // polymorphism: Weather reference
        stormTriggered  = false;
        flippedIndices.clear();
        canFlip = true;
        isPaused = false;
        pauseOverlay.setVisible(false);

        buildCards();
        refreshHUD();
        startTimer();
    }

    public void restartGame() { 
        startGame(); 
    }
    
    private void pauseGame() {
        if (!gameState.isRunning() || isPaused) return; // Don't pause if game is over
        stopTimer();
        canFlip = false; // Prevent card clicks
        isPaused = true;
        pauseOverlay.setVisible(true); // Show the semi-transparent overlay
        
    }
    
    private void resumeGame() {
        if (!isPaused) return;
        pauseOverlay.setVisible(false);
        canFlip = true;
        isPaused = false;
        
        // Re-start the timer where it left off
        countdownTimer = new javax.swing.Timer(1000, e -> tick());
        countdownTimer.start();
        gameState.setRunning(true);
    }

    private void buildCards() {
    int CARD_SIZE = 150; // Adjust this number to fit your grid buttons
    List<ImageIcon> symbolsList = new ArrayList<>();
    

    for (ImageIcon rawIcon : SYMBOLS) {
        // 1. Get the raw image from the ImageIcon
        Image img = rawIcon.getImage();
        
        // 2. Create the scaled version
        Image scaledImg = img.getScaledInstance(CARD_SIZE, CARD_SIZE, Image.SCALE_SMOOTH);
        
        // 3. Wrap it back into an ImageIcon
        ImageIcon finishedIcon = new ImageIcon(scaledImg);

        // 4. Add TWO to the list (one for each card in the pair)
        symbolsList.add(finishedIcon);
        symbolsList.add(finishedIcon);
    }

    // Shuffle so the pairs aren't sitting next to each other
            Collections.shuffle(symbolsList);

            cards = new Card[GRID_SIZE];
            for (int i = 0; i < GRID_SIZE; i++) {
        // Make sure your Card constructor accepts (int, ImageIcon)
               cards [i] = new Card(i, symbolsList.get(i));
                resetButtonVisual(i, ICON_FACE_DOWN);
            }
        }
    

    // ── Timer ────────────────────────────────────────────────────────────────

    private void startTimer() {
        stopTimer();
        gameState.setRunning(true);
        countdownTimer = new javax.swing.Timer(1000, e -> tick());
        countdownTimer.start();
    }

    private void stopTimer() {
        if (countdownTimer != null) {
            countdownTimer.stop();
            gameState.setRunning(false);
        }
    }

    private void tick() {
        gameState.decrementTime();
        timerLabel.setText("Timer: " + gameState.getTimeRemaining());

        // trigger storm once at STORM_TIME
        if (!stormTriggered && gameState.getTimeRemaining() == STORM_TIME) {
            stormTriggered = true;
            applyWeather(new StormyWeather());   // polymorphism
        }
        // revert to sunny 5 s later
        if (stormTriggered && gameState.getTimeRemaining() == STORM_TIME - 5
                && currentWeather instanceof StormyWeather) {
            applyWeather(new SunnyWeather());
        }

        if (gameState.isTimeUp()) {
            stopTimer();
            showGameOver(false);
        }
    }

    // ── Weather (polymorphism) ───────────────────────────────────────────────

    private void applyWeather(Weather weather) {
        currentWeather = weather;
        weatherLabel.setText(currentWeather.getDisplayText());

        if (currentWeather.causesShuffle()) {
            shuffleUnmatched();
        }
    }

    private void shuffleUnmatched() {
        List<Integer> unmatchedIdx = new ArrayList<>();
        List<ImageIcon>  unmatchedSym = new ArrayList<>();

        for (int i = 0; i < GRID_SIZE; i++) {
            if (!cards[i].isMatched()) {
                unmatchedIdx.add(i);
                unmatchedSym.add(cards[i].getSymbol());
            }
        }
        Collections.shuffle(unmatchedSym);

        for (int i = 0; i < unmatchedIdx.size(); i++) {
            int idx = unmatchedIdx.get(i);
            cards[idx] = new Card(idx, unmatchedSym.get(i));
            resetButtonVisual(idx, ICON_STORM_DOWN);
        }
        flippedIndices.clear();
    }

    // ── Card interaction ─────────────────────────────────────────────────────

    private void onCardClicked(int idx) {
        if (!canFlip)                         return;
        if (cards[idx].isFaceUp())            return;
        if (cards[idx].isMatched())           return;
        if (flippedIndices.contains(idx))     return;

        // flip card face-up (encapsulated setter)
        cards[idx].setFaceUp(true);
        cardButtons[idx].setIcon(cards[idx].getSymbol());
        cardButtons[idx].setBackground(COLOR_FACE_UP);
        flippedIndices.add(idx);

        if (flippedIndices.size() == 2) {
            player.incrementMoves();
            canFlip = false;
            checkMatch();
        }
    }

    private void checkMatch() {
        int i1 = flippedIndices.get(0);
        int i2 = flippedIndices.get(1);

        boolean isMatch = cards[i1].getSymbol().equals(cards[i2].getSymbol());

        if (isMatch) {
            cards[i1].setMatched(true);
            cards[i2].setMatched(true);
            cardButtons[i1].setBackground(COLOR_MATCHED);
            cardButtons[i2].setBackground(COLOR_MATCHED);
            cardButtons[i1].setEnabled(false);
            cardButtons[i2].setEnabled(false);

            player.addScore(10);
            gameState.incrementMatchedPairs();
            refreshHUD();
            flippedIndices.clear();
            canFlip = true;

            if (gameState.isComplete()) {
                stopTimer();
                showGameOver(true);
            }
        } else {
            // flip back after short delay
            javax.swing.Timer delay = new javax.swing.Timer(700, e -> {
                cards[i1].setFaceUp(false);
                cards[i2].setFaceUp(false);
                resetButtonVisual(i1, ICON_FACE_DOWN);
                resetButtonVisual(i2, ICON_FACE_DOWN);
                flippedIndices.clear();
                
                // Only allow flips again if the user hasn't paused the game during the delay!
                if (!isPaused) {
                    canFlip = true;
                }
            });
            delay.setRepeats(false);
            delay.start();
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private void resetButtonVisual(int idx, ImageIcon bg) {
        cardButtons[idx].setText("");
        cardButtons[idx].setIcon(null); 
        cardButtons[idx].setIcon(bg);
        cardButtons[idx].setEnabled(true);
    }

    private void refreshHUD() {
        timerLabel.setText("Timer: "   + gameState.getTimeRemaining());
        scoreLabel.setText("Score: "   + player.getScore());
        weatherLabel.setText(currentWeather.getDisplayText());
    }

    private void showGameOver(boolean won) {
        String msg;
        
        if (won) {
            int timeBonus = gameState.getTimeRemaining() * 10;
            player.addScore(timeBonus);
            
            // --- HIGH SCORE LOGIC ---
            boolean isNewHighScore = false;
            if (player.getScore() > GameWindow.highScore) {
                GameWindow.highScore = player.getScore();
                isNewHighScore = true;
            }
            
            msg = "You Win!\n"
                + "Time Bonus: +" + timeBonus + "\n"
                + "Final Score: " + player.getScore() + "\n"
                + "Moves: " + player.getMoves();
                
            // Add a fun message if they broke the record!
            if (isNewHighScore) {
                msg += "\n\n🏆 NEW HIGH SCORE! 🏆";
            }
            
        } else {
            msg = "Time's Up!\nFinal Score: " + player.getScore();
        }

        int choice = JOptionPane.showConfirmDialog(
                this, msg + "\n\nPlay again?", "Game Over",
                JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) restartGame();
        else { onExit(); cardLayout.show(GameWindow.container, "START"); }
    }
}