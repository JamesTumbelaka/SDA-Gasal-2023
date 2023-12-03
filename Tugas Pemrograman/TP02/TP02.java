import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.*;

public class TP02 {

    private static InputReader in;
    private static PrintWriter out;

    static int idKelas = 1;
    static int idSiswa = 1;

    static DoublyLinkedList sekolah = new DoublyLinkedList();

    public static void main(String[] args) {

        InputStream inputStream = System.in;
        in = new InputReader(inputStream);
        OutputStream outputStream = System.out;
        out = new PrintWriter(outputStream);

        int jumlahSiswa = 0;
        int jumlahKelas = in.nextInt();

        int count = 0;
        while (count < jumlahKelas) {
            int kapasitasKelas = in.nextInt();
            jumlahSiswa += kapasitasKelas;
            sekolah.add(kapasitasKelas, idKelas, new AVLTree());
            idKelas++;
            count++;
        }

        ListNode node = sekolah.current;
        for (int j = 0; j < jumlahSiswa; j++) {
            int nilaiSiswa = in.nextInt();
            if (node.cekKapasitas != 0) {
                sekolah.insertSiswa(node, nilaiSiswa, idSiswa, 0);
                node.cekKapasitas--;
                idSiswa++;
            }
           else {
                node = node.next;
                if(node == sekolah.first) {
                    break;
                }
                else {
                    sekolah.insertSiswa(node, nilaiSiswa, idSiswa, 0);
                    node.cekKapasitas--;
                    idSiswa++;
                }
            }
        }

        sekolah.rerataNilaiSeluruhKelas();

        int jumlahPerintah = in.nextInt();
        for (int k = 0; k < jumlahPerintah; k++) {
            String perintah = in.next();

            if (perintah.equals("T")) {

                int poin = in.nextInt();
                int studentId = in.nextInt();
                ListNode nodeNow = sekolah.current;
                
                int kelascore = nodeNow.mapSiswa.getOrDefault(studentId, -1);

                if (kelascore == -1) {
                    out.println(-1);
                } else {
                    AVLNode student = sekolah.current.kelas.search(sekolah.current.kelas.root, kelascore, studentId);
                
                    int extraScore = (int) nodeNow.mapSiswa.entrySet().stream()
                                    .filter(entry -> studentId != entry.getKey() && nodeNow.mapSiswa.get(studentId) >= entry.getValue())
                                    .count();
                
                    poin += Math.min(extraScore, poin);
                
                    int newScore = student.key + poin;
                    AVLNode temp = new AVLNode(newScore, student.id, student.curang);
                
                    sekolah.deleteSiswa(nodeNow, student.key, student.id);
                    sekolah.insertSiswa(nodeNow, temp.key, temp.id, temp.curang);
                    sekolah.rerataNilaiKelas(nodeNow);
                
                    out.println(temp.key);
                }
            } 

            else if (perintah.equals("C")) {

                int siswaId = in.nextInt();
                ListNode currentKelas = sekolah.current;
                int nilaiSiswa = currentKelas.mapSiswa.getOrDefault(siswaId, -1);
                
                if (nilaiSiswa == -1) {
                    out.println(-1);
                } else {
                    AVLNode student = currentKelas.kelas.search(currentKelas.kelas.root, nilaiSiswa, siswaId);
                    if (student != null) {
                        student.addCurang();
                        handleSiswaCurang(sekolah, currentKelas, student);
                    }
                }
            } 

            else if (perintah.equals("G")) {
                String direction = in.next();
                ListNode nextKelas = sekolah.move(direction);
                if (nextKelas != null) {
                    out.println(nextKelas.id);
                } else {
                    out.println(-1);
                }
            } 

            else if (perintah.equals("S")) {
                if (sekolah.size == 1) {
                    out.println("-1 -1");
                    return;
                }
            
                ListNode currentClassroom = sekolah.current;
                if (currentClassroom == sekolah.first || currentClassroom == sekolah.last || sekolah.size == 2) {
                    ListNode otherClassroom = (currentClassroom == sekolah.last) ? currentClassroom.prev : currentClassroom.next;
                    tukarSiswaTerbaikDanTerburuk(currentClassroom, otherClassroom);
                } else if (sekolah.size > 2) {
                    ListNode betterClassroom = currentClassroom.prev;
                    ListNode worseClassroom = currentClassroom.next;
                    tukarSiswaTerbaikDanTerburuk(currentClassroom, betterClassroom);
                    tukarSiswaTerbaikDanTerburuk(currentClassroom, worseClassroom);
                }
            
                sekolah.rerataNilaiKelas(currentClassroom);
                int bestStudentId = cariSiswaTerbaik(currentClassroom).id;
                int worstStudentId = cariSiswaTerburuk(currentClassroom).id;
                out.println(bestStudentId + " " + worstStudentId);
            } 

            else if (perintah.equals("K")) {
                out.println("-1");
            } 

            else if (perintah.equals("A")) {
                int kapasitas = in.nextInt();
                ListNode added = sekolah.add(kapasitas, idKelas, new AVLTree());
                idKelas++;
                for (int i = 0; i < kapasitas; i++) {
                    sekolah.insertSiswa(added, 0, idSiswa, 0);
                    sekolah.rerataNilaiKelas(added);
                    idSiswa++;
                }
                out.println(added.id);
            }
        }
        
        out.close();
    }

