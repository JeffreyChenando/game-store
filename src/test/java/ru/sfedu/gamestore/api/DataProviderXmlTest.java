package ru.sfedu.gamestore.api;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import ru.sfedu.gamestore.TestUtils;
import ru.sfedu.gamestore.model.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;
//DataProviderXmlTest

@FixMethodOrder(value = MethodSorters.NAME_ASCENDING)
public class DataProviderXmlTest {
    final static String xmlRoot = "C:/game-store/test/xml";
    DataProviderXml provider = new DataProviderXml(xmlRoot);

    static List<Player> players = TestUtils.playerSamle();
    static List<Publisher> publishers = TestUtils.publisherSamle();
    static List<Game> games = TestUtils.gameSamle();
    static List<Transaction> transactions = TestUtils.transSamle();
    static List<Review> reviews = TestUtils.reviewSamle();

    @Test
    public void a_saveOrUpdate() throws IOException {
        Path playerFilePath = Paths.get(xmlRoot, ModelType.PLAYER.toString() + ".xml");
        Path publisherFilePath = Paths.get(xmlRoot, ModelType.PUBLISHER.toString() + ".xml");
        Path gameFilePath = Paths.get(xmlRoot, ModelType.GAME.toString() + ".xml");
        Path transFilePath = Paths.get(xmlRoot, ModelType.TRANSACTION.toString() + ".xml");
        Path reviewFilePath = Paths.get(xmlRoot, ModelType.REVIEW.toString() + ".xml");

        long playerLinesCountBefore = Files.exists(playerFilePath) ? Files.readAllLines(playerFilePath).size() : 0;
        long publisherLinesCountBefore = Files.exists(publisherFilePath) ? Files.readAllLines(publisherFilePath).size() : 0;
        long gameLinesCountBefore = Files.exists(gameFilePath) ? Files.readAllLines(gameFilePath).size() : 0;
        long transLinesCountBefore = Files.exists(transFilePath) ? Files.readAllLines(transFilePath).size() : 0;
        long reviewLinesCountBefore = Files.exists(reviewFilePath) ? Files.readAllLines(reviewFilePath).size() : 0;

        players.forEach(player -> player.setId(provider.saveOrUpdate(player)));
        publishers.forEach(publisher -> publisher.setId(provider.saveOrUpdate(publisher)));
        games.forEach(game -> game.setId(provider.saveOrUpdate(game)));
        transactions.forEach(trans -> trans.setId(provider.saveOrUpdate(trans)));
        reviews.forEach(review -> review.setId(provider.saveOrUpdate(review)));

        long playerLinesCountAfter = Files.exists(playerFilePath) ? Files.readAllLines(playerFilePath).size() : 0;
        long publisherLinesCountAfter = Files.exists(publisherFilePath) ? Files.readAllLines(publisherFilePath).size() : 0;
        long gameLinesCountAfter = Files.exists(gameFilePath) ? Files.readAllLines(gameFilePath).size() : 0;
        long transLinesCountAfter = Files.exists(transFilePath) ? Files.readAllLines(transFilePath).size() : 0;
        long reviewLinesCountAfter = Files.exists(reviewFilePath) ? Files.readAllLines(reviewFilePath).size() : 0;

        assertTrue(playerLinesCountAfter > playerLinesCountBefore);
        assertTrue(publisherLinesCountAfter > publisherLinesCountBefore);
        assertTrue(gameLinesCountAfter > gameLinesCountBefore);
        assertTrue(transLinesCountAfter > transLinesCountBefore);
        assertTrue(reviewLinesCountAfter > reviewLinesCountBefore);
    }

    @Test
    public void b_getAll() {
        assertEquals(players, provider.getAll(ModelType.PLAYER));
        assertEquals(publishers, provider.getAll(ModelType.PUBLISHER));
        assertEquals(games, provider.getAll(ModelType.GAME));
        assertEquals(transactions, provider.getAll(ModelType.TRANSACTION));
        assertEquals(reviews, provider.getAll(ModelType.REVIEW));
    }

    @Test
    public void c_getById() {
        players.forEach(p -> assertEquals(p, provider.getById(ModelType.PLAYER, p.getId())));
        publishers.forEach(p -> assertEquals(p, provider.getById(ModelType.PUBLISHER, p.getId())));
        games.forEach(g -> assertEquals(g, provider.getById(ModelType.GAME, g.getId())));
        transactions.forEach(t -> assertEquals(t, provider.getById(ModelType.TRANSACTION, t.getId())));
        reviews.forEach(r -> assertEquals(r, provider.getById(ModelType.REVIEW, r.getId())));
    }

    @Test
    public void d_delete() throws IOException {
        Path playerFilePath = Paths.get(xmlRoot, ModelType.PLAYER.toString() + ".xml");
        Path publisherFilePath = Paths.get(xmlRoot, ModelType.PUBLISHER.toString() + ".xml");
        Path gameFilePath = Paths.get(xmlRoot, ModelType.GAME.toString() + ".xml");
        Path transFilePath = Paths.get(xmlRoot, ModelType.TRANSACTION.toString() + ".xml");
        Path reviewFilePath = Paths.get(xmlRoot, ModelType.REVIEW.toString() + ".xml");

        long playerLinesCountBefore = Files.exists(playerFilePath) ? Files.readAllLines(playerFilePath).size() : 0;
        long publisherLinesCountBefore = Files.exists(publisherFilePath) ? Files.readAllLines(publisherFilePath).size() : 0;
        long gameLinesCountBefore = Files.exists(gameFilePath) ? Files.readAllLines(gameFilePath).size() : 0;
        long transLinesCountBefore = Files.exists(transFilePath) ? Files.readAllLines(transFilePath).size() : 0;
        long reviewLinesCountBefore = Files.exists(reviewFilePath) ? Files.readAllLines(reviewFilePath).size() : 0;

        players.forEach(player -> provider.delete(player.getType(), player.getId()));
        publishers.forEach(publisher -> provider.delete(publisher.getType(), publisher.getId()));
        games.forEach(game -> provider.delete(game.getType(), game.getId()));
        transactions.forEach(trans -> provider.delete(trans.getType(),trans.getId()));
        reviews.forEach(review -> provider.delete(review.getType(), review.getId()));

        long playerLinesCountAfter = Files.exists(playerFilePath) ? Files.readAllLines(playerFilePath).size() : 0;
        long publisherLinesCountAfter = Files.exists(publisherFilePath) ? Files.readAllLines(publisherFilePath).size() : 0;
        long gameLinesCountAfter = Files.exists(gameFilePath) ? Files.readAllLines(gameFilePath).size() : 0;
        long transLinesCountAfter = Files.exists(transFilePath) ? Files.readAllLines(transFilePath).size() : 0;
        long reviewLinesCountAfter = Files.exists(reviewFilePath) ? Files.readAllLines(reviewFilePath).size() : 0;

        assertTrue(playerLinesCountAfter < playerLinesCountBefore);
        assertTrue(publisherLinesCountAfter < publisherLinesCountBefore);
        assertTrue(gameLinesCountAfter < gameLinesCountBefore);
        assertTrue(transLinesCountAfter < transLinesCountBefore);
        assertTrue(reviewLinesCountAfter < reviewLinesCountBefore);
    }
}