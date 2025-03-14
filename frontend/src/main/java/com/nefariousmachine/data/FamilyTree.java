package com.nefariousmachine.data;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class to represent the entire tree.
 */
public class FamilyTree {
    /**
     * All the members of the tree. Sorted by ascending id.
     */
    private ArrayList<Person> members = new ArrayList<>();

    /**
     * All the relationships (e.g. marriages, affairs, etc.) in the tree. Relationships are assumed to be made such that
     * the id of person1 is less than that of person2. The ArrayList is sorted ascending by the id of person1, and then
     * by the id of person2.
     */
    private ArrayList<Relationship> relationships = new ArrayList<>();

    /**
     * Map of colors to display for each noble house. Houses are added to the map as they are assigned colors.
     */
    private HashMap<String, Color> houseColors = new HashMap<>();

    /**
     * Adds a member to the ArrayList members in O(log(n)) time. If a person with member's id already exists, that
     * person in members is replaced with the new member.
     *
     * @param member Person to be added.
     */
    public void addMember(Person member){
        //Binary insert
        int upBound = members.size();
        int lowBound = 0;
        while(upBound != lowBound) {
            int middle = lowBound + (upBound - lowBound) / 2;
            if(members.get(middle).getId() == member.getId()) {
                //Person already exists
                members.set(middle, member);
                return;
            }
            if(members.get(middle).getId() < member.getId()) {
                lowBound = middle + 1;
            } else {
                upBound = middle;
            }
        }
        members.add(upBound, member);
    }

    /**
     * Gets a member of the family tree corresponding to the provided id. Returns null if there is no such member.
     * Completes the search in O(log(n)) time.
     *
     * @param id id of the person to be returned
     * @return the person with an id matching the provided one, or null if no such person exists
     */
    public Person getMemberById(int id){
        //Binary search
        int upBound = members.size();
        int lowBound = 0;
        while(upBound != lowBound) {
            int middle = lowBound + (upBound - lowBound) / 2;
            if(members.get(middle).getId() == id) {
                return members.get(middle);
            }
            if(members.get(middle).getId() < id) {
                lowBound = middle + 1;
            } else {
                upBound = middle;
            }
        }
        return null;
    }

    public void addRelationship() {

    }
}
