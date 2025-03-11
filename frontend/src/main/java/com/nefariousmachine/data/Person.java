package com.nefariousmachine.data;

import java.util.ArrayList;

/**
 * Data class to represent a member of the family tree.
 */

public class Person {
    static int idCounter = 1;

    //personal data
    private int id;
    private String name;
    private String title;
    private String region;
    private String house;
    private int birthYear;
    private int deathYear;
    private boolean isMonarch;
    private boolean isSaint;

    //relational data
    private Relationship parents;
    private ArrayList<Relationship> relationships;

    //constructor. Sets all data except
    public Person(String name, String title, String region,
                  String house, int birthYear, int deathYear,
                  boolean isMonarch, boolean isSaint, Relationship parents){
        this.id = idCounter;
        idCounter++;
        this.name = name;
        this.title = title;
        this.region = region;
        this.house = house;
        this.birthYear = birthYear;
        this.deathYear = deathYear;
        this.isMonarch = isMonarch;
        this.isSaint = isSaint;
        this.parents = parents;
    }

    //getters and setters
    public int getId(){
        return id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getTitle() {
        return title;
    }
    public void setRegion(String region) {
        this.region = region;
    }
    public String getRegion() {
        return region;
    }
    public void setHouse(String house) {
        this.house = house;
    }
    public String getHouse() {
        return house;
    }
    public void setBirthYear(int birthYear) {
        this.birthYear = birthYear;
    }
    public int getBirthYear() {
        return birthYear;
    }
    public void setDeathYear(int deathYear) {
        this.deathYear = deathYear;
    }
    public int getDeathYear() {
        return deathYear;
    }
    public void setMonarch(boolean monarch) {
        isMonarch = monarch;
    }
    public void setSaint(boolean saint) {
        isSaint = saint;
    }
    public void setParents(Relationship parents) {
        this.parents = parents;
    }
    public Relationship getParents() {
        return parents;
    }

    //crud operations for relationships
    public ArrayList<Relationship> getRelationships() {
        return relationships;
    }

    /**
     * Removes the relationship this person has with a person who has a particular id.
     * @param id id of the person whose relationship with this person is being removed.
     *           If this id equals the id of this person, then nothing happens.
     *           If this person does not have a relationship with a person of this id, nothing happens.
     */
    public void removeRelationship(int id) {
        if(id == this.id){
            return;
        }
        for(Relationship r : relationships){
            if(r.hasPerson(id)) relationships.remove(r);
        }
    }

    /**
     * Adds a relationship between this person and the person in the parameters.
     * @param person person to add the relationship to. If these people already have a relationship then nothing happens.
     * @param isMarriage true if this relationship is a marriage.
     */
    public void addRelationship(Person person, boolean isMarriage){
        for(Relationship r : relationships){
            if(r.hasPerson(person.getId())) return;
        }
        relationships.add(new Relationship(this, person, isMarriage));
    }

}
