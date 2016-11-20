import javax.ejb.Remote;
import java.util.List;

/**
 * @author Dominik Freicher
 * @version 1.0
 *
 */
@Remote
public interface IBlockRemote {

    /**
     *
     * @param points
     * @return volume of prism
     */
    public float calculateVolumeOfPrism(List<float[]> points);
}
