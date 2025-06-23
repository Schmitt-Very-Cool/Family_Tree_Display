package com.nefariousmachine.generate;

import com.nefariousmachine.data.FamilyTree;
import com.nefariousmachine.data.Person;
import com.nefariousmachine.data.Relationship;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

/**
 * Singleton class containing functions related to the creation of the family tree image.
 */
public class TreeImageGenerator {
    public static final int BOX_WIDTH = 200;
    public static final int BOX_HEIGHT = 100;
    public static final int TEXT_SIZE = 50;
    public static final int PADDING = 100;

    public static final Font baseFont = new Font(Font.SERIF, Font.PLAIN, TEXT_SIZE);

    public static final int MAX_ITERATIONS = 24;

    /**
     * The family tree image. Prior to generate() being called, familyTree is null.
     */
    private BufferedImage familyTreeImage;
    private FamilyTree familyTree;
    private final List<RelationshipUnit> units = new ArrayList<>();
    private final Map<Person, RelationshipUnit> personToUnit = new HashMap<>();
    private final Map<RelationshipUnit, List<RelationshipUnit>> parentMap = new HashMap<>(); //Unit -> (List<Child>)
    private final Map<RelationshipUnit, List<RelationshipUnit>> childMap = new HashMap<>(); //Unit -> (List<Parent>)
    private final Map<RelationshipUnit, Point> unitTopLefts = new HashMap<>(); //Unit -> (global x,y)
    private final Map<Integer, List<RelationshipUnit>> unitsByGeneration = new HashMap<>();
    private final Map<Integer, Integer> rowHeights = new HashMap<>();
    private RelationshipUnit[][] order;
    private Map<Person, Point> graph = new HashMap<>();
    private int graphWidth = 0;
    private int graphHeight = 0;
    private final ArrayList<Person> selected = new ArrayList<>();

    public BufferedImage getFamilyTreeImage() {
        return familyTreeImage;
    }

    public FamilyTree getFamilyTree() {
        return familyTree;
    }

    public void setFamilyTree(FamilyTree ft) {
        familyTree = ft;
    }

    /**
     * Generates the graph representing the family tree.
     */
    public void generate() {
        //Assigns each person a generation such that any Person has a higher generation number than their children.
        //TODO: Account for cycles
        if(familyTree == null) {
            return;
        }
        RelationshipUnit.reset();                   //Reset id counter for all RelationshipUnits
        createUnits();                              //O(n*n!), or average O(n^2)
        precomputeMaps();                           //O(n^2)
        System.out.println("parentMap = " + parentMap);
        System.out.println("childMap  = " + childMap);
        assignGenerations();                        //O(n^2)
        computeDimensions();                        //O(n)
        graph.clear();                              //O(n)
        initializeOrderMatrix();                    //O(n*log(n))
        optimizeOrder();                            //O(n^3), average O(n^2*log(n))
        buildGraph();                               //O(n^2)
        compressGraph();
        System.out.println();
    }

    /**
     * Combines networks of Persons in Relationships in the tree into monolithic units, allowing the algorithm to treat
     * the family tree like a Digraph. Persons in no relationships are added to a RelationshipUnit by themselves. Runs
     * in O(m*r + m^2*log(r)), where m is the average size of RelationshipUnits, n is the size of
     * familyTree.getMembers(), and r is the size of FamilyTree.getAllRelationships(). Worst case scenario, m = n and
     * r = n!, so the worst case is O(n*n!), but average case would be closer to O(n^2).
     */
    private void createUnits() {
        units.clear();                              //Reset units list
        Set<Person> visited = new HashSet<>();      //Keeps track of Persons assigned to units already
        for(Person person : familyTree.getMembers()) {      //O(n)
            if(person.getId() == -1) continue;
            if(visited.contains(person)) continue;  //Skip people already assigned (O(1))
            Set<Person> cluster = new HashSet<>();
            Queue<Person> queue = new LinkedList<>();
            queue.add(person);
            //Add everyone in the network to the cluster
            while(!queue.isEmpty()) {
                Person current = queue.poll();
                if(!cluster.add(current)) continue;
                for(Person relation : familyTree.getRelationships(current.getId())) {
                    if(relation != null && !cluster.contains(relation)) {
                        queue.add(relation);
                    }
                }
            }
            if(visited.containsAll(cluster)) continue;      //Skip if we already visited this cluster
            visited.addAll(cluster);                        //Add cluster to visited
            //Make new RelationshipUnit with everyone in cluster
            RelationshipUnit unit = new RelationshipUnit();
            unit.people = new ArrayList<>(cluster);
            unit.layout(familyTree); //if unit.people <= 2, O(1), else O(m*r + m^2*log(r)) -> O(n*n!), or typically O(n^2)
            //Update maps
            for(Person p : cluster) {   //O(m)
                personToUnit.put(p, unit);
            }
            units.add(unit);
        }
    }

