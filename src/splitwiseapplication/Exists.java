package splitwiseapplication;

/**
 * Utility class for verifying the existence of records via API calls.
 * 
 * Purpose:
 * This class provides a simple wrapper around API calls to check whether
 * specific data exists in the backend database without retrieving the full dataset.
 * 
 * Primary use cases:
 * - Authentication: Validate user credentials during login
 * - Registration: Check username availability before account creation
 * - Group operations: Verify group existence and membership status
 * 
 * Design notes:
 * - All methods are static (utility class pattern)
 * - Returns boolean for simple yes/no existence checks
 * - Delegates actual API communication to ApiCaller class
 * - Includes debug logging of result counts to stdout
 */
public class Exists {		
	
	/**
	 * Checks if a record exists by querying an API endpoint.
	 * 
	 * Contract:
	 * - Calls the specified API URL via ApiCaller.ApiCaller1()
	 * - Returns true if the API returns at least one row of data
	 * - Returns false if the API returns an empty result set
	 * 
	 * @param url  Complete API endpoint URL with query parameters
	 *             Format: "https://splitwise.up.railway.app/db{N}/GetRowData?table={table_name}&{filters}"
	 * @return     true if data exists (rows.length > 0), false otherwise
	 * 
	 * Implementation details:
	 * - ApiCaller.ApiCaller1() returns a 2D String array (String[][])
	 * - Prints the number of returned rows to stdout for debugging
	 * - Simple length check determines existence (> 0 means exists)
	 * 
	 * Current usage (7 call sites):
	 * 1. LoginPageGUI (2 calls):
	 *    - Verify username + password combination exists in Credentials table
	 *    
	 * 2. RegisterPageGUI (2 calls):
	 *    - Check if username is already taken before registration
	 *    
	 * 3. CreateGroup (1 call):
	 *    - Validate generated group code is unique (loop until unique code found)
	 *    
	 * 4. JoinGroup (2 calls):
	 *    - Verify group code exists in GroupNames table
	 *    - Check user is not already a member of the group
	 * 
	 * Example usage:
	 * boolean userExists = Exists.exist(
	 *     "https://splitwise.up.railway.app/db2/GetRowData?table=Credentials&username=" + username
	 * );
	 */
	public static Boolean exist(String url) {

		String[][] rows = ApiCaller.ApiCaller1(url);

        int numlength = rows.length;
		if (numlength > 0) {
			return true;
		} else {
			return false;
		}
	}
		
}
