package com.nefariousmachine.data;

/**
 * Data class that represents a relationship (e.g. marriage) versus
 */
public class Relationship {
    private Person person1;
    private Person person2;
    public boolean isMarriage;

    public Relationship(Person p1, Person p2, boolean isMarriage){
        person1 = p1;
        person2 = p2;
        this.isMarriage = isMarriage;
    }

    public void setMarriage(boolean marriage) {
        isMarriage = marriage;
    }

    /**
     * @return array of both people in the Relationship
     */
    public Person[] getPeople(){
        return new Person[] {person1, person2};
    }

    /**
     * @param id id of the person to be looked for
     * @return true if the person is in this relationship, false otherwise.
     */
    public boolean hasPerson(int id){
        return (id == person1.getId() || id == person2.getId());
    }
}
