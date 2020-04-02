package com.canonical;

import com.canonical.snapdapi.SnapdAPI;

import java.io.IOException;


public class App 
{
    public static void main( String[] args ) {
        try {
            SnapdAPI api = new SnapdAPI();
            api.getSystemInfo();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
