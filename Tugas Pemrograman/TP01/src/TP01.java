import java.util.*;
import java.io.*;

public class TP01 {
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

        public double nextDouble() {
            return Double.parseDouble(next());
        }
    }

    // Membuat Class Wahana untuk menyimpan data - data tiap Wahana
    static class Wahana {
        int id;
        int harga;
        int poin;
        int kapasitas;
        int prioritasFT;
        PriorityQueue<Pengunjung> antrian = new PriorityQueue<>();

        public Wahana(int id, int harga, int poin, int kapasitas, int prioritasFT) {
            this.id = id;
            this.harga = harga;
            this.poin = poin;
            this.kapasitas = kapasitas;
            this.prioritasFT = prioritasFT;
        }
    }

    // Membuat Class Pengunjung untuk menyimpan data - data tiap pengunjung
    static class Pengunjung implements Comparable<Pengunjung> {
        int id;
        String tipe;
        int uang;
        int poin;
        int bermain;

        @Override
        public int compareTo(Pengunjung o) {
            if (this.tipe.equals("FT") && o.tipe.equals("R")) {
                return -1;
            } else if (this.tipe.equals("R") && o.tipe.equals("FT")) {
                return 1;
            } else {
                if (this.bermain != o.bermain) {
                    return Integer.compare(this.bermain, o.bermain);
                } else {
                    return Integer.compare(this.id, o.id);
                }
            }
        }
    }

    public static void main(String[] args) {
        InputStream inputStream = System.in;
        InputReader in = new InputReader(inputStream);
        OutputStream outputStream = System.out;
        PrintWriter out = new PrintWriter(outputStream);

        int M = in.nextInt();
        // Menyimpan tiap wahana dalam array
        Wahana[] wahanas = new Wahana[M + 1];
        for (int i = 1; i <= M; i++) {
            int harga = in.nextInt();
            int poin = in.nextInt();
            int kapasitas = in.nextInt();
            int ft = (int) Math.ceil(in.nextDouble() / 100.0 * kapasitas);
            wahanas[i] = new Wahana(i, harga, poin, kapasitas, ft);
        }

        int N = in.nextInt();
        // Menyimpan tiap pengunjung dalam array
        Pengunjung[] pengunjungs = new Pengunjung[N + 1];
        List<Pengunjung> keluar = new ArrayList<>();

        for (int i = 1; i <= N; i++) {
            Pengunjung p = new Pengunjung();
            p.id = i;
            p.tipe = in.next();
            p.uang = in.nextInt();
            p.poin = 0;
            p.bermain = 0;
            pengunjungs[i] = p;
        }

        int T = in.nextInt();

        while (T-- > 0) {
            char query = in.next().charAt(0);
            if (query == 'A') {
                int pid = in.nextInt();
                int wid = in.nextInt();
                Pengunjung p = pengunjungs[pid];
                Wahana w = wahanas[wid];
                if (p.uang < w.harga) {
                    out.println("-1");
                    continue;
                }
                w.antrian.offer(p);
                out.println(w.antrian.size());
            } else if (query == 'E') {
                int wid = in.nextInt();
                Wahana w = wahanas[wid];
                List<Pengunjung> bermain = new ArrayList<>();
                int countFT = 0;
                while (!w.antrian.isEmpty() && bermain.size() < w.kapasitas) {
                    Pengunjung p = w.antrian.poll();
                    if (p.uang < w.harga) {
                        continue;
                    }
                    if (p.tipe.equals("FT") && countFT < w.prioritasFT) {
                        bermain.add(p);
                        countFT++;
                    } else if (p.tipe.equals("R")) {
                        bermain.add(p);
                    } else {
                        w.antrian.offer(p);
                        break;
                    }
                }
                for (Pengunjung p : bermain) {
                    p.uang -= w.harga;
                    p.poin += w.poin;
                    p.bermain++;
                    if (p.uang == 0) {
                        keluar.add(p);
                    }
                }
                if (bermain.isEmpty()) {
                    out.println("-1");
                } else {
                    for (Pengunjung p : bermain) {
                        out.print(p.id + " ");
                    }
                    out.println();
                }
            } else if (query == 'S') {
                int pid = in.nextInt();
                int wid = in.nextInt();
                Wahana w = wahanas[wid];
                boolean found = false;
                int idx = 1;
                for (Pengunjung p : w.antrian) {
                    if (p.id == pid) {
                        found = true;
                        break;
                    }
                    idx++;
                }
                out.println(found ? idx : "-1");
            } else if (query == 'F') {
                int P = in.nextInt();
                if (keluar.isEmpty()) {
                    out.println("-1");
                } else if (P == 0) {
                    out.println(keluar.get(0).poin);
                    keluar.remove(0);
                } else {
                    out.println(keluar.get(keluar.size() - 1).poin);
                    keluar.remove(keluar.size() - 1);
                }
            } else if (query == 'O') {
                int pid = in.nextInt();
                Pengunjung p = pengunjungs[pid];
                int[][] dp = new int[M + 1][p.uang + 1];
                for (int i = 0; i <= M; i++) {
                    Arrays.fill(dp[i], -1);
                }
                dp[0][p.uang] = 0;
                for (int i = 1; i <= M; i++) {
                    for (int j = 0; j <= p.uang; j++) {
                        dp[i][j] = dp[i - 1][j];
                        if (j >= wahanas[i].harga && dp[i - 1][j - wahanas[i].harga] != -1) {
                            dp[i][j] = Math.max(dp[i][j], dp[i - 1][j - wahanas[i].harga] + wahanas[i].poin);
                        }
                    }
                }
                int maxPoin = 0;
                for (int i = 0; i <= p.uang; i++) {
                    maxPoin = Math.max(maxPoin, dp[M][i]);
                }
                out.print(maxPoin + " ");
                int sisaUang = p.uang;
                for (int i = M; i > 0; i--) {
                    if (sisaUang >= wahanas[i].harga && dp[i][sisaUang] == dp[i - 1][sisaUang - wahanas[i].harga] + wahanas[i].poin) {
                        out.print(i + " ");
                        sisaUang -= wahanas[i].harga;
                    }
                }
                out.println();
            }
        }

        out.close();
    }
}