    /**
     * Precomputes parentMap and childMap for quick reference later. Runs in O(n^2) time.
     */
    private void precomputeMaps() {
        //Precalculate Parent Units
        parentMap.clear(); //O(u)
        for(RelationshipUnit unit : units) { //O(n*u*m), which is O(n^2)
            Set<RelationshipUnit> parents = new LinkedHashSet<>();
            for(Person person : unit.people) { //O(m*n)
                if(person.getParents() == null) {
                    continue;
                }
                for(Person parent : person.getParents().getPeople()) { //O(n)
                    if(personToUnit.containsKey(parent)) {
                        parents.add(personToUnit.get(parent));
                    }
                }
            }
            parentMap.put(unit, new ArrayList<>(parents));
        }

        //Precompute childMap too
        childMap.clear();
        for(var entry : parentMap.entrySet()) { //O(u)
            RelationshipUnit child = entry.getKey();
            for(RelationshipUnit parent : entry.getValue()) {
                childMap.computeIfAbsent(parent, k -> new ArrayList<>()).add(child);
            }
        }
    }

    /**
     * Uses a modified version of Kahn's algorithm to assign each RelationshipUnit a generation on the tree. Currently
     * does not protect against cycles. Runs in O(u^2), where u is the number of RelationshipUnits in the tree. Since
     * u <= n, the number of members in the tree, also runs in O(n^2)
     */
    private void assignGenerations() {
        //Compute inDegree from childMap
        Map<RelationshipUnit, Integer> inDegree = new HashMap<>();
        for(var entry : childMap.entrySet()) { //O(u^2)
            for(RelationshipUnit child : entry.getValue()) { //O(u)
                inDegree.merge(child, 1, Integer::sum);
            }
        }
        for(RelationshipUnit u : units) { //O(u)
            inDegree.putIfAbsent(u, 0);
        }

        System.out.println("\tinDegree before queue: " + inDegree);

        //Queue all units with inDegree 0
        Queue<RelationshipUnit> queue = new LinkedList<>();
        for(var entry : inDegree.entrySet()) { //O(u)
            if(entry.getValue() == 0) {
                queue.add(entry.getKey());
            }
        }
        System.out.println("\tinitial queue: " + queue);

        //Kahn’s algorithm: pop parent, assign its children
        while(!queue.isEmpty()) { //O(u^2)
            RelationshipUnit u = queue.poll();
            System.out.println("\tvisiting unit " + u.id);

            //assign generation = max(parent.generation + 1)
            int gen = 0;
            for(RelationshipUnit p : parentMap.getOrDefault(u, Collections.emptyList())) { //O(u)
                System.out.println("\t\thas parent " + p.id + " with gen=" + p.generation);
                gen = Math.max(gen, p.generation + 1);
            }
            u.generation = gen;
            System.out.println("\t\tsetting unit " + u + " to generation " + gen);

            //decrement children
            for(RelationshipUnit c : childMap.getOrDefault(u, Collections.emptyList())) { //O(u)
                inDegree.put(c, inDegree.get(c) - 1);
                System.out.println("\t\tchild " + c + " new inDegree=" + inDegree.get(c));
                if(inDegree.get(c) == 0) {
                    queue.add(c);
                    System.out.println("\t\tenqueue child " + c);
                }
            }
        }

        //Fallback
        for(RelationshipUnit u : units) { //O(u)
            if(u.generation < 0) {
                System.out.println("\tfallback unit " + u.id + " to gen 0");
                u.generation = 0;
            }
        }

        System.out.println("Assigned Generations:");
        for(RelationshipUnit u : units) { //O(u)
            System.out.printf("\tUnit %d -> generation %d%n", u.id, u.generation);
        }
    }

    /**
     * Computes the height and width of the order matrix. The height is the number of generations,
     * and the width is the amount of units in the generation with the most units. Runs in O(u), therefore runs in O(n).
     */
    private void computeDimensions(){
        //Compute order matrix dimensions
        unitsByGeneration.clear();
        System.out.println("Computing Dimensions");
        int height = 0;
        int maxWidth = 0;
        for(RelationshipUnit unit : units) { //O(u)
            unitsByGeneration.computeIfAbsent(unit.generation, k -> new ArrayList<>()).add(unit);
            height = Math.max(height, unit.generation);
            System.out.println("After Unit " + unit.id + ", height = " + height);
        }
        for(RelationshipUnit unit : units) { //O(u)
            System.out.println("Unit " + unit.id + ": " + unit.people);
        }
        for(var entry : unitsByGeneration.entrySet()) { //O(u) (typically less than O(sqrt(u))
            maxWidth = Math.max(maxWidth, entry.getValue().size());
        }
        graphWidth = maxWidth;
        graphHeight = height + 1;
    }

