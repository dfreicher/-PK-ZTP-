package pl.jrj.fnc;

import javax.ejb.Remote;

/**
 * @author Dominik Freicher
 * @version 1.0
 */
@Remote
public interface IFunctMonitor {

    /**
     *
     * @param x
     * @param y
     * @return value of function
     */
    public double f( double x, double y );
}