    private static void handleSiswaCurang (DoublyLinkedList sekolah, ListNode currentKelas, AVLNode student) {
        switch (student.curang) {
            case 1:
                updateKelasSetelahCurang(sekolah, currentKelas, student);
                AVLNode temp1 = new AVLNode(0, student.id, student.curang);
                out.println(temp1.key);
                break;
            case 2:
                AVLNode temp2 = new AVLNode(0, student.id, student.curang);
                pindahKelasTerakhir(sekolah, currentKelas, temp2);
                out.println(sekolah.last.id);
                break;
            case 3:
                handleSiswaKeluar(sekolah, currentKelas, student);
                out.println(student.id);
                break;
        }
    }
    
    public static void updateKelasSetelahCurang (DoublyLinkedList sekolah, ListNode currentKelas, AVLNode student) {
        AVLNode temp = new AVLNode(0, student.id, student.curang);
        sekolah.deleteSiswa(currentKelas, student.key, student.id);
        sekolah.insertSiswa(currentKelas, temp.key, temp.id, temp.curang);
        sekolah.rerataNilaiKelas(currentKelas);
    }
    
    public static void pindahKelasTerakhir (DoublyLinkedList sekolah, ListNode currentKelas, AVLNode student) {
        AVLNode temp = new AVLNode(0, student.id, student.curang);
        sekolah.deleteSiswa(currentKelas, student.key, student.id);
        if (currentKelas != sekolah.last) {
            pindahKelasBerikutnya(sekolah, currentKelas);
        }
        sekolah.insertSiswa(sekolah.last, temp.key, temp.id, temp.curang);
        sekolah.rerataNilaiKelas(sekolah.last);
    }
    
    public static void pindahKelasBerikutnya (DoublyLinkedList sekolah, ListNode currentKelas) {
        ListNode nextClass = currentKelas.next;
        for (Map.Entry<Integer, Integer> entry : currentKelas.mapSiswa.entrySet()) {
            int id = entry.getKey();
            int nilai = entry.getValue();
            AVLNode student = currentKelas.kelas.search(currentKelas.kelas.root, nilai, id);
            int cheatCount = student.curang;
            sekolah.insertSiswa(nextClass, nilai, id, cheatCount);
        }
        sekolah.move("R");
        sekolah.delete("L");
    }

