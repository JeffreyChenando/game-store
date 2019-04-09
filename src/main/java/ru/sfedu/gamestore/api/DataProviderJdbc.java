package ru.sfedu.gamestore.api;

import org.apache.log4j.Logger;
import ru.sfedu.gamestore.Constants;
import ru.sfedu.gamestore.model.Model;
import ru.sfedu.gamestore.model.ModelType;
import ru.sfedu.gamestore.utils.ConfigurationUtil;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataProviderJdbc implements IDataProvider {
    private static final Logger logger = Logger.getLogger(DataProviderJdbc.class);

    public DataProviderJdbc() {
        try(Connection connection = makeConnection();
            Statement st = connection.createStatement()) {
            st.addBatch("CREATE TABLE IF NOT EXISTS PLAYER(" +
                    "id BIGINT," +
                    "name VARCHAR (64)," +
                    "login VARCHAR (64)," +
                    "password VARCHAR (64)," +
                    "account FLOAT(32)," +
                    "games TEXT" +
                    ")");

//            st.addBatch("CREATE TABLE IF NOT EXISTS Player_Games(" +
//                    "id_player BIGINT," +
//                    "id_game BIGINT" +
//                    ")");

            st.addBatch("CREATE TABLE IF NOT EXISTS PUBLISHER(" +
                    "id BIGINT," +
                    "name VARCHAR (64)," +
                    "login VARCHAR (64)," +
                    "password VARCHAR (64)," +
                    "account FLOAT(32)," +
                    "description TEXT" +
                    ")");

            st.addBatch("CREATE TABLE IF NOT EXISTS GAME(" +
                    "id BIGINT," +
                    "title VARCHAR (64)," +
                    "publisher_id BIGINT," +
                    "price FLOAT(32)," +
                    "genre VARCHAR (64)" +
                    ")");

            st.addBatch("CREATE TABLE IF NOT EXISTS TRANSACTION(" +
                    "id BIGINT," +
                    "type VARCHAR (64)," +
                    "player_id BIGINT," +
                    "game_id BIGINT," +
                    "time VARCHAR (64)," +
                    "price FLOAT(32)" +
                    ")");

            st.addBatch("CREATE TABLE IF NOT EXISTS REVIEW(" +
                    "id BIGINT," +
                    "player_id BIGINT," +
                    "game_id BIGINT," +
                    "mark INT," +
                    "text TEXT" +
                    ")");

            st.executeBatch();
        } catch (Exception e) {
            logger.error(e);
        }
    }

    private Connection makeConnection() throws IOException, ClassNotFoundException, SQLException {
        Class.forName(ConfigurationUtil.getConfigurationEntry(Constants.DB_DRIVER_CLASS));

        String jdbcConnectionString = ConfigurationUtil.getConfigurationEntry(Constants.JDBC_CONNECTION_STRING);
        String user = ConfigurationUtil.getConfigurationEntry(Constants.DB_USER);
        String password = ConfigurationUtil.getConfigurationEntry(Constants.DB_USER_PASSWORD);
        return DriverManager.getConnection(jdbcConnectionString, user, password);
    }

    private static String paramsFromStringArray(String[] arr){
        for (int i = 0; i < arr.length; i++) {
            arr[i] = "'" + arr[i] + "'";
        }
        return String.join(",", Arrays.copyOfRange(arr,0, arr.length));
    }

    private static int calcColumnCount(ModelType type){
        switch (type){
            case PLAYER:
                return 6;
            case PUBLISHER:
                return 6;
            case GAME:
                return 5;
            case TRANSACTION:
                return 6;
            case REVIEW:
                return 5;
            default:
                throw new IllegalArgumentException();
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
            delete(model.getType(), model.getId()); // Save works like update as well
        }
        String queryTemplate = "INSERT INTO %s VALUES(%s)";
        String[] arr = DataProviderCsv.modelToStringArray(model);
        String query = String.format(queryTemplate, model.getType().toString(), paramsFromStringArray(arr));
        try(Connection connection = makeConnection();
            Statement st = connection.createStatement()) {
            st.execute(query, Statement.RETURN_GENERATED_KEYS);
            ResultSet rs = st.getGeneratedKeys();
            if ( rs.next() ) {
                // Retrieve the auto generated key(s).
                return rs.getInt(1);
            }
            return -1;
        } catch (Exception e) {
            logger.error(e);
            return -1;
        }
    }

    @Override
    public void delete(ModelType type, long id) {
        String queryTemplate = "DELETE FROM %s WHERE id=%d";
        String query = String.format(queryTemplate, type.toString(), id);
        try (Connection connection = makeConnection();
             Statement st = connection.createStatement())
        {
            st.executeUpdate(query);
        } catch (Exception ex) {
            logger.info(ex.getMessage());
        }
    }

    @Override
    public List<Model> getAll(ModelType type) {
        String queryTemplate = "SELECT * FROM %s";
        String query = String.format(queryTemplate, type.toString());
        try (Connection connection = makeConnection();
             Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            List<Model> models = new ArrayList<>();
            int columnCount = calcColumnCount(type);
            while (rs.next()) {
                String[] stringArray = new String[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    stringArray[i - 1] = rs.getString(i);
                }
                models.add(DataProviderCsv.stringArrayToModel(stringArray, type));
            }
            return models;
        } catch (Exception ex) {
            logger.info(ex.getMessage());
        }

        return new ArrayList<>();
    }

    @Override
    public Model getById(ModelType type, long id) {
        String queryTemplate = "SELECT * FROM %s WHERE id=%d";
        String query = String.format(queryTemplate, type.toString(), id);
        try (Connection connection = makeConnection();
             Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            if (rs.next()) {
                int columnCount = calcColumnCount(type);
                String[] stringArray = new String[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    stringArray[i - 1] = rs.getString(i);
                }
                return DataProviderCsv.stringArrayToModel(stringArray, type);
            }
        } catch (Exception ex) {
            logger.info(ex.getMessage());
        }
        return null;
    }
}
