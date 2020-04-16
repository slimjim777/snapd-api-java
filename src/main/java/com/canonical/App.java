package com.canonical;

import com.canonical.snapdapi.SnapdAPI;
import javafx.util.Pair;

import java.io.IOException;
import java.net.URISyntaxException;


public class App
{
    public static void main( String[] args ) {
        // Initialize the API
        SnapdAPI api = null;
        try {
            api = new SnapdAPI();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }


        // Get the system info from the API
        Pair<Integer, String> response = null;
        try {
            response = api.getSystemInfo();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.printf("HTTP Status: %d\n", response.getKey());
        System.out.printf("Body:\n%s", response.getValue());
    }

}
