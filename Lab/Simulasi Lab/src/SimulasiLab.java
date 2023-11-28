import java.io.*;
import java.util.StringTokenizer;

public class SimulasiLab {
    private static InputReader in;
    private static PrintWriter out;


    static int multiplyMod(int N, int[] x) {
        long result = 1;
        int Mod = 1000000007;
        for (int i = 0; i < N; i++) {
            result = (result * x[i]) % Mod;
        }
        return (int) result;
    }

    public static void main(String[] args) throws IOException {
        InputStream inputStream = System.in;
        in = new InputReader(inputStream);
        OutputStream outputStream = System.out;
        out = new PrintWriter(outputStream);

        int N = in.nextInt();

        if (N < 1 || N > 100000) {
            throw new IllegalArgumentException("Nilai N tidak sesuai batasan.");
        }

        int[] x = new int[N];
        for (int i = 0; i < N; ++i) {
            x[i] = in.nextInt();
        }

        int ans = multiplyMod(N, x);
        out.println(ans);

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