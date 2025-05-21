package splitwiseapplication;

import java.util.ArrayList;

public class Searches{

 /**
 * Searches items array for goal
 * pre: items is sorted from low to high
 * post: Position of goal has been returned,
 * or -1 has been returned if goal not found.
 */
	public static int binarySearch(ArrayList<String> items, int start, int end, String goal) {

	 if (start > end) {
		 return(-1);
	 } else {
		 int mid = (start + end) / 2;
		 if (goal.compareTo(items.get(mid)) == 0) {
			 return(mid);
		 } else if (goal.compareTo(items.get(mid)) < 0) {
			 return(binarySearch(items, start, mid-1, goal));
		 } else {
			 return(binarySearch(items, mid+1, end, goal));
	 	}
	 	}
	 }
	
}