import java.io.*;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.StringTokenizer;

public class Lab3 {
    private static InputReader input;
    private static PrintWriter output;
    static Deque<Deque<Integer>> buildingQueue = new ArrayDeque<>();
    static Long remainingStrength;
    static String direction = "RIGHT";

    // Method to Change Direction
    static String changeDirection() {
        if (direction.equals("RIGHT")) {
            direction = "LEFT";
        } else {
            direction = "RIGHT";
        }
        return direction;
    }

    // Method to Calculate Damage
    static long calculateDamage(long strength) {
        long floor = 0;
        long totalDamage = 0;

        if (direction.equals("RIGHT")) {
            Deque<Integer> temp = buildingQueue.pollFirst();
            try {
                while (!temp.isEmpty() && strength > 0) {
                    floor = (int) temp.pop();
                    totalDamage += floor;
                    remainingStrength = remainingStrength - floor;
                    strength--;
                }
                if (!temp.isEmpty()) {
                    buildingQueue.addLast(temp);
                }
            } catch (Exception e) {
            }
        } else {
            Deque<Integer> temp = buildingQueue.pollFirst();
            try {
                while (!temp.isEmpty() && strength > 0) {
                    floor = (int) temp.pop();
                    totalDamage += floor;
                    remainingStrength = remainingStrength - floor;
                    strength--;
                }
                if (!temp.isEmpty()) {
                    buildingQueue.addFirst(temp);
                }
                buildingQueue.addFirst(buildingQueue.pollLast());
            } catch (Exception e) {
            }
        }

        if (remainingStrength <= 0) {
            return -1;
        }

        if (buildingQueue.isEmpty()) {
            return -1;
        }

        return totalDamage;
    }

    // Main Method
    public static void main(String[] args) {
        InputStream inputStream = System.in;
        input = new InputReader(inputStream);
        OutputStream outputStream = System.out;
        output = new PrintWriter(outputStream);

        // Read input
        remainingStrength = input.nextLong();
        int buildings = input.nextInt();
        int floorsPerBuilding = input.nextInt();
        int queries = input.nextInt();

        for (int i = 0; i < buildings; i++) {
            Deque<Integer> floorQueue = new ArrayDeque<Integer>(floorsPerBuilding);

            // Insert into ADT
            for (int j = 0; j < floorsPerBuilding; j++) {
                int floorDamage = input.nextInt();
                floorQueue.addLast(floorDamage);
            }

            buildingQueue.add(floorQueue);
        }

        // Process the queries
        for (int i = 0; i < queries; i++) {
            String command = input.next();
            if (command.equals("GA")) {
                output.println(changeDirection());
            } else if (command.equals("S")) {
                long strength = input.nextLong();
                long damage = calculateDamage(strength);

                if (damage <= 0) {
                    output.println("WIN");
                } else {
                    output.println(damage);
                }
            }
        }

        // Close the output
        output.close();
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
