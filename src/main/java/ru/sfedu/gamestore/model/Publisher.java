package ru.sfedu.gamestore.model;

public class Publisher extends User {
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ModelType getType() {
        return ModelType.PUBLISHER;
    }

    @Override
    public String toString() {
        return "Publisher{" + super.toString() +
                ", description=" + description +
                "} ";
    }
}
