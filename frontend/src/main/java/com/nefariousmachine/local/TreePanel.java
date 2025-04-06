package com.nefariousmachine.local;

import com.nefariousmachine.data.FamilyTree;
import com.nefariousmachine.generate.TreeImageGenerator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class TreePanel extends JPanel {
    private static final double ZOOM_COEFFICIENT = 1.1;

    private BufferedImage treeImage;
    private int x = 0;
    private int y = 0;
    private double zoom = 1.0;

    public TreePanel(){
        setPreferredSize(new Dimension(800,450));
        setBackground(Color.WHITE);

        addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                zoom *= Math.pow(ZOOM_COEFFICIENT, e.getPreciseWheelRotation());
                zoom = Math.max(Math.min(zoom, 0.1), 4.0);
            }
        });
    }

    public void setTreeImage() {
        this.treeImage = TreeImageGenerator.getFamilyTreeImage();
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
        TreeImageGenerator.draw();
        this.treeImage = TreeImageGenerator.getFamilyTreeImage();
        repaint();
    }
}
