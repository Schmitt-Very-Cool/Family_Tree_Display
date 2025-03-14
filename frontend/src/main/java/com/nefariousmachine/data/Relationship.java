package com.nefariousmachine.data;

/**
 * Data class that represents a relationship (e.g. marriage) versus
 */
public class Relationship {
    /**
     * Smaller id of the 2 people in the relationship
     */
    private int person1;
    /**
     * Larger id of the 2 people in the relationship
     */
    private int person2;
    public boolean isMarriage;

    /**
     * Constructor. Note that arguments p1 and p2 do not necessarily correspond to fields person1 and person2.
     * Relationships are always constructed such that person1's id is less than or equal to person2's id.
     * @param p1 one of the Persons in this Relationship.
     * @param p2 one of the Persons in this Relationship.
     * @param isMarriage true if this relationship is a marriage.
     */
    public Relationship(int p1, int p2, boolean isMarriage){
        //Ensure person1's id is smaller than person2's id
        if(p1 > p2){
            var temp = p1;
            p1 = p2;
            p2 = temp;
        }
        person1 = p1;
        person2 = p2;
        this.isMarriage = isMarriage;
    }

    public void setMarriage(boolean marriage) {
        isMarriage = marriage;
    }

    /**
     * @return array of both people in the Relationship in the form {person1, person2}
     */
    public int[] getPeople(){
        return new int[] {person1, person2};
    }

    /**
     * @param id id of the person to be looked for
     * @return true if the person is in this relationship, false otherwise
     */
    public boolean hasPerson(int id){
        return (id == person1 || id == person2);
    }
}
