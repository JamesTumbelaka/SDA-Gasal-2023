import java.io.*;
import java.util.StringTokenizer;

public class temp {
    private static InputReader in;
    private static PrintWriter out;

    static long maxOddEvenSubSum(long[] a) {
        long result = 0;
        long flag = Long.MIN_VALUE;

        for (int i = 0; i < a.length; i++) {
            if (a.length % 2 == 0) {
                if (a[i] % 2 == 0) {
                    result += a[i];
                    if (result > flag) {
                        flag = result;
                    }
                } else {
                    result = 0;
                }
            } else {
                if (a[i] % 2 != 0) {
                    result += a[i];
                    if (result > flag) {
                        flag = result;
                    }
                } else {
                    result = 0;
                }
            }
        }
        return flag;
    }

    public static void main(String[] args) throws IOException {
        InputStream inputStream = System.in;
        in = new InputReader(inputStream);
        OutputStream outputStream = System.out;
        out = new PrintWriter(outputStream);

        // Read value of N
        int N = in.nextInt();

        // Read value of x
        long[] x = new long[N];
        for (int i = 0; i < N; ++i) {
            x[i] = Long.parseLong(in.next());
        }

        long ans = maxOddEvenSubSum(x);
        out.println(ans);

        // Don't forget to close/flush the output
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
