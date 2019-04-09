package ru.sfedu.gamestore.api;

import org.apache.log4j.Logger;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.convert.Converter;
import org.simpleframework.xml.convert.Registry;
import org.simpleframework.xml.convert.RegistryStrategy;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.strategy.Strategy;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;
import ru.sfedu.gamestore.model.Model;
import ru.sfedu.gamestore.model.ModelType;

import java.io.*;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DataProviderXml implements IDataProvider{
    private static final Logger logger = Logger.getLogger(DataProviderXml.class);

    private String dirPath;
    Serializer serializer;

    public DataProviderXml(String dirPath){
        File dir = new File(dirPath);

        if(!dir.exists()) {
            if(dir.mkdirs()) {
                logger.info("Directory " + dirPath + " is created");
            }
            else logger.error("Directory " + dirPath + " is NOT created");
        }

        this.dirPath = dirPath;

        Registry registry = new Registry();
        try {
            registry.bind(LocalDateTime.class, LocalDateTimeConverter.class);
        } catch (FileNotFoundException ex){} catch (Exception ex) {
            logger.error(ex);
        }
        Strategy strategy = new RegistryStrategy(registry);
        serializer = new Persister(strategy);
    }

    private String getFilePath(ModelType type) {
        return Paths.get(dirPath, type.toString() + ".xml").toString();
    }

    private static class LocalDateTimeConverter implements Converter<LocalDateTime> {
        @Override
        public LocalDateTime read(InputNode node) throws Exception {
            return LocalDateTime.parse(node.getValue());
        }

        @Override
        public void write(OutputNode node, LocalDateTime value) throws Exception {
            node.setValue(value.toString());
        }
    }

    private static class Models {
        private List<Model> list;

        public void add(Model model){
            if(list == null){
                list = new ArrayList<>();
            }
            list.add(model);
        }

        public void setList(List<Model> list){
            this.list = list;
        }

        public List<Model> getList(){
            return list;
        }
    }

    @Override
    public long saveOrUpdate(Model model) {
        long id = model.getId();
        if(id == 0 || getById(model.getType(), model.getId()) == null) {
            long maxId = getAll(model.getType())
                    .stream()
                    .mapToLong(Model::getId)
                    .max().orElse(0);
            id = maxId + 1;
            model.setId(id);
        } else {
            delete(model.getType(), model.getId());
        }
        List<Model> all = getAll(model.getType());
        try(FileOutputStream fos = new FileOutputStream(getFilePath(model.getType()), false)) {
            Models models = new Models();
            models.setList(all);
            models.add(model);
            serializer.write(models, fos);
            logger.info("Record has written into " + getFilePath(model.getType()) + ", " + model.toString());
        } catch (Exception ex) {
            logger.info(ex);
        }
        return id;
    }

    @Override
    public void delete(ModelType type, long id) {
        List<Model> all = getAll(type);
        try(FileOutputStream fos = new FileOutputStream(getFilePath(type))) {

            Models models = new Models();
            for(Model current : all){
                if( current.getId() != id){
                    models.add(current);
                }
            }

            serializer.write(models, fos);
            logger.info("Record has written into " + getFilePath(type));
        } catch (Exception ex) {
            logger.info(ex);
        }
    }

    @Override
    public List<Model> getAll(ModelType type) {
        try(FileInputStream in = new FileInputStream(getFilePath(type));
            Reader reader = new InputStreamReader(in)) {

            // Reading
            Models result = serializer.read(Models.class, reader, false);
            if(result != null && result.getList() != null){
                return result.getList();
            }
        } catch (Exception ex) {
            logger.info(ex);
        }
        return new ArrayList<>(); //returns empty list if fail
    }

    @Override
    public Model getById(ModelType type, long id) {
        for(Model model: getAll(type)){
            if(model.getId() == id){
                return model;
            }
        }
        return null;
    }

}
