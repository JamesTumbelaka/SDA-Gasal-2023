import java.util.*;
import java.io.*;

public class Lab7 {

    static class Box {
        int id;
        long value;
        String state;

        Box(int id, long value, String state) {
            this.id = id;
            this.value = value;
            this.state = state;
        }
    }

    static class BoxContainer {
        public ArrayList<Box> heap;
        public int size;
        public HashMap<Integer, Integer> idToIndexMap;

        public BoxContainer() {
            this.heap = new ArrayList<>();
            this.idToIndexMap = new HashMap<>();
        }

        public static int getParentIndex(int i) {
            return (i - 1) / 2;
        }

        public void percolateUp(int i) {
            
            while (i > 0) {
                int parentIndex = getParentIndex(i);
                Box current = heap.get(i);
                Box parent = heap.get(parentIndex);
        
                if (parent.value < current.value || (parent.value == current.value && parent.id > current.id)) {
                    swap(parentIndex, i);
                    i = parentIndex;
                } else {
                    break;
                }
            }
        }

        public void percolateDown(int i) {
            
            int leftChildIndex = 2 * i + 1;
            int rightChildIndex = 2 * i + 2;
            int largest = i;
            while (leftChildIndex < size) {
                Box current = heap.get(i);
                Box leftChild = heap.get(leftChildIndex);
                Box rightChild = rightChildIndex < size ? heap.get(rightChildIndex) : null;

                if (leftChild.value > current.value || (leftChild.value == current.value && leftChild.id < current.id)) {
                    largest = leftChildIndex;
                }

                if (rightChild != null && (rightChild.value > heap.get(largest).value || (rightChild.value == heap.get(largest).value && rightChild.id < heap.get(largest).id))) {
                    largest = rightChildIndex;
                }

                if (largest != i) {
                    swap(i, largest);
                    i = largest;
                    leftChildIndex = 2 * i + 1;
                    rightChildIndex = 2 * i + 2;
                } else {
                    break;
                }
            }
        }

        public void insert(Box box) {
            
            heap.add(box);
            size++;
            idToIndexMap.put(box.id, size - 1);
            percolateUp(size - 1);
        }

        public Box peek() {
            return heap.get(0);
        }

        public void swap(int firstIndex, int secondIndex) {
            
            Box temp = heap.get(firstIndex);
            heap.set(firstIndex, heap.get(secondIndex));
            heap.set(secondIndex, temp);
            idToIndexMap.put(heap.get(firstIndex).id, firstIndex);
            idToIndexMap.put(heap.get(secondIndex).id, secondIndex);
        }

        public void updateBox(Box box) {
            
            int index = idToIndexMap.get(box.id);
            heap.set(index, box);
            percolateUp(index);
            percolateDown(index);
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter pw = new PrintWriter(System.out);
    
        int N = Integer.parseInt(br.readLine());
    
        ArrayList<Box> boxes = new ArrayList<>();
        BoxContainer boxContainer = new BoxContainer();
    
        for (int i = 0; i < N; i++) {
            StringTokenizer st = new StringTokenizer(br.readLine());
            long value = Long.parseLong(st.nextToken());
            String state = st.nextToken();
    
            Box box = new Box(boxes.size(), value, state);
            boxes.add(box);
            boxContainer.insert(box);
        }
    
        int T = Integer.parseInt(br.readLine());
    
        for (int i = 0; i < T; i++) {
            StringTokenizer st = new StringTokenizer(br.readLine());
            String command = st.nextToken();
    
            if ("A".equals(command)) {
                
                long value = Long.parseLong(st.nextToken());
                String state = st.nextToken();
                Box box = new Box(boxes.size(), value, state);
                boxes.add(box);
                boxContainer.insert(box);
            } else if ("D".equals(command)) {
                
                int idI = Integer.parseInt(st.nextToken());
                int idJ = Integer.parseInt(st.nextToken());
                Box boxI = boxes.get(idI);
                Box boxJ = boxes.get(idJ);

                if (!boxI.state.equals(boxJ.state)) {
                    boolean boxIMenang = (boxI.state.equals("R") && boxJ.state.equals("S")) ||
                                    (boxI.state.equals("S") && boxJ.state.equals("P")) ||
                                    (boxI.state.equals("P") && boxJ.state.equals("R"));

                    if (boxIMenang) {
                        boxI.value += boxJ.value;
                        boxJ.value /= 2;
                    } else {
                        boxJ.value += boxI.value;
                        boxI.value /= 2;
                    }
                    boxContainer.updateBox(boxI);
                    boxContainer.updateBox(boxJ);
                }
            } else if ("N".equals(command)) {
                
                int idI = Integer.parseInt(st.nextToken());
                Box boxI = boxes.get(idI);

                if (idI > 0) {
                    Box boxImin1 = boxes.get(idI - 1);
                    if (!boxI.state.equals(boxImin1.state)) {
                        boolean boxIMenang = (boxI.state.equals("R") && boxImin1.state.equals("S")) ||
                                        (boxI.state.equals("S") && boxImin1.state.equals("P")) ||
                                        (boxI.state.equals("P") && boxImin1.state.equals("R"));

                        if (boxIMenang) {
                            boxI.value += boxImin1.value;
                        }
                        boxContainer.updateBox(boxI);
                    }
                }

                if (idI < boxes.size() - 1) {
                    Box boxIplus1 = boxes.get(idI + 1);
                    if (!boxI.state.equals(boxIplus1.state)) {
                        boolean boxIMenang = (boxI.state.equals("R") && boxIplus1.state.equals("S")) ||
                                        (boxI.state.equals("S") && boxIplus1.state.equals("P")) ||
                                        (boxI.state.equals("P") && boxIplus1.state.equals("R"));

                        if (boxIMenang) {
                            boxI.value += boxIplus1.value;
                        }
                        boxContainer.updateBox(boxI);
                    }
                }
            }

            Box topBox = boxContainer.peek();
            pw.println(topBox.value + " " + topBox.state);
        }
    
        pw.flush();
        pw.close();
    }    
}