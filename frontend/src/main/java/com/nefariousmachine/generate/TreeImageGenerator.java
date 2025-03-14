package com.nefariousmachine.generate;

import java.awt.image.BufferedImage;

/**
 * Class containing functions related to the creation of the family tree image.
 */
public class TreeImageGenerator {
    /**
     * The family tree image. Prior to generate() being called, familyTree is null.
     */
    private static BufferedImage familyTree;

    public static BufferedImage getFamilyTree() {
        return familyTree;
    }

    public static void generate(){

    }
}