    public static void handleSiswaKeluar (DoublyLinkedList sekolah, ListNode currentKelas, AVLNode student) {
        sekolah.deleteSiswa(currentKelas, student.key, student.id);
        if (currentKelas != sekolah.last) {
            if (currentKelas.kapasitas < 6) {
                memindahkanSiswa(sekolah, currentKelas, "next");
            }
        } else {
            if (currentKelas.kapasitas < 6) {
                memindahkanSiswa(sekolah, currentKelas, "previous");
            }
        }
        sekolah.rerataNilaiKelas(currentKelas);
    }
    
    public static void memindahkanSiswa (DoublyLinkedList sekolah, ListNode currentKelas, String direction) {
        ListNode targetClass = (direction.equals("next")) ? currentKelas.next : currentKelas.prev;
        for (Map.Entry<Integer, Integer> entry : currentKelas.mapSiswa.entrySet()) {
            int id = entry.getKey();
            int nilai = entry.getValue();
            AVLNode student = currentKelas.kelas.search(currentKelas.kelas.root, nilai, id);
            int cheatCount = student.curang;
            sekolah.insertSiswa(targetClass, nilai, id, cheatCount);
        }
        sekolah.move(direction.equals("next") ? "R" : "L");
        sekolah.delete(direction.equals("next") ? "L" : "R");
    }
    
    private static void tukarSiswaTerbaikDanTerburuk(ListNode kelasPertama, ListNode kelasKedua) {
        for (int i = 0; i < 3; i++) {
            AVLNode siswaTerbaikKelasPertama = kelasPertama.kelas.maxNode(kelasPertama.kelas.root);
            AVLNode sisaTerburukKelasTerakhir = kelasKedua.kelas.minNode(kelasKedua.kelas.root);
    
            if (siswaTerbaikKelasPertama != null && sisaTerburukKelasTerakhir != null) {
                sekolah.deleteSiswa(kelasPertama, siswaTerbaikKelasPertama.key, siswaTerbaikKelasPertama.id);
                sekolah.deleteSiswa(kelasKedua, sisaTerburukKelasTerakhir.key, sisaTerburukKelasTerakhir.id);
    
                sekolah.insertSiswa(kelasKedua, siswaTerbaikKelasPertama.key, siswaTerbaikKelasPertama.id, siswaTerbaikKelasPertama.curang);
                sekolah.insertSiswa(kelasPertama, sisaTerburukKelasTerakhir.key, sisaTerburukKelasTerakhir.id, sisaTerburukKelasTerakhir.curang);
            }
        }
        sekolah.rerataNilaiKelas(kelasPertama);
        sekolah.rerataNilaiKelas(kelasKedua);
    }
    
    private static AVLNode cariSiswaTerbaik(ListNode classroom) {
        return classroom.kelas.maxNode(classroom.kelas.root);
    }
    
    private static AVLNode cariSiswaTerburuk(ListNode classroom) {
        return classroom.kelas.minNode(classroom.kelas.root);
    }
    
    public static class InputReader {
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

    public ListNode next;
    public ListNode prev;
    
    public int id;

    public AVLTree kelas;

    public int kapasitas;
    public int cekKapasitas;
    public int totalNilai;
    public double rerataNilai;

    public HashMap<Integer, Integer> mapSiswa;

    ListNode(int kapasitas, int id, AVLTree kelas) {
        this.id = id;
        this.kelas = kelas;
        this.kapasitas = 0;
        this.cekKapasitas = kapasitas;
        this.totalNilai = 0;
        this.rerataNilai = 0;
        this.mapSiswa = new HashMap<>();
    }
}

class DoublyLinkedList {

    public ListNode first;
    public ListNode current;
    public ListNode last;
    public int size = 0;

    public ListNode add(int kapasitas, int id, AVLTree kelas) {

        ListNode newNode = new ListNode(kapasitas, id, kelas);

        if (size == 0) {
            first = newNode;
            last = newNode;
            current = newNode;
            newNode.next = newNode;
            newNode.prev = newNode;
        } 
        else {
            last.next = newNode;
           
            newNode.prev = last;
            newNode.next = first;

            last = newNode;
            first.prev = last;
        }
        size++;
        return newNode;
    }

