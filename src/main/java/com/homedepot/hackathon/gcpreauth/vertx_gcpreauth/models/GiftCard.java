package com.homedepot.hackathon.gcpreauth.vertx_gcpreauth.models;

public class GiftCard {
    private String cardNumber;
    private double amount;
    private String uuid;
    public GiftCard(String cardNumber, double amount, String uuid) {
        this.cardNumber = cardNumber;
        this.amount = amount;
        this.uuid = uuid;
    }

    public String getCardNumber() {
        return this.cardNumber;
    }

    public double getAmount() {
        return this.amount;
    }

    public String getUUID() {
        return this.uuid;
    }

}
