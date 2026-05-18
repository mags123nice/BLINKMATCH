package blinkmatch.panels;

import blinkmatch.GameWindow;
import blinkmatch.SoundPlayer;
import blinkmatch.model.Card;
import blinkmatch.model.GameState;
import blinkmatch.model.Player;
import blinkmatch.weather.StormyWeather;
import blinkmatch.weather.SunnyWeather;
import blinkmatch.weather.Weather;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;

import java.awt.event.ActionEvent;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentAdapter;


import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
// import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
// import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.JPanel;
// import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.text.SimpleAttributeSet;

public class GamePanel extends BasePanel {

    //Game data 
    private Player    player;
    private GameState gameState;
    private Card[]    cards;
    JButton pauseBtn;

    private Weather currentWeather;

    //Swing components
    private JLabel   timerLabel, weatherLabel, scoreLabel;
    private JPanel   gridPanel;
    private JButton[] cardButtons;
    private Timer delay;
    
    //Pause Menu Components 
    private JPanel mainContent = new JPanel();
    private JPanel pauseOverlay;
    private boolean isPaused = false;

    //for gameover
    private JPanel gameOverOverlay;

    //Game loop 
    private Timer countdownTimer;
    private final List<Integer> flippedIndices = new ArrayList<>();
    private boolean canFlip = true;
    private boolean stormTriggered = false;
        private JLabel gameOverTitle;
    private JTextPane gameOverStats;
    

    private ImageIcon pausePicture = new ImageIcon("resources/images/pauseIcon3.png");

    // Constants 
    private static final ImageIcon[] SYMBOLS = {
        new ImageIcon("resources/images/Apple.png"),// 🍎
        new ImageIcon("resources/images/Banana.png"), // 🍌
        new ImageIcon("resources/images/Grapes.png"), // 🍇
        new ImageIcon("resources/images/StrawBerry.png"), // 🍓
        new ImageIcon("resources/images/Orange.png"), // 🍊
        new ImageIcon("resources/images/Lemon.png"), // 🍋
        new ImageIcon("resources/images/WaterMelon.png"), // 🍉
        new ImageIcon("resources/images/Peach.png"),  // 🍑
    };
    
    private static final int TOTAL_PAIRS = 8;
    private static final int GRID_SIZE   = TOTAL_PAIRS * 2;
    private static final int GAME_TIME   = 60;
    private static final int STORM_MAX = 45; 
    private static final int STORM_MIN = 15;
    private int randomStormTime; 
    private final Random rand = new Random();  

    // CARDS 
    private static final String PATH_CARD_BACK = "resources/images/cardback.png";
    private static final String PATH_STORM_BACK = "resources/images/stormIcon.png";
    private static final Color SUNNY_ORANGE = new Color(255, 150, 50);
    private static final Color STORMY_BLUE   = new Color(44, 62, 80);

    private static final Color COLOR_FACE_UP    = Color.BLACK;
    private static final Color COLOR_MATCHED    = new Color(144, 238, 144);

    //

    private int pauseBtnSize = 60;
    private GameWindow window;

    public GamePanel(CardLayout cardLayout,  GameWindow window) {
        super(cardLayout);
        this.window = window;

        this.player     = new Player("Player 1");
        this.gameState  = new GameState(GAME_TIME, TOTAL_PAIRS);
        setBackground(new Color(0, 0, 255, 255));

        initComponents();
    }

    // ── BasePanel lifecycle ──────────────────────────────────────────────────

