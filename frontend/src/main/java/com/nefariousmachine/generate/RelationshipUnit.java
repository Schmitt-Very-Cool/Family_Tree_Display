package com.nefariousmachine.generate;

import com.nefariousmachine.data.FamilyTree;
import com.nefariousmachine.data.Person;
import com.nefariousmachine.data.Relationship;

import java.awt.*;
import java.util.*;
import java.util.List;

public class RelationshipUnit {
    public static final RelationshipUnit fakeunit = generateFakeunit();

    /**
     * List of people in this RelationshipUnit
     */
    List<Person> people = new ArrayList<>();
    /**
     * Generation of the highest generation member of the unit
     */
    int generation = -1;
    /**
     * Unique id for this unit
     */
    int id;
    /**
     * Layout graph for this RelationshipUnit.
     */
    Map<Person, Point> graph = new HashMap<>();
    int graphWidth = -1;
    int graphHeight = -1;
    private static int nextId = 0;

    public static void reset() {
        nextId = 0;
    }

    /**
     * Constructor. Sets the unique id for this unit.
     */
    RelationshipUnit() {
        id = nextId;
        nextId++;
    }

    private RelationshipUnit(int id_) {
        id = id_;
    }

    private static RelationshipUnit generateFakeunit() {
        return new RelationshipUnit(-1);
    }

