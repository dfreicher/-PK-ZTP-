import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Core algorithm
 *
 * @author Dominik Freicher
 * @version 1.0
 *
 */
class Path {

    private int startPoint;
    private int endPoint;
    private float totalCost;

    private Map<Integer, Edge> graph;
    private List<Float> costs;

    /**
     *
     * @param graph
     * @param startPoint
     * @param endPoint
     */
    public Path(Map<Integer, Edge> graph, int startPoint, int endPoint) {
        setGraph(graph);
        setStartPoint(startPoint);
        setEndPoint(endPoint);
    }

    /**
     *
     * @return startPoint
     */
    public int getStartPoint() {
        return startPoint;
    }

    /**
     *
     * @param startPoint
     */
    public void setStartPoint(int startPoint) {
        this.startPoint = startPoint;
    }

    /**
     *
     * @return endPoint
     */
    public int getEndPoint() {
        return endPoint;
    }

    /**
     *
     * @param endPoint
     */
    public void setEndPoint(int endPoint) {
        this.endPoint = endPoint;
    }

    /**
     *
     * @param graph
     */
    public void setGraph(Map<Integer, Edge> graph) {
        this.graph = graph;
    }

    /**
     *
     * @return totalCost
     */
    public float getTotalCost() {
        return totalCost;
    }

    /**
     *
     * @param totalCost
     */
    public void setTotalCost(float totalCost) {
        this.totalCost = totalCost;
    }

    /**
     *
     * @return costs
     */
    public List<Float> getCosts() {
        return costs;
    }

    /**
     *
     * @param costs
     */
    public void setCosts(List<Float> costs) {
        this.costs = costs;
    }

    void process() {
        List<Float> costsList = new ArrayList<Float>();
        List<Integer> road = new ArrayList<Integer>();
        road.add(getStartPoint());

        setTotalCost(Float.MAX_VALUE);
        setCosts(costsList);
        searchPath(getEndPoint(), road);
    }

    private void searchPath(int endPoint, List<Integer> road) {
        ArrayList<Integer> edges = getEdgesOfVertex(road.get(road.size() - 1));
        for (int id : edges) {

            int y = graph.get(id).getY();
            if (road.contains(y)) {
                continue;
            }

            if (y == endPoint) {
                addRoadData(road, id, y);
                calculateTotalCost(0);
                removeRoadData(road);
            } else {
                if (road.contains(y)) {
                    continue;
                }
                addRoadData(road, id, y);
                searchPath(endPoint, road);
                removeRoadData(road);
            }
        }
    }

    private void removeRoadData(List<Integer> road) {
        road.remove(road.size() - 1);
        getCosts().remove(getCosts().size() - 1);
    }

    private void addRoadData(List<Integer> road, int id, int y) {
        road.add(y);
        getCosts().add(graph.get(id).getP());
    }

    private ArrayList<Integer> getEdgesOfVertex(int id) {
        ArrayList<Integer> edges = new ArrayList<Integer>();
        for (Map.Entry<Integer, Edge> entry : graph.entrySet()) {
            int key = entry.getKey();
            Edge edge = entry.getValue();
            if (edge.getX() == id) {
                edges.add(key);
            }
        }
        return edges;
    }

    private void calculateTotalCost(float cost) {
        int costsSize = getCosts().size();
        for (int i = 0; i < costsSize; i++) {
            if (cost > getTotalCost())
                return;

            cost += 1 / getCosts().get(i);
            if (i != 0) {
                cost += Math.abs(getCosts().get(i - 1) - getCosts().get(i));
            }
        }

        if (cost < getTotalCost()) {
            setTotalCost(cost);
        }
    }

    void printResult() {
        System.out.format(Locale.US, "Koszt : %.3f", getTotalCost());
    }

    static class Edge {
        private final int x;
        private final int y;
        private final float p;

        Edge(int x, int y, float p) {
            this.x = x;
            this.y = y;
            this.p = p;
        }

        private int getX() {
            return x;
        }

        private int getY() {
            return y;
        }

        private float getP() {
            return p;
        }
    }
}
