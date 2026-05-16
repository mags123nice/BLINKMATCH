package blinkmatch.panels;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import blinkmatch.*;

public class HelpPanel extends BasePanel implements Runnable {
    
    private Image helpImg;


    public HelpPanel(CardLayout cardLayout) {
        super(cardLayout);
        startPanel();
    }

    private  void startPanel() {
   
        initComponents();

    }

    @Override
    public void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(bgColor());
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        helpImg = new ImageIcon("resources/gifs/helpPage.gif").getImage();

        Thread start = new Thread(this);
        start.start();

        //content
        JPanel content = new JPanel(new GridLayout(1, 2, 16, 0));
        content.setBackground(bgColor());

        //imageIcons
        ImageIcon backIcon = new ImageIcon("resources/images/backIcon.png");

        //MAKE ICONIMAGE
        JButton backBtn = makeButton(backIcon, 150, 75);
        backBtn.setBackground(new Color(0,0,0,0));

        backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        backBtn.addActionListener(e -> {
            SoundPlayer.playClickEffect();
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

    
    @Override public void onEnter() {}
    @Override public void onExit()  {}
}