    /**
     * Organizes the graph for this RelationshipUnit. Runs O(1) on first run if people.size() is 0, 1, or 2. For larger
     * RelationshipUnits, runs O(m*r + m^2*log(r)), where m is people.size() and r is the number of relationships in the
     * FamilyTree. Since m <= n (the total number of people in the tree), and r <= n factorial, the algorithm is
     * O(n*n!), although in typical trees it's closer to O(n^2).
     */
    public void layout(FamilyTree familyTree) {
        //TODO: Make [AB AC] custom layout instructions 3x1
        graph.clear();              //O(m) on reruns, O(1) on first run
        if(people.isEmpty()) {
            //Nobody in unit.
            return;
        }
        if(people.size() == 1) {
            //Single person.
            graph.put(people.get(0), new Point(0,0));
            graphHeight = 1;
            graphWidth = 1;
            return;
        }
        if(people.size() == 2) {
            //Couple. Order will be revisited later during generation.
            graph.put(people.get(0), new Point(0, 0));
            graph.put(people.get(1), new Point(1, 0));
            graphHeight = 1;
            graphWidth = 2;
            return;
        }
        //Complex layout
        //Get relevant relationships
        System.out.println("Complex Layout: " + people);
        ArrayList<Relationship> relationships = familyTree.getAllRelationships(people); //O(r*m)
        System.out.print("\tgetAllRelationships results: ");
        for(Relationship relationship : relationships) {
            System.out.print("[" + relationship.getPeople()[0].getName() + "+" + relationship.getPeople()[1].getName() + "]");
        }
        System.out.println();
        //Find the root
        Map<Person, Point> numRelationships = new HashMap<>(); //Maps Persons to (# marriages, #non-marriages)
        int mostRelationships = 0;
        ArrayList<Person> rootCandidates = new ArrayList<>();
        for(Person p : people) {    //O(m^2*log(r))
            for(Person q : people) {    //O(m*log(r))
                //TODO: query relationships instead of entire tree
                Relationship r = familyTree.getRelationship(p.getId(), q.getId()); //O(log(r))
                if(r != null) {
                    numRelationships.putIfAbsent(p, new Point(0,0));
                    if(r.isMarriage) {
                        numRelationships.get(p).x++;
                    } else {
                        numRelationships.get(p).y++;
                    }
                } else {
                }
            }
            int n = numRelationships.get(p).x + numRelationships.get(p).y; //O(1)
            if(n == mostRelationships) {
                rootCandidates.add(p);
            } else if(n > mostRelationships) {
                mostRelationships = n;
                rootCandidates.clear(); //O(1) most of the time, O(m) worst case
                rootCandidates.add(p);
            }
        }
        Person root;
        //Choose the largest branch if there's a tie for most relationships
        if(rootCandidates.size() > 1) {
            int largestBranch = 0;
            ArrayList<Person> betterRootCandidates = new ArrayList<>();
            for(Person p : rootCandidates) {
                int n = Math.max(numRelationships.get(p).x, numRelationships.get(p).y);
                if(n == largestBranch) {
                    betterRootCandidates.add(p);
                } else if(n > largestBranch) {
                    largestBranch = n;
                    betterRootCandidates.clear();
                    betterRootCandidates.add(p);
                }
            }
            //If there's still a tie, just choose the lowest id
            root = betterRootCandidates.get(0);
            if(betterRootCandidates.size() > 1) {
                for(Person n : betterRootCandidates) {
                    if(n.getId() < root.getId()) root = n;
                }
            }
        } else {
            root = rootCandidates.get(0);
        }
        System.out.println("\tRoot: " + root);

        //Build fast lookup of relationships by person
        Map<Person, List<Relationship>> relMap = new HashMap<>();
        for(Relationship r : relationships) { //O(r)
            for(Person p : r.getPeople()) {
                relMap.computeIfAbsent(p, k -> new ArrayList<>()).add(r);
            }
        }

        Set<Person> visited = new HashSet<>();
        Queue<Person> queue = new LinkedList<>();
        queue.add(root);
        visited.add(root);
        Map<Person, List<Person>> marriages = new HashMap<>();
        Map<Person, List<Person>> nonMarriages = new HashMap<>();
        while(!queue.isEmpty()) { //O(m*m)
            Person current = queue.poll();
            System.out.println("\tVisiting Person: " + current.getName());
            marriages.put(current, new ArrayList<>());
            nonMarriages.put(current, new ArrayList<>());
            for(Relationship r : relMap.getOrDefault(current, List.of())) { //O(m)
                Person[] pair = r.getPeople();
                Person other = (pair[0] == current) ? pair[1] : pair[0];
                if(!visited.add(other)) continue;
                if(r.isMarriage) {
                    marriages.get(current).add(other); //O(m)
                    System.out.println("\t\tAdded " + other.getName() + " to " + current.getName() + "'s marriages.");
                } else {
                    nonMarriages.get(current).add(other); //O(m)
                    System.out.println("\t\tAdded " + other.getName() + " to " + current.getName() + "'s non-marriages.");
                }
                queue.add(other);
            }
        }

        //Write to graph
        Set<Point> occupied = new HashSet<>();
        queue = new LinkedList<>(); //new queue
        //Place root at (0,0)
        graph.put(root, new Point(0, 0));
        occupied.add(new Point(0, 0));
        queue.add(root);
        //Track bounds to size the graph later
        int minX = 0, maxX = 0, maxY = 0;
        while(!queue.isEmpty()) { //O(m)
            Person current = queue.poll();
            Point currPos = graph.get(current);
            int baseX = currPos.x;
            int baseY = currPos.y;
            //Place marriages to the right
            List<Person> spouses = marriages.getOrDefault(current, new ArrayList<>());
            if(!spouses.isEmpty()) { //Since everybody gets placed once, no effect on asymptotic time complexity
                int startY = (spouses.size() == 1) ? baseY : baseY + 1;
                for(int i = 0; i < spouses.size(); i++) {
                    Person spouse = spouses.get(i);
                    if(graph.containsKey(spouse)) continue;
                    int x = baseX + i + 1;
                    int y = startY;
                    Point pos = new Point(x, y);
                    while(occupied.contains(pos)) {
                        x++;
                        pos = new Point(x, y);
                    }
                    graph.put(spouse, pos);
                    occupied.add(pos);
                    queue.add(spouse);
                    maxX = Math.max(maxX, x);
                    maxY = Math.max(maxY, y);
                }
            }
            //Place non-marriages to the left
            List<Person> others = nonMarriages.getOrDefault(current, new ArrayList<>());
            if(!others.isEmpty()) {
                int startY = (others.size() == 1) ? baseY : baseY + 1;
                for(int i = 0; i < others.size(); i++) {
                    Person other = others.get(i);
                    if(graph.containsKey(other)) continue;
                    int x = baseX - (i + 1);
                    int y = startY;
                    Point pos = new Point(x, y);
                    while(occupied.contains(pos)) {
                        x--;
                        pos = new Point(x, y);
                    }
                    graph.put(other, pos);
                    occupied.add(pos);
                    queue.add(other);
                    minX = Math.min(minX, x);
                    maxY = Math.max(maxY, y);
                }
            }
        }

        //Normalize coordinates so top left is (0,0)
        System.out.println("\tLayout:");
        graphWidth = maxX - minX + 1;
        graphHeight = maxY + 1;
        for(Map.Entry<Person, Point> entry : graph.entrySet()) {
            Person person = entry.getKey();
            Point p = entry.getValue();
            p.x -= minX;
            graph.put(person, p);
            System.out.println("\t\t" + person.getName() + ": (" + p.x + "," + p.y + ")");
        }
    }
}
