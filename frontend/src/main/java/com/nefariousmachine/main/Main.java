package com.nefariousmachine.main;

import com.nefariousmachine.local.LocalDisplay;

public class Main {
    public static void main(String[] args) {
        //For now, the only option is local. In the future, this will allow for the user to run this program
        //as the backend for a larger web product.

        LocalDisplay.run();
    }
}
