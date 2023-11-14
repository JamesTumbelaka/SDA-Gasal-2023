import java.io.*;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.StringTokenizer;

public class Lab3 {
    private static InputReader in;
    private static PrintWriter out;
    static Long T;
    static String defaultDirection = "KANAN";
    static Deque<Deque<Integer>> Building = new ArrayDeque<> ();

    // Metode GA
    static String GA() {
        if (defaultDirection.equals( "KANAN")) {
            defaultDirection = "KIRI";
        }
        else {
            defaultDirection = "KANAN";
        }
        return defaultDirection;
    }

    // Metode S
    static long S (long Si) {
        long floor = 0;
        long currentDamage = 0;

        if (defaultDirection.equals("KANAN")) {
            Deque<Integer> temp = Building.pollFirst();
            try {
                while (!temp.isEmpty() && Si > 0) {
                    floor = (int) temp.pop();
                    currentDamage += floor;
                    T = T - floor;
                    Si--;
                }
                if (!temp.isEmpty()) {
                    Building.addLast(temp);
                }
            }
            catch (Exception e){
            }
        }

        else {
            Deque<Integer> temp = Building.pollFirst();
            while (!temp.isEmpty() && Si > 0) {
                floor = temp.pop();
                currentDamage += floor;
                T = T - floor;
                Si--;
            }
            Building.addFirst(temp);
            Deque<Integer> tempDeque = Building.pollLast();
            Building.addFirst(tempDeque);
        }

        if (T <= 0) {
            return -1;
        }

        return currentDamage;
    }

    // Template
    public static void main(String[] args) {
        InputStream inputStream = System.in;
        in = new InputReader(inputStream);
        OutputStream outputStream = System.out;
        out = new PrintWriter(outputStream);

        // Read input
        T = in.nextLong();
        int X = in.nextInt();
        int C = in.nextInt();
        int Q = in.nextInt();

        for (int i = 0; i < X; i++) {
            Deque<Integer> dequeFloor = new ArrayDeque<>(C);

            // Insert into ADT
            for (int j = 0; j < C; j++) {
                int Ci = in.nextInt();
                dequeFloor.addLast(Ci);
            }
            Building.add(dequeFloor);
        }

        // Process the query
        for (int i = 0; i < Q; i++) {
            String perintah = in.next();
            if (perintah.equals("GA")) {
                out.println(GA());
            } else if (perintah.equals("S")) {
                long Si = in.nextLong();
                long a = S(Si);

                if (a <= 0) {
                    out.println("MENANG");
                }
                else {
                    out.println(a);
                }
            }
        }

        // don't forget to close the output
        out.close();
    }
    // taken from https://codeforces.com/submissions/Petr
    // together with PrintWriter, these input-output (IO) is much faster than the usual Scanner(System.in) and System.out
    // please use these classes to avoid your fast algorithm gets Time Limit Exceeded caused by slow input-output (IO)
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