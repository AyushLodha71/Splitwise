package splitwiseapplication;

import java.util.ArrayList;

public class Sorts {

    public static void merge(ArrayList<String> items, int start, int mid, int end) {
        // Create a temporary ArrayList to hold the merged elements.
        // It's crucial to use add() to populate it, not set(), as it's initially empty.
        ArrayList<String> temp = new ArrayList<>();

        int pos1 = start;       // Pointer for the first sub-array (items[start...mid])
        int pos2 = mid + 1;     // Pointer for the second sub-array (items[mid+1...end])

        // Merge elements from both sub-arrays into the temporary list in sorted order
        while (pos1 <= mid && pos2 <= end) {
            // Compare elements from both halves
            // For ascending order, use <= 0 for compareTo
            if (items.get(pos1).compareTo(items.get(pos2)) <= 0) {
                temp.add(items.get(pos1));
                pos1++;
            } else {
                temp.add(items.get(pos2));
                pos2++;
            }
        }

        // Add any remaining elements from the first sub-array (if pos1 hasn't reached mid)
        while (pos1 <= mid) {
            temp.add(items.get(pos1));
            pos1++;
        }

        // Add any remaining elements from the second sub-array (if pos2 hasn't reached end)
        while (pos2 <= end) {
            temp.add(items.get(pos2));
            pos2++;
        }

        // Copy the sorted elements from the temporary list back to the original ArrayList
        // This loop iterates through the 'temp' list and places elements back into 'items'
        // starting from the original 'start' index.
        for (int i = 0; i < temp.size(); i++) {
            items.set(start + i, temp.get(i));
        }
    }

    public static void mergesort(ArrayList<String> items, int start, int end) {
        // Base case: if start is less than end, there's more than one element to sort
        if (start < end) {
            int mid = (start + end) / 2; // Calculate the middle point

            // Recursively sort the first half
            mergesort(items, start, mid);
            // Recursively sort the second half
            mergesort(items, mid + 1, end);

            // Merge the two sorted halves
            merge(items, start, mid, end);
            
        }
    }
    
}
