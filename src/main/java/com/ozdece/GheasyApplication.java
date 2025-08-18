package com.ozdece;

import com.formdev.flatlaf.FlatDarkLaf;

public class GheasyApplication {
    public static void main(String[] args) {
        //Set up the theme
        FlatDarkLaf.setup();

        final FrmRepository frmRepository = new FrmRepository();

        frmRepository.setVisible(true);
    }
}