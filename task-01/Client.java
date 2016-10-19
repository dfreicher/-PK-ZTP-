
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

/**
 * The main class of project.
 *
 * @author Dominik Freicher
 * @version 1.0
 *
 */
public class Client {

    private static final Logger LOGGER = Logger.getLogger(Client.class.getName());
    private static final String DB_QUERY = "SELECT x, y, p FROM GData";
    private static Map<Integer, Path.Edge> graph;

    /**
     * Main method
     * @param args connection_string, date.
     *
     */
    public static void main(String[] args) {
        String url = args[0].trim();
        int endPoint = Integer.parseInt(args[1]);

        initDatabaseConnection(url);
        searchBestPath(1, endPoint);
    }

    private static void initDatabaseConnection(final String url) {
        try {
            Connection connection = DriverManager.getConnection(url);
            Statement statement = connection.createStatement();
            fetchingData(statement.executeQuery(DB_QUERY));
            statement.close();
            connection.close();
        } catch (SQLException e) {
            LOGGER.info(e.getMessage());
            System.out.format(Locale.US, "Koszt : %.3f", 0f);
        }
    }

    private static void fetchingData(final ResultSet result)
            throws SQLException {
        graph = new HashMap<Integer, Path.Edge>();
        int i = 0;

        boolean isNext = result.next();
        if (isNext) {
            do {
                graph.put(i++, new Path.Edge(
                        result.getInt("x"),
                        result.getInt("y"),
                        result.getFloat("p")));
                isNext = result.next();
            } while (isNext);
        }
    }

    private static void searchBestPath(int startPoint, int endPoint) {
        Path path = new Path(graph, startPoint, endPoint);
        path.process();
        path.printResult();
    }
}
