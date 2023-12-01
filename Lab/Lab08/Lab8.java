import java.io.*;
import java.util.*;

public class Lab8 {
    private static InputReader in;
    private static PrintWriter out;

    public static void main(String[] args) {
        InputStream inputStream = System.in;
        in = new InputReader(inputStream);
        OutputStream outputStream = System.out;
        out = new PrintWriter(outputStream);

        int N = in.nextInt();
        int E = in.nextInt();
        Graph graph = new Graph(N);

        for (int i = 0; i < E; i++) {
            int A = in.nextInt();
            int B = in.nextInt();
            long W = in.nextLong();
            graph.addEdge(A, B, W);
        }

        int H = in.nextInt(); 
        ArrayList<Integer> treasureNodes = new ArrayList<Integer>();
        treasureNodes.add(1);
        for (int i = 0; i < H; i++) {
            int K = in.nextInt();
            treasureNodes.add(K);
        }

        HashMap<Integer,ArrayList<Long>> idToShortestPath = new HashMap<>();
        for (int titik : treasureNodes) {
            ArrayList<Long> jarak = graph.shortestPath(titik);
            idToShortestPath.put(titik, jarak);
        }

        int Q = in.nextInt();
        int O = in.nextInt();
        while (Q-- > 0) {
            Long totalOxygenNeeded = (long) 0;

            int T = in.nextInt();
            int davePosition = 1;
            while (T-- > 0) {
                int D = in.nextInt();
                ArrayList<Long> arr = idToShortestPath.get(davePosition);
                totalOxygenNeeded += arr.get(D);
                davePosition = D;
            }
            ArrayList<Long> arr = idToShortestPath.get(davePosition);
            totalOxygenNeeded += arr.get(1);
            davePosition = 1;
            out.println((totalOxygenNeeded >= O) ? 0 : 1);
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

class Edge {
    int to;
    long weight;

    public Edge(int to, long weight) {
        this.to = to;
        this.weight = weight;
    }
}

class Pair implements Comparable<Pair> {
    int from;
    long weight;

    public Pair(int from, long weight) {
        this.from = from;
        this.weight = weight;
    }

    @Override
    public int compareTo(Pair o) {
        return (int) (this.weight - o.weight);
    }
}

class Graph {
    int vertex;
    ArrayList<ArrayDeque<Edge>> adj;

    public Graph(int N) {
        vertex = N;
        adj = new ArrayList<>();
        for (int i = 0; i <= N; i++) 
            adj.add(new ArrayDeque<>());
    }

    public void addEdge(int from, int to, long weight) {
        this.adj.get(from).add(new Edge(to, weight));
        this.adj.get(to).add(new Edge(from, weight));
    }

    public ArrayList<Long> shortestPath(int source) {
        if (source == 0)
            return null;
        ArrayList<Long> jarak = new ArrayList<>();
        for (int i = 0; i <= this.vertex; i++)
            jarak.add(Long.MAX_VALUE);
        jarak.set(source, (long) 0);

        PriorityQueue<Pair> pq = new PriorityQueue<>();
        pq.add(new Pair(source, 0));

        while (!pq.isEmpty()) {
            Pair curr = pq.poll();
            int v = curr.from;
            long w = curr.weight;

            if (w > jarak.get(v))
                continue;

            for (Edge e : this.adj.get(v)) {
                int u = e.to;
                long weight = e.weight;
                if (jarak.get(v) + weight < jarak.get(u)) {
                    jarak.set(u, jarak.get(v) + weight);
                    pq.add(new Pair(u, jarak.get(u)));
                }
            }
        }
        return jarak;
    }
}