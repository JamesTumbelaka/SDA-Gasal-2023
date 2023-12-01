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
            char roomType = in.next().charAt(0);
            graph.rooms.add(new Room(roomType));
        }

        for (int i = 0; i < E; i++) {
            int from = in.nextInt();
            int to = in.nextInt();
            int length = in.nextInt();
            int size = in.nextInt();
            graph.addEdge(from, to, length, size);
        }

        int Q = in.nextInt();
        for (int i = 0; i < Q; i++) {
            char queryType = in.next().charAt(0);

            switch (queryType) {
                case 'M':
                    int groupSize = in.nextInt();
                    int resultM = graph.maxTreasureRooms(groupSize, 1);
                    out.println(resultM);
                    break;
                case 'S':
                    int startId = in.nextInt();
                    int resultS = graph.minGroupSizeForTreasure(startId);
                    out.println(resultS);
                    break;
                case 'T':
                    startId = in.nextInt();
                    int middleId = in.nextInt();
                    int endId = in.nextInt();
                    groupSize = in.nextInt();
                    char resultT = graph.canReachTreasureRoom(startId, middleId, endId, groupSize);
                    out.println(resultT == 'Y' ? 1 : (resultT == 'H' ? 2 : 0));
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
    }
}

class Pair implements Comparable<Pair> {
    int src, weight, k;

    public Pair(int src, int weight) {
        this.src = src;
        this.weight = weight;
    }

    public Pair(int src, int weight, int k) {
        this.src = src;
        this.weight = weight;
        this.k = k;
    }

    @Override
    public int compareTo(Pair o) {
        return (int) (this.weight - o.weight);
    }
}

class Edge {
    int to, length, size;

    public Edge(int to, int length, int size) {
        this.to = to;
        this.length = length;
        this.size = size;
    }
}

class Room {
    public char type;

    public Room(char type) {
        this.type = type;
    }
}

class Graph {
    public int V;
    public ArrayList<Room> rooms;
    public ArrayList<ArrayDeque<Edge>> adj;

    public Graph(int v) {
        this.V = v;
        this.rooms = new ArrayList<>();
        this.adj = new ArrayList<>();
        this.rooms.add(new Room('X'));
        this.adj.add(new ArrayDeque<>()); 
        for (int i = 1; i <= v; i++)
            this.adj.add(new ArrayDeque<>());
            this.rooms.add(new Room('N'));
    }

    public void addEdge(int from, int to, int length, int size) {
        if (from >= 1 && from <= V && to >= 1 && to <= V) {
        this.adj.get(from - 1).add(new Edge(to - 1, length, size));
    }
    }

    public ArrayList<Integer> getMaxSize(int source) {
        if (source == 0)
            return null;
        ArrayList<Integer> bottleneck = new ArrayList<>();
        for (int i = 0; i <= this.V; i++)
            bottleneck.add(Integer.MIN_VALUE);
        bottleneck.set(source, Integer.MAX_VALUE);

        Heap pq = new Heap(this.V);
        pq.add(new Pair(source, 0));

        boolean[] visited = new boolean[this.V + 1];

        while (!pq.isEmpty()) {
            Pair curr = pq.poll();
            int v = curr.src;

            visited[v] = true;
            for (Edge e : this.adj.get(v)) {
                int u = e.to;
                int weight = e.size;
                int maxBottleneck = Math.max(bottleneck.get(u), Math.min(bottleneck.get(v), weight));
                if (!visited[u] && maxBottleneck > bottleneck.get(u)) {
                    bottleneck.set(u, maxBottleneck);
                    pq.add(new Pair(u, maxBottleneck));
                }
            }
        }

        return bottleneck;
    }

    public ArrayList<Integer> dijkstra(ArrayList<Integer> sources) {
        if (sources.isEmpty())
            return null;
        ArrayList<Integer> dist = new ArrayList<>();
        for (int i = 0; i <= this.V; i++)
            dist.add(Integer.MAX_VALUE);

        Heap pq = new Heap(this.V, true);

        for (int src : sources) {
            dist.set(src, 0);
            pq.add(new Pair(src, 0));
        }

        boolean[] visited = new boolean[this.V + 1];

        while (!pq.isEmpty()) {
            Pair curr = pq.poll();
            int v = curr.src;
            int w = curr.weight;

            visited[v] = true;

            if (w > dist.get(v))
                continue;

            for (Edge e : this.adj.get(v)) {
                int u = e.to;
                int weight = e.length;
                if (!visited[u] && dist.get(v) + weight < dist.get(u)) {
                    dist.set(u, dist.get(v) + weight);
                    pq.add(new Pair(u, dist.get(u)));
                }
            }
        }

        return dist;
    }

