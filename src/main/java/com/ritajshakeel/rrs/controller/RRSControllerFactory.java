package com.ritajshakeel.rrs.controller;

import com.ritajshakeel.rrs.view.RRSView;

public interface RRSControllerFactory {
    RRSController create(RRSView view);
}