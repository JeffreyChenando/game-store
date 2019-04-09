package ru.sfedu.gamestore.api;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import org.apache.log4j.Logger;
import ru.sfedu.gamestore.model.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataProviderCsv implements IDataProvider{
    private static final Logger logger = Logger.getLogger(DataProviderCsv.class);

    private String dirPath;

    public DataProviderCsv(String dirPath){

        File dir = new File(dirPath);
        if(!dir.exists()) {
            if(dir.mkdirs()) {
                logger.info("Directory " + dirPath + " is created");
            }
            else logger.error("Directory " + dirPath + " is NOT created");
        }

        this.dirPath = dirPath;
    }

    private String getFilePath(ModelType type) {
        return Paths.get(dirPath, type.toString() + ".csv").toString();
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

        try(FileOutputStream fos = new FileOutputStream(getFilePath(model.getType()), true);
            Writer writer = new OutputStreamWriter(fos);
            CSVWriter csvWriter = new CSVWriter(writer))
        {
            csvWriter.writeNext(modelToStringArray(model));
        } catch (IOException ex) {
            logger.error(ex);
        }
        return id;
    }

    @Override
    public void delete(ModelType type, long id) {
        List<Model> all = getAll(type);
        try(FileOutputStream fos = new FileOutputStream(getFilePath(type), false);
            Writer writer = new OutputStreamWriter(fos);
            CSVWriter csvWriter = new CSVWriter(writer))
        {
            for(Model current: all){
                if(id != current.getId()){
                    csvWriter.writeNext(modelToStringArray(current));
                }
            }
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    @Override
    public List<Model> getAll(ModelType type) {
        List<Model> all = new ArrayList<>();
        try (Reader fileReader = new FileReader(getFilePath(type));
             CSVReader reader = new CSVReader(fileReader)) {
            reader.readAll().forEach(row -> {
                try {
                    all.add(stringArrayToModel(row, type));
                } catch (Exception e) {
                    logger.warn(e.getMessage());
                }
            });
            return all;
        } catch (FileNotFoundException ex){} catch (IOException ex) {
            logger.error(ex);
        }
        return new ArrayList<>();
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


    public static ArrayList<Long> arrayLongFromString(String str){
        ArrayList<Long> result = new ArrayList<>();
        Pattern p = Pattern.compile("-?\\d+");
        Matcher m = p.matcher(str);

        while(m.find()){
            result.add(Long.valueOf(m.group()));
        }
        return result;
    }

    public static String[] modelToStringArray(Model model){
        switch (model.getType()){
            case PLAYER:
                Player player = (Player) model;
                return new String[]{
                        String.valueOf(player.getId()),
                        player.getName(),
                        player.getLogin(),
                        player.getPassword(),
                        String.valueOf(player.getAccount()),
                        player.getGames().toString()
                };
            case PUBLISHER:
                Publisher publisher = (Publisher) model;
                return new String[]{
                        String.valueOf(publisher.getId()),
                        publisher.getName(),
                        publisher.getLogin(),
                        publisher.getPassword(),
                        String.valueOf(publisher.getAccount()),
                        publisher.getDescription()
                };
            case GAME:
                Game game = (Game) model;
                return new String[]{
                        String.valueOf(game.getId()),
                        game.getTitle(),
                        String.valueOf(game.getPublisherId()),
                        String.valueOf(game.getPrice()),
                        game.getGenre().toString()
                };
            case TRANSACTION:
                Transaction trans = (Transaction) model;
                return new String[]{
                        String.valueOf(trans.getId()),
                        trans.getTransType().toString(),
                        String.valueOf(trans.getPlayerId()),
                        String.valueOf(trans.getGameId()),
                        trans.getTime().toString(),
                        String.valueOf(trans.getPrice())
                };
            case REVIEW:
                Review review = (Review) model;
                return new String[]{
                        String.valueOf(review.getId()),
                        String.valueOf(review.getPlayerId()),
                        String.valueOf(review.getGameId()),
                        String.valueOf(review.getMark()),
                        review.getText()
                };
            default:
                    throw new IllegalArgumentException(model.toString());
        }
    }

    public static Model stringArrayToModel(String[] row, ModelType type) throws Exception {
        List<String> errors = new ArrayList<>();
        String id = row[0];
        if(!id.matches("\\d+")){
            errors.add("id should be number");
        }
        switch (type){
            case PLAYER:
                if (!row[4].matches("[+-]?([0-9]*[.])?[0-9]+")){
                    errors.add("incorrect account");
                }
                if(!row[5].matches("^\\[(\\d+[,]\\s?)*\\d*\\]$")){
                    errors.add("incorrect list of games");
                }
                if (errors.size() != 0){
                    throw new Exception("Player " + id + ": " + errors.toString());
                }
                Player player = new Player();
                player.setId(Long.parseLong(id));
                player.setName(row[1]);
                player.setLogin(row[2]);
                player.setPassword(row[3]);
                player.setAccount(Float.parseFloat(row[4]));
                player.setGames(arrayLongFromString(row[5]));

                return player;

            case PUBLISHER:
                if (!row[4].matches("[+-]?([0-9]*[.])?[0-9]+")){
                    errors.add("incorrect account");
                }
                if (errors.size() != 0){
                    throw new Exception("Publisher " + id + ": " + errors.toString());
                }

                Publisher publisher = new Publisher();
                publisher.setId(Long.parseLong(id));
                publisher.setName(row[1]);
                publisher.setLogin(row[2]);
                publisher.setPassword(row[3]);
                publisher.setAccount(Float.parseFloat(row[4]));
                publisher.setDescription(row[5]);

                return publisher;

            case GAME:
                if (!row[2].matches("\\d+")){
                    errors.add("incorrect publisherId");
                }
                if (!row[3].matches("[+-]?([0-9]*[.])?[0-9]+")){
                    errors.add("incorrect price");
                }
                try {
                    GameGenre.valueOf(row[4]);
                } catch (IllegalArgumentException e) {
                    errors.add("incorrect gameGenre");
                }
                if (errors.size() != 0){
                    throw new Exception("Game " + id + ": " + errors.toString());
                }

                Game game = new Game();
                game.setId(Long.parseLong(id));
                game.setTitle(row[1]);
                game.setPublisherId(Long.parseLong(row[2]));
                game.setPrice(Float.parseFloat(row[3]));
                game.setGenre(GameGenre.valueOf(row[4]));

                return game;

            case TRANSACTION:
                try {
                    TransactionType.valueOf(row[1]);
                } catch (IllegalArgumentException e) {
                    errors.add("incorrect TransactionType");
                }

                if (!row[2].matches("\\d+")){
                    errors.add("incorrect PlayerId");
                }
                if (!row[3].matches("\\d+")){
                    errors.add("incorrect GameId");
                }
                LocalDateTime date = null;
                try {
                    date = LocalDateTime.parse(row[4]);
                } catch (Exception ex) {
                    errors.add("invalid datetime format");
                }
                if (!row[5].matches("[+-]?([0-9]*[.])?[0-9]+")){
                    errors.add("incorrect price");
                }

                if (errors.size() != 0){
                    throw new Exception("Transaction " + id + ": " + errors.toString());
                }

                Transaction trans = new Transaction();
                trans.setId(Long.parseLong(id));
                trans.setTransType(TransactionType.valueOf(row[1]));
                trans.setPlayerId(Long.parseLong(row[2]));
                trans.setGameId(Long.parseLong(row[3]));
                trans.setTime(date);
                trans.setPrice(Float.parseFloat(row[5]));

                return trans;

            case REVIEW:
                if (!row[1].matches("\\d+")){
                    errors.add("incorrect PlayerId");
                }
                if (!row[2].matches("\\d+")){
                    errors.add("incorrect GameId");
                }
                if (!row[3].matches("\\d+")){
                    errors.add("incorrect mark");
                }
                if (errors.size() != 0){
                    throw new Exception("Review " + id + ": " + errors.toString());
                }

                Review review = new Review();
                review.setId(Long.parseLong(id));
                review.setPlayerId(Long.parseLong(row[1]));
                review.setGameId(Long.parseLong(row[2]));
                review.setMark(Integer.parseInt(row[3]));
                review.setText(row[4]);

                return review;

            default:
                throw new IllegalArgumentException(Arrays.toString(row));
        }
    }

    public static String validateRefs(IDataProvider provider, ModelType type) throws Exception{
        List<String> errors = new ArrayList<>();

        HashMap<Long,Boolean> hashGames;
        HashMap<Long,Boolean> hashPlayers;
        HashMap<Long,Boolean> hashPublishers;
        Boolean plug = new Boolean(true);

        switch (type){
            case PLAYER:
                hashGames = new HashMap<>();
                ArrayList<Long> brokenGames = new ArrayList<>();

                provider.getAll(ModelType.GAME).forEach(game -> { // getAll calling on this DataProvider
                    hashGames.put(game.getId(), plug);
                });

                provider.getAll(type).forEach(player -> {
                    ((Player)player).getGames().forEach(pgame ->{
                        if(!hashGames.containsKey(pgame)) brokenGames.add(pgame);
                    });
                    errors.add("Player " + player.getId() + ": broken games refs " + brokenGames.toString());
                    brokenGames.clear();
                });

            break;

            case TRANSACTION:
                hashGames = new HashMap<>();
                hashPlayers = new HashMap<>();

                provider.getAll(ModelType.GAME).forEach(game -> { // getAll calling on this DataProvider
                    hashGames.put(game.getId(), plug);
                });

                provider.getAll(ModelType.PLAYER).forEach(player -> { // getAll calling on this DataProvider
                    hashPlayers.put(player.getId(), plug);
                });

                provider.getAll(type).forEach(t ->{
                    Transaction trans = (Transaction)t;
                    if(!hashGames.containsKey(trans.getGameId())) errors.add("Transaction " + trans.getId() + ": broken game ref " + trans.getGameId());
                    if(!hashPlayers.containsKey(trans.getPlayerId())) errors.add("Transaction " + trans.getId() + ": broken player ref " + trans.getGameId());
                });
            break;

            case REVIEW:
                hashGames = new HashMap<>();
                hashPlayers = new HashMap<>();

                provider.getAll(ModelType.GAME).forEach(game -> { // getAll calling on this DataProvider
                    hashGames.put(game.getId(), plug);
                });

                provider.getAll(ModelType.PLAYER).forEach(player -> { // getAll calling on this DataProvider
                    hashPlayers.put(player.getId(), plug);
                });

                provider.getAll(type).forEach(t ->{
                    Review review = (Review)t;
                    if(!hashGames.containsKey(review.getGameId())) errors.add("Review " + review.getId() + ": broken game ref " + review.getGameId());
                    if(!hashPlayers.containsKey(review.getPlayerId())) errors.add("Review " + review.getId() + ": broken player ref " + review.getGameId());
                });
            break;

            case GAME:
                hashPublishers = new HashMap<>();

                provider.getAll(ModelType.PUBLISHER).forEach(game -> { // getAll calling on this DataProvider
                    hashPublishers.put(game.getId(), plug);
                });

                provider.getAll(type).forEach(g ->{
                    Game game = (Game) g;
                    if(!hashPublishers.containsKey(game.getPublisherId())) errors.add("Game " + game.getId() + ": broken publisher ref " + game.getPublisherId());
                });
            break;
        }

        if (errors.size() != 0) {
            return "Errors: " + errors.toString();
        }
        return "";
    }
}
