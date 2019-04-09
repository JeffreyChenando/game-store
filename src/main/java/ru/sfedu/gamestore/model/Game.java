package ru.sfedu.gamestore.model;

public class Game implements Model{
    private long id;
    private String title;
    private long publisherId;
    private float price;
    private GameGenre genre;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ModelType getType() {
        return ModelType.GAME;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(long publisherId) {
        this.publisherId = publisherId;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public GameGenre getGenre() {
        return genre;
    }

    public void setGenre(GameGenre genre) {
        this.genre = genre;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Game game = (Game) o;

        if (id != game.id) return false;
        if (publisherId != game.publisherId) return false;
        if (Float.compare(game.price, price) != 0) return false;
        return title != null ? title.equals(game.title) : game.title == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (int) (publisherId ^ (publisherId >>> 32));
        result = 31 * result + (price != +0.0f ? Float.floatToIntBits(price) : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Game{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", publisherId=" + publisherId +
                ", price=" + price +
                ", genre=" + genre +
                '}';
    }
}
