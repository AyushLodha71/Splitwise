package splitwiseapplication;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList; // Used for subjects and for the list of students
import java.util.Arrays;    // For convenience in initializing ArrayLists

class Member_Info {
    private String name;
    private ArrayList<String> members;

    public Member_Info(String name) {
        
    	this.name = name;
        members = new ArrayList<String>();
    
    }

    public String getName() {
        
    	return name;
    	
    }

    public ArrayList<String> getMembers() {
        
    	return members;
    	
    }

    
    public void addMembers(String member) {
    	
    	members.add(member);
    	
    }
    
    public String toString() {
        
    	return name; // Useful if you were to put Student objects directly into a JList or JComboBox
    
    }
}