    /**
     * Initializes and fills order matrix with an initial layout for all the units. The purpose of this is to get a
     * good-enough starting point for optimizeOrder() to have a greater optimizing effect. Runs in O(u*log(u)), which
     * is O(n*log(n)).
     */
    private void initializeOrderMatrix() {
        order = new RelationshipUnit[graphHeight][graphWidth]; //O(u^2)
        for(int r = 0; r < graphHeight; r++) { //O(u^2)
            Arrays.fill(order[r], RelationshipUnit.fakeunit);
        }
        for(int r = 0; r < graphHeight; r++) { //O(u*log(u))
            Map<RelationshipUnit, Integer> candidates = new HashMap<>();
            for(RelationshipUnit unit : unitsByGeneration.get(r)) {
                var parents = parentMap.getOrDefault(unit, null);
                if(parents == null) {
                    candidates.put(unit, Integer.MAX_VALUE);
                } else {
                    ArrayList<Integer> positions = new ArrayList<>();
                    for(RelationshipUnit parent : parents) { //O(u)
                        for (int i = 0; i < order[r - 1].length; i++) {
                            if(order[r - 1][i] == parent) {
                                positions.add(i);
                                break;
                            }
                        }
                    }
                    Collections.sort(positions); //O(log(u))
                    int mid = positions.size()/2;
                    if(positions.isEmpty()) candidates.put(unit, Integer.MAX_VALUE);
                    else if(positions.size() % 2 == 1) candidates.put(unit, positions.get(mid));
                    else candidates.put(unit, (positions.get(mid-1) + positions.get(mid)) / 2);
                }
            }
            List<Map.Entry<RelationshipUnit, Integer>> candidatesList = new ArrayList<>(candidates.entrySet());
            candidatesList.sort(Comparator.comparingInt(Map.Entry::getValue));
            for(int i = 0; i < candidatesList.size(); i++) {
                order[r][i] = candidatesList.get(i).getKey();
            }
        }
    }

    /**
     * Uses heuristic algorithms to improve the overall layout of order by reducing amount of line crossings. Algorithm
     * is similar to other Sugiyama layout algorithms, specifically derived from this
     * <a href="https://graphviz.org/documentation/TSE93.pdf">this graphviz paper</a>. Runs in O(u^3), which is O(n^3),
     * although average case is closer to O(n^2*log(n)), probably less.
     */
    private void optimizeOrder() {
        //deep clone order (O(u^2))
        RelationshipUnit[][] best = new RelationshipUnit[order.length][];
        for(int i = 0; i < order.length; i++) {
            best[i] = order[i].clone();
        }
        Map<RelationshipUnit, Point> orderMap = buildOrderMap(); //O(u^2)
        for(int i = 0; i < MAX_ITERATIONS; i++) {
            //sort each rank by median of parent x locations
            //Every iteration, swap between going top-bottom and bottom-top
            int iteBegin, iteEnd, iteStep;
            if(i % 2 == 1) {
                iteBegin = 1;
                iteEnd = graphHeight;
                iteStep = 1;
            } else {
                iteBegin = graphHeight - 2;
                iteEnd = -1;
                iteStep = -1;
            }
            for(int r = iteBegin; r != iteEnd; r += iteStep) {
                Map<RelationshipUnit, Integer> medians = new HashMap<>();
                for(int k = 0; k < order[r].length; k++) { //O(u)
                    RelationshipUnit v = order[r][k];
                    if(v.id == -1) continue;
                    //Find median of columns of parents
                    List<RelationshipUnit> parents = parentMap.getOrDefault(v, Collections.emptyList());
                    ArrayList<Integer> positions = new ArrayList<>();
                    for(RelationshipUnit parent : parents) {
                        Point pos = orderMap.get(parent);
                        if(pos != null) {
                            positions.add(pos.x);
                        }
                    }
                    int m = positions.size() / 2;
                    if(positions.isEmpty()) {
                        medians.put(v, Integer.MAX_VALUE);
                    } else if (positions.size() % 2 == 1) {
                        medians.put(v, positions.get(m));
                    } else if (positions.size() == 2) {
                        medians.put(v, (positions.get(0)+positions.get(1))/2);
                    } else {
                        int left = positions.get(m-1) - positions.get(0);
                        int right = positions.get(positions.size()-1) - positions.get(m);
                        medians.put(v, (positions.get(m-1)*right + positions.get(m)*left) / (left + right));
                    }
                }
                //Sort medians and rearrange order
                List<Map.Entry<RelationshipUnit, Integer>> mediansList = new ArrayList<>(medians.entrySet());
                mediansList.sort(Comparator.comparingInt(Map.Entry::getValue));
                for(int j = 0; j < mediansList.size(); j++) {
                    order[r][j] = mediansList.get(j).getKey();
                }
                orderMap = buildOrderMap(); //O(u^2)
            }

            //Transpose
            boolean improved = true;
            while(improved) { //O(n^2) I think?
                improved = false;
                for(int r = 0; r < order.length; r++) {
                    for(int j = 0; j < order[r].length - 1; j++) {
                        RelationshipUnit v = order[r][j];
                        RelationshipUnit w = order[r][j + 1];
                        if(v.id < 0 || w.id < 0) continue;
                        if(crossing(v, w, orderMap) > crossing(w, v, orderMap)) {
                            improved = true;
                            order[r][j] = w;
                            order[r][j + 1] = v;
                            orderMap.put(v, new Point(j + 1, r));
                            orderMap.put(w, new Point(j, r));
                        }
                    }
                }
            }
            if(crossing(order) < crossing(best)) {
                //Deep copy order to best
                for(int j = 0; j < order.length; j++) {
                    best[j] = order[j].clone();
                }
            }
        }
        order = best;
        System.out.println("Final order:");
        for(RelationshipUnit[] r : order) {
            System.out.print("\t");
            for(RelationshipUnit i : r) {
                System.out.print(i.id + " ");
            }
            System.out.println();
        }
    }

