package ru.sfedu.gamestore.model;

import java.time.LocalDateTime;

public class Transaction implements Model{
    private long id;
    private TransactionType TransType;
    private long PlayerId;
    private long GameId;
    private LocalDateTime time;
    private float price;

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    @Override
    public ModelType getType() {
        return ModelType.TRANSACTION;
    }

    public TransactionType getTransType() {
        return TransType;
    }

    public void setTransType(TransactionType transType) {
        TransType = transType;
    }

    public long getPlayerId() {
        return PlayerId;
    }

    public void setPlayerId(long playerId) {
        PlayerId = playerId;
    }

    public long getGameId() {
        return GameId;
    }

    public void setGameId(long gameId) {
        GameId = gameId;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Transaction that = (Transaction) o;

        if (id != that.id) return false;
        if (PlayerId != that.PlayerId) return false;
        if (GameId != that.GameId) return false;
        if (Float.compare(that.price, price) != 0) return false;
        if (TransType != that.TransType) return false;
        return time != null ? time.equals(that.time) : that.time == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (TransType != null ? TransType.hashCode() : 0);
        result = 31 * result + (int) (PlayerId ^ (PlayerId >>> 32));
        result = 31 * result + (int) (GameId ^ (GameId >>> 32));
        result = 31 * result + (time != null ? time.hashCode() : 0);
        result = 31 * result + (price != +0.0f ? Float.floatToIntBits(price) : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", TransType=" + TransType +
                ", PlayerId=" + PlayerId +
                ", GameId=" + GameId +
                ", time=" + time +
                ", price=" + price +
                '}';
    }
}
