import java.io.*;
import java.util.StringTokenizer;

public class Template {
    private static InputReader in;
    private static PrintWriter out;

    static long maxOddEvenSubSum(int[] a) {
        boolean isEven = a.length % 2 == 0;
        long currentValue = 0;
        long maxFinal = 0;

        for (int i = 0; i < a.length; i++) {
            if ((isEven && a[i] % 2 == 0) || (!isEven && a[i] % 2 != 0)) {
                currentValue += a[i];
                if (currentValue > maxFinal) {
                    maxFinal = currentValue;
                }
                if (currentValue < 0) {
                    currentValue = 0;
                }
            } else {
                currentValue = 0;
            }
        }

        return maxFinal;
    }

    public static void main(String[] args) throws IOException {
        InputStream inputStream = System.in;
        in = new InputReader(inputStream);
        OutputStream outputStream = System.out;
        out = new PrintWriter(outputStream);

        // Read value of N
        int N = in.nextInt();

        // Read value of x
        int[] x = new int[N];
        for (int i = 0; i < N; ++i) {
            x[i] = in.nextInt();
        }

        long ans = maxOddEvenSubSum(x);
        out.println(ans);

        // don't forget to close/flush the output
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

    }
}