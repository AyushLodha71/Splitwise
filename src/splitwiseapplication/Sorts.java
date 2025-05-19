package splitwiseapplication;

import java.util.ArrayList;

public class Sorts {
	
	private static void merge(ArrayList<String> items, int start,
		int mid, int end) {
		ArrayList<String> temp = new ArrayList<String>();
		int pos1 = start;
		int pos2 = mid + 1;
		int spot = start;
		
		while (!(pos1 > mid && pos2 > end)) {
			if ((pos1 > mid) || ((pos2 <= end) && (items.get(pos1).compareTo(items.get(pos2)) > 0))) {
				temp.set(spot, items.get(pos2));
				pos2 += 1;
			} else {
				temp.set(spot, items.get(pos1));
				pos1 += 1;
			}
			spot += 1;
		}
		
		for (int i = start; i <= end; i++) {
			items.set(spot, temp.get(i));
		}
		
	}

	public static void mergesort(ArrayList<String> items, int start, int end) {
		
		if (start < end) {
			int mid = (start + end) / 2;
			mergesort(items, start, mid);
			mergesort(items, mid + 1, end);
			merge(items, start, mid, end);
		}
	}
	
}
