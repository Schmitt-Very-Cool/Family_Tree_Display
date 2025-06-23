package com.nefariousmachine.data;

import java.util.List;
import java.util.Map;

public class FamilyTreeDTO {
    public List<PersonDTO> members;
    public List<RelationshipDTO> relationships;
    public Map<String, String> houseColors; //Store colors as hex strings

    public static class PersonDTO {
        public int id;
        public String name;
        public String title;
        public String region;
        public String house;
        public String birthYear;
        public String deathYear;
        public boolean isMonarch;
        public boolean isUnknown;
        public Integer parentRelationshipId;
    }

    public static class RelationshipDTO {
        public int person1Id;
        public int person2Id;
        public boolean isMarriage;
    }
}