    /**
     * Constructs a map of units to their positions in order[][]. Runs in O(n^2).
     * @return map of units to their positions in order[][].
     */
    private Map<RelationshipUnit,Point> buildOrderMap() {
        Map<RelationshipUnit, Point> orderMap = new HashMap<>();
        for(int r = 0; r < order.length; r++) {
            for(int c = 0; c < order[r].length; c++) {
                RelationshipUnit unit = order[r][c];
                if(unit.id >= 0) {
                    orderMap.put(unit, new Point(r, c));
                }
            }
        }
        return orderMap;
    }

    /**
     * Initializes and populates graph based on the order of RelationshipUnits in the order matrix as well as the
     * internal layouts of each RelationshipUnit. The resulting graph preserves both, but can contain lots of excess
     * space. Runs in O(u*n), which is O(n^2).
     */
    private void buildGraph() {
        graph = new HashMap<>();
        //Precompute row heights and col widths based on units in the order matrix
        Map<Integer, Integer> colWidths = new HashMap<>();
        rowHeights.clear();
        int runningHeight = 0;
        for(int r = 0; r < order.length; r++) { //O(u^2)
            int maxHeight = 0;
            for(int c = 0; c < order[0].length; c++) { //O(u)
                RelationshipUnit unit = order[r][c];
                if(unit.id >= 0) {
                    maxHeight = Math.max(maxHeight, unit.graphHeight);
                }
            }
            rowHeights.put(r, runningHeight);
            runningHeight += maxHeight;
        }
        int runningWidth = 0;
        for(int c = 0; c < order[0].length; c++) { //O(u^2)
            int maxWidth = 0;
            for (RelationshipUnit[] relationshipUnits : order) { //O(u)
                RelationshipUnit unit = relationshipUnits[c];
                if (unit.id >= 0) {
                    maxWidth = Math.max(maxWidth, unit.graphWidth);
                }
            }
            colWidths.put(c, runningWidth);
            runningWidth += maxWidth;
        }

        //Repurpose graphWidth and graphHeight now that we're done with order.
        graphWidth = 0;
        graphHeight = 0;
        // Place people using each unit’s local layout
        for(int r = 0; r < order.length; r++) { //O(u*n)
            for(int c = 0; c < order[0].length; c++) { //O(u*m) -> O(n)
                RelationshipUnit unit = order[r][c];
                if(unit.id < 0) continue;
                int anchorX = colWidths.get(c);
                int anchorY = rowHeights.get(r);
                for(Map.Entry<Person, Point> entry : unit.graph.entrySet()) { //O(m)
                    Person person = entry.getKey();
                    Point local = entry.getValue();
                    int globalX = anchorX + local.x;
                    int globalY = anchorY + local.y;
                    graph.put(person, new Point(globalX, globalY));
                    graphWidth = Math.max(graphWidth, globalX + 1);
                    graphHeight = Math.max(graphHeight, globalY + 1);
                }
                unitTopLefts.put(unit, new Point(anchorX, anchorY));
            }
        }
    }

    /**
     * Compresses the graph layout by removing unnecessary whitespace through horizontal sliding to center children
     * under parents and vertical compression. Maintains unit order and internal layouts while optimizing positioning.
     * Runs in O(n^2).
     */
    private void compressGraph() {
        System.out.println("Compress Graph:");

        //top-down horizontal pass that centers children under parents and otherwise moves people left.
        System.out.println(" * Pass 1");
        for(int gen = 0; gen < graphHeight; gen++) {
            List<RelationshipUnit> rowUnits = unitsByGeneration.get(gen);
            if(rowUnits == null || rowUnits.isEmpty()) continue;
            //Order units by their placement in order[][]
            List<RelationshipUnit> orderedUnits = new ArrayList<>();
            for(int c = 0; c < order[gen].length; c++) {
                RelationshipUnit unit = order[gen][c];
                if(unit.id >= 0) {
                    orderedUnits.add(unit);
                }
            }
            Map<RelationshipUnit, Integer> idealPositions = new HashMap<>();
            for(RelationshipUnit unit : orderedUnits) {
                int idealX = calculateIdealPositionFromParents(unit);
                idealPositions.put(unit, idealX);
                System.out.println("\tUnit " + unit.id + " ideal position: " + idealX);
            }
            List<List<RelationshipUnit>> blocks = groupIntoBlocks(orderedUnits, idealPositions);
            positionBlocks(blocks, idealPositions);
        }

        //bottom-up horizontal pass that centers parents above children
        System.out.println("Pass 2: Centering parents above children (bottom-up)");
        for(int gen = graphHeight - 1; gen >= 0; gen--) {
            List<RelationshipUnit> rowUnits = unitsByGeneration.get(gen);
            if(rowUnits == null || rowUnits.isEmpty()) continue;
            //again order units using order[][]
            List<RelationshipUnit> orderedUnits = new ArrayList<>();
            for(int c = 0; c < order[gen].length; c++) {
                RelationshipUnit unit = order[gen][c];
                if(unit.id >= 0) {
                    orderedUnits.add(unit);
                }
            }
            Map<RelationshipUnit, Integer> idealPositions = new HashMap<>();
            for(RelationshipUnit unit : orderedUnits) {
                int idealX = calculateIdealPositionFromChildren(unit);
                idealPositions.put(unit, idealX);
                System.out.println("\tUnit " + unit.id + " ideal position from children: " + idealX);
            }
            List<List<RelationshipUnit>> blocks = groupIntoBlocks(orderedUnits, idealPositions);
            positionBlocks(blocks, idealPositions);
        }

        //vertical compression
        System.out.println("Pass 3: Vertical compression");
        for(RelationshipUnit unit : units.stream().sorted(Comparator.comparingInt(u -> u.generation)).toList()) {
            Set<Person> unitMembers = unit.graph.keySet();
            int ceiling = -1;
            for(Person person : unit.people) {
                if(person.getParents() == null) continue;
                for(Person parent : person.getParents().getPeople()) {
                    Point parentPos = graph.get(parent);
                    if(parentPos != null) {
                        ceiling = Math.max(ceiling, parentPos.y);
                    }
                }
            }
            int minShift = Integer.MAX_VALUE;
            for(Person person : unitMembers) {
                Point pos = graph.get(person);
                int shift = 0;
                while((pos.y - shift - 1) > ceiling && !graph.containsValue(new Point(pos.x, pos.y - shift - 1))) {
                    shift++;
                }
                minShift = Math.min(minShift, shift);
            }
            if(minShift > 0 && minShift < Integer.MAX_VALUE) {
                System.out.println("\tShifting unit " + unit.id + " up " + minShift);
                for(Person person : unitMembers) {
                    Point pos = graph.get(person);
                    graph.put(person, new Point(pos.x, pos.y - minShift));
                }
            }
        }

        //recalculate bounds
        graphWidth = graph.values().stream().mapToInt(p -> p.x + 1).max().orElse(0);
        graphHeight = graph.values().stream().mapToInt(p -> p.y + 1).max().orElse(0);
    }

