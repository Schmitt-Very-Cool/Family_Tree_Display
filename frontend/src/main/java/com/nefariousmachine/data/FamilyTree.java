package com.nefariousmachine.data;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Class to represent the entire tree.
 */
public class FamilyTree {

    public static final Person fakeman = new Person(-1);

    /**
     * All the members of the tree. index corresponds to id.
     */
    private final ArrayList<Person> members = new ArrayList<>(20);

    /**
     * All the relationships (e.g. marriages, affairs, etc.) in the tree. Relationships are assumed to be made such that
     * the id of person1 is less than that of person2. The ArrayList is sorted ascending by the id of person1, and then
     * by the id of person2.
     */
    private final ArrayList<Relationship> relationships = new ArrayList<>();

    /**
     * Map of colors to display for each noble house. Houses are added to the map as they are assigned colors.
     */
    private final HashMap<String, Color> houseColors = new HashMap<>();

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
        Person p1 = relationship.getPeople()[0];
        Person p2 = relationship.getPeople()[1];
        if(p1 == null || p2 == null){
            return;
        }

        //Add relationship to relationships (binary insert)
        int upBound = relationships.size();
        int lowBound = 0;
        while(upBound != lowBound) {
            int middle = lowBound + (upBound - lowBound) / 2;
            if(relationships.get(middle).getPeople()[0].getId() == p1.getId()) {
                if(relationships.get(middle).getPeople()[1].getId() == p2.getId()) {
                    relationships.set(middle, relationship);
                    return;
                }
                if(relationships.get(middle).getPeople()[1].getId() < p2.getId()) {
                    lowBound = middle + 1;
                } else {
                    upBound = middle;
                }
            } else if(relationships.get(middle).getPeople()[0].getId() < p1.getId()) {
                lowBound = middle + 1;
            } else {
                upBound = middle;
            }
        }
        relationships.add(upBound, relationship);
    }

    /**
     * Finds the Relationship between the two members of the FamilyTree whose ids match p1 and p2, if one exists. Runs
     * in O(log(r)).
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
            if(relationships.get(middle).getPeople()[0].getId() == p1) {
                if(relationships.get(middle).getPeople()[1].getId() == p2) {
                    return relationships.get(middle);
                }
                if(relationships.get(middle).getPeople()[1].getId() < p2) {
                    lowBound = middle + 1;
                } else {
                    upBound = middle;
                }
            } else if(relationships.get(middle).getPeople()[0].getId() < p1) {
                lowBound = middle + 1;
            } else {
                upBound = middle;
            }
        }
        return null;
    }

    /**
     * Gets all Persons in a relationship with the Person whose id is p. Runs in O(N)
     * @param p id of the Person to find the Relationships for.
     * @return array of Persons in a Relationship with the Person with id p.
     */
    public Person[] getRelationships(int p) {
        ArrayList<Person> spouses = new ArrayList<>();
        for(Relationship r : relationships) {
            if(r.getPeople()[0].getId() > p) break;
            if(r.getPeople()[0].getId() == p) spouses.add(r.getPeople()[1]);
            else if(r.getPeople()[1].getId() == p) spouses.add(r.getPeople()[0]);
        }
        Person[] ss = new Person[spouses.size()];
        for(int i = 0; i < spouses.size(); i++) {
            ss[i] = spouses.get(i);
        }
        return ss;
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
     * Get the entire ArrayList of Relationships
     * @return ArrayList of every relationship in the tree.
     */
    public ArrayList<Relationship> getAllRelationships() {
        return relationships;
    }

    /**
     * Get all relationships containing anybody in list people. Runs in O(r*m) time, where r is the number of
     * Relationship and m is the size of people. Worst case scenario, r is n factorial, which makes the algorithm O(n!)
     * in the worst-case. Typically, r ~ n and m is much less than n, which makes it O(n) on average.
     *
     * @param people List of people whose relationships are wanted.
     * @return all relationships containing anybody in people.
     */
    public ArrayList<Relationship> getAllRelationships(List<Person> people) {
        ArrayList<Relationship> rs = new ArrayList<>();
        for(Relationship r : relationships) { //O(r * m)
            if(people.contains(r.getPeople()[0]) || people.contains(r.getPeople()[1])) {
                rs.add(r);
            }
        }
        return rs;
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

    public FamilyTreeDTO toDTO() {
        FamilyTreeDTO dto = new FamilyTreeDTO();
        //Convert members
        dto.members = new ArrayList<>();
        for(Person person : members) {
            if(person != null && person != fakeman) {
                FamilyTreeDTO.PersonDTO personDTO = new FamilyTreeDTO.PersonDTO();
                personDTO.id = person.getId();
                personDTO.name = person.getName();
                personDTO.title = person.getTitle();
                personDTO.region = person.getRegion();
                personDTO.house = person.getHouse();
                personDTO.birthYear = person.getBirthYear();
                personDTO.deathYear = person.getDeathYear();
                personDTO.isMonarch = person.getIsMonarch();
                personDTO.isUnknown = person.getIsUnknown();
                if(person.getParents() != null) {
                    personDTO.parentRelationshipId = relationships.indexOf(person.getParents());
                }
                dto.members.add(personDTO);
            }
        }

        //Convert relationships
        dto.relationships = new ArrayList<>();
        for(Relationship rel : relationships) {
            FamilyTreeDTO.RelationshipDTO relDTO = new FamilyTreeDTO.RelationshipDTO();
            relDTO.person1Id = rel.getPeople()[0].getId();
            relDTO.person2Id = rel.getPeople()[1].getId();
            relDTO.isMarriage = rel.isMarriage;
            dto.relationships.add(relDTO);
        }

        //Convert house colors
        dto.houseColors = new HashMap<>();
        for(Map.Entry<String, Color> entry : houseColors.entrySet()) {
            Color color = entry.getValue();
            String hex = String.format("#%02x%02x%02x",
                    color.getRed(), color.getGreen(), color.getBlue());
            dto.houseColors.put(entry.getKey(), hex);
        }

        return dto;
    }

    // Create from DTO for loading
    public static FamilyTree fromDTO(FamilyTreeDTO dto) {
        FamilyTree tree = new FamilyTree();
        Map<Integer, Person> personMap = new HashMap<>();
        //create all persons
        tree.members.clear();
        int maxId = 0;
        for(FamilyTreeDTO.PersonDTO personDTO : dto.members) {
            Person person = new Person(personDTO.id);
            person.setName(personDTO.name);
            person.setTitle(personDTO.title);
            person.setRegion(personDTO.region);
            person.setHouse(personDTO.house);
            person.setBirthYear(personDTO.birthYear);
            person.setDeathYear(personDTO.deathYear);
            person.setMonarch(personDTO.isMonarch);
            person.setUnknown(personDTO.isUnknown);

            personMap.put(personDTO.id, person);
            maxId = Math.max(maxId, personDTO.id);
        }
        Person.idCounter = maxId + 1;
        while(tree.members.size() <= maxId) {
            tree.members.add(FamilyTree.fakeman);
        }
        for(Person person : personMap.values()) {
            tree.members.set(person.getId(), person);
        }

        //create relationships
        tree.relationships.clear();
        List<Relationship> relationshipList = new ArrayList<>();
        for(FamilyTreeDTO.RelationshipDTO relDTO : dto.relationships) {
            Person person1 = personMap.get(relDTO.person1Id);
            Person person2 = personMap.get(relDTO.person2Id);
            if(person1 != null && person2 != null) {
                Relationship rel = new Relationship(person1, person2, relDTO.isMarriage);
                relationshipList.add(rel);
            }
        }
        tree.relationships.addAll(relationshipList);

        //set parents
        for(FamilyTreeDTO.PersonDTO personDTO : dto.members) {
            if(personDTO.parentRelationshipId != null &&
                    personDTO.parentRelationshipId >= 0 &&
                    personDTO.parentRelationshipId < relationshipList.size()) {
                Person person = personMap.get(personDTO.id);
                if(person != null) {
                    person.setParents(relationshipList.get(personDTO.parentRelationshipId));
                }
            }
        }

        //load house colors
        tree.houseColors.clear();
        for(Map.Entry<String, String> entry : dto.houseColors.entrySet()) {
            tree.houseColors.put(entry.getKey(), Color.decode(entry.getValue()));
        }

        return tree;
    }
}
