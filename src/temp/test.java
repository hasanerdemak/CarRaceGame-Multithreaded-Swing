package temp;

import java.util.ArrayList;

public class test {
    public static void main(String[] args) {
        ArrayList<Coordinates> coordinates = new ArrayList<>(10);
    }

    private static class Coordinates{
        public int x;
        public int y;

    }
}


class LimitedSizeArray {
    private final int maxSize;
    private int[] array;
    private int currentIndex;

    public LimitedSizeArray(int maxSize) {
        this.maxSize = maxSize;
        this.array = new int[maxSize];
        this.currentIndex = 0;
    }

    public void addElement(int element) {
        array[currentIndex] = element;
        currentIndex = (currentIndex + 1) % maxSize;
    }

    public int[] getArray() {
        int[] result = new int[maxSize];
        int startIndex = (currentIndex == maxSize) ? 0 : currentIndex;
        int j = 0;

        for (int i = startIndex; i < maxSize; i++) {
            result[j] = array[i];
            j++;
        }

        if (startIndex > 0) {
            for (int i = 0; i < startIndex; i++) {
                result[j] = array[i];
                j++;
            }
        }

        return result;
    }
}
