import java.io.*;
import java.util.*;

public class TP03 {
    public static void main(String[] args) {
        InputStream inputStream = System.in;
        InputReader in = new InputReader(inputStream);
        OutputStream outputStream = System.out;
        PrintWriter out = new PrintWriter(outputStream);

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
            long weight = in.nextLong();
            graph.addEdge(from, to, weight);
            graph.addEdge(to, from, weight);
        }

        int Q = in.nextInt();
        for (int i = 0; i < Q; i++) {
            String query = in.next();
            switch (query) {
                case "M":
                    M(in, out, graph);
                    break;
                case "S":
                    S(in, out, graph);
                    break;
                case "T":
                    T(in, out, graph);
                    break;
                default:
                    break;
            }

        }
        out.close();
    }

    private static void M(InputReader in, PrintWriter out, Graph graph) {
        long groupSize = in.nextLong();
        out.println(graph.jumlahMaksimumTreasureRoom(groupSize));
    }

    private static void S(InputReader in, PrintWriter out, Graph graph) {
        int ruangStart = in.nextInt();
        out.println(graph.banyakAnggotaTerkecil(ruangStart));
    }

    private static void T(InputReader in, PrintWriter out, Graph graph) {
        int idStart = in.nextInt();
        int idMiddle = in.nextInt();
        int idEnd = in.nextInt();
        long size = in.nextLong();
        out.println(graph.bisaBergerak(idStart, idMiddle, idEnd, size));
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

class Edge {
    int to;
    long weight;

    Edge(int to, long weight) {
        this.to = to;
        this.weight = weight;
    }
}

class Graph {
    private ArrayList<ArrayList<Edge>> graph;
    private ArrayList<Boolean> treasureRoom;

    public Graph(int V) {
        graph = new ArrayList<>();
        treasureRoom = new ArrayList<>(Collections.nCopies(V + 1, false));
        for (int i = 0; i <= V; i++) {
            graph.add(new ArrayList<>());
        }
    }

    public void addEdge(int from, int to, long weight) {
        graph.get(from).add(new Edge(to, weight));
    }

    public void addTreasureRoom(int idRuang) {
        if (idRuang > 0 && idRuang < treasureRoom.size()) {
            treasureRoom.set(idRuang, true);
        } else {
            throw new IllegalArgumentException("Invalid room ID: " + idRuang);
        }
    }


    public int jumlahMaksimumTreasureRoom(long groupSize) {
        return dfs(groupSize);
    }

    public int banyakAnggotaTerkecil(int ruangStart) {
        return bfs(ruangStart);
    }

    public String bisaBergerak(int idStart, int idMiddle, int idEnd, long groupSize) {
        return bisaMencapai(idStart, idMiddle, idEnd, groupSize);
    }

    private int dfs(long groupSize) {
        boolean[] visited = new boolean[graph.size()];
        ArrayDeque<Integer> stack = new ArrayDeque<>();
        stack.push(1);
        int maxRooms = 0;

        while (!stack.isEmpty()) {
            int node = stack.pop();
            if (visited[node]) {
                continue;
            }
            visited[node] = true;

            if (treasureRoom.get(node)) {
                maxRooms++;
            }

            for (Edge edge : graph.get(node)) {
                if (edge.weight <= groupSize && !visited[edge.to]) {
                    stack.push(edge.to);
                }
            }
        }
        return maxRooms;
    }

    private int bfs(int ruangStart) {
        boolean[] visited = new boolean[graph.size()];
        LinkedList<Integer> queue = new LinkedList<>();
        long[] maxEdgeWeightTo = new long[graph.size()];
        Arrays.fill(maxEdgeWeightTo, Long.MAX_VALUE);
        maxEdgeWeightTo[ruangStart] = 0;
        queue.add(ruangStart);

        while (!queue.isEmpty()) {
            int currentRoom = queue.poll();
            if (visited[currentRoom]) {
                continue;
            }
            visited[currentRoom] = true;

            if (treasureRoom.get(currentRoom)) {
                return (int) maxEdgeWeightTo[currentRoom];
            }

            for (Edge edge : graph.get(currentRoom)) {
                if (!visited[edge.to]) {
                    long weightToNeighbour = Math.max(maxEdgeWeightTo[currentRoom], edge.weight);
                    if (weightToNeighbour < maxEdgeWeightTo[edge.to]) {
                        maxEdgeWeightTo[edge.to] = weightToNeighbour;
                        queue.add(edge.to);
                    }
                }
            }
        }
        return -1;
    }

    private String bisaMencapai(int idStart, int idMiddle, int idEnd, long groupSize) {
        boolean[] visited = new boolean[graph.size()];
        ArrayDeque<Integer> queue = new ArrayDeque<>();
        queue.add(idStart);
        visited[idStart] = true;

        boolean reachedMiddle = idStart == idMiddle;

        while (!queue.isEmpty()) {
            int current = queue.poll();
            if (current == idEnd && reachedMiddle) {
                return "Y";
            }
            if (current == idMiddle) {
                reachedMiddle = true;
            }

            for (Edge edge : graph.get(current)) {
                if (!visited[edge.to] && edge.weight <= groupSize) {
                    visited[edge.to] = true;
                    queue.add(edge.to);
                }
            }
        }
        return reachedMiddle ? "H" : "N";
    }
}