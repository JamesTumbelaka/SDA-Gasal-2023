import java.io.*;
import java.util.*;

public class TP03 {

    private static InputReader in;
    private static PrintWriter out;
    public static void main(String[] args) {
        InputStream inputStream = System.in;
        in = new InputReader(inputStream);
        OutputStream outputStream = System.out;
        out = new PrintWriter(outputStream);

        int V = in.nextInt();
        int E = in.nextInt();

        Graph graph = new Graph(V);

        for (int i = 1; i <= V; i++) {
            String tipeRuang = in.next();
            if ("S".equals(tipeRuang)) {
                graph.addTreasureRoom(i);
            }
        }

        for (int i = 0; i < E; i++) {
            int from = in.nextInt();
            int to = in.nextInt();
            int weight = in.nextInt();
            graph.addEdge(from, to, weight);
            graph.addEdge(to, from, weight);
        }

        int Q = in.nextInt();
        for (int i = 0; i < Q; i++) {
            String query = in.next();
            switch (query) {
                case "M":
                    int groupSize = in.nextInt();
                    out.println(graph.findMaxTreasureRooms(groupSize));
                    break;
                case "S":
                    int startRoom = in.nextInt();
                    out.println(graph.findMinGroupSizeToTreasure(startRoom));
                    break;
                case "T":
                    int startId = in.nextInt();
                    int middleId = in.nextInt();
                    int endId = in.nextInt();
                    int size = in.nextInt();
                    out.println(graph.canTravel(startId, middleId, endId, size));
                    break;
                default:
                    break;
            }
        }
        out.close();
    }
    static class InputReader {
        public BufferedReader reader;
        public StringTokenizer tokenizer;

        public InputReader(InputStream stream) {
            reader = new BufferedReader(new InputStreamReader(stream), 32768);
            tokenizer = null;
        }

        public String next() {
            while (tokenizer == null || !tokenizer.hasMoreTokens()) {
                try {
                    tokenizer = new StringTokenizer(reader.readLine());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return tokenizer.nextToken();
        }

        public int nextInt() {
            return Integer.parseInt(next());
        }

        public long nextLong() {
            return Long.parseLong(next());
        }
    }
}

class Graph {
    private Map<Integer, List<Edge>> graph;
    private Set<Integer> treasureRooms;

    public Graph(int V) {
        graph = new HashMap<>();
        treasureRooms = new HashSet<>();
        for (int i = 1; i <= V; i++) {
            graph.put(i, new ArrayList<>());
        }
    }

    public void addEdge(int from, int to, int weight) {
        graph.get(from).add(new Edge(to, weight));
    }

    public void addTreasureRoom(int roomId) {
        treasureRooms.add(roomId);
    }

    public int findMaxTreasureRooms(int groupSize) {
        Set<Integer> visited = new HashSet<>();
        return dfs(1, groupSize, visited);
    }

    private int dfs(int node, int groupSize, Set<Integer> visited) {
        visited.add(node);
        int count = treasureRooms.contains(node) ? 1 : 0;

        for (Edge edge : graph.get(node)) {
            if (!visited.contains(edge.to) && edge.weight <= groupSize) {
                count += dfs(edge.to, groupSize, visited);
            }
        }

        return count;
    }

    public int findMinGroupSizeToTreasure(int startRoom) {
        Queue<Integer> queue = new LinkedList<>();
        Map<Integer, Integer> minGroupSize = new HashMap<>();
        queue.offer(startRoom);
        minGroupSize.put(startRoom, 0);

        while (!queue.isEmpty()) {
            int current = queue.poll();

            if (treasureRooms.contains(current)) {
                return minGroupSize.get(current);
            }

            for (Edge edge : graph.get(current)) {
                if (!minGroupSize.containsKey(edge.to) || minGroupSize.get(edge.to) > edge.weight) {
                    minGroupSize.put(edge.to, edge.weight);
                    queue.offer(edge.to);
                }
            }
        }

        return -1;
    }
    public String canTravel(int startId, int middleId, int endId, int groupSize) {
        boolean canReachMiddle = canReach(startId, middleId, groupSize);
        if (!canReachMiddle) {
            return "N";
        }
        boolean canReachEnd = canReach(middleId, endId, groupSize);
        return canReachEnd ? "Y" : "H";
    }

    private boolean canReach(int start, int end, int groupSize) {
        Queue<Integer> queue = new LinkedList<>();
        Set<Integer> visited = new HashSet<>();
        queue.offer(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            int current = queue.poll();
            if (current == end) {
                return true;
            }

            for (Edge edge : graph.get(current)) {
                if (!visited.contains(edge.to) && edge.weight <= groupSize) {
                    visited.add(edge.to);
                    queue.offer(edge.to);
                }
            }
        }
        return false;
    }

    static class Edge {
        int to;
        int weight;

        Edge(int to, int weight) {
            this.to = to;
            this.weight = weight;
        }
    }
}
