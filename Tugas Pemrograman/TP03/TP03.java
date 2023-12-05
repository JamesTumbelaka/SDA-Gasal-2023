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
        List<Integer> tRooms = new ArrayList<>();

        for (long i = 1; i <= V; i++) {
            String tipeRuang = in.next();
            if ("S".equals(tipeRuang)) {
                tRooms.add((int)i);
            }
        }

        for (long i = 0; i < E; i++) {
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
                    long groupSize = in.nextLong();
                    out.println(graph.jumlahMaksimumTreasureRoom(groupSize, tRooms));
                    break;
                case "S":
                    int ruangStart = in.nextInt();
                    out.println(graph.banyakAnggotaTerkecil(ruangStart, tRooms));
                    break;
                case "T":
                    int idStart = in.nextInt();
                    int idMiddle = in.nextInt();
                    int idEnd = in.nextInt();
                    long size = in.nextLong();
                    out.println(graph.bisaBergerak(idStart, idMiddle, idEnd, size));
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

class Edge {
    int to;
    long weight;

    Edge(int to, long weight) {
        this.to = to;
        this.weight = weight;
    }
}

class Graph {
    public long V;
    private ArrayList<ArrayList<Edge>> graph;
    public ArrayList<Integer> treasureRoom;

    public Graph(long v) {
        this.V = v;
        graph = new ArrayList<>((int)v + 1);
        treasureRoom = new ArrayList<>();
        for (int i = 0; i <= V; i++) {
            graph.add(new ArrayList<>());
        }
    }

    public void addEdge(int from, int to, long weight) {
        graph.get(from).add(new Edge(to, weight));
    }

    public int jumlahMaksimumTreasureRoom(long groupSize, List<Integer> rooms) {
        return maxTRooms(groupSize, rooms);
    }

    public long banyakAnggotaTerkecil(int ruangStart, List<Integer> tRooms) {
        return minGroupSize(ruangStart, tRooms);
    }

    public String bisaBergerak(int idStart, int idMiddle, int idEnd, long groupSize) {
        ArrayList<Long> distancesFromStart = dijkstra(idStart);

        if (distancesFromStart.get(idMiddle) > groupSize) {
            return "N";
        } else {
            ArrayList<Long> distancesFromMiddle = dijkstra(idMiddle);
            if (distancesFromMiddle.get(idEnd) <= groupSize) {
                return "Y";
            } else {
                return "H";
            }
        }
    }

    private int maxTRooms(long groupSize, List<Integer> rooms) {
        boolean[] isTRoom = new boolean[(int) V + 1];
        for (int room : rooms) {
            isTRoom[room] = true;
        }
        boolean[] visited = new boolean[(int) V + 1];
        Queue<Integer> q = new LinkedList<>();
        q.add(1);
        visited[1] = true;
        int maxRooms = 0;

        while (!q.isEmpty() && maxRooms < rooms.size()) { 
            int node = q.poll();
            if (isTRoom[node]) {  
                maxRooms++;
            }

            for (Edge edge : graph.get(node)) {
                if (!visited[edge.to] && edge.weight <= groupSize) {  
                    visited[edge.to] = true;
                    q.add(edge.to);
                }
            }
        }
        return maxRooms;
    }

    public long minGroupSize(int ruangStart, List<Integer> tRooms) {
        if (tRooms.contains(ruangStart)) {
            return 0;
        }

        long[] minGroupSizeToReach = new long[(int)(V + 1)];
        boolean[] processed = new boolean[(int)(V + 1)];
        Arrays.fill(minGroupSizeToReach, Long.MAX_VALUE);
        minGroupSizeToReach[ruangStart] = 0;

        Heap queue = new Heap((int)V, true);
        queue.add(new Pair(ruangStart, 0));

        while (!queue.isEmpty()) {
            Pair current = queue.poll();
            int currentNode = current.node;

            if (processed[currentNode]) {
                continue;
            }

            processed[currentNode] = true;

            for (Edge edge : graph.get(currentNode)) {
                int neighborNode = edge.to;
                long newWeight = Math.max(minGroupSizeToReach[currentNode], edge.weight);

                if (newWeight < minGroupSizeToReach[neighborNode]) {
                    minGroupSizeToReach[neighborNode] = newWeight;
                    if (!processed[neighborNode]) {
                        queue.add(new Pair(neighborNode, newWeight));
                    }
                }
            }
        }

        long minGroupSize = Long.MAX_VALUE;
        for (int room : tRooms) {
            if (minGroupSizeToReach[room] < minGroupSize) {
                minGroupSize = minGroupSizeToReach[room];
            }
        }
        return minGroupSize != Long.MAX_VALUE ? minGroupSize : -1;
    }

    public ArrayList<Long> dijkstra(int source) {
        int numVertices = graph.size();
        ArrayList<Long> groupSizeList = new ArrayList<>(Collections.nCopies(numVertices, Long.MAX_VALUE));
        boolean[] processed = new boolean[numVertices];
        groupSizeList.set(source, 0L);

        Heap pq = new Heap(numVertices, true);
        pq.add(new Pair(source, 0));

        while (!pq.isEmpty()) {
            Pair currentPair = pq.poll();
            int currentNode = currentPair.node;
            long currentGroupSize = currentPair.weight;

            if (processed[currentNode]) {
                continue;
            }

            processed[currentNode] = true;

            for (Edge edge : graph.get(currentNode)) {
                int neighborNode = edge.to;
                long newGroupSize = Math.max(currentGroupSize, edge.weight);

                if (!processed[neighborNode] && newGroupSize < groupSizeList.get(neighborNode)) {
                    groupSizeList.set(neighborNode, newGroupSize);
                    pq.add(new Pair(neighborNode, newGroupSize));
                }
            }
        }
        return groupSizeList;
    }
}

class Pair implements Comparable<Pair> {
    int node;
    long weight;

    public Pair(int node, long weight) {
        this.node = node;
        this.weight = weight;
    }

    @Override
    public int compareTo(Pair other) {
        return Long.compare(this.weight, other.weight);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair pair = (Pair) o;
        return node == pair.node;
    }

    @Override
    public int hashCode() {
        return Objects.hash(node);
    }
}

class Heap {

    private Pair[] data;
    private int size;
    private int capacity;
    private boolean minHeap = false;

    public Heap(int capacity) {
        this.capacity = capacity;
        this.size = 0;
        this.data = new Pair[capacity];
    }

    public Heap(int capacity, boolean minHeap) {
        this(capacity);
        this.minHeap = minHeap;
    }

    private int getLeftChildIndex(int parentIndex) { return 2 * parentIndex + 1; }
    private int getRightChildIndex(int parentIndex) { return 2 * parentIndex + 2; }
    private int getParentIndex(int childIndex) { return (childIndex - 1) / 2; }

    private boolean hasLeftChild(int index) { return getLeftChildIndex(index) < size; }
    private boolean hasRightChild(int index) { return getRightChildIndex(index) < size; }
    private boolean hasParent(int index) { return getParentIndex(index) >= 0; }

    private Pair leftChild(int index) { return data[getLeftChildIndex(index)]; }
    private Pair rightChild(int index) { return data[getRightChildIndex(index)]; }
    private Pair parent(int index) { return data[getParentIndex(index)]; }

    private void swap(int indexOne, int indexTwo) {
        Pair temp = data[indexOne];
        data[indexOne] = data[indexTwo];
        data[indexTwo] = temp;
    }

    public void ensureCapacity() {
        if (size == capacity) {
            int newCapacity = (capacity == 0) ? 1 : capacity * 2;
            data = Arrays.copyOf(data, newCapacity);
            capacity = newCapacity;
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
        if (minHeap) {
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
            if (minHeap) {
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

    public boolean isEmpty() {
        return size == 0;
    }

    public int findIndex(int node) {
        for (int i = 0; i < size; i++) {
            if (data[i].node == node) {
                return i;
            }
        }
        return -1;
    }

    public void adjustedAdd(Pair item) {
        int index = findIndex(item.node);
        if (index != -1) {
            data[index].weight = item.weight;
            if (hasParent(index) && parent(index).compareTo(data[index]) > 0) {
                heapifyUp(index);
            } else {
                heapifyDown(index);
            }
        } else {
            add(item);
        }
    }
}