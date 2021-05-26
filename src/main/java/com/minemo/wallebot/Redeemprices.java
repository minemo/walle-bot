package com.minemo.wallebot;

import java.util.Hashtable;

//Class for referencing prices of redeems

public class Redeemprices {
    public Hashtable<String, Integer> prices = new Hashtable<>();

    public Redeemprices() {
        this.prices.put("spause", 10);
        this.prices.put("sskip", 5);
        this.prices.put("wall", 50);
    }
}
