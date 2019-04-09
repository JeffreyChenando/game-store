package ru.sfedu.gamestore;

import org.apache.log4j.Logger;
import ru.sfedu.gamestore.api.DataProviderCsv;
import ru.sfedu.gamestore.api.DataProviderJdbc;
import ru.sfedu.gamestore.api.DataProviderXml;
import ru.sfedu.gamestore.api.IDataProvider;
import ru.sfedu.gamestore.model.Game;
import ru.sfedu.gamestore.model.GameGenre;
import ru.sfedu.gamestore.model.ModelType;
import ru.sfedu.gamestore.model.Review;
import ru.sfedu.gamestore.utils.ConfigurationUtil;
import ru.sfedu.gamestore.SpecificFunctionality;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Client {
    private static final Logger logger = Logger.getLogger(Client.class);

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args){
        logger.debug("stardust: starting application.........");
        logger.info("Input data source type: ");
        String dataSourceType = sc.nextLine().toLowerCase();
        if (!dataSourceType.equals("csv") && !dataSourceType.equals("xml") && !dataSourceType.equals("jdbc")){
            logger.error("wrong data source type: " + dataSourceType);
            return;
        }
        IDataProvider provider = null;
        try {
            switch (dataSourceType) {
                case "csv":
                    provider = new DataProviderCsv(ConfigurationUtil.getConfigurationEntry(Constants.CSV_DATA_PATH));
                    break;
                case "xml":
                    provider = new DataProviderXml(ConfigurationUtil.getConfigurationEntry(Constants.XML_DATA_PATH));
                    break;
                case "jdbc":
                    provider = new DataProviderJdbc();
                    break;
            }
        } catch (IOException e) {
            logger.error(e);
            return;
        }

        String[] actions = {"save", "update","get_all", "get_by_id", "delete", "exit", "val_refs"};

        while (true){
            System.out.print("> ");
            try {
                String command[] = sc.nextLine().split(" ");
                if(command[0].toLowerCase()=="exit"){
                    logger.info("exit");
                    return;
                }
                ModelType type;
                String[] saveArgs;
                switch (command[0].toLowerCase()){ // action
                    case "save":
                        saveArgs = new String[command.length - 1];
                        type = ModelType.valueOf(command[1].toUpperCase());
                        System.arraycopy(command, 2, saveArgs, 1, command.length - 2);
                        saveArgs[0] = "0";
                        provider.saveOrUpdate(DataProviderCsv.stringArrayToModel(saveArgs, type));
                        // player           Name Login Password Account Games[0,1,2]
                        // publisher        Name Login Password Account Description
                        // game             Title PublishId Price Genre
                        // transaction      Type PlayerId GameId Time Price
                        // review           PlayerId GameId Time Mark Text

                        break;
                    case "update":
                        String[] updateArgs = new String[command.length - 2];
                        type = ModelType.valueOf(command[1].toUpperCase());
                        System.arraycopy(command, 2, updateArgs, 0, command.length - 2);
                        provider.saveOrUpdate(DataProviderCsv.stringArrayToModel(updateArgs, type));
                        break;
                    case "get_all":
                        type = ModelType.valueOf(command[1].toUpperCase());
                        provider.getAll(type).forEach(logger::info);
                        break;
                    case "get_by_id":
                        type = ModelType.valueOf(command[1].toUpperCase());
                        logger.info(provider.getById(type, Long.valueOf(command[2])));
                        break;
                    case "delete":
                        type = ModelType.valueOf(command[1].toUpperCase());
                        provider.delete(type, Long.valueOf(command[2]));
                        logger.info("deleted " + command[2]);
                        break;
                    case "val_refs":
                        type = ModelType.valueOf(command[1].toUpperCase());
                        logger.info(DataProviderCsv.validateRefs(provider, type));
                        break;
                    case "buy_game":
                        SpecificFunctionality.buyGame(
                                provider,
                                Long.valueOf(command[1]), // playerId
                                Long.valueOf(command[2]) // gameId
                                );
                        break;
                    case "refund_game":
                        SpecificFunctionality.refundGame(
                                provider,
                                Long.valueOf(command[1]),
                                Long.valueOf(command[2])
                        );
                        break;
                    case "see_own_games":
                        logger.info(SpecificFunctionality.seeBoughtGames(
                                provider,
                                Long.valueOf(command[1])
                        ));
                        break;
                    case "view_statistic":
                        logger.info(SpecificFunctionality.viewStatistic(
                                provider,
                                Long.valueOf(command[1]),
                                Long.valueOf(command[2])
                        ));
                        break;
                    case "change_price":
                        SpecificFunctionality.changePrice(
                                provider,
                                Long.valueOf(command[1]),
                                Long.valueOf(command[2]),
                                Float.parseFloat(command[3])
                        );
                        break;
                    case "sort_by_genre":
                        SpecificFunctionality.sortByGenre(
                                provider,
                                GameGenre.valueOf(command[1].toUpperCase())
                        ).forEach(logger::info);
                        break;
                    case "write_review":
                        saveArgs = new String[command.length];
                        System.arraycopy(command, 1, saveArgs, 1, command.length - 1);
                        saveArgs[0] = "0";
                        SpecificFunctionality.writeReview(
                                provider,
                                (Review) DataProviderCsv.stringArrayToModel(saveArgs, ModelType.REVIEW)
                        );
                        break;
                    case "publish_game":
                        saveArgs = new String[command.length];
                        System.arraycopy(command, 1, saveArgs, 1, command.length - 1);
                        saveArgs[0] = "0";
                        SpecificFunctionality.publishGame(
                                provider,
                                (Game) DataProviderCsv.stringArrayToModel(saveArgs, ModelType.GAME)
                        );
                        break;

                }
            } catch (Exception e){
                logger.error("something wrong, error=" + e.toString());
            }
        }
    }
}
