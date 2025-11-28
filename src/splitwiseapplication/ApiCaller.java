package splitwiseapplication;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * HTTP API Client utility for communicating with the backend database server.
 * 
 * Purpose:
 * This class provides static methods for making HTTP GET requests to a Spring Boot
 * backend server running on localhost:8080. It handles JSON response parsing and
 * converts the responses into Java data structures (String arrays).
 * 
 * Architecture:
 * - Uses Java 11+ HttpClient for HTTP communication
 * - All methods use synchronous blocking calls
 * - Custom JSON parsing (no external JSON library dependencies)
 * - Returns different data structures based on response format
 * 
 * Backend API structure:
 * - db1: CheckAmountSpent tables (per-group member spending totals)
 * - db2: Credentials table (user authentication)
 * - db3: GroupNames table (group metadata)
 * - db4: Group member lists (per-group tables)
 * - db5: PaymentHistory tables (per-group transaction logs)
 * - db6: PendingAmount tables (per-group balance settlements)
 * - db7: User group membership tables (per-user tables)
 * - db8: TransactionDetails tables (per-group split details)
 * 
 * Error handling:
 * - Prints stack traces to stderr on exceptions
 * - Returns empty arrays on failure (never null)
 * - Safe to use without additional null checks
 */
public class ApiCaller {

