package com.nefariousmachine.local;

import com.nefariousmachine.generate.TreeImageGenerator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class TreePanel extends JPanel {
    private static final double ZOOM_COEFFICIENT = 1.1;

    private BufferedImage treeImage;
    private int x = 0;
    private int y = 0;
    private double zoom = 1.0;
    private boolean isPanning = false;
    private int prevX = -1;
    private int prevY = -1;

    public TreePanel(){
        setPreferredSize(new Dimension(800,450));
        setBackground(new Color(230, 230, 230));

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (isPanning) {
                    if (prevX != -1 && prevY != -1) {
                        int dx = e.getX() - prevX;
                        int dy = e.getY() - prevY;
                        x -= (int) (dx / zoom);
                        y -= (int) (dy / zoom);
                    }
                    prevX = e.getX();
                    prevY = e.getY();
                    repaint();
                }
            }
        });

        addMouseListener(new MouseListener() {
            @Override public void mouseClicked(MouseEvent e) {}
            @Override
            public void mousePressed(MouseEvent e) {
                if(e.getButton() == MouseEvent.BUTTON3){
                    isPanning = true;
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if(e.getButton() == MouseEvent.BUTTON3){
                    isPanning = false;
                    prevX = -1; prevY = -1;
                }
            }

            @Override public void mouseEntered(MouseEvent e) {}
            @Override public void mouseExited(MouseEvent e) {}
        });

        addMouseWheelListener(e -> {
            double prevZoom = zoom;
            zoom *= Math.pow(ZOOM_COEFFICIENT, -e.getPreciseWheelRotation());
            zoom = Math.min(Math.max(zoom, 0.1), 4.0);

            double mouseX = e.getX();
            double mouseY = e.getY();

            x += (int) (mouseX / prevZoom - mouseX / zoom);
            y += (int) (mouseY / prevZoom - mouseY / zoom);

            repaint();
        });
    }

    public void setTreeImage(BufferedImage image) {
        this.treeImage = image;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(treeImage != null) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            AffineTransform at = new AffineTransform();
            at.translate(-x * zoom, -y * zoom);
            at.scale(zoom, zoom);
            g2d.drawImage(treeImage, at, null);
        }
    }

    public void onClick(MouseEvent e, TreeImageGenerator treeImageGenerator) {
        if(e.getButton() == MouseEvent.BUTTON1) {
            int realX = (int) (e.getX() / zoom + x);
            int realY = (int) (e.getY() / zoom + y);
            treeImageGenerator.select(realX, realY);
            treeImageGenerator.draw();
            this.treeImage = treeImageGenerator.getFamilyTreeImage();
            repaint();
        }
    }
}