    /**
     * Calculates the ideal X position for a unit based on the center of its parents. Returns 0 if the unit has no
     * parents.
     *
     * @param unit RelationshipUnit whose ideal position is being calculated
     */
    private int calculateIdealPositionFromParents(RelationshipUnit unit) {
        Set<RelationshipUnit> parentUnits = new HashSet<>();
        for(Person person : unit.people) {
            Relationship parents = person.getParents();
            if(parents != null) {
                for(Person parent : parents.getPeople()) {
                    RelationshipUnit parentUnit = personToUnit.get(parent);
                    if(parentUnit != null) {
                        parentUnits.add(parentUnit);
                    }
                }
            }
        }
        if(parentUnits.isEmpty()) {
            return 0;
        }
        int minParentX = Integer.MAX_VALUE;
        int maxParentX = Integer.MIN_VALUE;
        for(RelationshipUnit parentUnit : parentUnits) {
            int unitLeft = unitTopLefts.get(parentUnit).x;
            int unitRight = unitLeft + parentUnit.graphWidth - 1;
            minParentX = Math.min(minParentX, unitLeft);
            maxParentX = Math.max(maxParentX, unitRight);
        }
        double parentCenter = (minParentX + maxParentX) / 2.0;
        int unitWidth = unit.graphWidth;
        return (int) Math.round(parentCenter - unitWidth / 2.0);
    }

    /**
     * Calculates the ideal X position for a unit based on the center of its children. Returns 0 if the unit has no
     * children.
     *
     * @param unit RelationshipUnit whose ideal position is being calculated
     */
    private int calculateIdealPositionFromChildren(RelationshipUnit unit) {
        Set<RelationshipUnit> childUnits = new HashSet<>();
        List<RelationshipUnit> childUnitsList = childMap.get(unit);
        if(childUnitsList != null) {
            childUnits.addAll(childUnitsList);
        }
        if(childUnits.isEmpty()) {
            return unitTopLefts.get(unit).x;
        }
        int minChildX = Integer.MAX_VALUE;
        int maxChildX = Integer.MIN_VALUE;
        for(RelationshipUnit childUnit : childUnits) {
            int unitLeft = unitTopLefts.get(childUnit).x;
            int unitRight = unitLeft + childUnit.graphWidth - 1;
            minChildX = Math.min(minChildX, unitLeft);
            maxChildX = Math.max(maxChildX, unitRight);
        }
        double childCenter = (minChildX + maxChildX) / 2.0;
        int unitWidth = unit.graphWidth;
        return (int) Math.round(childCenter - unitWidth / 2.0);
    }

    /**
     * Groups units into blocks that must move together to avoid overlaps.
     * Units that would overlap at their ideal positions form a single block.
     *
     * @param orderedUnits list of RelationshipUnits to be grouped into blocks
     * @param idealPositions map of RelationshipUnits to their ideal x index
     */
    private List<List<RelationshipUnit>> groupIntoBlocks(
            List<RelationshipUnit> orderedUnits, Map<RelationshipUnit, Integer> idealPositions) {
        List<List<RelationshipUnit>> blocks = new ArrayList<>();
        boolean[] assigned = new boolean[orderedUnits.size()];
        for(int i = 0; i < orderedUnits.size(); i++) {
            if(assigned[i]) continue;
            List<RelationshipUnit> block = new ArrayList<>();
            block.add(orderedUnits.get(i));
            assigned[i] = true;
            boolean expanded;
            do {
                expanded = false;
                for(int j = 0; j < orderedUnits.size(); j++) {
                    if(assigned[j]) continue;

                    RelationshipUnit candidate = orderedUnits.get(j);
                    if(wouldBlockOverlap(block, candidate, idealPositions)) {
                        block.add(candidate);
                        assigned[j] = true;
                        expanded = true;
                    }
                }
            } while(expanded);
            block.sort(Comparator.comparingInt(orderedUnits::indexOf));
            blocks.add(block);
        }
        return blocks;
    }