    public ListNode delete(String direction) {
        if (size == 0 || current == null || size < 2) {
            return null;
        }
    
        ListNode newNode;
        if (direction.equals("R")) {
            newNode = current.next;
            if (newNode != current) {
                current.next = newNode.next;
                newNode.next.prev = current;
                if (newNode == last) {
                    last = current;
                }
            } 
            
            else {
                current = null;
                first = null;
                last = null;
            }
        } 
        else {
            newNode = current.prev;

            if (newNode != current) {
                current.prev = newNode.prev;
                newNode.prev.next = current;
                if (newNode == first) {
                    first = current;
                }
            } 
            
            else {
                current = null;
                first = null;
                last = null;
            }
        }
        size--;
        return newNode;
    }

    public ListNode move(String direction) {
        if (size == 0 || current == null) {
            return null;
        }
        else{
            if (direction.equals("R")) {
                current = current.next;
            } 
            else if (direction.equals("L")) {
                current = current.prev;
            }
        }
        return current;
    }

    public void insertSiswa(ListNode node, int poin, int siswaID, int curang) {
        node.totalNilai += poin;
        node.mapSiswa.put(siswaID, poin);
        node.kapasitas++;
        node.kelas.root = node.kelas.insert(node.kelas.root, poin, siswaID, curang);
    }

     public void deleteSiswa(ListNode node, int poin, int siswaID) {
        node.totalNilai -= poin;
        node.mapSiswa.remove(siswaID);
        node.kapasitas--;
        node.kelas.root = node.kelas.delete(node.kelas.root, poin, siswaID);
    }

    private void deleteNode(ListNode node) {
        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            first = node.next;
        }

        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            last = node.prev;
        }

        if (node == current) {
            current = node.next;
        }

        node.prev = null;
        node.next = null;

        size--;
    }

    public void deleteAll() {

        if (size == 0) {
            System.out.println("-1");
            return;
        }

        ListNode nodeToDelete = first;

        while (size > 0) {
            ListNode nextNode = nodeToDelete.next;
            deleteNode(nodeToDelete);
            nodeToDelete = nextNode;
        }

        first = null;
        last = null;
        current = null;
        size = 0;
    }

    public void rerataNilaiSeluruhKelas() {
        ListNode newNode = first;
        if (newNode == null) {
            System.out.println("-1");
            return;
        }
        do {
            newNode.rerataNilai = (double) newNode.totalNilai / (double) newNode.kapasitas;
            newNode = newNode.next;
        } while (newNode != first);
    }

    public void rerataNilaiKelas(ListNode node) {
        node.rerataNilai = (double) node.totalNilai / (double) node.kapasitas;
    }

}

class AVLNode {

    public int key;
    public int height, id;
    public AVLNode left, right;
    public int curang;

    AVLNode (int key, int id, int curang) {

        this.key = key;
        this.id = id;
        this.curang = 0;
        this.height = 1;
        this.curang = curang;
    }

    public void addCurang() {

        this.curang++;
    }
}

class AVLTree {

    public AVLNode root;

    public int height (AVLNode node) {

        if (node == null) {
            return 0;
        }
        return node.height;
    }

    public int getBalance (AVLNode node) {

        if (node == null) {
            return 0;
        }
        return height(node.left) - height(node.right);
    }

    public AVLNode singleLeftRotate (AVLNode a) {

        AVLNode b = a.right;
        AVLNode T2 = b.left;
  
        b.left = a;
        a.right = T2;
  
        a.height = Math.max(height(a.left), height(a.right)) + 1;
        b.height = Math.max(height(b.left), height(b.right)) + 1;
   
        return b;
    }

    public AVLNode singleRightRotate (AVLNode b) {

        AVLNode a = b.left; 
        AVLNode T2 = a.right; 
  
        a.right = b; 
        b.left = T2; 
  
        b.height = Math.max(height(b.left), height(b.right)) + 1; 
        a.height = Math.max(height(a.left), height(a.right)) + 1; 
  
        return a; 
    }

