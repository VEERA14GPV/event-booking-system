package com.booking.security.websocket;

import java.security.Principal;

public class WebSocketPrincipal
        implements Principal {

    private final String name;

    public WebSocketPrincipal(
            String name) {

        this.name = name;
    }

    @Override
    public String getName() {

        return name;
    }
}