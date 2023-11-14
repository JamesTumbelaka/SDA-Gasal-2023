import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.*;

public class TP02 {
    private static InputReader in;
    static PrintWriter out;

    public static void main(String[] args) {
        InputStream inputStream = System.in;
        in = new InputReader(inputStream);
        OutputStream outputStream = System.out;
        out = new PrintWriter(outputStream);

        DoublyLinkedList sekolah = new DoublyLinkedList();
        int studentIdCounter = 1;

        int M = in.nextInt();
        int[] siswaDiKelas = new int[M];

        for (int i = 0; i < M; i++) {
            siswaDiKelas[i] = in.nextInt();
        }
        for (int i = 0; i < M; i++) {
            AVLTree kelas = new AVLTree();
            for (int j = 0; j < siswaDiKelas[i]; j++) {
                int Poin = in.nextInt();
                Student siswa = new Student(studentIdCounter++, Poin);
                kelas.root = kelas.insert(kelas.root, siswa);
            }
            sekolah.add(kelas);
        }

        sekolah.current = sekolah.first;
        for (int k = 0; k < M; k++) {
            out.print("Class " + (k + 1) + " Points: ");
            sekolah.current.kelas.inOrderTraversal(sekolah.current.kelas.root, out);
            sekolah.current = sekolah.current.next;
            out.println();
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

class ListNode {

    AVLTree kelas;
    ListNode next;
    ListNode prev;
    int idKelas;

    ListNode(AVLTree kelas, int idKelas) {
        this.kelas = kelas;
        this.idKelas = idKelas;
    }
}

class DoublyLinkedList {

    private int nodeIdCounter = 1;
    ListNode first;
    ListNode current;
    ListNode last;
    int size = 0;

    public void add(AVLTree kelas) {
        ListNode newNode = new ListNode(kelas, nodeIdCounter++);

        if (first == null) {
            first = newNode;
            last = newNode;
            newNode.next = newNode;
            newNode.prev = newNode;
            current = newNode;
        } else {
            newNode.prev = current;
            newNode.next = current.next;
            current.next.prev = newNode;
            current.next = newNode;
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
        return deletedNode.idKelas;
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
        return current.idKelas;
    }
}

class Student {
    int idSiswa;
    int poin;

    Student(int idSiswa, int poin) {
        this.idSiswa = idSiswa;
        this.poin = poin;
    }
}

class AVLNode {
    Student siswa;
    int height;
    AVLNode left, right;

    AVLNode(Student siswa) {
        this.siswa = siswa;
        height = 1;
    }
}

class AVLTree {
    AVLNode root;

    int getHeight(AVLNode node) {
        if (node == null) {
            return 0;
        }
        return node.height;
    }

    int getBalance(AVLNode node) {
        if (node == null) {
            return 0;
        }
        return getHeight(node.left) - getHeight(node.right);
    }

    AVLNode insert(AVLNode node, Student siswa) {
        if (node == null) {
            return new AVLNode(siswa);
        }

        if (siswa.idSiswa < node.siswa.idSiswa) {
            node.left = insert(node.left, siswa);
        } else if (siswa.idSiswa > node.siswa.idSiswa) {
            node.right = insert(node.right, siswa);
        } else {
            return node;
        }

        node.height = 1 + Math.max(getHeight(node.left), getHeight(node.right));

        int balance = getBalance(node);

        if (balance > 1 && siswa.idSiswa < node.left.siswa.idSiswa) {
            return singleRightRotate(node);
        }

        if (balance < -1 && siswa.idSiswa > node.right.siswa.idSiswa) {
            return singleLeftRotate(node);
        }

        if (balance > 1 && siswa.idSiswa > node.left.siswa.idSiswa) {
            node.left = singleLeftRotate(node.left);
            return singleRightRotate(node);
        }

        if (balance < -1 && siswa.idSiswa < node.right.siswa.idSiswa) {
            node.right = singleRightRotate(node.right);
            return singleLeftRotate(node);
        }

        return node;
    }

    AVLNode minValueNode(AVLNode node) {
        AVLNode current = node;
        while (current.left != null) {
            current = current.left;
        }
        return current;
    }

    AVLNode delete(AVLNode root, int idSiswa) {
        if (root == null) {
            return root;
        }

        if (idSiswa < root.siswa.idSiswa) {
            root.left = delete(root.left, idSiswa);
        } else if (idSiswa > root.siswa.idSiswa) {
            root.right = delete(root.right, idSiswa);
        } else {
            if (root.left == null) root = root.right;
            else if (root.right == null) root = root.left;
            else {
                root.siswa.idSiswa = findMaxNode(root.left).siswa.idSiswa;
                root.left = delete(root.left, root.siswa.idSiswa);
            }
        }

        if (root == null) return root;

        root.height = 1 + Math.max(getHeight(root.left), getHeight(root.right));
        int balance = getBalance(root);

        if (balance > 1 && getBalance(root.left) >= 0) return singleRightRotate(root);
        if (balance > 1 && getBalance(root.left) < 0) {
            root.left = singleLeftRotate(root.left);
            return singleRightRotate(root);
        }
        if (balance < -1 && getBalance(root.right) <= 0) return singleLeftRotate(root);
        if (balance < -1 && getBalance(root.right) > 0) {
            root.right = singleRightRotate(root.right);
            return singleLeftRotate(root);
        }

        return root;
    }

    AVLNode singleLeftRotate(AVLNode y) {
        AVLNode x = y.right;
        AVLNode T2 = x.left;

        x.left = y;
        y.right = T2;

        y.height = 1 + Math.max(getHeight(y.left), getHeight(y.right));
        x.height = 1 + Math.max(getHeight(x.left), getHeight(x.right));

        return x;
    }

    AVLNode singleRightRotate(AVLNode x) {
        AVLNode y = x.left;
        AVLNode T2 = y.right;

        y.right = x;
        x.left = T2;

        x.height = 1 + Math.max(getHeight(x.left), getHeight(x.right));
        y.height = 1 + Math.max(getHeight(y.left), getHeight(y.right));

        return y;
    }

    AVLNode search(AVLNode root, int idSiswa) {
        while (root != null) {
            if (idSiswa < root.siswa.idSiswa) {
                root = root.left;
            } else if (idSiswa > root.siswa.idSiswa) {
                root = root.right;
            } else {
                return root;
            }
        }
        return null;
    }

    int findLargestNode(AVLNode node) {
        AVLNode current = node;
        while (current.right != null) {
            current = current.right;
        }
        return current.siswa.idSiswa;
    }
    AVLNode findMaxNode(AVLNode root) {
        AVLNode current = root;
        while (current.right != null) {
            current = current.right;
        }
        return current;
    }

    void inOrderTraversal(AVLNode node, PrintWriter out) {
        if (node != null) {
            inOrderTraversal(node.left, out);
            out.print(node.siswa.poin + " ");
            inOrderTraversal(node.right, out);
        }
    }
}