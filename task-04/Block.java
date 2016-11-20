import javax.ejb.Stateless;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dominik Freicher
 * @version 1.0
 *
 */
@Stateless
public class Block implements IBlockRemote {

    private List<float[]> data;
    private List<AxisPoint> axisPoints = new ArrayList<AxisPoint>();
    private float surfaceArea;
    private float height;

    /**
     *
     * @param points
     * @return volume of prism
     */
    @Override
    public float calculateVolumeOfPrism(List<float[]> points) {
        setData(points);
        calculateHeightOfPrism();
        calculateSurfaceAreaOfPrism();
        return getSurfaceArea() * getHeight();
    }

    private void calculateHeightOfPrism() {
        float sum = 0;
        for (float[] point : getData()) {
            sum += point[2];
        }
        setHeight(sum / getData().size());
    }

    private void calculateSurfaceAreaOfPrism() {
        prepareAxisPoints();

        AxisPoint startPoint = axisPoints.remove(0);
        int neighborPointIndex = getNeighborPointIndex(startPoint);

        AxisPoint neighborPoint = axisPoints.remove(neighborPointIndex);
        axisPoints.add(0, neighborPoint);

        sortAxisPoints(startPoint, neighborPoint);

        float surfaceArea = addAreas(startPoint);
        setSurfaceArea(surfaceArea);
    }

    private void prepareAxisPoints() {
        for (float[] p : getData()) {
            axisPoints.add(new AxisPoint(p[0], p[1]));
        }
    }

    private int getNeighborPointIndex(AxisPoint startPoint) {
        int neighborPointIndex = 0;
        double maxCorner = 0;
        double corner;

        int size = axisPoints.size();
        for (int i = 0; i < size; i++) {
            corner = startPoint.calculateVectorAngle(axisPoints.get(0),
                    axisPoints.get(i));
            if (corner > maxCorner) {
                maxCorner = corner;
                neighborPointIndex = i;
            }
        }

        return neighborPointIndex;
    }

    private void sortAxisPoints(AxisPoint startPoint, AxisPoint neighborPoint) {
        double corner;
        int size = axisPoints.size();
        for (int i = 1; i < size; i++) {
            int counter = i;
            AxisPoint axisPoint = axisPoints.get(i);
            corner = startPoint.calculateVectorAngle(neighborPoint, axisPoint);
            double angle = startPoint.calculateVectorAngle(neighborPoint,
                    axisPoints.get(counter - 1));

            while (counter > 0 && (angle > corner)) {
                axisPoints.set(counter, axisPoints.get(counter - 1));
                counter--;
            }

            axisPoints.set(counter, axisPoint);
        }
    }

    private float addAreas(AxisPoint p) {
        float sum = 0;
        int size = axisPoints.size();
        for (int i = 0; i < size - 1; i++) {
            sum += p.calculateArea(axisPoints.get(i), axisPoints.get(i+1));
        }

        return sum;
    }

    private List<float[]> getData() {
        return data;
    }

    private void setData(List<float[]> data) {
        this.data = data;
    }

    private float getSurfaceArea() {
        return surfaceArea;
    }

    private void setSurfaceArea(float surfaceArea) {
        this.surfaceArea = surfaceArea;
    }

    private float getHeight() {
        return height;
    }

    private void setHeight(float height) {
        this.height = height;
    }

    private class AxisPoint {
        private float x;
        private float y;

        AxisPoint(float x, float y) {
            this.x = x;
            this.y = y;
        }

        double calculateVectorAngle(AxisPoint firstPoint,
                                    AxisPoint secondPoint) {
            double dPoints = firstPoint.calculateDistanceBetweenPoints(secondPoint);
            double dFirst = this.calculateDistanceBetweenPoints(firstPoint);
            double dSecond = this.calculateDistanceBetweenPoints(secondPoint);

            return Math.toDegrees(
                    Math.acos((Math.pow(dFirst, 2) + Math.pow(dSecond, 2)
                    - Math.pow(dPoints, 2)) / (2 * dFirst * dSecond)));
        }

        float calculateArea(AxisPoint firstPoint, AxisPoint secondPoint) {
            float areaFirst = firstPoint.x * (secondPoint.y - this.y);
            float areaSecond = secondPoint.x * (this.y - firstPoint.y);
            float areaCommon = this.x * (firstPoint.y - secondPoint.y);

            return Math.abs((areaCommon + areaFirst + areaSecond) / 2);
        }

        double calculateDistanceBetweenPoints(AxisPoint point) {
            float distX = (point.x - this.x) * (point.x - this.x);
            float distY = (point.y - this.y) * (point.y - this.y);
            double distance = Math.sqrt(distY + distX);

            return distance;
        }
    }
}
