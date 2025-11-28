package splitwiseapplication;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Data model class representing a group member and their associated balance information.
 * 
 * Purpose:
 * This class is used by the CheckBalances feature to store and display:
 * - A member's name
 * - A list of balance-related messages showing who owes whom
 * 
 * Design pattern: Simple data holder (DTO - Data Transfer Object)
 * - Encapsulates member name and their balance relationships
 * - Provides accessor methods for reading data
 * - Provides mutator method for adding balance messages
 * 
 * Usage:
 * Only used in CheckBalances.java to organize and display balance information
 * in the group balances UI. Each Member_Info object represents one person
 * in the group and stores all the "you owe" / "owes you" messages for that person.
 * 
 * this class is specifically for member balance tracking in the Splitwise application.
 */
class Member_Info {
    /**
     * The name of the group member.
     * Immutable after construction.
     */
    private String name;
    
    /**
     * List of balance-related messages for this member.
     * Each string describes a balance relationship, such as:
     * - "Alice owes 25.50"
     * - "You owe 30.00 to Bob"
     * - "Charlie owes you 15.75"
     */
    private ArrayList<String> members;

    /**
     * Constructs a new Member_Info object for a group member.
     * 
     * Contract:
     * - Initializes the member's name
     * - Creates an empty list for storing balance messages
     * - The members list can be populated later via addMembers()
     * 
     * @param name  The name of the group member (cannot be null)
     * 
     * Post-conditions:
     * - this.name is set to the provided name
     * - this.members is initialized as an empty ArrayList
     */
    public Member_Info(String name) {
        
    	this.name = name;
        members = new ArrayList<String>();
    
    }

    /**
     * Gets the name of this group member.
     * 
     * Contract:
     * - Returns the member's name as set during construction
     * - Name is immutable (cannot be changed after construction)
     * 
     * @return  The member's name
     */
    public String getName() {
        
    	return name;
    	
    }

    /**
     * Gets the list of balance messages associated with this member.
     * 
     * Contract:
     * - Returns the ArrayList containing all balance-related messages
     * - List may be empty if no balance messages have been added yet
     * - Returned list is mutable (caller can modify it, though addMembers() is preferred)
     * 
     * @return  ArrayList of strings, each describing a balance relationship
     *          Examples: "Alice owes 25.50", "You owe 30.00 to Bob"
     * 
     * Usage:
     * - CheckBalances uses this to retrieve messages for display in the UI
     * - Typically called when a member button is clicked to show their balances
     */
    public ArrayList<String> getMembers() {
        
    	return members;
    	
    }

    /**
     * Adds a balance message to this member's list.
     * 
     * Contract:
     * - Appends a new balance-related message to the members list
     * - Messages describe who owes whom and how much
     * - Order of messages is preserved (FIFO - first added, first retrieved)
     * 
     * @param member  A string describing a balance relationship
     *                Format examples:
     *                - "Alice owes 25.50"
     *                - "You owe 30.00 to Bob"
     *                - "Bob owes you 15.75"
     * 
     * Usage:
     * - CheckBalances calls this method to populate balance information
     * - Called once for each balance relationship involving this member
     * - Typically called during initialization based on PendingAmount data
     */
    public void addMembers(String member) {
    	
    	members.add(member);
    	
    }
    
    /**
     * Returns a string representation of this Member_Info object.
     * 
     * Contract:
     * - Returns the member's name (same as getName())
     * - Useful for displaying Member_Info objects in Swing components
     * - Overrides Object.toString() for better display in UI components
     * 
     * @return  The member's name
     * 
     * Usage:
     * - Allows Member_Info objects to be directly added to JList or JComboBox
     * - The component will automatically display the member's name
     * - Currently not actively used in CheckBalances (uses separate name handling)
     */
    @Override
    public String toString() {
        
    	return name;
    
    }
}