package blinkmatch.panels;

import blinkmatch.GameWindow;
import blinkmatch.model.Card;
import blinkmatch.model.GameState;
import blinkmatch.model.Player;
import blinkmatch.weather.StormyWeather;
import blinkmatch.weather.SunnyWeather;
import blinkmatch.weather.Weather;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
    private static final String[] SYMBOLS = {
        "\uD83C\uDF4E", // 🍎
        "\uD83C\uDF4C", // 🍌
        "\uD83C\uDF47", // 🍇
        "\uD83C\uDF53", // 🍓
        "\uD83C\uDF4A", // 🍊
        "\uD83C\uDF4B", // 🍋
        "\uD83C\uDF49", // 🍉
        "\uD83C\uDF51"  // 🍑
    };

    private static final int TOTAL_PAIRS = 8;
    private static final int GRID_SIZE   = TOTAL_PAIRS * 2;
    private static final int GAME_TIME   = 60;
    private static final int STORM_TIME  = 30;   // seconds remaining when storm hits

    // ── Colors ───────────────────────────────────────────────────────────────
    private static final Color COLOR_FACE_DOWN  = new Color(239, 159, 39);
    private static final Color COLOR_FACE_UP    = Color.WHITE;
    private static final Color COLOR_MATCHED    = new Color(144, 238, 144);
    private static final Color COLOR_STORM_DOWN = new Color(100, 149, 200);

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
            JButton btn = makeButton( new ImageIcon("src/resources/images/cardback.png"), 300, 300);
            btn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 26));
            btn.setBackground(COLOR_FACE_DOWN);
            btn.setOpaque(true);
            btn.setBorderPainted(true);
            btn.setFocusPainted(false);
            btn.setCursor(customHoverCursor);

            final int idx = i;
            btn.addActionListener(e -> onCardClicked(idx));
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

        pauseBtn.addActionListener(e -> pauseGame());

        

        // Add them in this exact order to force PAUSE into the middle
        south.add(pauseBtn);
        
        return south;
    }
    
    private JPanel buildPauseMenu() {
        // GridBagLayout automatically centers contents inside the panel
        JPanel overlay = new JPanel(new GridBagLayout()); 
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
        ImageIcon exitIcon = new ImageIcon("src/resources/images/exitIcon.png");
        
        JButton resumeBtn = makeButton(resumeIcon, 150,90);
        JButton restartBtn = makeButton(restartIcon, 150,90);
        JButton quitBtn    = makeButton(exitIcon, 150,90);

        for (JButton btn : new JButton[]{resumeBtn, restartBtn, quitBtn}) {
            btn.setFont(new Font("SansSerif", Font.BOLD, 18));
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setFocusPainted(false);
            btn.setMaximumSize(new Dimension(200, 50));
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

        menuBox.add(pauseTitle);
        menuBox.add(Box.createVerticalStrut(40)); // Space between title and buttons
        menuBox.add(resumeBtn);
        menuBox.add(Box.createVerticalStrut(15));
        menuBox.add(restartBtn);
        menuBox.add(Box.createVerticalStrut(15));
        menuBox.add(quitBtn);

        overlay.add(menuBox);
        return overlay;
    }

    private void bindKeys() {
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('R'), "restart");
        getActionMap().put("restart", new AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent e) { restartGame(); }
        });

        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('E'), "exit");
        getActionMap().put("exit", new AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                onExit();
                cardLayout.show(GameWindow.container, "START");
            }
        });
        
        // Pressing Escape or 'P' pauses/unpauses
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "togglePause");
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('P'), "togglePause");
        getActionMap().put("togglePause", new AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
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
        List<String> symbols = new ArrayList<>();
        for (String s : SYMBOLS) { symbols.add(s); symbols.add(s); }
        Collections.shuffle(symbols);

        cards = new Card[GRID_SIZE];
        for (int i = 0; i < GRID_SIZE; i++) {
            cards[i] = new Card(i, symbols.get(i));   // encapsulated Card objects
            resetButtonVisual(i, COLOR_FACE_DOWN);
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
        List<String>  unmatchedSym = new ArrayList<>();

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
            resetButtonVisual(idx, COLOR_STORM_DOWN);
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
        cardButtons[idx].setText(cards[idx].getSymbol());
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
                resetButtonVisual(i1, COLOR_FACE_DOWN);
                resetButtonVisual(i2, COLOR_FACE_DOWN);
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

    private void resetButtonVisual(int idx, Color bg) {
        cardButtons[idx].setText("");
        cardButtons[idx].setBackground(bg);
        cardButtons[idx].setEnabled(true);
    }

    private void refreshHUD() {
        timerLabel.setText("Timer: "   + gameState.getTimeRemaining());
        scoreLabel.setText("Score: "   + player.getScore());
        weatherLabel.setText(currentWeather.getDisplayText());
    }

    private void showGameOver(boolean won) {
        String msg = won
            ? "You Win!\nScore: " + player.getScore() + "  |  Moves: " + player.getMoves()
            : "Time's Up!\nFinal Score: " + player.getScore();

        int choice = JOptionPane.showConfirmDialog(
                this, msg + "\n\nPlay again?", "Game Over",
                JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) restartGame();
        else { onExit(); cardLayout.show(GameWindow.container, "START"); }
    }
}