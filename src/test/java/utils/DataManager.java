package utils;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TreeMap;

/***
 * The Class manage the data received and sent to xml / remote database
 */
public class DataManager {
    private static Connection con;
    private static TreeMap<Integer, String> testsRunResults = new TreeMap<>();

    /***
     * Gets the data from a remote database or local xml file, by key name
     * @param keyName The key by which we want to extract the information
     * @return Returns the data if found.
     * @throws SQLException Exception occurred - database access error or other errors
     */
    public static String getData(String keyName) throws SQLException {
        String data = DataManager.getXMLData(keyName);
        if (data == null) {
            data = DataManager.getDataBaseData(keyName);
        }
        return data;
    }

    /***
     * Gets the data from an xml file according to the key sent
     * @param keyName The key by which we want to extract the information
     * @return Returns the data if found
     */
    private static String getXMLData(String keyName) {
        try {
            File xmlFile = new File("src/main/resources/data.xml");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);
            document.getDocumentElement().normalize();
            NodeList elements = document.getElementsByTagName(keyName);
            if (elements.getLength() != 0)
                return elements.item(0).getTextContent();
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /***
     * Gets the data from a remote db according to the key sent.
     * @param keyName The key by which we want to extract the information.
     * @return Returns the data if found.
     * @throws SQLException Exception occurred - database access error or other errors
     */
    private static String getDataBaseData(String keyName) throws SQLException {
        String data = null;
        con = DataBaseSingleton.getConnection();

        String selectStatement = "Select config_data " +
                "From " + DataBaseSingleton.getDatabaseName() + ".config " +
                "WHERE config_name = '" + keyName + "'";

        ResultSet result = con.createStatement().executeQuery(selectStatement);
        while (result.next()) {
            data = result.getString("config_data");
        }
        result.close();
        return data;
    }

    /***
     * Gets the data from a URL using JSON
     * @param apiURL The URL that stores the json data
     * @return Returns the URL and the driver data
     * @throws IOException Exception occurred - failed or interrupted I/O operations
     */
    public static String getJsonData(String apiURL) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(apiURL).build();
        Response response = client.newCall(request).execute();
        String jsonData = response.body().string();
        JSONArray jsonArray = new JSONArray(jsonData);
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject item = jsonArray.getJSONObject(i);
            if (item.keySet().contains("driver")){
                result.append("driver : ").append(item.get("driver").toString()).append("\n");
            }

            if (item.keySet().contains("URL")){
                result.append("URL : ").append(item.get("URL").toString()).append("\n");
            }
        }
        return result.toString();
    }

    /***
     * Create tables in remote database: config, history, history_date
     */
    public static void createTables() throws SQLException {
        String dbName = DataBaseSingleton.getDatabaseName();
        String configStatement = "CREATE TABLE " + dbName + ".config " +
                "(config_id INT NOT NULL, " +
                "config_name VARCHAR(45) NOT NULL, " +
                "config_data VARCHAR(100) NOT NULL, PRIMARY KEY('config_id'))";

        String historyStatement = "CREATE TABLE " + dbName + ".history " +
                "(test_id INT NOT NULL AUTO_INCREMENT, " +
                "test_date VARCHAR(50) NOT NULL, PRIMARY KEY('test_id'))";

        String historyDateTimeStatement = "CREATE TABLE " + dbName + ".history_datetime " +
                "(test_id INT NOT NULL AUTO_INCREMENT, " +
                "test_date DATETIME NOT NULL, PRIMARY KEY('test_id'))";

        createTable("config", configStatement);
        createTable("history", historyStatement);
        createTable("history_datetime", historyDateTimeStatement);
    }

    /***
     * Create a table in a remote database
     * @param tableName The table name
     * @param createStatement The sql query that creates the table
     * @throws SQLException Exception occurred - database access error or other errors
     */
    private static void createTable(String tableName, String createStatement) throws SQLException {
        con = DataBaseSingleton.getConnection();
        ResultSet resultSet = con.getMetaData().getTables(null, null, tableName, new String[]{"Table"});
        // If the table don't exist, create a new one
        if (!resultSet.next()) {
            con.createStatement().execute(createStatement);
        }
        resultSet.close();
    }

    /***
     * Logs the test id, date and time
     * @param testId The id of the test
     */
    public static void logTest(int testId) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String testTime = dtf.format(now);
        testsRunResults.put(testId, testTime);
    }

    /***
     * Saves all the tests logs to a remote database. If the connection failed, saves the data to a text file
     * @param testID The id of the test
     * @throws IOException Exception occurred - failed or interrupted I/O operations
     */
    public static void saveTestsLogs(int testID) throws IOException {
        try {
            writeLogsToDB(testID);
        } catch (SQLException e) {
            writeLogsToFile("results.txt");
        }
    }

    /***
     * Saves all the tests logs to a remote database, using prepare statement.
     * If the connection failed, saves the data to a text file
     * @throws IOException Exception occurred - failed or interrupted I/O operations
     */
    public static void saveExtraTestsLogs() throws IOException {
        try {
            writeLogsToDB_PS("history_datetime");
        } catch (SQLException e) {
            writeLogsToFile("extra_results.csv");
        }
    }

    /***
     * Writes the test logs to a remote database
     * @param testID The id of the test
     * @throws SQLException Exception occurred - database access error or other errors
     */
    private static void writeLogsToDB(int testID) throws SQLException {
        String dbName = DataBaseSingleton.getDatabaseName();
        con = DataBaseSingleton.getConnection();

        StringBuilder insertStatement = new StringBuilder();
        insertStatement.append("INSERT INTO ").append(dbName).append(".history");
        insertStatement.append(" (test_date) ");
        insertStatement.append("VALUES ");
        testsRunResults.forEach((k, v) -> {
            insertStatement.append("('").append(v).append("')");
            if (k != testID)
                insertStatement.append(",");
        });

        con.createStatement().execute(insertStatement.toString());
    }

    /***
     *  Writes the test logs to a text file
     * @throws IOException Exception occurred - failed or interrupted I/O operations
     */
    private static void writeLogsToFile(String fileName) throws IOException {
        StringBuilder data = new StringBuilder();
        data.append("test_id , test_date").append("\n");

        for (Integer key : testsRunResults.keySet()) {
            String value = testsRunResults.get(key);
            data.append(key).append(" , ").append(value).append("\n");
        }

        Path path = Paths.get("reports//" + fileName);
        BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8);
        writer.write(data.toString());
        writer.close();
    }

    /***
     * Writes the test logs to a remote database using Prepared Statement
     * @param tableName The table name in the remote database
     * @throws SQLException Exception occurred - database access error or other errors
     */
    public static void writeLogsToDB_PS(String tableName) throws SQLException {
        String dbName = DataBaseSingleton.getDatabaseName();

        con = DataBaseSingleton.getConnection();
        StringBuilder query = new StringBuilder();
        query.append("INSERT INTO ").append(dbName).append(".").append(tableName).append(" (test_id,test_date) VALUES (null,?)");
        query.append(", (null,?)".repeat(testsRunResults.size() - 1));

        PreparedStatement preparedStatement = con.prepareStatement(query.toString());
        testsRunResults.forEach((k, v) -> {
            try {
                preparedStatement.setString(k, v);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        preparedStatement.executeUpdate();
    }

    /***
     * Download and save a picture from a website
     * @param fileName The image file name
     * @param webSite The image URL
     * @return Returns the full URL of the image file
     * @throws IOException Exception occurred - failed or interrupted I/O operations
     */
    public static String downloadPictureFromWeb(String fileName, String webSite) throws IOException {
        try (InputStream in = new URL(webSite).openStream()) {
            Path path = Paths.get(System.getProperty("user.dir") + "\\" + fileName);
            Files.copy(in, path);
            return path.toString();
        }
    }

    /***
     * Close the connection to the remote database
     * @throws SQLException Exception occurred - database access error or other errors
     */
    public static void closeConnection() throws SQLException {
        if (!con.isClosed())
            con.close();
    }
}