package ru.sfedu.gamestore;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import ru.sfedu.gamestore.model.*;

public class TestUtils {
    public static List<Player> playerSamle(){
        List<Player> players = new ArrayList<>();

        ArrayList<Long> games1 = new ArrayList<>();
        games1.add(1L);
        games1.add(2L);
        Player player1 = new Player();
        player1.setLogin("A");
        player1.setPassword("123");
        player1.setName("aaa");
        player1.setGames(games1);

        ArrayList<Long> games2 = new ArrayList<>();
        games2.add(2l);
        Player player2 = new Player();
        player2.setLogin("B");
        player2.setPassword("456");
        player2.setName("bbb");
        player2.setGames(games2);

        players.add(player1);
        players.add(player2);
        return players;
    }

    public static List<Publisher> publisherSamle(){
        List<Publisher> publishers = new ArrayList<>();
        Publisher publisher1 = new Publisher();
        publisher1.setLogin("P1");
        publisher1.setPassword("123");
        publisher1.setName("P1arts");
        publisher1.setDescription("");

        Publisher publisher2 = new Publisher();
        publisher2.setLogin("P2");
        publisher2.setPassword("qwerty");
        publisher2.setName("P2vision");
        publisher2.setDescription("");

        publishers.add(publisher1);
        publishers.add(publisher2);
        return publishers;
    }

    public static List<Game> gameSamle(){
        List<Game> games = new ArrayList<>();
        Game game1 = new Game();
        game1.setTitle("StarCraft");
        game1.setPublisherId(1);
        game1.setPrice(400f);
        game1.setGenre(GameGenre.STRATEGY);

        Game game2 = new Game();
        game2.setTitle("Call of Duty");
        game2.setPublisherId(1);
        game2.setPrice(1000f);
        game2.setGenre(GameGenre.ACTION);

        games.add(game1);
        games.add(game2);
        return games;
    }

    public static List<Transaction> transSamle(){
        List<Transaction> transactions = new ArrayList<>();
        Transaction trans1 = new Transaction();
        trans1.setPlayerId(1);
        trans1.setGameId(1);
        trans1.setPrice(400f);
        trans1.setTime(LocalDateTime.now());
        trans1.setTransType(TransactionType.PURCHASE);

        Transaction trans2 = new Transaction();
        trans2.setPlayerId(1);
        trans2.setGameId(2);
        trans2.setPrice(1000f);
        trans2.setTime(LocalDateTime.now());
        trans2.setTransType(TransactionType.PURCHASE);

        transactions.add(trans1);
        transactions.add(trans2);
        return transactions;
    }

    public static List<Review> reviewSamle(){
        List<Review> reviews = new ArrayList<>();
        Review review1 = new Review();
        review1.setPlayerId(1);
        review1.setGameId(1);
        review1.setMark(98);
        review1.setText("Great thing ever");


        Review review2 = new Review();
        review2.setPlayerId(1);
        review2.setGameId(2);
        review2.setMark(44);
        review2.setText("Very bad");

        reviews.add(review1);
        reviews.add(review2);
        return reviews;
    }
}
