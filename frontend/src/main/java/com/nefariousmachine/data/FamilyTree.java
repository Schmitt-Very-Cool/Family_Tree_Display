package com.nefariousmachine.data;

import java.awt.*;
import java.util.*;

/**
 * Class to represent the entire tree.
 */
public class FamilyTree {

    private final Person fakeman = new Person(-1);

    /**
     * All the members of the tree. index corresponds to id.
     */
    private ArrayList<Person> members = new ArrayList<>(20);

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
     * Adds a member to the ArrayList members. If a person with member's id already exists, that
     * person in members is replaced with the new member.
     *
     * @param member Person to be added.
     */
    public void addMember(Person member){
        while(members.size() < (int)(member.getId() * 1.5)) {
            members.add(fakeman);
        }
        members.add(member.getId(), member);
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
     *
     * @param id id of the person to be returned
     * @return the person with an id matching the provided one, or null if no such person exists
     */
    public Person getMemberById(int id) {
        if(members.size() <= id) {
            return null;
        }
        return members.get(id);
    }

    /**
     * Removes Person p from the Family Tree, assuming it is in the tree.
     *
     * @param p Person to be removed
     */
    public void removeMember(Person p) {
        members.remove(p);
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
            } else if(relationships.get(middle).getPeople()[0] < p1.getId()) {
                lowBound = middle + 1;
            } else {
                upBound = middle;
            }
        }
        relationships.add(upBound, relationship);
    }

    /**
     * Finds the Relationship between the two members of the FamilyTree whose ids match p1 and p2, if one exists.
     * @param p1 one of the members of the Relationship to find
     * @param p2 one of the members of the Relationship to find
     * @return the Relationship containing the Persons with ids p1 and p2. Returns null if no such Relationship exists.
     */
    public Relationship getRelationship(int p1, int p2) {
        //Ensure p1 != p2
        if(p1 == p2) {
            return null;
        }
        //Ensure p1 < p2
        if(p1 > p2) {
            int temp = p1;
            p1 = p2;
            p2 = temp;
        }
        //Ensure p1 and p2 exist
        if(getMemberById(p1) == null || getMemberById(p2) == null) {
            return null;
        }
        //Binary Search
        int upBound = relationships.size();
        int lowBound = 0;
        while(upBound != lowBound) {
            int middle = lowBound + (upBound - lowBound) / 2;
            if(relationships.get(middle).getPeople()[0] == p1) {
                if(relationships.get(middle).getPeople()[1] == p2) {
                    return relationships.get(middle);
                }
                if(relationships.get(middle).getPeople()[1] < p2) {
                    lowBound = middle + 1;
                } else {
                    upBound = middle;
                }
            } else if(relationships.get(middle).getPeople()[0] < p1) {
                lowBound = middle + 1;
            } else {
                upBound = middle;
            }
        }
        return null;
    }

    /**
     * Removes a relationship from the relationships ArrayList. Does nothing if r is not hin relationships.
     *
     * @param r Relationship to remove.
     */
    public void removeRelationship(Relationship r) {
        relationships.remove(r);
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

    /**
     * Gets an array of Persons.
     *
     * @param ids array of the ids of the persons to be returned
     * @return an array of persons whose ids the ids array. The order of persons in the return array will match the
     * order in the ids array.
     */
    public Person[] getPersons(int[] ids) {
        Person[] persons = new Person[ids.length];
        for(int i = 0; i < ids.length; i++){
            persons[i] = getMemberById(ids[i]);
        }
        return persons;
    }
}
