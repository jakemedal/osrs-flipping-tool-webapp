package com.osrs.fliptool.service;

public class GEItem {
    private String name;
    private int id;
    private int buyAvg;
    private int sellAvg;
    private int buyQuantity;
    private int sellQuantity;
    private boolean members;

    GEItem(String name,
           int id,
           int buyAvg, int sellAvg,
           int buyQuantity, int sellQuantity,
           boolean members) {
        this.name = name;
        this.id = id;
        this.buyAvg = buyAvg;
        this.sellAvg = sellAvg;
        this.buyQuantity = buyQuantity;
        this.sellQuantity = sellQuantity;
        this.members = members;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBuyAvg() {
        return buyAvg;
    }

    public void setBuyAvg(int buyAvg) {
        this.buyAvg = buyAvg;
    }

    public int getSellAvg() {
        return sellAvg;
    }

    public void setSellAvg(int sellAvg) {
        this.sellAvg = sellAvg;
    }

    public int getBuyQuantity() {
        return buyQuantity;
    }

    public void setBuyQuantity(int buyQuantity) {
        this.buyQuantity = buyQuantity;
    }

    public int getSellQuantity() {
        return sellQuantity;
    }

    public void setSellQuantity(int sellQuantity) {
        this.sellQuantity = sellQuantity;
    }

    public boolean isMembers() {
        return members;
    }

    public void setMembers(boolean members) {
        this.members = members;
    }

    public int getProfitGP(){
        return sellAvg-buyAvg;
    }

    public int getProfitPercent(){
        double buy = buyAvg;
        double sell = sellAvg;
        return (int)(((sell-buy)/buy)*100);
    }

    public int getPotentialProfit(){
        return Math.max(buyQuantity, sellQuantity) * this.getProfitGP();
    }

}
