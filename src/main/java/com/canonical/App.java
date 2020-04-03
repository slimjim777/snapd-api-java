package com.canonical;

import com.canonical.snapdapi.SnapdAPI;
import javafx.util.Pair;

import java.io.IOException;


public class App
{
    public static void main( String[] args ) {
        try {
            // Initialize the API
            SnapdAPI api = new SnapdAPI();

            // Get the system info from the API
            Pair<Integer, String> response = api.getSystemInfo();

            System.out.printf("HTTP Status: %d\n", response.getKey());
            System.out.printf("Body:\n%s", response.getValue());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
