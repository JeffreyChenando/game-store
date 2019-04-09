package ru.sfedu.gamestore.model;

public class Review implements Model{
    private long id;
    private long playerId;
    private long gameId;
    private int mark;
    private String text;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public ModelType getType() {
        return ModelType.REVIEW;
    }

    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    public long getGameId() {
        return gameId;
    }

    public void setGameId(long gameId) {
        this.gameId = gameId;
    }

    public int getMark() {
        return mark;
    }

    public void setMark(int mark) {
        this.mark = mark;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Review review = (Review) o;

        if (id != review.id) return false;
        if (playerId != review.playerId) return false;
        if (gameId != review.gameId) return false;
        if (mark != review.mark) return false;
        return text != null ? text.equals(review.text) : review.text == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (int) (playerId ^ (playerId >>> 32));
        result = 31 * result + (int) (gameId ^ (gameId >>> 32));
        result = 31 * result + mark;
        result = 31 * result + (text != null ? text.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Review{" +
                "id=" + id +
                ", playerId=" + playerId +
                ", gameId=" + gameId +
                ", mark=" + mark +
                ", text='" + text + '\'' +
                '}';
    }
}
