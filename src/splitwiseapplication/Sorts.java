package splitwiseapplication;

import java.util.ArrayList;

/**
 * Sorts - Merge Sort Algorithm Implementation
 * 
 * Provides recursive merge sort for ArrayLists of Strings.
 * 
 * Algorithm: Merge Sort
 * - Time Complexity: O(n log n) - Efficient for all cases (best, average, worst)
 * - Space Complexity: O(n) - Requires temporary ArrayList for merging
 * - Stability: Stable sort (preserves relative order of equal elements)
 * - Divide and Conquer: Recursively splits, then merges sorted halves
 * 
 * Why Merge Sort?
 * - Consistently efficient: O(n log n) in all cases
 * - Better than Quick Sort's worst case O(n²)
 * - Predictable performance for production systems
 * - Stable: maintains order of equal elements
 * 
 * Usage in Application:
 * - Used by Exists.exist() to sort usernames before binary search
 * - Used by EnterGroup to alphabetically sort group names
 * 
 * @version 2.0 - Enhanced with comprehensive documentation
 */
public class Sorts {

    /**
     * Merges two sorted sub-arrays into one sorted array.
     * 
     * This is the "Conquer" step of merge sort. Takes two already-sorted
     * segments of the ArrayList and merges them into one sorted segment.
     * 
     * Sub-arrays:
     * - First half: items[start...mid]
     * - Second half: items[mid+1...end]
     * 
     * Algorithm:
     * 1. Create temporary ArrayList
     * 2. Compare elements from both halves, adding smaller to temp
     * 3. Add any remaining elements from either half
     * 4. Copy sorted elements from temp back to original ArrayList
     * 
     * Example:
     * items = ["alice", "charlie", "bob", "david"]
     * After merge(items, 0, 1, 3):
     * items = ["alice", "bob", "charlie", "david"]
     * 
     * @param items The ArrayList being sorted
     * @param start Starting index of first sub-array
     * @param mid Ending index of first sub-array (start of second is mid+1)
     * @param end Ending index of second sub-array
     */
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
                temp.add(items.get(pos1));  // First half element is smaller or equal
                pos1++;
            } else {
                temp.add(items.get(pos2));  // Second half element is smaller
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

    /**
     * Recursively sorts an ArrayList using the merge sort algorithm.
     * 
     * This is the main merge sort method. It uses the "Divide and Conquer" approach:
     * 
     * Divide: Recursively split the ArrayList into halves until single elements
     * Conquer: Merge the sorted halves back together
     * 
     * How It Works:
     * 1. Base case: if start >= end, list has 0 or 1 element (already sorted)
     * 2. Calculate middle point
     * 3. Recursively sort left half (start to mid)
     * 4. Recursively sort right half (mid+1 to end)
     * 5. Merge the two sorted halves
     * 
     * Recursion Tree Example for ["david", "alice", "charlie", "bob"]:
     * 
     *           [david, alice, charlie, bob]
     *               /                   \
     *      [david, alice]           [charlie, bob]
     *        /       \                /        \
     *    [david]  [alice]        [charlie]  [bob]
     *        \       /                \        /
     *    [alice, david]           [bob, charlie]
     *               \                   /
     *           [alice, bob, charlie, david]
     * 
     * Time Complexity: O(n log n)
     * - log n levels of recursion (divide in half each time)
     * - O(n) work at each level (merging all elements)
     * 
     * @param items The ArrayList to sort (modified in place)
     * @param start Starting index of the segment to sort
     * @param end Ending index of the segment to sort (inclusive)
     */
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
