package pl.jrj.data;

import javax.ejb.Remote;

/**
 * @author Dominik Freicher
 * @version 1.0
 */
@Remote
public interface IDataMonitor {

    /**
     *
     * @return flag if element exists
     */
    public boolean hasNext();

    /**
     *
     * @return next element
     */
    public double next();
}