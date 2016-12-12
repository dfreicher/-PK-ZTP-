import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Freicher
 * @version 1.0
 *
 */
public class Block extends HttpServlet {

    private static final long serialVersionUID = -1733800765473301018L;

    private List<Damage> damages = new ArrayList<Damage>();
    private double result;

    private List<Damage> getDamages() {
        return damages;
    }

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
        if (req.getParameter("x") != null
                && req.getParameter("y") != null
                && req.getParameter("z") != null
                && req.getParameter("r") != null) {

            createDamage(req.getParameter("x"),
                    req.getParameter("y"),
                    req.getParameter("z"),
                    req.getParameter("r")
            );
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

        try {
            if (req.getParameter("r") != null
                    && req.getParameter("h") != null
                    && req.getParameter("c") != null
                    && req.getParameter("g") != null) {

                performAnalysis(convertParameter(req.getParameter("r")),
                        convertParameter(req.getParameter("h")),
                        convertParameter(req.getParameter("c")),
                        convertParameter(req.getParameter("g"))
                );
            }

            PrintWriter writer = res.getWriter();
            printResult(writer);

        } catch (IOException io) {
            PrintWriter writer = res.getWriter();
            printResult(writer);
        }
    }

    private void performAnalysis(double r, double h, double c, double g) {
        double points = 1000000;
        double correctPoints = calculateMonteCarloMethod(points, r, h);
        double cylinderVolume = Math.PI * Math.pow(r, 2) * h;

        double defectsMass = g*(correctPoints/points)*cylinderVolume;
        double cylinderMass = c*((points-correctPoints)/points)*cylinderVolume;
        double totalMass = defectsMass + cylinderMass;
        setResult(totalMass);
    }

    private double calculateMonteCarloMethod(double points,
                                             double rangeMin,
                                             double rangeMax) {
        Random random = new Random();
        double correctPoints = 0;

        for (int i = 1; i <= points; i++) {
            double x = getRandomNumber(random, -rangeMin, rangeMin);
            double y = getRandomNumber(random, -rangeMin, rangeMin);
            double z = getRandomNumber(random, 0, rangeMax);

            for (Damage d : getDamages()) {
                double distancePoints = Math.pow(x - d.getX(), 2) +
                        Math.pow(y - d.getY(), 2) +
                        Math.pow(z - d.getZ(), 2);

                if (distancePoints <= Math.pow(d.getR(), 2)) {
                    correctPoints++;
                    break;
                }
            }
        }

        return correctPoints;
    }

    private double getRandomNumber(Random r, double rangeMin, double rangeMax) {
        return rangeMin + (r.nextDouble() * (rangeMax - rangeMin));
    }

    private void printResult(PrintWriter writer) {
        writer.println(String.format("%.2f", getResult()));
    }

    private void createDamage(String x, String y, String z, String r) {
        getDamages().add(new Damage(convertParameter(x),
                convertParameter(y),
                convertParameter(z),
                convertParameter(r)
        ));
    }

    private double convertParameter(String parameter) {
        return Double.parseDouble(parameter.replaceAll(",","."));
    }

    /**
     * this class represent damage object
     */
    private static class Damage {
        private double x;
        private double y;
        private double z;
        private double r;

        Damage(double x, double y, double z, double r) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.r = r;
        }

        double getX() {
            return x;
        }

        double getY() {
            return y;
        }

        double getZ() {
            return z;
        }

        double getR() {
            return r;
        }
    }

}
