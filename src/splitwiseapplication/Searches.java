package splitwiseapplication;

import java.util.ArrayList;

/**
 * Searches - Binary Search Algorithm Implementation
 * 
 * Provides recursive binary search for sorted ArrayLists of Strings.
 * 
 * Algorithm: Binary Search
 * - Time Complexity: O(log n) - Very efficient for large datasets
 * - Space Complexity: O(log n) - Due to recursive call stack
 * - Prerequisite: The ArrayList MUST be sorted before searching
 * 
 * How It Works:
 * 1. Check the middle element
 * 2. If match found, return the index
 * 3. If goal < middle, search left half
 * 4. If goal > middle, search right half
 * 5. Repeat until found or search space exhausted
 * 
 * Usage in Application:
 * - Used by Exists.exist() to check if username exists
 * - Always preceded by Sorts.mergesort() to ensure data is sorted
 * 
 * @version 2.0 - Enhanced with comprehensive documentation
 */
public class Searches{

 /**
 * Performs recursive binary search on a sorted ArrayList.
 * 
 * Searches for the goal string in the sorted items list between
 * start and end indices (inclusive).
 * 
 * Algorithm Steps:
 * 1. Base case: if start > end, goal not found, return -1
 * 2. Calculate middle index: mid = (start + end) / 2
 * 3. Compare goal with items[mid]:
 *    - If equal: found! Return mid
 *    - If goal < items[mid]: search left half (start to mid-1)
 *    - If goal > items[mid]: search right half (mid+1 to end)
 * 
 * String Comparison:
 * Uses compareTo() for lexicographic (alphabetical) comparison:
 * - compareTo() == 0: strings are equal
 * - compareTo() < 0: goal comes before items[mid] alphabetically
 * - compareTo() > 0: goal comes after items[mid] alphabetically
 * 
 * Example:
 * items = ["alice", "bob", "charlie", "david"]
 * binarySearch(items, 0, 3, "charlie") → returns 2
 * binarySearch(items, 0, 3, "eve") → returns -1
 * 
 * @param items Sorted ArrayList of strings to search in
 * @param start Starting index of search range (inclusive)
 * @param end Ending index of search range (inclusive)
 * @param goal The string value to search for
 * @return Index of goal if found, -1 if not found
 * 
 * @pre items is sorted from low to high (alphabetically)
 * @post Position of goal returned, or -1 if not found
 */
	public static int binarySearch(ArrayList<String> items, int start, int end, String goal) {

	 if (start > end) {
		 // Base case: search space exhausted, goal not found
		 return(-1);
	 } else {
		 int mid = (start + end) / 2;  // Calculate middle index
		 if (goal.compareTo(items.get(mid)) == 0) {
			 // Found! Goal equals middle element
			 return(mid);
		 } else if (goal.compareTo(items.get(mid)) < 0) {
			 // Goal comes before middle, search left half
			 return(binarySearch(items, start, mid-1, goal));
		 } else {
			 // Goal comes after middle, search right half
			 return(binarySearch(items, mid+1, end, goal));
	 	}
	 	}
	 }
	
}