    @Override
    public void initComponents() {
        setLayout(new BorderLayout()); // Root layout
        
        JLayeredPane layeredPane = new JLayeredPane();
        
        // Creates Main Content
        mainContent = new JPanel(new BorderLayout(8, 8))
        {
            
            @Override
            public void paintComponent(Graphics g)
            {
                    
                super.paintComponent(g);
                g.drawImage(
                    new ImageIcon("resources/images/areaBackground.jpg").getImage(),
                    0, 0, getWidth(), getHeight(), null
                );
            }
                
            
        };
        mainContent.setBackground(bgColor());
        mainContent.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        mainContent.add(buildHUD(), BorderLayout.NORTH);
        mainContent.add(buildGrid(), BorderLayout.CENTER);
        mainContent.add(buildButtons(), BorderLayout.SOUTH);
        
        // Pause Menu
        pauseOverlay = buildPauseMenu();
        gameOverOverlay = buildGameOver();
        
        // 3. Add both to the LayeredPane
       layeredPane.add(mainContent, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(pauseOverlay, JLayeredPane.MODAL_LAYER);
        layeredPane.add(gameOverOverlay, JLayeredPane.POPUP_LAYER);

        
  
        layeredPane.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int w = layeredPane.getWidth();
                int h = layeredPane.getHeight();
                mainContent.setBounds(0, 0, w, h);
                pauseOverlay.setBounds(0, 0, w, h);
                if (gameOverOverlay != null) {
                    gameOverOverlay.setBounds(0, 0, w, h);
                }
            }
        });

        
        try {         
            Clip sound;
            AudioInputStream audio = AudioSystem.getAudioInputStream(
                    new File("resources/music/itty.wav"));
            sound = AudioSystem.getClip();
            
            sound.open(audio);
            sound.loop(Clip.LOOP_CONTINUOUSLY);
            sound.start();

        } catch (Exception e) {
        }

        add(layeredPane, BorderLayout.CENTER);
        bindKeys();

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                if (!isPaused && !gameState.isRunning()) {
                    startGame();
                }
            }
            @Override
            public void componentHidden(ComponentEvent e) {
                stopTimer();
            }
        });

   

        repaint();
    }

    @Override
    public void onEnter() {             
        startGame();
        resumeGame(); 
        restartGame();  
    }

    @Override
    public void onExit()  { stopTimer(); }

    // ── UI builders ─────────────────────────────────────────────────────────

    private JPanel buildHUD() {
        JPanel hud = new JPanel(new GridLayout(1, 3, 4, 0))
        {
            
        };
        hud.setBackground(new Color(0, 0, 0, 0));
        hud.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));

        Font f = new Font("Monospaced", Font.BOLD, 55);

        timerLabel   = new JLabel("Timer: " + GAME_TIME, SwingConstants.CENTER);
        weatherLabel = new JLabel("\u2600 Sunny",SwingConstants.CENTER);
        scoreLabel   = new JLabel("Score: 0",SwingConstants.CENTER);

        for (JLabel l : new JLabel[]{timerLabel, weatherLabel, scoreLabel}) {
            l.setFont(f);
            hud.add(l);
        }
        repaint();
        return hud;
    }

    private JPanel buildGrid() {
        gridPanel = new JPanel(new GridLayout(4, 4, 8, 8))
        {
            @Override
            public void paintComponent(Graphics g)
            {
                super.paintComponent(g);
                g.drawImage(
                    new ImageIcon("resources/images/areaBackground.jpg").getImage(),
                    0, 0, getWidth(), getHeight(), null
                );

            }

        };
        gridPanel.setBackground(bgColor());
        cardButtons = new JButton[GRID_SIZE];

        for (int i = 0; i < GRID_SIZE; i++) {
            JButton btn = new JButton();
            btn.setFont(new Font("Monospaced", Font.PLAIN, 26));
        
            btn.setOpaque(true);
            btn.setBorderPainted(true);
            btn.setContentAreaFilled(true);
            btn.setBackground(SUNNY_ORANGE);
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
        JPanel south = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10)){

        };
        //south.setBackground(bgColor());


        

        ImageIcon pauseIcon = new ImageIcon("resources/images/pauseIcon3.png");        
        pauseBtn = makeButton(pauseIcon, pauseBtnSize, pauseBtnSize);
        pauseBtn.setRolloverEnabled(false);


        pauseBtn.addActionListener(e -> {
            pauseOverlay.repaint();
            ImageIcon psBtn3  =  new ImageIcon("resources/images/pauseIcon2.png");
            Dimension pauseButtonDimenstion = pauseBtn.getPreferredSize();
            pausePicture = new ImageIcon(psBtn3.getImage().getScaledInstance(pauseBtnSize, pauseBtnSize, Image.SCALE_SMOOTH));
            pauseBtn.repaint();
            pauseBtn.setIcon(pausePicture);
            pauseBtn.setContentAreaFilled(false);
            pauseGame();
            mainContent.repaint();
        });

        south.add(pauseBtn);
        south.setBackground(new Color(0, 0, 0, 0));
        return south;
    }
    
    private JPanel buildPauseMenu() {
        JPanel overlay = new JPanel(){
            @Override
            protected void paintComponent(Graphics g)
            {

                super.paintComponent(g);
                int panelWidth = getWidth();
                int panelHeight = getHeight();

                g.drawImage(
                        new ImageIcon("resources/images/PauseImage.png").getImage(),0,0,panelWidth, panelHeight,null);
            
            }
        }; 



        overlay.setBorder(new EmptyBorder(110, 12, 12, 10));
        // overlay.setBackground(Color.blue);
        overlay.repaint();
        overlay.setVisible(false); 
        overlay.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.NONE;  
        gbc.anchor = GridBagConstraints.CENTER; 
        gbc.insets = new Insets(10, 50, 10, 50);

        overlay.setOpaque(false);

        //Icons in Pause
        ImageIcon resumeIcon = new ImageIcon("resources/images/resumeIcon.png");
        ImageIcon restartIcon = new ImageIcon("resources/images/restartIcon.png");
        ImageIcon exitIcon = new ImageIcon("resources/images/quitIcon.png");
        
        int buttonX = 250;
        int buttonY = 100;
        
        JButton resumeBtn = makeButton(resumeIcon, buttonX, buttonY);
        JButton restartBtn = makeButton(restartIcon, buttonX, buttonY);
        JButton quitBtn    = makeButton(exitIcon, buttonX, buttonY);
         
        for (JButton btn : new JButton[]{resumeBtn, restartBtn, quitBtn}) {
            btn.setFont(new Font("Monospaced", Font.BOLD, 18));
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setFocusPainted(false);
            btn.setMaximumSize(new Dimension(buttonX, buttonY));
        }

         resumeBtn.addActionListener(e -> resumeGame());
        
        restartBtn.addActionListener(e -> { 
            pauseOverlay.repaint();
            mainContent.repaint();
            resumeGame(); 
            restartGame(); 
            pauseBtn.repaint();
        });
        
        quitBtn.addActionListener(e -> {
            
            onExit();
            overlay.setVisible(false);
            window.showPanel("START");
            // cardLayout.show(GameWindow.container, "START");
        });

        overlay.add(resumeBtn, gbc);
        overlay.add(restartBtn, gbc);        
        overlay.add(quitBtn, gbc);

        overlay.setBackground(new Color(0, 0, 0)); 
        overlay.repaint();

        return overlay;
    }

    private void bindKeys() {
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('R'), "restart");
        getActionMap().put("restart", new AbstractAction() {
            @Override
            public void actionPerformed( ActionEvent e) 
            { 
                restartGame();
                SoundPlayer.playClickEffect();
            }
        });

        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('E'), "exit");
        getActionMap().put("exit", new AbstractAction() {

            @Override
            public void actionPerformed( ActionEvent e) 
            { 
                onExit();
                SoundPlayer.playClickEffect();
                window.showPanel("START");

                // cardLayout.show(GameWindow.container, "START");
            }
            
        });
        
        // Key Inputs to Pause
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "togglePause");
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('P'), "togglePause");
        getActionMap().put("togglePause", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
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
        randomStormTime = rand.nextInt((STORM_MAX - STORM_MIN) + 1) + STORM_MIN;
        currentWeather  = new SunnyWeather();   
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
        canFlip = false; 
        isPaused = true;
        
        
        ImageIcon psBtn3  =  new ImageIcon("resources/images/pauseIcon2.png");
        
        pausePicture = new ImageIcon(psBtn3.getImage().getScaledInstance(pauseBtnSize,pauseBtnSize, Image.SCALE_SMOOTH));
        pauseBtn.setIcon(pausePicture);
        pauseBtn.repaint();

        pauseOverlay.setVisible(true); 


        
    }
    
    private void resumeGame() {
        if (!isPaused) return;
        pauseOverlay.setVisible(false);
        canFlip = true;
        isPaused = false;
        
        ImageIcon psBtn3  =  new ImageIcon("resources/images/pauseIcon3.png");
        
        pausePicture = new ImageIcon(psBtn3.getImage().getScaledInstance(50,50, Image.SCALE_SMOOTH));
        pauseBtn.setIcon(pausePicture);
        pauseBtn.repaint();
        

        // Re-start the timer where it left off
        countdownTimer = new Timer(1000, e -> tick());
        countdownTimer.start();
        gameState.setRunning(true);
    }

    private void buildCards() {
    // Dynamically addjust size based on the gridPanel's current size
        int cardW = gridPanel.getWidth() / 4 - 10; 
        int cardH = gridPanel.getHeight() / 4 - 10; 

        List<ImageIcon> symbolsList = new ArrayList<>();
        for (ImageIcon rawIcon : SYMBOLS) {
            Image img = rawIcon.getImage();
            Image scaledImg = img.getScaledInstance(cardW, cardH, Image.SCALE_SMOOTH);
            ImageIcon finishedIcon = new ImageIcon(scaledImg);

            symbolsList.add(finishedIcon);
            symbolsList.add(finishedIcon);
        }

        Collections.shuffle(symbolsList);
        cards = new Card[GRID_SIZE];
        for (int i = 0; i < GRID_SIZE; i++) {
            cards[i] = new Card(i, symbolsList.get(i));
            resetButtonVisual(i, getStretchedIcon(PATH_CARD_BACK), SUNNY_ORANGE);
        }
    }
    

    // ── Timer ────────────────────────────────────────────────────────────────

    private void startTimer() {
        stopTimer();
        gameState.setRunning(true);
        countdownTimer = new Timer(1000, e -> tick());
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
        mainContent.repaint();
        

        // trigger storm once at STORM_TIME
        if (!stormTriggered && gameState.getTimeRemaining() == randomStormTime) {
            stormTriggered = true;
            applyWeather(new StormyWeather());   
        }
        // revert to sunny 5 s later
        if (stormTriggered && gameState.getTimeRemaining() == randomStormTime - 5
                && currentWeather instanceof StormyWeather) {
            applyWeather(new SunnyWeather());
        }

        if (gameState.isTimeUp()) {
            stopTimer();
            showGameOver(false);
        }
    }

    // ── Weather ───────────────────────────────────────────────

    private void applyWeather(Weather weather) {
        currentWeather = weather;
        weatherLabel.setText(currentWeather.getDisplayText());

    if (currentWeather instanceof StormyWeather) { //Causes Background to Change Color depending on Weather
        gridPanel.setBackground(STORMY_BLUE); 
        this.setBackground(STORMY_BLUE);      
    } else {
        gridPanel.setBackground(bgColor());             
        this.setBackground(bgColor());
    }

    if (currentWeather.causesShuffle()) {
        shuffleUnmatched();
    }
}

    private void shuffleUnmatched() {
        List<Integer> unmatchedIdx = new ArrayList<>();
        List<ImageIcon>  unmatchedSym = new ArrayList<>();

        for (int i = 0; i < GRID_SIZE; i++) { //Sorts through the grid to create a list of all unmatched Symbols
            if (!cards[i].isMatched()) {
                unmatchedIdx.add(i);
                unmatchedSym.add(cards[i].getSymbol());
            }
        }
        Collections.shuffle(unmatchedSym); //Scrambles them

    for (int i = 0; i < unmatchedIdx.size(); i++) {//Add the remaining symbols back to the list
        int idx = unmatchedIdx.get(i);
        cards[idx] = new Card(idx, unmatchedSym.get(i));
        
            resetButtonVisual(idx, getStretchedIcon(PATH_STORM_BACK), STORMY_BLUE);
    }
    flippedIndices.clear();
}

    // ── Card interaction ─────────────────────────────────────────────────────

    private void onCardClicked(int idx) {
        if (!canFlip)                         return;
        if (cards[idx].isFaceUp())            return;
        if (cards[idx].isMatched())           return;
        if (flippedIndices.contains(idx))     return;

        // flip card face-up 
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

    private void checkMatch() 
    { //Checks if the next two cards flipped are the same
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
        delay = new Timer(700, e -> {
            cards[i1].setFaceUp(false);
            cards[i2].setFaceUp(false);
    
        Color currentBG = (currentWeather instanceof StormyWeather) ? STORMY_BLUE : SUNNY_ORANGE;

            resetButtonVisual(i1, getStretchedIcon(PATH_CARD_BACK), currentBG);
            resetButtonVisual(i2, getStretchedIcon(PATH_CARD_BACK), currentBG);
    
                flippedIndices.clear();
                if (!isPaused) canFlip = true;
            });
            delay.setRepeats(false);
            delay.start();
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private void resetButtonVisual(int idx, ImageIcon icon, Color bgColor) { //Helps reset Button Visual after certain triggers
        cardButtons[idx].setText("");
        cardButtons[idx].setIcon(icon);
        cardButtons[idx].setBackground(bgColor); 
        cardButtons[idx].setEnabled(true);
    }
    private void refreshHUD() { 
        timerLabel.setText("Timer: "   + gameState.getTimeRemaining());
        scoreLabel.setText("Score: "   + player.getScore());
        weatherLabel.setText(currentWeather.getDisplayText());
    }



    private JPanel buildGameOver() {

        JPanel overlay = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(
                    new ImageIcon("resources/images/gameOverBoard.png").getImage(),
                    0, 0, getWidth(), getHeight(), null
                );
            }
        };

        overlay.setBorder(BorderFactory.createEmptyBorder(125, 0, 0, 10));
        overlay.setLayout(new GridBagLayout());
        overlay.setOpaque(false);
        overlay.setVisible(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.CENTER;

        // ── TITLE ──
        gameOverTitle = new JLabel("", SwingConstants.CENTER);
        gameOverTitle.setFont(new Font("Monospaced", Font.BOLD, 200));
        gameOverTitle.setForeground(Color.BLACK);

        // ── STATS ──
        gameOverStats = new JTextPane();
        gameOverStats.setEditable(false);
        gameOverStats.setFont(new Font("Monospaced", Font.PLAIN, 100));
        
        gameOverStats.setOpaque(false);
        gameOverStats.setForeground(Color.BLACK);

        SimpleAttributeSet center = new SimpleAttributeSet();
        gameOverStats.setParagraphAttributes(center, false);

        // ── BUTTONS ──
        ImageIcon restartIcon = new ImageIcon("resources/images/restartIcon.png");
        ImageIcon exitIcon = new ImageIcon("resources/images/quitIcon.png");

        JButton restartBtn = makeButton(restartIcon,170, 70);
        JButton exitBtn = makeButton(exitIcon, 170, 70);

        restartBtn.addActionListener(e -> {
            hideGameOver();
            restartGame();
        });

        exitBtn.addActionListener(e -> {
            resumeGame();
            onExit();
            overlay.setVisible(false);
            window.showPanel("START");

            // cardLayout.show(GameWindow.container, "START");
        });

        // ── ADD IN ORDER ──
        gbc.gridy = 0;
        overlay.add(gameOverTitle, gbc);

        gbc.gridy = 1;
        overlay.add(gameOverStats, gbc);

        
        gbc.gridy = 2;
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 0));


        panel.add(restartBtn);
        panel.add(exitBtn);
        panel.setBackground(new Color(0, 0, 0, 0));
        



        //panel.setBackground(new Color(0, 0, 0, 0));
        overlay.add(panel, gbc);
        //overlay.add(Box.createVerticalStrut(75), gbc);
        
        // gbc.gridy = 3;

        return overlay;
    }

   

    private void showGameOver(boolean won) {

        stopTimer();
        canFlip = false;
        isPaused = true;

        player.addScore(gameState.getTimeRemaining()*10);

        if (gameOverOverlay == null) return;

        gameOverOverlay.setVisible(true);
        gameOverOverlay.revalidate();
        gameOverOverlay.repaint();
        gameOverOverlay.requestFocusInWindow();

        gameOverTitle.setFont(new Font("Monospaced", Font.BOLD, won ? 50 : 50));

        if (won) {
            gameOverTitle.setForeground(new Color(0, 220, 0)); // GREEN WIN
            gameOverTitle.setText("🏆 YOU WIN!");
        } else {
            gameOverTitle.setForeground(new Color(220, 0, 0)); // RED LOSE
            gameOverTitle.setText("YOU LOSE!");
        }

        boolean isNewHighScore = player.getScore() > GameWindow.highScore;
        if (isNewHighScore) {
            
            GameWindow.saveHighScore(player.getScore()); 
        };

        String msg =
                "Moves: " + player.getMoves() +
                "\nScore: " + player.getScore() +
                "\nTime Left: " + gameState.getTimeRemaining() + "\n";

        if (isNewHighScore) {
            msg += "NEW HIGH SCORE!\n";
            
        }


        // TEXT AREA styling directly here (no shared style vars)
        gameOverStats.setFont(new Font("Monospaced", Font.PLAIN, 40));
        gameOverStats.setForeground(Color.BLACK);
        gameOverStats.setText(msg);
    }

    private void hideGameOver() {
        gameOverOverlay.setVisible(false);
    }

    private ImageIcon getStretchedIcon(String path) { //Adjusts the image size to grid based on path
        ImageIcon raw = new ImageIcon(path);
            int w = cardButtons[0].getWidth();
            int h = cardButtons[0].getHeight();

        // If the grid hasn't rendered yet (first run), use a sensible default
            if (w <= 0) w = 200; 
            if (h <= 0) h = 200;

        Image scaled = raw.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        g.drawImage(
            new ImageIcon("resources/images/areaBackground.jpg").getImage(),
            0, 0, getWidth(), getHeight(), null
        );

    }
}