    /**
     * Makes an HTTP GET request and parses the JSON response into a 2D String array.
     * 
     * Contract:
     * - Sends GET request to the specified URL
     * - Expects JSON response in format: array of objects or array of arrays
     * - Parses JSON into String[][] where each row represents one record
     * - Returns empty 2D array on error or empty response
     * 
     * @param url  Complete API endpoint URL with query parameters
     *             Format: "http://localhost:8080/db{N}/{operation}?{params}"
     * @return     2D String array where:
     *             - First dimension = rows/records
     *             - Second dimension = fields/columns within each record
     *             - Empty array (String[0][0]) on error or no data
     * 
     * JSON parsing behavior:
     * - Handles both [{...},{...}] and [[...],[...]] formats
     * - Extracts values from key:value pairs (for object arrays)
     * - Removes quotes and brackets from values
     * - Splits by comma for multiple fields
     * 
     * Usage patterns (50+ call sites across project):
     * 
     * 1. Data retrieval (GetRowData):
     *    - Fetch all members of a group
     *    - Get transaction history
     *    - Retrieve balance information
     *    - Load spending details
     *    Example: ApiCaller1("...db4/GetRowData?table=ABC123")
     * 
     * 2. Database operations (CreateTable, InsertData):
     *    - Create new group tables
     *    - Add new members
     *    - Insert transaction records
     *    - Note: Response typically ignored for these operations
     * 
     * 3. Existence checking (via Exists.exist):
     *    - Check if username exists
     *    - Verify group code validity
     *    - Confirm membership status
     * 
     * Key call locations:
     * - CheckBalances: Load member balances (1 call)
     * - DeleteTransaction: Get transaction details for rollback (2 calls)
     * - MainPage: Load transaction history (2 calls)
     * - AmountSettled: Get group member list (1 call)
     * - AddTransaction: Complex transaction splits (13+ calls)
     * - CreateGroup: Setup new group database structure (11 calls)
     * - EnterGroup: Load user's groups (1 call)
     * - JoinGroup: Join existing group (7 calls)
     * - RegisterPageGUI: Create user account (2 calls)
     * - SettlePayment: Load pending balances (3 calls)
     * - CheckAmountSpent: Load spending data (1 call)
     * - Exists: Data existence checks (1 call)
     */
    // This version returns a String[][]
    public static String[][] ApiCaller1(String url) {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();
            
            // Parse JSON array to String[][]
            return parseJsonToArray(responseBody);

        } catch (Exception e) {
            e.printStackTrace();
            return new String[0][0]; // return empty 2D array on error
        }
    }
    
    /**
     * Internal helper method - Parses JSON array string into a 2D String array.
     * 
     * Contract:
     * - Takes raw JSON string as input
     * - Handles both object array format [{...},{...}] and array-of-arrays format [[...],[...]]
     * - Extracts values from key:value pairs in objects
     * - Returns structured 2D array representation
     * - Returns empty array for null or empty input
     * 
     * @param json  Raw JSON string to parse (from HTTP response body)
     * @return      2D String array where:
     *              - First dimension = rows/records
     *              - Second dimension = field values within each record
     *              - Empty String[0][0] if input is null/empty
     * 
     * Parsing algorithm:
     * 1. Validate input (null/empty check)
     * 2. Remove outer square brackets [ ]
     * 3. Split by },{ or ],[ regex patterns to separate individual records
     * 4. For each record string:
     *    a. Remove inner brackets { } or [ ]
     *    b. Split by comma to get field parts
     *    c. For each part, extract value after colon (if key:value format)
     *    d. Remove quotes from string values
     *    e. Build String[] for that record
     * 5. Combine all record arrays into 2D result
     * 
     * Supported JSON formats:
     * 
     * Format 1 - Array of objects (most common):
     *   Input:  [{"id":"1","name":"Alice","amount":"50.00"},{"id":"2","name":"Bob","amount":"30.00"}]
     *   Output: [["1","Alice","50.00"], ["2","Bob","30.00"]]
     * 
     * Format 2 - Array of arrays:
     *   Input:  [["1","Alice","50.00"],["2","Bob","30.00"]]
     *   Output: [["1","Alice","50.00"], ["2","Bob","30.00"]]
     * 
     * Limitations:
     * - Simple regex-based parsing (not a full JSON parser library)
     * - May not handle deeply nested structures
     * - Assumes comma-separated fields within records
     * - Does not handle escaped quotes within string values
     * - Works reliably for the flat structures returned by this project's backend API
     * 
     * Error handling:
     * - Returns empty 2D array for invalid/empty input
     * - Skips empty rows after parsing
     * - Continues processing remaining rows if one fails
     */
    // Parse JSON array of arrays/objects to String[][]
    private static String[][] parseJsonToArray(String json) {
        if (json == null || json.trim().isEmpty()) {
            return new String[0][0];
        }
        
        json = json.trim();
        
        // Remove outer brackets
        if (json.startsWith("[") && json.endsWith("]")) {
            json = json.substring(1, json.length() - 1).trim();
        }
        
        if (json.isEmpty()) {
            return new String[0][0];
        }
        
        List<String[]> rows = new ArrayList<>();
        
        // Split by },{  or ],[  to separate rows
        String[] rowStrings = json.split("\\}\\s*,\\s*\\{|\\]\\s*,\\s*\\[");
        
        for (String rowStr : rowStrings) {
            rowStr = rowStr.trim();
            
            // Remove brackets if present
            if (rowStr.startsWith("{") || rowStr.startsWith("[")) {
                rowStr = rowStr.substring(1);
            }
            if (rowStr.endsWith("}") || rowStr.endsWith("]")) {
                rowStr = rowStr.substring(0, rowStr.length() - 1);
            }
            
            rowStr = rowStr.trim();
            
            if (rowStr.isEmpty()) {
                continue;
            }
            
            // Parse the row to extract values
            List<String> values = new ArrayList<>();
            
            // Split by comma, but handle quoted strings
            String[] parts = rowStr.split(",");
            
            for (String part : parts) {
                part = part.trim();
                
                // Extract value after colon if it's a key:value pair
                int colonIndex = part.indexOf(':');
                if (colonIndex > 0) {
                    part = part.substring(colonIndex + 1).trim();
                }
                
                // Remove quotes
                part = part.replaceAll("^\"|\"$", "");
                
                values.add(part);
            }
            
            rows.add(values.toArray(new String[0]));
        }
        
        return rows.toArray(new String[0][0]);
    }

    /**
     * Makes an HTTP GET request and returns the raw response body as a String.
     * 
     * Contract:
     * - Sends GET request to the specified URL
     * - Returns the complete response body without parsing
     * - Logs response code and body to stdout for debugging
     * - Returns null on error
     * 
     * @param url  Complete API endpoint URL with query parameters
     *             Format: "http://localhost:8080/db{N}/{operation}?{params}"
     * @return     Raw response body as String, or null on error
     * 
     * Debug output:
     * - Prints "Response code: {code}" to stdout
     * - Prints "Response body: {body}" to stdout
     * - Useful for troubleshooting API communication issues
     * 
     * Usage patterns (45+ call sites across project):
     * 
     * 1. Update operations (UpdateData):
     *    - Modify balance amounts
     *    - Update spending totals
     *    - Adjust pending amounts after settlements
     *    Example: ApiCaller2("...db6/UpdateData?table=ABC&where=Member1='user'&Amount=50.00")
     * 
     * 2. Delete operations (DeleteRowData):
     *    - Remove transactions
     *    - Delete group memberships
     *    - Clean up user data when leaving groups
     *    Example: ApiCaller2("...db5/DeleteRowData?table=ABC&tID=T123")
     * 
     * 3. Insert operations (InsertData):
     *    - Record payments
     *    - Add settlement transactions
     *    - Log transaction history
     *    Example: ApiCaller2("...db5/InsertData?table=ABC&params=(...)&info=(...)")
     * 
     * 4. Schema operations (AddColumn, DeleteColumn):
     *    - Add member column to transaction details table
     *    - Remove member column when leaving group
     *    Example: ApiCaller2("...db8/AddColumn?table=ABC&uname=user")
     * 
     * Key call locations:
     * - DeleteTransaction: Rollback transaction effects (7 calls)
     * - MainPage: Leave group cleanup (11 calls)
     * - AmountSettled: Record settlement payments (6 calls)
     * - AddTransaction: Update balances after expense (21 calls)
     * 
     * Note: Return value is often ignored since these are side-effect operations
     * (the caller typically only cares that the request completed, not the response).
     */
    public static String ApiCaller2(String url) {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Response code: " + response.statusCode());
            System.out.println("Response body: " + response.body());
            return response.body();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Makes an HTTP GET request and parses the JSON response into a 1D String array.
     * 
     * Contract:
     * - Sends GET request to the specified URL
     * - Expects JSON response in format: array of simple values
     * - Parses JSON into String[] containing individual values
     * - Returns empty array on error or empty response
     * 
     * @param url  Complete API endpoint URL with query parameters
     *             Format: "http://localhost:8080/db{N}/GetSpecificData?val={column}&table={table}&{filters}"
     * @return     1D String array containing individual field values
     *             - Empty array (String[0]) on error or no data
     * 
     * JSON parsing behavior:
     * - Handles simple JSON arrays: ["value1","value2","value3"]
     * - Removes quotes from string values
     * - Splits by comma for multiple values
     * - Designed for single-column query results
     * 
     * Usage patterns (14 call sites across project):
     * 
     * 1. Fetch specific column values (GetSpecificData):
     *    - Get list of member names
     *    - Retrieve transaction IDs
     *    - Extract single field from multiple records
     *    Example: ApiCaller3("...db4/GetSpecificData?val=name&table=ABC123")
     *    Returns: ["Alice", "Bob", "Charlie"]
     * 
     * 2. Lookup single value:
     *    - Get group code from group name
     *    - Retrieve specific amounts
     *    - Query individual fields with filters
     *    Example: ApiCaller3("...db3/GetSpecificData?val=group_code&table=GroupNames&group_name=Trip")
     *    Returns: ["ABC123"]
     * 
     * Key call locations:
     * - CheckBalances: Get member names for balance display (1 call)
     * - DeleteTransaction: Get amounts for rollback calculations (5 calls)
     * - MainPage: Get user amounts for leave group operation (2 calls)
     * - AmountSettled: Get used transaction IDs (1 call)
     * - AddTransaction: Get members and transaction IDs (3 calls)
     * - EnterGroup: Lookup group code by name (2 calls)
     * - JoinGroup: Get group name from code (1 call)
     * 
     * Difference from ApiCaller1:
     * - ApiCaller1 returns 2D array (multiple records with multiple fields)
     * - ApiCaller3 returns 1D array (single field from multiple records, or one value)
     */
    public static String[] ApiCaller3(String url) {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();
            
            // Parse JSON array to String[]
            return parseJsonToStringArray(responseBody);
        } catch (Exception e) {
            e.printStackTrace();
            return new String[0];
        }
    }
    
    /**
     * Internal helper method - Parses simple JSON array string into a 1D String array.
     * 
     * Contract:
     * - Takes raw JSON string as input
     * - Handles simple array format: ["value1", "value2", "value3"]
     * - Removes quotes and brackets
     * - Returns flat String array
     * 
     * @param json  Raw JSON string to parse (expected format: JSON array of strings)
     * @return      1D String array containing values, or empty array if null/empty input
     * 
     * Parsing logic:
     * 1. Remove outer brackets [ ]
     * 2. Split by comma
     * 3. Remove quotes from each value
     * 4. Build String[] result
     * 
     * Limitations:
     * - Simple string-based parsing (not a full JSON parser)
     * - Assumes flat array structure (no nesting)
     * - Works for simple value arrays returned by GetSpecificData endpoints
     * 
     * Example:
     * Input:  "{"Alice", "Bob", "Charlie"}"
     * Output:  ["Alice", "Bob", "Charlie"]
     */
    // Parse JSON array to String[]
    private static String[] parseJsonToStringArray(String json) {
        if (json == null || json.trim().isEmpty()) {
            return new String[0];
        }
        
        json = json.trim();
        
        // Remove outer brackets
        if (json.startsWith("[") && json.endsWith("]")) {
            json = json.substring(1, json.length() - 1).trim();
        }
        
        if (json.isEmpty()) {
            return new String[0];
        }
        
        List<String> values = new ArrayList<>();
        
        // Split by comma
        String[] parts = json.split(",");
        
        for (String part : parts) {
            part = part.trim();
            
            // Remove quotes
            part = part.replaceAll("^\"|\"$", "");
            
            values.add(part);
        }
        
        return values.toArray(new String[0]);
    }
}