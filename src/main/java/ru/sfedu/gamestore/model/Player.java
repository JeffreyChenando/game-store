package ru.sfedu.gamestore.model;

import java.util.ArrayList;

public class Player extends User{
    private ArrayList<Long> games;


    public ModelType getType() {
        return ModelType.PLAYER;
    }

    public ArrayList<Long> getGames() {
        return games;
    }

    public void setGames(ArrayList<Long> games) {
        this.games = games;
    }

    @Override
    public String toString() {
        return "Player{" + super.toString() +
                ", games=" + games +
                "} ";
    }
}
