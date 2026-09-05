package com.ritajshakeel.rrs;

import java.awt.EventQueue;

import com.google.inject.Guice;

import com.ritajshakeel.rrs.guice.RRSModule;
import com.ritajshakeel.rrs.view.swing.RRSSwingView;

public class Main {
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
        	try {
                RRSSwingView view = Guice.createInjector(new RRSModule())
                    .getInstance(RRSSwingView.class);
                view.setVisible(true);
                view.getController().loadResources();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}