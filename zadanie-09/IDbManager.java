package pl.jrj.db;

import javax.ejb.Remote;

/**
 * @author Dominik Freicher
 * @version 1.0
 */
@Remote
public interface IDbManager {
    /**
     *
     * @param hwork - task number
     * @param album - student album number
     * @return result of registration process
     */
    public boolean register(int hwork, String album);
}