    public AVLNode insert (AVLNode node, int key, int id, int curang) {

        if (node == null) {
            return (new AVLNode(key, id, curang));
        }
        if (key < node.key) {
            node.left = insert(node.left, key, id, curang); 
        }
        else if (key > node.key) {
            node.right = insert(node.right, key, id, curang); 
        }
        else {
            if (id < node.id) {
                node.right = insert(node.right, key, id, curang);
            }
            else {
                node.left = insert(node.left, key, id, curang);
            }
            return node;
        }
  
        node.height = Math.max(height(node.left), height(node.right)) + 1; 
  
        int balance = getBalance(node); 
  
        // RR
        if (balance < -1 && getBalance(node.right) <= 0) {
            return singleLeftRotate(node); 
        }
 
        // RL
        if (balance < -1 && getBalance(node.right) > 0) { 
            node.right = singleRightRotate(node.right); 
            return singleLeftRotate(node); 
        } 

        // LL
        if (balance > 1 && getBalance(node.left) >= 0) {
            return singleRightRotate(node); 
        }
 
        // LR
        if (balance > 1 && getBalance(node.left) < 0) { 
            node.left = singleLeftRotate(node.left); 
            return singleRightRotate(node); 
        }
  
        return node;
       
    }

    public AVLNode delete (AVLNode root, int key, int id) {

        if (root == null) {
            return root; 
        }
 
        if (key < root.key) {
            root.left = delete(root.left, key, id); 
        }
 
        else if (key > root.key) {
            root.right = delete(root.right, key, id); 
        }
 
        else{ 
            if(id > root.id) {
                root.left = delete(root.left, key, id);
            }
            else if(id < root.id) {
                root.right = delete(root.right, key, id);
            }
            else if(id == root.id) {
                if ((root.left == null) || (root.right == null)) { 
                    AVLNode temp = null; 
                    if (temp == root.left){ 
                        temp = root.right; 
                    } else {
                        temp = root.left; 
                    }
                    if (temp == null) { 
                        temp = root; 
                        root = null; 
                    } else { 
                        root = temp; 
                    }
                } else { 
                    AVLNode temp = minNode(root.right); 

                    root.key = temp.key; 
                    root.id = temp.id;
                    root.curang = temp.curang;

                    root.right = delete(root.right, temp.key, temp.id);
                } 
            }
        } 
 
        if (root == null) {
            return root; 
        }
 
        root.height = Math.max(height(root.left), height(root.right)) + 1; 
 
        int balance = getBalance(root); 
 
        if (balance > 1 && getBalance(root.left) >= 0) {
            return singleRightRotate(root); 
        }
 
        if (balance < -1 && getBalance(root.right) > 0) { 
            root.right = singleRightRotate(root.right); 
            return singleLeftRotate(root); 
        }
 
        if (balance < -1 && getBalance(root.right) <= 0) {
            return singleLeftRotate(root); 
        }
 
        if (balance > 1 && getBalance(root.left) < 0) { 
            root.left = singleLeftRotate(root.left); 
            return singleRightRotate(root); 
        } 
        return root; 
    }

    public AVLNode maxNode (AVLNode node) {
        AVLNode current = node;
        while (current.right != null){
            current = current.right;
        }
        return current;
    }
    
    public AVLNode minNode (AVLNode node) {
        AVLNode current = node;
        while (current.left != null) {
            current = current.left;
        }
        return current;
    }

    public AVLNode search (AVLNode root, int key, int id) {
        while (root != null) {
            if (key < root.key) {
                root = root.left;
            } 
            else if (key > root.key) {
                root = root.right;
            } 
            else {
                if (id == root.id) {
                    return root;
                }
                else if (id < root.id) {
                    root = root.right;
                }
                else if (id > root.id) {
                    root = root.left;
                    
                }
            }
        }
        return null;
    }
}