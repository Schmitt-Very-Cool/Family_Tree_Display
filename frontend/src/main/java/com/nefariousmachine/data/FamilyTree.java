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
     * Getter for members arraylist.
     * @return The members arraylist
     */
    public ArrayList<Person> getMembers() {
        return members;
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

    /**
     * Adds a relationship to the tree. Does nothing if either of the people in the relationship does not exist.
     * Completes in O(log(n)) time. If there already exists a relationship between these two people, nothing happens,
     * even if the marital status is different! To update marital status, you can just set isMarried directly.
     *
     * @param relationship the relationship to be added to the tree.
     */
    public void addRelationship(Relationship relationship) {
        //Ensure people exist
        Person p1 = getMemberById(relationship.getPeople()[0]);
        Person p2 = getMemberById(relationship.getPeople()[1]);
        if(p1 == null || p2 == null){
            return;
        }

        //Add relationship to relationships (binary insert)
        int upBound = relationships.size();
        int lowBound = 0;
        while(upBound != lowBound) {
            int middle = lowBound + (upBound - lowBound) / 2;
            if(relationships.get(middle).getPeople()[0] == p1.getId()) {
                if(relationships.get(middle).getPeople()[1] == p2.getId()) {
                    relationships.set(middle, relationship);
                    return;
                }
                if(relationships.get(middle).getPeople()[1] < p2.getId()) {
                    lowBound = middle + 1;
                } else {
                    upBound = middle;
                }
            }
            if(relationships.get(middle).getPeople()[0] < p1.getId()) {
                lowBound = middle + 1;
            } else {
                upBound = middle;
            }
        }
        relationships.add(upBound, relationship);
    }

    /**
     * Returns an array of the provided person's 2 parents. Returns null if the person is not in this tree or if the
     * person's parents field is null. Elements of the parents array will be null if no person exists on this tree with
     * an id matching that of the Person's corresponding parent.
     *
     * @param person the Person whose parents will be returned
     * @return Array of Persons corresponding to person's parents field. Elements of the array will be null if no
     * such corresponding Person exists. Returns null if person doesn't exist.
     */
    public Person[] getParents(Person person) {
        if(person.getParents() == null) {
            return null;
        }
        Person[] parents = new Person[2];
        parents[0] = getMemberById(person.getParents().getPeople()[0]);
        parents[1] = getMemberById(person.getParents().getPeople()[1]);
        return parents;
    }
}
