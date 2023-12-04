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
    private Map<Integer, List<Edge>> graph;
    private Set<Integer> treasureRoom;

    public Graph(int V) {
        graph = new HashMap<>();
        treasureRoom = new HashSet<>();
        for (int i = 1; i <= V; i++) {
            graph.put(i, new ArrayList<>());
        }
    }

    public void addEdge(int from, int to, long weight) {
        graph.get(from).add(new Edge(to, weight));
    }

    public void addTreasureRoom(int idRuang) {
        treasureRoom.add(idRuang);
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
        Set<Integer> visited = new HashSet<>();
        ArrayDeque<Integer> stack = new ArrayDeque<>();
        stack.push(1);
        int maxRooms = 0;

        while (!stack.isEmpty()) {
            int node = stack.pop();
            if (visited.contains(node)) {
                continue;
            }
            visited.add(node);

            if (treasureRoom.contains(node)) {
                maxRooms++;
            }

            for (Edge edge : graph.get(node)) {
                if (edge.weight <= groupSize && !visited.contains(edge.to)) {
                    stack.push(edge.to);
                }
            }
        }
        return maxRooms;
    }

    private int bfs(int ruangStart) {
        PriorityQueue<Edge> queue = new PriorityQueue<>(Comparator.comparingLong(e -> e.weight));
        Set<Integer> visited = new HashSet<>();
        queue.add(new Edge(ruangStart, 0));

        while (!queue.isEmpty()) {
            Edge currentEdge = queue.poll();
            int currentRoom = currentEdge.to;
            long currentWeight = currentEdge.weight;

            if (visited.contains(currentRoom)) {
                continue;
            }
            visited.add(currentRoom);

            if (treasureRoom.contains(currentRoom)) {
                return (int) currentWeight;
            }

            for (Edge edge : graph.get(currentRoom)) {
                long nextWeight = Math.max(currentWeight, edge.weight);
                if (!visited.contains(edge.to)) {
                    queue.add(new Edge(edge.to, nextWeight));
                }
            }
        }
        return -1;
    }

    private String bisaMencapai(int idStart, int idMiddle, int idEnd, long groupSize) {
        boolean canReachMiddle = isReachable(idStart, idMiddle, groupSize);
        boolean canReachEndFromMiddle = isReachable(idMiddle, idEnd, groupSize);

        if (!canReachMiddle) {
            return "N";
        } else if (canReachMiddle && !canReachEndFromMiddle) {
            return "H";
        } else {
            return "Y";
        }
    }

    private boolean isReachable(int start, int end, long groupSize) {
        int[] distance = new int[graph.size() + 1];
        Arrays.fill(distance, Integer.MAX_VALUE);
        distance[start] = 0;

        Queue<Integer> queue = new LinkedList<>();
        queue.offer(start);

        while (!queue.isEmpty()) {
            int current = queue.poll();
            if (current == end) {
                return true;
            }

            for (Edge edge : graph.get(current)) {
                if (distance[edge.to] == Integer.MAX_VALUE && edge.weight <= groupSize) {
                    distance[edge.to] = distance[current] + 1;
                    queue.offer(edge.to);
                }
            }
        }
        return false;
    }
}