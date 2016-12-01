import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;

/**
 * @author Dominik Freicher
 * @version 1.0
 *
 */
public class Solver extends HttpServlet {

    private static final long serialVersionUID = -6921106949135639063L;

    private static final Logger LOG=Logger.getLogger(Solver.class.getName());

    private double result = 0;

    private double a = 0;
    private double b = 0;
    private int n = 0;

    @EJB
    private ISIntegralRemote integral;

    private double getResult() {
        return result;
    }

    private void setResult(double result) {
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
            prepareParameters(req);
            calculateResult();
            printResult(res.getWriter());
        } catch (NullPointerException e) {
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

    private void calculateResult() {
        double result = integral.solve(a, b, n);
        setResult(result);
    }

    private void prepareParameters(HttpServletRequest request) {
        if (request.getParameter("n") != null &&
                request.getParameter("a") != null &&
                request.getParameter("b") != null) {
            n = Integer.parseInt(request.getParameter("n"));
            a = convertParameter(request.getParameter("a"));
            b = convertParameter(request.getParameter("b"));
        }
    }

    private void printResult(PrintWriter writer) {
        writer.println(getResult());
    }

    private double convertParameter(String parameter) {
        return Double.parseDouble(parameter.replaceAll(",","."));
    }
}
