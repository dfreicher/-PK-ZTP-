import pl.jrj.data.IDataMonitor;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.logging.Logger;

/**
 * @author Dominik Freicher
 * @version 1.0
 */
public class EjbClient {

    private static final Logger LOG=Logger.getLogger(EjbClient.class.getName());
    private static final String JNDI_NAME = "java:global/ejb-project/" +
            "DataMonitor!pl.jrj.data.IDataMonitor";
    private static double result = 0;

    private static double a;
    private static double b;
    private static double c;

    /**
     *
     * @param args - arguments
     * @throws NamingException
     */
    public static void main(String[] args) {
        try {
            IDataMonitor data = getDataMonitor();
            processAxisParameters(data);
            calculateMomentOfInertia(data);
            printResult();
        } catch (NamingException e) {
            LOG.info(e.getMessage());
            printResult();
        }
    }

    private static IDataMonitor getDataMonitor() throws NamingException {
        InitialContext ctx = new InitialContext();
        IDataMonitor data = (IDataMonitor) ctx.lookup(JNDI_NAME);
        return data;
    }

    private static void printResult() {
        System.out.printf("%.5f", result);
    }

    private static void processAxisParameters(IDataMonitor data) {
        if (data.hasNext()) {
            a = data.next();
        }
        if (data.hasNext()) {
            b = data.next();
        }
        if (data.hasNext()) {
            c = data.next();
        }
    }

    private static void calculateMomentOfInertia(IDataMonitor data) {
        while (data.hasNext()) {
            double x = data.next();
            double y = data.next();
            double z = data.next();
            double m = data.next();

            double r = Math.abs(a * x + b * y - z + c) /
                    Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2) + 1);

            result += m * Math.pow(r, 2);
        }
    }

}
