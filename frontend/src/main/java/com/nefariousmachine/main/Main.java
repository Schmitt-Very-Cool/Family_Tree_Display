package com.nefariousmachine.main;

import com.nefariousmachine.data.FamilyTree;
import com.nefariousmachine.local.LocalDisplay;

public class Main {
    public static void main(String[] args) {
        FamilyTree familyTree = new FamilyTree();
        //For now, the only option is local. In the future, this might allow for the user to run this program
        //as the backend for a larger web product.
        LocalDisplay display = new LocalDisplay(familyTree);
        display.setVisible(true);
    }
}
