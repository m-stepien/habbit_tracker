package com.keycloak.listener;

import com.google.gson.JsonObject;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class CustomEventListener implements EventListenerProvider {

    private final String address = "http://localhost:8080/points/create";
    public CustomEventListener() {
    }

    @Override
    public void onEvent(Event event) {
        if(event.getType().equals(EventType.REGISTER)){
            this.sendEvent(event);
        }
    }

    @Override
    public void onEvent(AdminEvent adminEvent, boolean b) {
        return ;
    }

    @Override
    public void close() {

    }

    private void sendEvent(Event event){
        try {
            URL url = new URL(address);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            JsonObject json = new JsonObject();
            json.addProperty("type", event.getType().toString());
            json.addProperty("userId", event.getUserId());
            json.addProperty("clientid", event.getClientId());
            try(OutputStream os =conn.getOutputStream()){
                os.write(json.toString().getBytes());
            }
            int responseCode = conn.getResponseCode();
            System.out.println("Response code webhook: "+responseCode);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
