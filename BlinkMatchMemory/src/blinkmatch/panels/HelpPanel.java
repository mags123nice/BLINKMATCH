package blinkmatch.panels;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;

import blinkmatch.GameWindow;

/**
 * Help / instructions screen.
 *
 * INHERITANCE:  extends BasePanel — reuses shared helpers.
 * POLYMORPHISM: overrides lifecycle methods.
 */
public class HelpPanel extends BasePanel implements Runnable {
    
    private Image helpImg;


    public HelpPanel(CardLayout cardLayout) {
        super(cardLayout);
        startPanel();
    }

    private  void startPanel()
    {
        // Method call so that panel is instantiated once setUp is happening
        initComponents();

    }

    @Override
    public void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(bgColor());
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        helpImg = new ImageIcon("src/resources/gifs/helpPage.gif").getImage();

        Thread start = new Thread(this);
        start.start();

        //content
        JPanel content = new JPanel(new GridLayout(1, 2, 16, 0));
        content.setBackground(bgColor());

        //imageIcons
        ImageIcon backIcon = new ImageIcon("src/resources/images/backIcon.png");

        //back btn
        //MAKE ICONIMAGE
        JButton backBtn = makeButton(backIcon, 150, 75);
        backBtn.setBackground(new Color(0,0,0,0));

        backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        backBtn.addActionListener(e -> {
            onExit();
            cardLayout.show(GameWindow.container, "START");
        });

        JPanel south = new JPanel();
        south.setBackground(new Color(0,0,0,0));
        south.add(backBtn);
        add(south, BorderLayout.SOUTH);
    }

    @Override
    public void run() {
        while(true) {
            repaint();
        }
    }

    private JPanel makeSection(String title, String[] lines) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(100, 149, 237), 1),
                title, TitledBorder.LEFT, TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 13), new Color(60, 60, 120)));

        for (String line : lines) {
            JLabel lbl = new JLabel(line);
            lbl.setFont(new Font("SansSerif", Font.PLAIN, 13));
            lbl.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
            p.add(lbl);
        }
        return p;
    }

    @Override
    public void paintComponent(Graphics g)
    {
        g.drawImage(
            helpImg,
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
