package com.keycloak.listener;

import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

public class CustomEventListenerFactory implements EventListenerProviderFactory {
    public static final String ID="custom-event-listener";

    @Override
    public EventListenerProvider create(KeycloakSession keycloakSession) {
        return new CustomEventListener();
    }

    @Override
    public void init(org.keycloak.Config.Scope scope) {

    }

    @Override
    public void postInit(KeycloakSessionFactory keycloakSessionFactory) {

    }

    @Override
    public void close() {

    }

    @Override
    public String getId() {
        return ID;
    }
}
