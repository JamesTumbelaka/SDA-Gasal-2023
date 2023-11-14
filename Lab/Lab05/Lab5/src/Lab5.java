import java.io.*;
import java.util.StringTokenizer;

public class Lab5 {

    private static InputReader in;
    private static PrintWriter out;
    private static DoublyLinkedList rooms = new DoublyLinkedList();

    public static void main(String[] args) {
        InputStream inputStream = System.in;
        in = new InputReader(inputStream);
        OutputStream outputStream = System.out;
        out = new PrintWriter(outputStream);

        int N = in.nextInt();

        for (int i = 0; i < N; i++) {
            char command = in.nextChar();
            char direction;

            switch (command) {
                case 'A':
                    direction = in.nextChar();
                    char type = in.nextChar();
                    add(type, direction);
                    break;
                case 'D':
                    direction = in.nextChar();
                    out.println(delete(direction));
                    break;
                case 'M':
                    direction = in.nextChar();
                    out.println(move(direction));
                    break;
                case 'J':
                    direction = in.nextChar();
                    out.println(jump(direction));
                    break;
            }
        }

        out.close();
    }

    public static void add(char type, char direction) {
        rooms.add(type, direction);
    }
    public static int delete(char direction) {
        return rooms.delete(direction);
    }
    public static int move(char direction) {
        return rooms.move(direction);
    }
    public static int jump(char direction) {
        return rooms.jump(direction);
    }
    private static class InputReader {
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

        public char nextChar() {
            return next().charAt(0);
        }

        public int nextInt() {
            return Integer.parseInt(next());
        }
    }
}

class DoublyLinkedList {

    private int nodeIdCounter = 1;
    ListNode first;
    ListNode current;
    ListNode last;
    int size = 0;

    public void add(char type, char direction) {
        ListNode newNode = new ListNode(type, nodeIdCounter++);
        if (first == null) {
            first = newNode;
            last = newNode;
            newNode.next = newNode;
            newNode.prev = newNode;
            current = newNode;
        } else {
            if (direction == 'L') {
                newNode.prev = current.prev;
                newNode.next = current;
                current.prev.next = newNode;
                current.prev = newNode;
            } else {
                newNode.prev = current;
                newNode.next = current.next;
                current.next.prev = newNode;
                current.next = newNode;
            }
        }
        size++;
    }

    public int delete(char direction) {
        if (size < 2) {
            return -1;
        }
        ListNode deletedNode;

        if (direction == 'R') {
            deletedNode = current.next;
            current.next = current.next.next;
            current.next.prev = current;
        } else {
            deletedNode = current.prev;
            current.prev = current.prev.prev;
            current.prev.next = current;
        }

        size--;
        return deletedNode.id;
    }

    public int move(char direction) {
        if (direction == 'R') {
            if (current.next != null) {
                current = current.next;
            }
        } else {
            if (current.prev != null) {
                current = current.prev;
            }
        }
        return current.id;
    }

    public int jump(char direction) {
        if (current.type == 'C') {
            return -1;
        }

        if (direction == 'L') {
            ListNode newNode = current.prev;
            while (newNode != null && newNode.type != 'S') {
                newNode = newNode.prev;
            }
            if (newNode != null) {
                current = newNode;
                return newNode.id;
            }
        } else {
            ListNode newNode = current.next;
            while (newNode != null && newNode.type != 'S') {
                newNode = newNode.next;
            }
            if (newNode != null) {
                current = newNode;
                return newNode.id;
            }
        }
        return -1;
    }
}

class ListNode {

    char type;
    ListNode next;
    ListNode prev;
    int id;

    ListNode(char type, int id) {
        this.type = type;
        this.id = id;
    }
}
