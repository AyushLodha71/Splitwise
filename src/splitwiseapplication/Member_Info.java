package splitwiseapplication;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList; // Used for subjects and for the list of students
import java.util.Arrays;    // For convenience in initializing ArrayLists

/**
 * Member_Info - Data class for member balance information
 * 
 * Stores a member's name and their associated balance details (amounts owed/owing).
 * Used primarily in the CheckBalances interface to display member financial details.
 * 
 * Purpose:
 * - Encapsulates member name and their list of balance entries
 * - Each balance entry shows: Other Member > Amount > Direction (Owes/Lends)
 * - Enables easy data transfer between file I/O and GUI components
 * 
 * Structure:
 * - name: The member whose balance details we're tracking
 * - members: List of balance strings (e.g., "john>100.50>Owes")
 * 
 * Usage in CheckBalances.java:
 * 1. Read member balance file (e.g., "PendingAmount/mKEILR5/alice.lodha")
 * 2. Create Member_Info object for alice.lodha
 * 3. Add each balance line as a member entry
 * 4. Display in JList to show who alice owes or who owes alice
 * 
 * Example Data Flow:
 * File: alice.lodha contains:
 *   bob>50.00>Owes
 *   charlie>75.00>Lends
 * 
 * Creates Member_Info("alice.lodha") with:
 *   members = ["bob>50.00>Owes", "charlie>75.00>Lends"]
 * 
 * @version 2.0 - Enhanced with comprehensive documentation
 */
class Member_Info {
    private String name;                    // Member's username (file owner)
    private ArrayList<String> members;      // List of balance entries for this member

    /**
     * Constructs a new Member_Info object for the specified member.
     * 
     * Initializes an empty list of balance entries that can be populated
     * using addMembers() as balance data is read from files.
     * 
     * @param name The member's username (e.g., "alice.lodha")
     */
    public Member_Info(String name) {
        this.name = name;
        members = new ArrayList<String>();
    }

    /**
     * Gets the member's name.
     * 
     * @return The username of the member whose balances are stored
     */
    public String getName() {
        return name;
    }

    /**
     * Gets all balance entries for this member.
     * 
     * Each entry is a string formatted as:
     * "otherMember>amount>direction"
     * 
     * Where direction is either "Owes" or "Lends"
     * 
     * @return ArrayList of balance entry strings
     */
    public ArrayList<String> getMembers() {
        return members;
    }

    /**
     * Adds a balance entry for this member.
     * 
     * Typically called once per line when reading from a member's balance file.
     * 
     * Format: "otherMember>amount>direction"
     * 
     * Examples:
     * - addMembers("bob>50.00>Owes")      // This member owes bob $50
     * - addMembers("charlie>75.00>Lends")  // charlie owes this member $75
     * 
     * @param member A balance entry string with format "name>amount>direction"
     */
    public void addMembers(String member) {
        members.add(member);
    }
    
    /**
     * Returns the member's name as a string representation.
     * 
     * Useful when displaying Member_Info objects directly in Swing components
     * like JList or JComboBox, as they call toString() to display items.
     * 
     * @return The member's username
     */
    public String toString() {
        return name; // Useful if you were to put Member_Info objects directly into a JList or JComboBox
    }
}