import pl.jrj.fnc.IFunctMonitor;

import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.logging.Logger;

/**
 * @author Dominik Freicher
 * @version 1.0
 *
 */
@Stateless
public class SIntegral implements ISIntegralRemote {

    private static final Logger LOG=Logger.getLogger(SIntegral.class.getName());

    private static final String JNDI_NAME = "java:global/ejb-project/" +
            "FunctMonitor!pl.jrj.fnc.IFunctMonitor";

    private double result = 0;

    private double getResult() {
        return result;
    }

    private void setResult(double result) {
        this.result = result;
    }

    /**
     *
     * @param a
     * @param b
     * @param n
     * @return value of integral
     */
    @Override
    public double solve(double a, double b, int n) {
        try {
            IFunctMonitor monitor = getBean();
            calculateIntegralValue(monitor, a, b, n);
            return getResult();
        } catch (NamingException e) {
            LOG.info(e.getMessage());
            return getResult();
        }
    }

    private IFunctMonitor getBean()
            throws NamingException {
        InitialContext ctx = new InitialContext();
        IFunctMonitor monitor = (IFunctMonitor) ctx.lookup(JNDI_NAME);
        return monitor;
    }

    private void calculateIntegralValue(IFunctMonitor monitor,
                                        double a, double b, int n) {
        double xSegmentSize = getXSegmentSize(a, n);
        double ySegmentSize = getYSegmentSize(b, n);
        double result = calculateVolume(monitor, xSegmentSize, ySegmentSize, n);
        setResult(result);
    }

    private double calculateVolume(IFunctMonitor monitor,
                                   double xSegmentSize, double ySegmentSize, int n) {

        double result = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                double figureHeight = monitor.f((xSegmentSize * i),
                        (ySegmentSize * j));
                figureHeight += monitor.f((xSegmentSize * (i + 1)),
                        (ySegmentSize * j));
                figureHeight += monitor.f((xSegmentSize * (i + 1)),
                        (ySegmentSize * (j + 1)));
                figureHeight += monitor.f((xSegmentSize * i),
                        (ySegmentSize * (j + 1)));
                figureHeight = figureHeight / 4;
                result += xSegmentSize * ySegmentSize * figureHeight;
            }
        }

        return result;
    }

    private double getXSegmentSize(double a, int n) {
        return a / n;
    }

    private double getYSegmentSize(double b, int n) {
        return b / n;
    }
}
