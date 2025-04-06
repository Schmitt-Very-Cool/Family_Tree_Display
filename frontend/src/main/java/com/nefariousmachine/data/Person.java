package com.nefariousmachine.data;

/**
 * Data class to represent a member of the family tree.
 */

public class Person {
    static int idCounter = 1;

    //personal data
    private final int id;
    private String name;
    private String title;
    private String region;
    private String house;
    private String birthYear;
    private String deathYear;
    private boolean isMonarch;
    private boolean isUnknown;

    //relational data
    private Relationship parents;

    //constructor. Sets all data except id
    public Person(String name, String title, String region,
                  String house, String birthYear, String deathYear,
                  boolean isMonarch, boolean isUnknown, Relationship parents){
        this.id = idCounter;
        idCounter++;
        this.name = name;
        this.title = title;
        this.region = region;
        this.house = house;
        this.birthYear = birthYear;
        this.deathYear = deathYear;
        this.isMonarch = isMonarch;
        this.isUnknown = isUnknown;
        this.parents = parents;
    }

    public Person(int id) {
        this.id = id;
        this.name = "";
        this.title = "";
        this.region = "";
        this.house = "";
        this.birthYear = "";
        this.deathYear = "";
        this.isMonarch = false;
        this.isUnknown = false;
        this.parents = null;
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
    public void setBirthYear(String birthYear) {
        this.birthYear = birthYear;
    }
    public String getBirthYear() {
        return birthYear;
    }
    public void setDeathYear(String deathYear) {
        this.deathYear = deathYear;
    }
    public String getDeathYear() {
        return deathYear;
    }
    public boolean getIsMonarch() {
        return isMonarch;
    }
    public void setMonarch(boolean monarch) {
        isMonarch = monarch;
    }
    public boolean getIsUnknown() {
        return isUnknown;
    }
    public void setUnknown(boolean unknown) {
        isUnknown = unknown;
    }
    public void setParents(Relationship parents) {
        this.parents = parents;
    }
    public Relationship getParents() {
        return parents;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        if(title != null && !title.isEmpty()) {
            str.append(title);
            str.append(" ");
        }
        str.append(name);
        if(region != null && !region.isEmpty()) {
            str.append(" of ");
            str.append(region);
        }
        if(house != null && !house.isEmpty()) {
            str.append("\nHouse of ");
            str.append(house);
        }
        if(birthYear != null && !birthYear.split(" ")[0].isEmpty()) {
            str.append("\n");
            str.append(birthYear);
            str.append(" - ");
            if(!deathYear.split(" ")[0].isEmpty()) {
                str.append(deathYear);
            } else {
                str.append("Present");
            }
        }
        return str.toString();
    }
}
