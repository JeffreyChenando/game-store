package ru.sfedu.gamestore.api;

import ru.sfedu.gamestore.model.Model;
import ru.sfedu.gamestore.model.ModelType;

import java.util.List;

public interface IDataProvider {
    long saveOrUpdate(Model model);
    void delete(ModelType type, long id);
    List<Model> getAll(ModelType type);
    Model getById(ModelType type, long id);
}