    public ArrayList<Integer> dijkstra(int source) {
        if (source == 0)
            return null;
        ArrayList<Integer> dist = new ArrayList<>();
        for (int i = 0; i <= this.V; i++)
            dist.add(Integer.MAX_VALUE);
        dist.set(source, 0);

        Heap pq = new Heap(this.V, true);
        pq.add(new Pair(source, 0));

        boolean[] visited = new boolean[this.V + 1];

        while (!pq.isEmpty()) {
            Pair curr = pq.poll();
            int v = curr.src;
            int w = curr.weight;

            visited[v] = true;

            if (w > dist.get(v))
                continue;

            for (Edge e : this.adj.get(v)) {
                int u = e.to;
                int weight = e.length;
                if (!visited[u] && dist.get(v) + weight < dist.get(u)) {
                    dist.set(u, dist.get(v) + weight);
                    pq.add(new Pair(u, dist.get(u)));
                }

            }
        }

        return dist;
    }

    public int[][] superDijkstra(int source) {
        if (source == 0)
            return null;
        int K = 1;
        int[][] dp = new int[this.V + 1][K + 1];
        for (int i = 0; i <= this.V; i++) {
            dp[i][0] = Integer.MAX_VALUE;
            dp[i][1] = Integer.MAX_VALUE;
        }
        dp[source][0] = 0;

        Heap pq = new Heap(this.V, true);
        pq.add(new Pair(source, 0, 0));

        while (!pq.isEmpty()) {
            Pair curr = pq.poll();
            int v = curr.src;
            int w = curr.weight;
            int k = curr.k;

            if (w > dp[v][k])
                continue;

            // relax
            for (Edge e : this.adj.get(v)) {
                int u = e.to;
                int weight = e.length;

                if (dp[v][k] + weight < dp[u][k]) {
                    dp[u][k] = dp[v][k] + weight;
                    pq.add(new Pair(u, dp[u][k], k));
                }

                if (k + 1 <= K && dp[v][k] < dp[u][k + 1]) {
                    dp[u][k + 1] = dp[v][k];
                    pq.add(new Pair(u, dp[u][k + 1], k + 1));
                }
            }
        }

        return dp;
    }

    public int maxTreasureRooms(int groupSize, int startRoom) {
        boolean[] visited = new boolean[this.V + 1];
        Queue<Integer> queue = new LinkedList<>();
        queue.add(startRoom);
        visited[startRoom] = true;

        int countTreasureRooms = 0;
        while (!queue.isEmpty()) {
            int currentRoom = queue.poll();
            if (rooms.get(currentRoom).type == 'S' && canAccessWithGroupSize(currentRoom, groupSize)) {
                countTreasureRooms++;
            }
            for (Edge e : this.adj.get(currentRoom)) {
                if (!visited[e.to] && e.size >= groupSize) {
                    visited[e.to] = true;
                    queue.add(e.to);
                }
            }
        }
        return countTreasureRooms;
    }
    private boolean canAccessWithGroupSize(int room, int groupSize) {
        for (Edge edge : this.adj.get(room)) {
            if (edge.size >= groupSize) {
                return true;
            }
        }
        return false;
    }
    private boolean checkPath(int from, int to, int groupSize) {
        boolean[] visited = new boolean[this.V + 1];
        Queue<Integer> queue = new LinkedList<>();
        queue.add(from);
        visited[from] = true;

        while (!queue.isEmpty()) {
            int current = queue.poll();

            if (current == to) {
                return true;
            }

            for (Edge e : this.adj.get(current)) {
                if (!visited[e.to] && e.size >= groupSize) {
                    visited[e.to] = true;
                    queue.add(e.to);
                }
            }
        }
        return false;
    }
    public char canReachTreasureRoom(int startId, int middleId, int endId, int groupSize) {
       
        boolean canReachMiddle = checkPath(startId, middleId, groupSize);
        boolean canReachEnd = checkPath(middleId, endId, groupSize);

        if (!canReachMiddle) {
            return 'N';
        } else if (!canReachEnd) {
            return 'H';
        } else {
            return 'Y';
        }
    }

