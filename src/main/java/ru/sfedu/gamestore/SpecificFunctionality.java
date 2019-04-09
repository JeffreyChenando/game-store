package ru.sfedu.gamestore;

import org.apache.log4j.Logger;
import ru.sfedu.gamestore.api.IDataProvider;
import ru.sfedu.gamestore.model.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class SpecificFunctionality {
    private static final Logger logger = Logger.getLogger(SpecificFunctionality.class);

    public static boolean buyGame(IDataProvider provider, Long playerId, Long gameId){
        Player player = (Player) provider.getById(ModelType.PLAYER, playerId);
        if(player == null) {
            logger.info("Player no exist");
            return false;
        }
        if(player.getGames().contains(gameId)){
            logger.info("Game is already bought");
            return false;
        }
        Game game = (Game)provider.getById(ModelType.GAME, gameId);
        if(game == null){
            logger.info("Game is not exist");
            return false;
        }
        if(game.getPrice() > player.getAccount()){
            logger.info("Not enough money in account");
            return false;
        }

        Transaction transaction = new Transaction();
        transaction.setId(0);
        transaction.setTransType(TransactionType.PURCHASE);
        transaction.setPlayerId(playerId);
        transaction.setGameId(gameId);
        transaction.setPrice(game.getPrice());
        transaction.setTime(LocalDateTime.now());
        provider.saveOrUpdate(transaction);

        player.getGames().add(gameId);
        player.setAccount(player.getAccount() - game.getPrice());
        provider.saveOrUpdate(player);

        Publisher publisher = (Publisher)provider.getById(ModelType.PUBLISHER, game.getPublisherId());
        publisher.setAccount(publisher.getAccount() + game.getPrice());
        provider.saveOrUpdate(publisher);
        logger.info("Game bought!");
        return true;
    }

    public static boolean refundGame(IDataProvider provider, Long playerId, Long gameId){
        Player player = (Player) provider.getById(ModelType.PLAYER, playerId);
        if(player == null) logger.info("Player no exist");

        if(!player.getGames().contains(gameId)){
            logger.info("Game is not bought");
            return false;
        }
        Game game = (Game)provider.getById(ModelType.GAME, gameId);
        if(game == null){
            logger.info("Game is not exist");
            return false;
        }
        List<Model> transactions = provider.getAll(ModelType.TRANSACTION);
        List<Transaction> trans = transactions.stream()
                .map(t -> (Transaction)t)
                .filter(t -> t.getPlayerId() == playerId
                    && t.getGameId() == gameId
                    && t.getTransType() == TransactionType.PURCHASE)
                .collect(Collectors.toList());
        if (trans.size() == 0) {
            logger.error("Game is bought, but transaction is not exist");
            return false;
        }
        float price = trans.stream().max(Comparator.comparing(Transaction::getTime)).get().getPrice();

        Transaction transaction = new Transaction();
        transaction.setId(0);
        transaction.setTransType(TransactionType.REFUND);
        transaction.setPlayerId(playerId);
        transaction.setGameId(gameId);
        transaction.setPrice(price);
        transaction.setTime(LocalDateTime.now());
        provider.saveOrUpdate(transaction);

        player.getGames().remove(gameId);
        player.setAccount(player.getAccount() + price);
        provider.saveOrUpdate(player);

        Publisher publisher = (Publisher)provider.getById(ModelType.PUBLISHER, game.getPublisherId());
        publisher.setAccount(publisher.getAccount() - price);
        provider.saveOrUpdate(publisher);
        logger.info("Game refunded!");
        return true;
}

    public static ArrayList<Long> seeBoughtGames(IDataProvider provider, Long playerId){
        Player player = (Player) provider.getById(ModelType.PLAYER, playerId);
        if(player == null) logger.info("Player no exist");

        return player.getGames();
    }

    public static boolean writeReview(IDataProvider provider, Review review){
        Player player = (Player) provider.getById(ModelType.PLAYER, review.getPlayerId());
        if(player == null) {
            logger.info("Player no exist");
            return false;
        }
        Game game = (Game)provider.getById(ModelType.GAME, review.getGameId());
        if(game == null){
            logger.info("Game is not exist");
            return false;
        }

        if(!player.getGames().contains(review.getGameId())){
            List<Model> transactions = provider.getAll(ModelType.TRANSACTION);
            long count = transactions.stream()
                    .map(t -> (Transaction)t)
                    .filter(t -> t.getPlayerId() == review.getPlayerId()
                            && t.getGameId() == review.getGameId()
                            && t.getTransType() == TransactionType.PURCHASE).count();
            if(count == 0){
                logger.info("Player didn't buy game, so this functionality is unable");
                return false;
            }

        }

        if (review.getMark() <0 || review.getMark()>10){
            logger.info("Mark not in 0 to 10");
            return false;
        }

        provider.saveOrUpdate(review);
        logger.info("Review added");
        return true;
    }

    public static String viewStatistic(IDataProvider provider, Long publisherId, Long gameId){
        Publisher publisher = (Publisher) provider.getById(ModelType.PUBLISHER, publisherId);
        if(publisher == null) {
            logger.info("Publisher not exist");
            return "Error";
        }

        Game game = (Game)provider.getById(ModelType.GAME, gameId);
        if(game == null){
            logger.info("Game is not exist");
            return "Error";
        }

        if(game.getPublisherId() != publisherId){
            logger.info("The game does not belong to this publisher");
            return "Error";
        }

        List<Transaction> transactions = provider.getAll(ModelType.TRANSACTION).stream().map(t -> (Transaction)t)
                .filter(t -> t.getGameId() == gameId)
                .collect(Collectors.toList());
        if(transactions.size() == 0){
            logger.info("No transactions");
            return "No transactions";
        }
        Long countBought = transactions.stream().filter(trans -> trans.getTransType() == TransactionType.PURCHASE).count();
        Long countRefund = transactions.stream().filter(trans -> trans.getTransType() == TransactionType.REFUND).count();

        Float profit = transactions.stream().map(t -> {
            if (t.getTransType() == TransactionType.REFUND) return t.getPrice()*-1;
            return t.getPrice();
        }).reduce((aFloat, aFloat2) -> aFloat + aFloat2).get();

        return "Game purchased: " + countBought +
                ", game refunded: " + countRefund +
                ", profit: " + profit;
    }

    public static boolean changePrice(IDataProvider provider, Long publisherId, Long gameId, float newPrice){
        Publisher publisher = (Publisher) provider.getById(ModelType.PUBLISHER, publisherId);
        if(publisher == null) {
            logger.info("Publisher not exist");
            return false;
        }

        Game game = (Game)provider.getById(ModelType.GAME, gameId);
        if(game == null){
            logger.info("Game is not exist");
            return false;
        }

        if(game.getPublisherId() != publisherId){
            logger.info("The game does not belong to this publisher");
            return false;
        }

        game.setPrice(newPrice);
        provider.saveOrUpdate(game);
        return true;
    }

    public static ArrayList<Game> sortByGenre(IDataProvider provider, GameGenre genre){
        ArrayList<Game> games = provider.getAll(ModelType.GAME)
                .stream()
                .map(t -> (Game)t)
                .filter(game -> game.getGenre() == genre)
                .collect(Collectors.toCollection(ArrayList::new));
        return games;

    }

    public static boolean publishGame(IDataProvider provider, Game game){
        Publisher publisher = (Publisher) provider.getById(ModelType.PUBLISHER, game.getPublisherId());
        if(publisher == null) {
            logger.info("Publisher not exist");
            return false;
        }

        if(game.getPrice() < 0) {
            logger.info("Price is negative");
            return false;
        }

        long flag = provider.getAll(ModelType.GAME)
                .stream()
                .map(g -> ((Game)g).getTitle())
                .filter(t -> t.equals(game.getTitle()))
                .count();

        if(flag != 0) {
            logger.info("A game with this name already exists");
            return false;
        }

        provider.saveOrUpdate(game);

        return true;
    }
}
