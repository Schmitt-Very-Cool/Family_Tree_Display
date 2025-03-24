package com.nefariousmachine.local;

import com.nefariousmachine.data.FamilyTree;
import com.nefariousmachine.generate.TreeImageGenerator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class TreePanel extends JPanel {
    private BufferedImage treeImage;
    private int x = 0;
    private int y = 0;
    private double zoom = 1.0;

    public TreePanel(){
        setPreferredSize(new Dimension(800,450));
        setBackground(Color.WHITE);

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