    public int minGroupSizeForTreasure(int startId) {
        
        int[] minGroupSize = new int[this.V + 1];
        Arrays.fill(minGroupSize, Integer.MAX_VALUE);
        minGroupSize[startId] = 0;

        Queue<Integer> queue = new LinkedList<>();
        queue.add(startId);

        while (!queue.isEmpty()) {
            int current = queue.poll();

            if (rooms.get(current).type == 'S') {
                return minGroupSize[current];
            }

            for (Edge e : this.adj.get(current)) {
                if (minGroupSize[current] < e.size && minGroupSize[e.to] > e.size) {
                    minGroupSize[e.to] = e.size;
                    queue.add(e.to);
                }
            }
        }
        return -1;
    }
}

class Heap {

    public int capacity;
    public int size;
    public Pair[] data;
    public boolean isMinHeap = false;

    public Heap(int capacity) {
        this.capacity = capacity;
        this.size = 0;
        this.data = new Pair[capacity];
    }

    public Heap(int capacity, boolean isMinHeap) {
        this(capacity);
        this.isMinHeap = isMinHeap;
    }

    public boolean isEmpty() {
        return this.size == 0;
    }

    public int getLeftChildIndex(int parentIndex) {
        return 2 * parentIndex + 1;
    }

    public int getRightChildIndex(int parentIndex) {
        return 2 * parentIndex + 2;
    }

    public int getParentIndex(int childIndex) {
        return (childIndex - 1) / 2;
    }

    public boolean hasLeftChild(int index) {
        return getLeftChildIndex(index) < size;
    }

    public boolean hasRightChild(int index) {
        return getRightChildIndex(index) < size;
    }

    public boolean hasParent(int index) {
        return getParentIndex(index) >= 0;
    }

    public Pair leftChild(int index) {
        return data[getLeftChildIndex(index)];
    }

    public Pair rightChild(int index) {
        return data[getRightChildIndex(index)];
    }

    public Pair parent(int index) {
        return data[getParentIndex(index)];
    }

    public void swap(int a, int b) {
        Pair temp = data[a];
        data[a] = data[b];
        data[b] = temp;
    }

    public void ensureCapacity() {
        if (size == capacity) {
            if (capacity == 0) {
                data = Arrays.copyOf(data, 1);
                capacity = 1;
            } else {
                data = Arrays.copyOf(data, capacity * 2);
                capacity *= 2;
            }
        }
    }

    public Pair peek() {
        if (size == 0) {
            return null;
        }
        return data[0];
    }

    public Pair poll() {
        if (size == 0) {
            return null;
        }
        Pair item = data[0];
        data[0] = data[size - 1];
        size--;

        heapifyDown(0);
        return item;
    }

    public void add(Pair item) {
        ensureCapacity();
        data[size] = item;
        size++;

        heapifyUp(size - 1);
    }

    public void heapifyUp(int index) {
        if (isMinHeap) {
            while (hasParent(index) && parent(index).compareTo(data[index]) > 0) {
                swap(getParentIndex(index), index);
                index = getParentIndex(index);
            }
        } else {
            while (hasParent(index) && parent(index).compareTo(data[index]) < 0) {
                swap(getParentIndex(index), index);
                index = getParentIndex(index);
            }
        }
    }

    public void heapifyDown(int index) {
        while (hasLeftChild(index)) {
            int smallestChildIndex = getLeftChildIndex(index);

            if (isMinHeap) {
                if (hasRightChild(index) && rightChild(index).compareTo(leftChild(index)) < 0) {
                    smallestChildIndex = getRightChildIndex(index);
                }
                if (data[index].compareTo(data[smallestChildIndex]) < 0) {
                    break;
                } else {
                    swap(index, smallestChildIndex);
                }
            } else {
                if (hasRightChild(index) && rightChild(index).compareTo(leftChild(index)) > 0) {
                    smallestChildIndex = getRightChildIndex(index);
                }
                if (data[index].compareTo(data[smallestChildIndex]) > 0) {
                    break;
                } else {
                    swap(index, smallestChildIndex);
                }
            }
            index = smallestChildIndex;
        }
    }
}
