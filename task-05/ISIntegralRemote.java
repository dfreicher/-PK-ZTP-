import javax.ejb.Remote;

/**
 * @author Dominik Freicher
 * @version 1.0
 *
 */
@Remote
public interface ISIntegralRemote {

    /**
     *
     * @param a
     * @param b
     * @param n
     * @return value of integral
     */
    public double solve(double a, double b, int n);
}