    /**
     * Checks if a unit would overlap with another unit in the block at ideal positions.
     *
     * @param block the list of RelationshipUnits in the block
     * @param candidate the unit being checked
     * @param idealPositions Map of ideal x index for each unit
     */
    private boolean wouldBlockOverlap(List<RelationshipUnit> block, RelationshipUnit candidate,
            Map<RelationshipUnit, Integer> idealPositions) {
        int candidateStart = idealPositions.get(candidate);
        int candidateEnd = candidateStart + candidate.graphWidth - 1;
        for(RelationshipUnit unit : block) {
            int unitStart = idealPositions.get(unit);
            int unitEnd = unitStart + unit.graphWidth - 1;
            if(!(candidateEnd < unitStart || candidateStart > unitEnd)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Positions each block of RelationshipUnit in its row according to idealPositions while avoiding overlaps.
     *
     * @param blocks row of RelationshipUnits to position
     * @param idealPositions map of RelationshipUnits to their ideal x index
     */
    private void positionBlocks(
            List<List<RelationshipUnit>> blocks,
            Map<RelationshipUnit, Integer> idealPositions) {
        blocks.sort((a, b) -> {
            int aMin = a.stream().mapToInt(idealPositions::get).min().orElse(0);
            int bMin = b.stream().mapToInt(idealPositions::get).min().orElse(0);
            return Integer.compare(aMin, bMin);
        });
        int nextAvailableX = 0;
        for(List<RelationshipUnit> block : blocks) {
            int blockIdealX = calculateBlockIdealPosition(block, idealPositions);
            int currentX = Math.max(blockIdealX, nextAvailableX);
            for(RelationshipUnit unit : block) {
                int shift = currentX - unitTopLefts.get(unit).x;
                if(shift != 0) {
                    System.out.println("\tShifting unit " + unit.id + " by " + shift);
                    shiftUnit(unit, shift);
                }
                currentX += unit.graphWidth;
            }
            nextAvailableX = currentX;
        }
    }

    /**
     * Calculates the ideal position for a block based on its units' ideal positions.
     *
     * @param block list of RelationshipUnits grouped together
     * @param idealPositions map of RelationshipUnits to their ideal x index
     */
    private int calculateBlockIdealPosition(List<RelationshipUnit> block,
            Map<RelationshipUnit, Integer> idealPositions) {
        if(block.size() == 1) {
            return idealPositions.get(block.get(0));
        }
        int totalWeight = 0;
        int weightedSum = 0;
        for(RelationshipUnit unit : block) {
            int weight = getParentCount(unit) + 1; //+1 to avoid zero weight
            totalWeight += weight;
            weightedSum += idealPositions.get(unit) * weight;
        }
        return weightedSum / totalWeight;
    }

    /**
     * Gets the number of parents for all people in a unit. This can be different to parentMap.get(unit).size() if
     * multiple people in the unit have parents from the same unit.
     *
     * @param unit unit whose parents are being counted
     */
    private int getParentCount(RelationshipUnit unit) {
        int count = 0;
        for(Person person : unit.people) {
            if(person.getParents() != null) {
                count += person.getParents().getPeople().length;
            }
        }
        return count;
    }

    /**
     * Shifts all people in a unit horizontally by the given amount.
     *
     * @param unit RelationshipUnit to be shifted
     * @param shiftX number of indices to be shifted
     */
    private void shiftUnit(RelationshipUnit unit, int shiftX) {
        for(Person person : unit.graph.keySet()) {
            graph.computeIfPresent(person, (k, oldPos) -> new Point(oldPos.x + shiftX, oldPos.y));
        }
        //Update unit top-left
        if(unitTopLefts.containsKey(unit)) {
            unitTopLefts.computeIfPresent(unit, (k, oldTopLeft) -> new Point(oldTopLeft.x + shiftX, oldTopLeft.y));
        }
    }

    /**
     * Computes the number of edge crossings that would result if unit v were placed before unit w in the same row of
     * the graph layout.
     * <p>
     * Specifically, this method counts how many child or parent edges from v would cross over edges from w in adjacent
     * layers (above and below), based on their positions in orderMap.
     *
     * @param v one RelationshipUnit being measured
     * @param w other RelationshipUnit being measured
     * @param orderMap map of units to their coordinates in the order matrix
     * @return Amount of intersections between lines connecting to v and lines connecting to w.
     */
    private int crossing(RelationshipUnit v, RelationshipUnit w, Map<RelationshipUnit, Point> orderMap) {
        Point vPos = orderMap.get(v);
        if(vPos == null) return 0;
        //If v is on the first or last row, we can't safely access parents or children
        if(vPos.y <= 0 || vPos.y >= order.length - 1) {
            return 0;
        }
        int crossCount = 0;

        //Children
        List<RelationshipUnit> vChildren = childMap.getOrDefault(v, Collections.emptyList());
        List<RelationshipUnit> wChildren = childMap.getOrDefault(w, Collections.emptyList());
        for(RelationshipUnit vc : vChildren) {
            Point vcPos = orderMap.get(vc);
            if(vcPos == null) continue;
            for(RelationshipUnit wc : wChildren) {
                Point wcPos = orderMap.get(wc);
                if(wcPos == null) continue;
                if(vcPos.x > wcPos.x) crossCount++;
            }
        }

        //Parents
        List<RelationshipUnit> vParents = parentMap.getOrDefault(v, Collections.emptyList());
        List<RelationshipUnit> wParents = parentMap.getOrDefault(w, Collections.emptyList());
        for(RelationshipUnit vp : vParents) {
            Point vpPos = orderMap.get(vp);
            if(vpPos == null) continue;
            for(RelationshipUnit wp : wParents) {
                Point wpPos = orderMap.get(wp);
                if (wpPos == null) continue;
                if (vpPos.x > wpPos.x) crossCount++;
            }
        }
        return crossCount;
    }

    /**
     * Counts most line intersections in the entire order matrix. Runs O(n^3).
     *
     * @param order matrix of RelationshipUnits being benchmarked.
     * @return the approximate number of line intersections in the entire order matrix
     */
    private int crossing(RelationshipUnit[][] order) {
        int total = 0;
        for(int r = 0; r < order.length - 1; r++) {
            RelationshipUnit[] currRow = order[r];
            RelationshipUnit[] nextRow = order[r + 1];
            //Precompute a map: unit ID -> column index for faster lookup
            Map<RelationshipUnit, Integer> nextRowIndexMap = new HashMap<>();
            for(int i = 0; i < nextRow.length; i++) {
                nextRowIndexMap.put(nextRow[i], i);
            }
            //Compare all pairs of nodes in the current row
            for(int i = 0; i < currRow.length; i++) {
                RelationshipUnit v = currRow[i];
                List<RelationshipUnit> vChildren = childMap.getOrDefault(v, Collections.emptyList());
                for(int j = i + 1; j < currRow.length; j++) {
                    RelationshipUnit w = currRow[j];
                    List<RelationshipUnit> wChildren = childMap.getOrDefault(w, Collections.emptyList());
                    for(RelationshipUnit vc : vChildren) {
                        Integer vcCol = nextRowIndexMap.get(vc);
                        if (vcCol == null) continue;
                        for(RelationshipUnit wc : wChildren) {
                            Integer wcCol = nextRowIndexMap.get(wc);
                            if (wcCol == null) continue;
                            if (vcCol > wcCol) total++;
                        }
                    }
                }
            }
        }
        return total;
    }

    /**
     * Draws the family tree to familyTreeImage based on the current state of the graph. generate() must have been
     * called at least once so that graph is not null.
     */
    public void draw() {
        if(graph == null || familyTree == null) {
            return;
        }
        int image_width = BOX_WIDTH * graphWidth + PADDING * (graphWidth + 1);
        int image_height = BOX_HEIGHT * graphHeight + PADDING * (graphHeight + 1);
        familyTreeImage = new BufferedImage(image_width, image_height, BufferedImage.TYPE_INT_ARGB);
        //Initialize Image
        Graphics2D g2d = familyTreeImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setColor(ColorManager.getBGColor());
        g2d.fillRect(0, 0, image_width, image_height);

        //Draw Child Lines

        //Draw Relationship Lines
        BasicStroke thick = new BasicStroke(6);
        BasicStroke thin = new BasicStroke(2);
        BasicStroke dashed = new BasicStroke(2, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_BEVEL, 0, new float[]{10}, 5);
        for(Relationship r : familyTree.getAllRelationships()) {
            Point personOneCoords = new Point(graph.get(r.getPeople()[0]));
            Point personTwoCoords = new Point(graph.get(r.getPeople()[1]));

            personOneCoords.x = PADDING + personOneCoords.x * (BOX_WIDTH + PADDING) + BOX_WIDTH / 2;
            personOneCoords.y = PADDING + personOneCoords.y * (BOX_HEIGHT + PADDING) + BOX_HEIGHT / 2;
            personTwoCoords.x = PADDING + personTwoCoords.x * (BOX_WIDTH + PADDING) + BOX_WIDTH / 2;
            personTwoCoords.y = PADDING + personTwoCoords.y * (BOX_HEIGHT + PADDING) + BOX_HEIGHT / 2;

            if(r.isMarriage) {
                g2d.setStroke(thick);
                g2d.setColor(ColorManager.getDefaultPrimary());
                g2d.drawLine(personOneCoords.x, personOneCoords.y, personTwoCoords.x, personTwoCoords.y);

                g2d.setStroke(thin);
                g2d.setColor(ColorManager.getBGColor());
                g2d.drawLine(personOneCoords.x, personOneCoords.y, personTwoCoords.x, personTwoCoords.y);
            } else {
                g2d.setStroke(dashed);
                g2d.setColor(ColorManager.getDefaultPrimary());
                g2d.drawLine(personOneCoords.x, personOneCoords.y, personTwoCoords.x, personTwoCoords.y);
            }
        }

        //Draw Boxes
        g2d.setStroke(new BasicStroke(2));
        g2d.setColor(Color.BLACK);

        for(var member : graph.entrySet() ) {
            int x = member.getValue().x;
            int y = member.getValue().y;
            int box_x = PADDING + x * (BOX_WIDTH + PADDING);
            int box_y = PADDING + y * (BOX_HEIGHT + PADDING);
            g2d.setColor(Color.WHITE);
            g2d.fillRect(box_x, box_y, BOX_WIDTH, BOX_HEIGHT);
            if(selected.contains(member.getKey())) {
                g2d.setColor(Color.RED);
            } else {
                g2d.setColor(Color.BLACK);
            }
            g2d.drawRect(box_x, box_y, BOX_WIDTH, BOX_HEIGHT);
            writeBoxInfo(g2d, box_x, box_y, member.getKey().toString());
        }
    }

    /**
     * Draws the provided string into a box of size (BOX_WIDTH, BOX_HEIGHT), at location (x,y) to the provided
     * graphics. The size of the font is TEXT_SIZE by default, but will be made smaller to fit the box. A margin of 2px
     * is ensured on the left, right, top, and bottom.
     *
     * @param g2d graphics on which to draw str.
     * @param x x-coordinate of the top-left of the box.
     * @param y y-coordinate of the top-left of the box.
     * @param str string to be drawn.
     */
    private void writeBoxInfo(Graphics2D g2d, int x, int y, String str) {

        Font font = new Font(baseFont.getName(), baseFont.getStyle(), baseFont.getSize());
        g2d.setFont(font);

        while(true) {
            FontMetrics metrics = g2d.getFontMetrics(font);
            int lineHeight = metrics.getHeight();
            int maxLineWidth = BOX_WIDTH - 4;
            int maxBoxHeight = BOX_HEIGHT - 4;

            ArrayList<String> wrappedLines = new ArrayList<>();
            String[] lines = str.split("\\n");

            for (String line : lines) {
                StringBuilder currentLine = new StringBuilder();
                for (String word : line.split(" ")) {
                    String testLine = currentLine.isEmpty() ? word : currentLine + " " + word;
                    if (metrics.stringWidth(testLine) > maxLineWidth) {
                        if (!currentLine.isEmpty()) {
                            wrappedLines.add(currentLine.toString());
                        }
                        currentLine = new StringBuilder(word);
                    } else {
                        currentLine = new StringBuilder(testLine);
                    }
                }
                if (!currentLine.isEmpty()) {
                    wrappedLines.add(currentLine.toString());
                }
            }

            int totalTextHeight = wrappedLines.size() * lineHeight;
            if(totalTextHeight <= maxBoxHeight) {
                int startY = y + (BOX_HEIGHT - totalTextHeight) / 2 + metrics.getAscent();
                for(int i = 0; i < wrappedLines.size(); i++) {
                    String line = wrappedLines.get(i);
                    int lineWidth = metrics.stringWidth(line);
                    int startX = x + (BOX_WIDTH - lineWidth) / 2;
                    g2d.drawString(line, startX, startY + i * lineHeight);
                }
                return;
            }

            //If text still doesn't fit, reduce font size.
            font = font.deriveFont((float) font.getSize() - 1);
            g2d.setFont(font);
        }
    }

    /**
     * Adds person whose box contains the provided coordinates to the selected arraylist. If no box is at the
     * coordinates or the coordinates are out of bounds, the selected arraylist is cleared. If the person whose
     * box contains those coordinates is already in the selected arraylist, it is instead removed.
     *
     * @param x x coordinate to check
     * @param y y coordinate to check
     */
    public void select(int x, int y) {
        if (familyTreeImage == null || x < 0 || y < 0 ||
                x >= familyTreeImage.getWidth() || y >= familyTreeImage.getHeight() ||
                x % (BOX_WIDTH + PADDING) < PADDING || y % (BOX_HEIGHT + PADDING) < PADDING) {
            selected.clear();
            return;
        }
        int arrayX = x / (BOX_WIDTH + PADDING);
        int arrayY = y / (BOX_HEIGHT + PADDING);

        Person selectedPerson = null;
        for(var entry : graph.entrySet()) {
            if(entry.getValue().x == arrayX && entry.getValue().y == arrayY) {
                selectedPerson = entry.getKey();
                break;
            }
        }
        if(selectedPerson == null) {
            selected.clear();
            return;
        }

        if(selected.contains(selectedPerson)) {
            selected.remove(selectedPerson);
        } else {
            System.out.println("Selected " + selectedPerson.getName());
            selected.add(selectedPerson);
        }
    }

    /**
     * @return array of the selected persons
     */
    public Person[] getSelected() {
        Person[] s = new Person[selected.size()];
        if(selected.isEmpty()) {
            return new Person[0];
        }
        for(int i = 0; i < selected.size(); i++) {
            s[i] = selected.get(i);
        }
        return s;
    }

    /**
     * @return an array of ints representing the ids of the persons selected at this moment
     */
    public int[] getSelectedIds() {
        int[] s = new int[selected.size()];
        for(int i = 0; i < selected.size(); i++) {
            s[i] = selected.get(i).getId();
        }
        return s;
    }
}
