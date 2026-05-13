package blinkmatch.panels;

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
 *   ENCAPSULATION  — Card / Player / GameState fields are private.
 *   ABSTRACTION    — Weather used only via abstract type; BasePanel lifecycle used.
 *   INHERITANCE    — extends BasePanel, inherits makeButton() / bgColor() etc.
 *   POLYMORPHISM   — currentWeather variable is Weather; causesShuffle() /
 *                    getDisplayText() dispatch to the right subclass at runtime.
 */
public class GamePanel extends BasePanel {

    // ── CardLayout navigation ────────────────────────────────────────────────
    private final CardLayout cardLayout;
    private final JPanel     container;

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
    public GamePanel(CardLayout cardLayout, JPanel container) {
        this.cardLayout = cardLayout;
        this.container  = container;
        this.player     = new Player("Player 1");
        this.gameState  = new GameState(GAME_TIME, TOTAL_PAIRS);
        initComponents();
    }

    // ── BasePanel lifecycle ──────────────────────────────────────────────────

    @Override
    public void initComponents() {
        panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(bgColor());
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        panel.add(buildHUD(),    BorderLayout.NORTH);
        panel.add(buildGrid(),   BorderLayout.CENTER);
        panel.add(buildButtons(), BorderLayout.SOUTH);

        bindKeys();
    }

    /** Called by GameWindow right before GAME panel is shown. */
    @Override
    public void onEnter() { startGame(); }

    /** Called by GameWindow right before GAME panel is hidden. */
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
            btn.setBackground(COLOR_FACE_DOWN);
            btn.setOpaque(true);
            btn.setBorderPainted(true);
            btn.setFocusPainted(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            final int idx = i;
            btn.addActionListener(e -> onCardClicked(idx));
            cardButtons[i] = btn;
            gridPanel.add(btn);
        }
        return gridPanel;
    }

    private JPanel buildButtons() {
        JPanel south = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 4));
        south.setBackground(bgColor());

        //imageIcons 
        ImageIcon restartIcon = new ImageIcon("src/resources/images/restartIcon.png");
        ImageIcon exitIcon = new ImageIcon("src/resources/images/restartIcon.png");

        JButton restartBtn = makeButton(restartIcon);
        JButton exitBtn    = makeButton(exitIcon);

        restartBtn.addActionListener(e -> restartGame());
        exitBtn.addActionListener(e -> {
            onExit();
            cardLayout.show(container, "START");
        });

        south.add(restartBtn);
        south.add(exitBtn);
        return south;
    }

    private void bindKeys() {
        panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
             .put(KeyStroke.getKeyStroke('R'), "restart");
        panel.getActionMap().put("restart", new AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent e) { restartGame(); }
        });

        panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
             .put(KeyStroke.getKeyStroke('E'), "exit");
        panel.getActionMap().put("exit", new AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                onExit();
                cardLayout.show(container, "START");
            }
        });
    }

    // ── Game flow ────────────────────────────────────────────────────────────

    public void startGame() {
        player.reset();
        gameState.reset(GAME_TIME);
        currentWeather  = new SunnyWeather();   // polymorphism: Weather reference
        stormTriggered  = false;
        flippedIndices.clear();
        canFlip = true;

        buildCards();
        refreshHUD();
        startTimer();
    }

    public void restartGame() { startGame(); }

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
        if (countdownTimer != null) countdownTimer.stop();
        gameState.setRunning(false);
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

    /**
     * Applies weather effects via the abstract Weather type.
     * POLYMORPHISM: causesShuffle() and getDisplayText() resolve at runtime.
     */
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
                canFlip = true;
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
                panel, msg + "\n\nPlay again?", "Game Over",
                JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) restartGame();
        else { onExit(); cardLayout.show(container, "START"); }
    }
}
