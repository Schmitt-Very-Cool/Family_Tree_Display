package com.nefariousmachine.local;

import com.nefariousmachine.generate.TreeImageGenerator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

public class TreePanel extends JPanel {
    private BufferedImage treeImage;
    private int x = 0;
    private int y = 0;
    private double zoom = 1.0;
    private int selectX = -1;
    private int selectY = -1;

    public TreePanel(){
        setPreferredSize(new Dimension(400,400));
        setBackground(Color.WHITE);
        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onClick(e);
            }

            @Override public void mousePressed(MouseEvent e) {}
            @Override public void mouseReleased(MouseEvent e) {}
            @Override public void mouseEntered(MouseEvent e) {}
            @Override public void mouseExited(MouseEvent e) {}
        });
    }

    public void setTreeImage(BufferedImage treeImage) {
        this.treeImage = treeImage;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(treeImage != null) {
            g.drawImage(treeImage, 0, 0, null);
        }
    }

    public void onClick(MouseEvent e) {
        int realX = e.getX() + x;
        int realY = e.getY() + y;
        TreeImageGenerator.select(realX, realY);
        TreeImageGenerator.generate();
        this.treeImage = TreeImageGenerator.getFamilyTreeImage();
        repaint();
    }
}
