package com.nefariousmachine.generate;

import com.nefariousmachine.data.FamilyTree;
import com.nefariousmachine.data.Person;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Singleton class containing functions related to the creation of the family tree image.
 */
public class TreeImageGenerator {
    public static final int BOX_WIDTH = 200;
    public static final int BOX_HEIGHT = 100;
    public static final int TEXT_SIZE = 20;
    public static final int PADDING = 100;

    public static final Font baseFont = new Font(Font.SERIF, Font.PLAIN, TEXT_SIZE);

    /**
     * The family tree image. Prior to generate() being called, familyTree is null.
     */
    private static BufferedImage familyTreeImage;
    private static FamilyTree familyTree;
    private static int[][] graph;
    private static ArrayList<Integer> selected = new ArrayList<>();

    public static BufferedImage getFamilyTreeImage() {
        return familyTreeImage;
    }

    public static FamilyTree getFamilyTree() {
        return familyTree;
    }

    public static void setFamilyTree(FamilyTree ft) {
        familyTree = ft;
    }

    /**
     * Generates the graph representing the family tree. Runs in O(n^2) time.
     */
    public static void generate() {
        //Assigns each person a generation such that any Person has a higher generation number than their children.
        //Assigns generations in O(n^2) time.
        if(familyTree == null) {
            return;
        }
        HashMap<Person, Integer> generations = new HashMap<>();
        for(Person member : familyTree.getMembers()) {
            if(!generations.containsKey(member)) {
                generations.put(member, 0);
                LinkedList<Person> parentQueue = new LinkedList<>(); //FIFO queue
                parentQueue.add(member);
                //follow family tree upwards. Iterative approach to avoid stack overflows for large trees.
                while(!parentQueue.isEmpty()) {
                    Person current = parentQueue.poll();
                    Person[] parents = familyTree.getParents(current);
                    if(parents == null) {
                        continue;
                    }
                    if(parents[0] != null) {
                        int temp = 0;
                        if(generations.containsKey(parents[0]))
                            temp = generations.get(parents[0]);
                        generations.put(parents[0], Math.max(temp, generations.get(current) + 1));
                        parentQueue.add(parents[0]);
                    }
                    if(parents[1] != null) {
                        int temp = 0;
                        if(generations.containsKey(parents[1]))
                            temp = generations.get(parents[1]);
                        generations.put(parents[1], Math.max(temp, generations.get(current) + 1));
                        parentQueue.add(parents[1]);
                    }
                }
            }
        }

        //Determine dimensions of graph
        HashMap<Integer, Integer> generationFrequency = new HashMap<>();
        int height = 0; //set to max value in generations, i.e. highest generation
        int width = 0; //set to max value in generationFrequency, i.e. the number of Persons in the generation with the most persons
        for(Map.Entry<Person, Integer> entry : generations.entrySet()) {
            if(generationFrequency.containsKey(entry.getValue())) {
                generationFrequency.put(entry.getValue(), generationFrequency.get(entry.getValue()) + 1);
            } else {
                generationFrequency.put(entry.getValue(), 1);
            }
            if(height < entry.getValue()) {
                height = entry.getValue();
            }
        }
        height++; //Generations start counting at zero, so you need one more to represent the height of the graph
        for(Map.Entry<Integer, Integer> entry : generationFrequency.entrySet()) {
            if(width < entry.getValue()) {
                width = entry.getValue();
            }
        }

        //Generate graph as a 2d array.
        //This is bad design but right now it's a proof of concept so I can test functionality elsewhere.
        int[] positions = new int[height];
        graph = new int[width][height];
        for(int x = 0; x < width; x++){
            for(int y = 0; y < height; y++){
                graph[x][y] = -1;
            }
        }
        for(Map.Entry<Person, Integer> entry : generations.entrySet()) {
            graph[positions[entry.getValue()]][entry.getValue()] = entry.getKey().getId();
            positions[entry.getValue()]++;
        }

        //Print graph
        for(int y = height - 1; y >= 0; y--) {
            for(int x = 0; x < width; x++) {
                if(graph[x][y] != -1) {
                    System.out.print(familyTree.getMemberById(graph[x][y]).getName() + " ");
                }
            }
            System.out.println();
        }

        //Draw Graph
        int image_width = BOX_WIDTH * width + PADDING * (width + 1);
        int image_height = BOX_HEIGHT * height + PADDING * (height + 1);
        familyTreeImage = new BufferedImage(image_width, image_height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = familyTreeImage.createGraphics();
        g2d.fillRect(0, 0, image_width, image_height);
        g2d.setStroke(new BasicStroke(2));
        g2d.setColor(Color.BLACK);
        for(int x = 0; x < width; x++) {
            for(int y = 0; y < height; y++) {
                if(graph[x][y] == -1){
                    continue;
                }
                if(selected.contains(graph[x][y])) {
                    g2d.setColor(Color.RED);
                } else {
                    g2d.setColor(Color.BLACK);
                }
                int box_x = PADDING + x * (BOX_WIDTH + PADDING);
                int box_y = PADDING + y * (BOX_HEIGHT + PADDING);
                g2d.drawRect(box_x, box_y, BOX_WIDTH, BOX_HEIGHT);
                writeBoxInfo(g2d, box_x, box_y, familyTree.getMemberById(graph[x][y]).toString());
            }
        }
    }

    /**
     * Draws the provided string into a box of size (BOX_WIDTH, BOX_HEIGHT), at location (x,y) to the provided
     * graphics. The size of the font is TEXT_SIZE by default, but will be made smaller to fit the box. A margin of 2px
     * is ensured on the left, right, top, and bottom.
     *
     * @param g2d graphics on which to draw str.
     * @param x x-coordinate of the top-left of the box.
     * @param y y-coordinate of the top-left of the box.
     * @param str string to be drawn.
     */
    private static void writeBoxInfo(Graphics2D g2d, int x, int y, String str){
        Font font = new Font(baseFont.getName(), baseFont.getStyle(), baseFont.getSize());
        g2d.setFont(font);

        while(true) {
            FontMetrics metrics = g2d.getFontMetrics(font);
            int lineHeight = metrics.getHeight();
            int maxLineWidth = BOX_WIDTH - 4;
            int maxBoxHeight = BOX_HEIGHT - 4;

            ArrayList<String> wrappedLines = new ArrayList<>();
            String[] lines = str.split("\\n");

            for (String line : lines) {
                StringBuilder currentLine = new StringBuilder();
                for (String word : line.split(" ")) {
                    String testLine = currentLine.isEmpty() ? word : currentLine + " " + word;
                    if (metrics.stringWidth(testLine) > maxLineWidth) {
                        if (!currentLine.isEmpty()) {
                            wrappedLines.add(currentLine.toString());
                        }
                        currentLine = new StringBuilder(word);
                    } else {
                        currentLine = new StringBuilder(testLine);
                    }
                }
                if (!currentLine.isEmpty()) {
                    wrappedLines.add(currentLine.toString());
                }
            }

            int totalTextHeight = wrappedLines.size() * lineHeight;
            if(totalTextHeight <= maxBoxHeight) {
                int startY = y + (BOX_HEIGHT - totalTextHeight) / 2 + metrics.getAscent();
                for(int i = 0; i < wrappedLines.size(); i++) {
                    String line = wrappedLines.get(i);
                    int lineWidth = metrics.stringWidth(line);
                    int startX = x + (BOX_WIDTH - lineWidth) / 2;
                    g2d.drawString(line, startX, startY + i * lineHeight);
                }
                return;
            }

            //If text still doesn't fit, reduce font size.
            font = font.deriveFont((float) font.getSize() - 1);
            g2d.setFont(font);
        }
    }

    /**
     * Adds person whose box contains the provided coordinates to the selected arraylist. If no box is at the
     * coordinates or the coordinates are out of bounds, the selected arraylist is cleared. If the person whose
     * box contains those coordinates is already in the selected arraylist, it is instead removed.
     *
     * @param x x coordinate to check
     * @param y y coordinate to check
     */
    public static void select(int x, int y) {
        System.out.println("Selecting at ("+x+","+y+")");
        if (familyTreeImage == null || x < 0 || y < 0 ||
                x >= familyTreeImage.getWidth() || y >= familyTreeImage.getHeight() ||
                x % (BOX_WIDTH + PADDING) < PADDING || y % (BOX_HEIGHT + PADDING) < PADDING) {
            System.out.println("Case 1");
            selected.clear();
            return;
        }
        int arrayX = x / (BOX_WIDTH + PADDING);
        int arrayY = y / (BOX_HEIGHT + PADDING);

        int selectedPersonId = graph[arrayX][arrayY];
        if(selectedPersonId == -1) {
            System.out.println("Case 2");
            selected.clear();
            return;
        }
        if(selected.contains(selectedPersonId)) {
            System.out.println("Case 3");
            selected.remove(Integer.valueOf(selectedPersonId));
        } else {
            System.out.println("Selected ID " + selectedPersonId);
            selected.add(selectedPersonId);
        }
    }

    /**
     * @return array of the ids of the selected persons
     */
    public static Person[] getSelected() {
        int[] s = new int[selected.size()];
        if(selected.isEmpty()) {
            return new Person[0];
        }
        for(int i = 0; i < selected.size(); i++) {
            s[i] = selected.get(i);
        }
        return familyTree.getPersons(s);
    }
}
