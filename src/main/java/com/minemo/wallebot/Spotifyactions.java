package com.minemo.wallebot;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

//Class for webserver-based redeems

public class Spotifyactions {
    private final HttpClient client;
    private String serverip;

    public Spotifyactions() {
        this.client = HttpClient.newHttpClient();
        this.serverip = "http://127.0.0.1:8000";
    }

    public void pause() {
        var request = HttpRequest.newBuilder(
                URI.create(this.serverip+"/spause"))
                .headers("accept", "application/json")
                .build();
        try {
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
            /*TODO if intent matches and successful is true, then count the request
                otherwise report failure in User readable output*/
        } catch (IOException e) {
            System.out.println("Connection Error!");
        } catch (InterruptedException ignored) {
        }
    }

    public void skip(int numsongs) {
        var request = HttpRequest.newBuilder(
                URI.create(this.serverip+"/skip/?num=" + numsongs))
                .headers("accept", "application/json")
                .build();
        try {
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
            /*TODO if intent matches and successful is true, then count the request
                otherwise report failure in User readable output*/
        } catch (IOException e) {
            System.out.println("Connection Error!");
        } catch (InterruptedException ignored) {
        }
    }

    public void changewall(String url) {
        var request = HttpRequest.newBuilder(
                URI.create(this.serverip+"/wall?img=" + url))
                .headers("accept", "application/json")
                .build();
        try {
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
            /*TODO if intent matches and successful is true, then count the request
                otherwise report failure in User readable output*/
        } catch (IOException e) {
            System.out.println("Connection Error!");
        } catch (InterruptedException ignored) {
        }
    }

}
