package pl.jrj.dsm;

import javax.ejb.Remote;

/**
 * @author Dominik Freicher
 * @version 1.0
 */
@Remote
public interface IDSManagerRemote {

    /**
     *
     * @return datasource string
     */
    public String getDS();
}
