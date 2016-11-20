import pl.jrj.dsm.IDSManagerRemote;

import javax.ejb.EJB;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Dominik Freicher
 * @version 1.0
 *
 */
public class Solver extends HttpServlet {

    private static final long serialVersionUID = 5553340773081649855L;

    private static final Logger LOG=Logger.getLogger(Solver.class.getName());

    private static final String JNDI_NAME = "java:global/ejb-project/" +
            "DSManager!pl.jrj.dsm.IDSManagerRemote";

    private static final String DB_QUERY = "SELECT x, y, z FROM ";

    private static List<float[]> points = new ArrayList<float[]>();

    private float result = 1;

    @EJB
    private IBlockRemote block;

    private float getResult() {
        return result;
    }

    private void setResult(float result) {
        this.result = result;
    }

    /**
     *
     * @param req http request
     * @param res http response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        try {
            IDSManagerRemote manager = getDSManager();
            String dataSource = manager.getDS();
            String tableName = getTableName(req);

            Connection connection = initDatabaseConnection(dataSource);
            fetchingData(connection, tableName);

            setResult(block.calculateVolumeOfPrism(points));
            printResult(res.getWriter());
        } catch (NamingException e) {
            LOG.info(e.getMessage());
            printResult(res.getWriter());
        } catch (SQLException e) {
            LOG.info(e.getMessage());
            printResult(res.getWriter());
        }
    }

    /**
     *
     * @param req http request
     * @param res http response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        doPost(req, res);
    }

    private String getTableName(HttpServletRequest request) {
        if (request.getParameter("t") != null) {
            return request.getParameter("t");
        }
        return null;
    }

    private static IDSManagerRemote getDSManager()
            throws NamingException {
        InitialContext ctx = new InitialContext();
        IDSManagerRemote data = (IDSManagerRemote) ctx.lookup(JNDI_NAME);
        return data;
    }

    private static Connection initDatabaseConnection(final String path)
            throws NamingException, SQLException {
        InitialContext context = new InitialContext();
        DataSource ds = (DataSource) context.lookup(path);
        Connection connection = ds.getConnection();

        return connection;
    }

    private static void fetchingData(Connection connection, String tableName)
            throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery(DB_QUERY + tableName);

        boolean isNext = result.next();
        if (isNext) {
            do {
                float[] data = new float[3];
                data[0] = result.getFloat("x");
                data[1] = result.getFloat("y");
                data[2] = result.getFloat("z");
                points.add(data);
                isNext = result.next();
            } while (isNext);
        }

        statement.close();
        connection.close();
    }

    private void printResult(PrintWriter writer) {
        writer.println(String.format("%.5f", getResult()));
    }
}
