package student;

import game.EscapeState;
import game.ExplorationState;

import java.util.*;
import java.util.concurrent.SynchronousQueue;

import game.Node;
import game.NodeStatus;

public class Explorer {

    /**
     * Explore the cavern, trying to find the orb in as few steps as possible.
     * Once you find the orb, you must return from the function in order to pick
     * it up. If you continue to move after finding the orb rather
     * than returning, it will not count.
     * If you return from this function while not standing on top of the orb,
     * it will count as a failure.
     * <p>
     * There is no limit to how many steps you can take, but you will receive
     * a score bonus multiplier for finding the orb in fewer steps.
     * <p>
     * At every step, you only know your current tile's ID and the ID of all
     * open neighbor tiles, as well as the distance to the orb at each of these tiles
     * (ignoring walls and obstacles).
     * <p>
     * To get information about the current state, use functions
     * getCurrentLocation(),
     * getNeighbours(), and
     * getDistanceToTarget()
     * in ExplorationState.
     * You know you are standing on the orb when getDistanceToTarget() is 0.
     * <p>
     * Use function moveTo(long id) in ExplorationState to move to a neighboring
     * tile by its ID. Doing this will change state to reflect your new position.
     * <p>
     * A suggested first implementation that will always find the orb, but likely won't
     * receive a large bonus multiplier, is a depth-first search.
     *
     * @param state the information available at the current state
     */
    public void explore(ExplorationState state) {
        System.out.println("starting to explore.......");
        List<Long> visitedNodes = new ArrayList<>();
        //add the starting node to visited
        visitedNodes.add(state.getCurrentLocation());

        Collection<NodeStatus> neighboursCollection;
        List<NodeStatus> neighbours;
        List<NodeStatus> unvisitedNeighbours;
        List<NodeStatus> visitedNeighbours;

        //A list of nodes that are to be avoided because they get George stuck
        List<Long> blacklist = new ArrayList<>();

        long currentId;

        while (state.getDistanceToTarget() != 0) {
            System.out.println("starting a  new move........");
            currentId = state.getCurrentLocation();
            System.out.println("The current node is: " + currentId);
            System.out.println("This distance away from the orb is: " + state.getDistanceToTarget());
            neighboursCollection = state.getNeighbours();
            //convert the new neighbours collection into a new list
            neighbours = new ArrayList<>(neighboursCollection);
            System.out.print("current node neighbours: ");
            neighbours.forEach(node -> System.out.print("node id:" + node.getId()+ "..."));
            System.out.println();

            //take out any neighbours that are on the blacklist
            for(int i = 0; i < neighbours.size(); i++){
                if(blacklist.contains(neighbours.get(i).getId())){
                    neighbours.remove(i);
                }
            }
            System.out.print("current node neighbours, excluding blacklisted: ");
            neighbours.forEach(node -> System.out.print("node id:" + node.getId() + "..."));
            System.out.println();
            //sort the list
            Collections.sort(neighbours);
            System.out.println("available neighbours, sorted: ");
            neighbours.forEach(node -> System.out.print("node id:" + node.getId() + "..."));

            //set the visited and unvisited neighbours lists to empty
            System.out.println();
            unvisitedNeighbours = new ArrayList<>();
            visitedNeighbours = new ArrayList<>();

            // split the sorted list into 2 sublists
            splitList(visitedNodes, neighbours, unvisitedNeighbours, visitedNeighbours);
            System.out.println("Here are the 2 neighbour lists for the current node: ");
            System.out.print("unvisited: [ ");
            unvisitedNeighbours.forEach(node -> System.out.print("  node id: " + node.getId()));
            System.out.println(" ]");
            System.out.print("visited: [ ");
            visitedNeighbours.forEach(node -> System.out.print("  node id: " + node.getId()));
            System.out.println(" ]");

            //Decide nextNode
            // now need to check 2 lists and decide next move
            long nextNodeId;
            // move to the first node in unvisitedNeighbours, if such a node exists
            if (unvisitedNeighbours.size() > 0) {
                System.out.println("There is an unvisited neighbour. We move to the one nearest the orb");
                nextNodeId = unvisitedNeighbours.get(0).getId();
            } else {
                System.out.println("All neighbours are visited. We move to the visited neighbour nearest to the orb");
                nextNodeId = visitedNeighbours.get(0).getId();
                //ensure it less often gets stuck in a loop between 2,3 or 4 nodes
                if (nextNodeId == visitedNodes.get(visitedNodes.size() - 1)) {
                    if (visitedNeighbours.size() > 1) {
                        nextNodeId = visitedNeighbours.get(1).getId();
                    }
                }
                // If there is more than one visited node, then rule the secondLast out if there is another neighbour
                if (visitedNodes.size() > 1) {
                    if (nextNodeId == visitedNodes.get(visitedNodes.size() - 2)) {
                        if (visitedNeighbours.size() > 1) {
                            Collections.shuffle(visitedNeighbours);
                            nextNodeId = visitedNeighbours.get(0).getId();

                        }
                    }
                }
                // If there is more than two visited nodes, then rule the thirdlast out if there is another neighbour
                if (visitedNodes.size() > 2) {
                    if (nextNodeId == visitedNodes.get(visitedNodes.size() - 2)) {
                        if (visitedNeighbours.size() > 1) {
                            Collections.shuffle(visitedNeighbours);
                            nextNodeId = visitedNeighbours.get(0).getId(); //ERROR
                        }
                    }
                }

            }
            //check if it is stuck in a loop of 3 or more squares
            if(stuckInLoop(visitedNodes)){
                System.out.println("STUCK IN LOOP!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                //add last 3 nodes to blacklist
                for(int j = 1; j < 3; j++) {
                    if (!visitedNeighbours.isEmpty()) {
                        //add last element of visited nodes to blacklist
                        blacklist.add(visitedNodes.get(visitedNodes.size() - 1));
                        System.out.print("node added to blacklist...");
                    }
                }
                System.out.println();
                // make 8 moves away from Orb and then 4 at random
                System.out.println(" Need to move away...");
                moveAwayFromOrb(state, visitedNodes, 7);
                moveAtRandom(state, visitedNodes, 15);
            }else {
                move(nextNodeId, visitedNodes, state);
            }
        }
    }
    // a method to check if george is repeating his moves in a loop
    private Boolean stuckInLoop(List<Long> visitedNodes){
        Boolean stuckInLoop = false;
        Boolean currentIdRepeat = false;
        Boolean lastIdRepeat = false;
        Boolean secondLastIdRepeat = false;

        // check if currentId has previously been visited
        if (visitedNodes.size() > 1) {
            long currentId = visitedNodes.get(visitedNodes.size()-1);
            for (int i = 0; i < visitedNodes.size() - 2; i++) {
                if (currentId == visitedNodes.get(i))
                    currentIdRepeat = true;
            }
        }
        // check if lastId was previously visited
        if (visitedNodes.size() > 2) {
            long lastId = visitedNodes.get(visitedNodes.size()-2);
            for(int i = 0 ; i < visitedNodes.size()-3; i++ ){
                if(lastId == visitedNodes.get(i))
                    lastIdRepeat = true;
            }
        }
        // check if secondLastId was previously visited
        if (visitedNodes.size() > 3) {
            long lastId = visitedNodes.get(visitedNodes.size()-3);
            for(int i = 0 ; i < visitedNodes.size()-4; i++ ){
                if(lastId == visitedNodes.get(i))
                    secondLastIdRepeat = true;
            }
        }


        //If all three nodes have been previously visited, we must be in a loop
        if(currentIdRepeat){
            if(lastIdRepeat){
                if(secondLastIdRepeat){
                    stuckInLoop = true;
                }

            }
        }
        return stuckInLoop;
    }

    // a method to move to a neighbour, updating visited nodes when move is made
    private void move(long nextNode, List<Long> visitedNodes, ExplorationState state){
        long nextNodeId;
        nextNodeId = nextNode;
        visitedNodes.add(nextNodeId);
        System.out.println("moving to node with id: " + nextNodeId);
        state.moveTo(nextNodeId);
        System.out.println("Check move: node should be: " + nextNodeId + " node is: " + state.getCurrentLocation());
        System.out.println();
    }


    private void moveAwayFromOrb(ExplorationState state , List<Long> visitedNodes, int moves){
        Collection<NodeStatus> neighboursCollection;
        List<NodeStatus> neighbours;
        List<NodeStatus> unvisitedNeighbours;
        List<NodeStatus> visitedNeighbours;


        long nextNodeId;
        for(int i = 0; i < moves; i++){
            neighboursCollection = state.getNeighbours();
            //convert the new neighbours collection into a new list
            neighbours = new ArrayList<>(neighboursCollection);
            Collections.sort(neighbours);
            unvisitedNeighbours = new ArrayList<>();
            visitedNeighbours = new ArrayList<>();

            // split the sorted list into 2 sublists
            splitList(visitedNodes, neighbours, unvisitedNeighbours, visitedNeighbours);
            if (unvisitedNeighbours.size() > 0) {
                System.out.print("step nr: " + (i+1) + " towards orb to unvisited...");
                System.out.println();
                //make next node the one furthest from the orb
                nextNodeId = unvisitedNeighbours.get(0).getId();
            } else {
                System.out.print("step nr: " + (i + 1) + " away from orb...to visited");
                System.out.println();
                //make next node the one furthest from the orb
                nextNodeId = visitedNeighbours.get((visitedNeighbours.size() - 1)).getId();
                //ensure it less often gets stuck in a loop between 2,3 or 4 nodes
                if (nextNodeId == visitedNodes.get(visitedNodes.size() - 1)) {
                    if (visitedNeighbours.size() > 1) {
                        nextNodeId = visitedNeighbours.get(1).getId();
                    }
                }
            }
            move(nextNodeId, visitedNodes, state);
        }
    }
   // method to go back to start if completely stuck. It should then prioritise other unvisited nodes from there
   // private void moveBackToExit(ExplorationState state , List<Long> visitedNodes, int moves){
   // }
    private void moveAtRandom(ExplorationState state , List<Long> visitedNodes, int moves){
        Collection<NodeStatus> neighboursCollection;
        List<NodeStatus> neighbours;
        List<NodeStatus> unvisitedNeighbours;
        List<NodeStatus> visitedNeighbours;

        long nextNodeId;
        for(int i = 0; i < moves; i++){
            neighboursCollection = state.getNeighbours();
            //convert the new neighbours collection into a new list
            neighbours = new ArrayList<>(neighboursCollection);
            Collections.sort(neighbours);
            unvisitedNeighbours = new ArrayList<>();
            visitedNeighbours = new ArrayList<>();

            // split the sorted list into 2 sublists
            NodeStatus tempNode;
            splitList(visitedNodes, neighbours, unvisitedNeighbours, visitedNeighbours);
            if (unvisitedNeighbours.size() > 0) {
                System.out.print("step nr: " + (i+1) + " towards orb unvisited...");
                System.out.println();
                //make next node the one furthest from the orb
                nextNodeId = unvisitedNeighbours.get(0).getId();
            } else {
                System.out.print("step nr: " + (i + 1) + " at random...to visited");
                System.out.println();
                //make next node the one furthest from the orb
                Collections.shuffle(visitedNeighbours);
                nextNodeId = visitedNeighbours.get(0).getId();
                //ensure it less often gets stuck in a loop between 2,3 or 4 nodes
                if (nextNodeId == visitedNodes.get(visitedNodes.size() - 1)) {
                    if (visitedNeighbours.size() > 1) {
                        nextNodeId = visitedNeighbours.get(1).getId();
                    }
                }
            }
            move(nextNodeId, visitedNodes, state);
        }
    }
    private void splitList (List<Long> visitedNodes, List<NodeStatus> neighbours,
                            List<NodeStatus> unvisitedNeighbours, List<NodeStatus> visitedNeighbours){
        NodeStatus tempNode;
        for(int i = 0; i < neighbours.size(); i++){
            tempNode = neighbours.get(i);
            if (visitedNodes.contains(tempNode.getId())) {
                visitedNeighbours.add(tempNode);
            } else{
                unvisitedNeighbours.add(tempNode);
            }
        }
    }

    /**
     * Escape from the cavern before the ceiling collapses, trying to collect as much
     * gold as possible along the way. Your solution must ALWAYS escape before time runs
     * out, and this should be prioritized above collecting gold.
     * <p>
     * You now have access to the entire underlying graph, which can be accessed through EscapeState.
     * getCurrentNode() and getExit() will return you Node objects of interest, and getVertices()
     * will return a collection of all nodes on the graph.
     * <p>
     * Note that time is measured entirely in the number of steps taken, and for each step
     * the time remaining is decremented by the weight of the edge taken. You can use
     * getTimeRemaining() to get the time still remaining, pickUpGold() to pick up any gold
     * on your current tile (this will fail if no such gold exists), and moveTo() to move
     * to a destination node adjacent to your current node.
     * <p>
     * You must return from this function while standing at the exit. Failing to do so before time
     * runs out or returning from the wrong location will be considered a failed run.
     * <p>
     * You will always have enough time to escape using the shortest path from the starting
     * position to the exit, although this will not collect much gold.
     *
     * @param state the information available at the current state
     */
    public void escape(EscapeState state) {
        //TODO: Escape from the cavern before time runs out
        Long startId = state.getCurrentNode().getId();
        Long exitId = state.getExit().getId();
        System.out.println("starting escape.....");
        System.out.println("start nodeId is " + startId);
        System.out.println("exit nodeId is " + exitId);
        System.out.println("time remaining is....." + state.getTimeRemaining());
        System.out.println("There are " + state.getVertices().size() + " nodes.");

        Node start = state.getCurrentNode();
        Node exit = state.getExit();

        //A List of all Nodes
        Collection<Node> nodesCollection= state.getVertices();
        List<Node> nodes = new ArrayList<>(nodesCollection);

        //A Map of the shortest distances from the start to every node, set to 100,000
        Map<Node, Integer> dstToNodes = new HashMap<>();
        nodes.forEach(node -> dstToNodes.put(node , 100000));
        dstToNodes.replace(start, 0);
        System.out.print("The distances to all nodes are: [ ");
        nodes.forEach(node -> System.out.print((dstToNodes.get(node)) + ", "));
        System.out.println(" ]");
        // note: start points to state.getCurrentNode()

        //fill in the maps to get the shortest paths and the corresponding distances
        //A Map of ordered predecessors for each node in path from start to node
        //These are initialized as having no predecessors.
        //Predecessors will be added as and when they are discovered to reduce the shortest distance
        //Also updates dstToNodes Map
        //note: I will eventually change so it only returns path for exit to save time
        Map<Node, List<Node>> pathsToNodes = findPathsToNodes(start, exit, nodes, dstToNodes);

        //Make the journey from the state's currentNode to the exit, along the shortest path
        //NOTE: NO NEED TO PASS IN 2ND PARAMETER - NEED TO DELETE
        makeJourney(state, pathsToNodes.get(exit), pathsToNodes);
        /*System.out.println();
        System.out.print("exit path nodes: [ ");
        for(int i = 0; i < pathsToNodes.size(); i++){
            System.out.print(pathsToNodes.get(exit).get(i).getId());
            System.out.print(",  ");
        }
        System.out.println(" ]");*/

    }

    private Map<Node,List<Node>> findPathsToNodes(Node start, Node exit, List<Node> nodes, Map<Node, Integer> dstToNodes) {
        //create the Map of stacks(ie paths) and set each path to contain it's own node
        Map<Node, List<Node>> pathsToNodes = new HashMap<>();
        for (int i = 0; i < nodes.size(); i++) {
            List<Node> path = new ArrayList<>();
            path.add(nodes.get(i));
            pathsToNodes.put(nodes.get(i), path);
        }
        System.out.print("Paths created for each node, and put into map...");
        // check pathsToNodes constructed properly
        Node exampleNode = nodes.get(0);
        System.out.println("Example- node: " + exampleNode.getId() + "  path: " + (pathsToNodes.get(exampleNode).get(0)).getId());

        //A List of all Nodes for which we don't know shortest dst - all of them to begin with
        List<Node> unopt = nodes;
        //A List of all nodes for which we do know shortest dst
        List<Node> opt = new ArrayList<>();

        // The lists of neighbours to be used for each node
        Collection<Node> neighboursSet;
        List<Node> neighbours;
        List<Node> unoptNeighbours;

        //The node we are optimizing next
        Node currentOptNode = null;

        while(unopt.size() > 0 && ((currentOptNode == null ) || (currentOptNode != exit))) {
                // get another node to optimize - this is 'start' the first time
                currentOptNode = getNextNode(unopt, dstToNodes);

                System.out.println("next node for opt: " + currentOptNode.getId());

                //take this node out of unoptimized and put into optimized
                opt.add(currentOptNode);
                unopt.remove(currentOptNode);
                System.out.print("nodes in opt: [ ");
                opt.forEach(node -> System.out.print(node.getId() + ", "));
                System.out.println(" ]");
                // no need to carry on if we have the exit optimized
                if(currentOptNode.getId() == exit.getId()){
                    System.out.println("OPTIMIZING THE EXIT NODE. THIS IS THE LAST OPTIMIZATION");
                }
                //update the neighbours
                neighboursSet = currentOptNode.getNeighbours();
                neighbours = new ArrayList<>(neighboursSet);
                unoptNeighbours = new ArrayList<>();
                for (int i = 0; i < neighbours.size(); i++) {
                    if (unopt.contains(neighbours.get(i))) {
                        unoptNeighbours.add(neighbours.get(i));
                    }
                }
                // update the shortestDst estimates and paths for these neighbours
                updateMaps(currentOptNode, unoptNeighbours, dstToNodes, pathsToNodes, exit);
        }
        return pathsToNodes;
    }

    //returns the node in unopt that has the lowest current shortestDst
    //these dst have all been initially set to 100,000, except start node set to 0
    //This should return the start node the first time
    private Node getNextNode(List<Node> unopt, Map<Node, Integer> distances){
        Node nextNode = unopt.get(0);
        if(unopt.size() > 1) {
            for (int i = 1; i < unopt.size(); i++) {
                if (distances.get(unopt.get(i)) < distances.get(nextNode)){
                    nextNode = unopt.get(i);
                }
            }
        }
        return nextNode;
    }
    // This adds the currentOptNode to the paths for all unoptNeighbours if it makes a shorter route
    // It also adds the nodes in the predecessor nodes recursively all the way back to the start
    private void updateMaps(Node current, List<Node> neighbours, Map<Node,Integer> shortestDst,
                            Map<Node,List<Node>> paths, Node exitNode){
        System.out.println("Updating maps (if shorter) for neighbours....");
        neighbours.forEach(neighbour -> {
            //sum the dst from start to current with dst from current to this neighbour
            int newPathDst = shortestDst.get(current) + current.getEdge(neighbour).length();
            System.out.print("[ " + neighbour.getId() +"- ");
            System.out.print("current dst: " + shortestDst.get(neighbour) );
            System.out.print(" new dst: " + newPathDst + " ]");
            System.out.println();
            // check if this dst is shorter than current best estimate for neighbour
            if (newPathDst < shortestDst.get(neighbour)) {
                System.out.println(" Updating path for " + neighbour.getId() + "...");
                //get the path of current node
                List<Node> pathOfCurrent = paths.get(current);
                // create a new path
                List<Node> newPathForNeighbour = new ArrayList<>();
                newPathForNeighbour.add(neighbour);
                for(int i = 0; i < pathOfCurrent.size(); i++){
                    newPathForNeighbour.add(pathOfCurrent.get(i));
                }

                //replace the path of neighbour in paths
                paths.replace(neighbour, newPathForNeighbour);
                System.out.print("...UPDATE COMPLETE- ");
                System.out.println(" CHECK!!! NEW PATH SHOULD BE " + ((paths.get(current).size())+1));
                System.out.println(" CHECK!!! NEW PATH SIZE IS " + (paths.get(neighbour).size()));

                //update the shortest distance
                shortestDst.replace(neighbour, newPathDst);
            } else{
                System.out.println(" Not updating neighbour.");
                System.out.println();
            }
        });
    }
    private void makeJourney(EscapeState state, List<Node> journeyNodes, Map<Node, List<Node>> pathsForAllNodes) {
        System.out.println("Our journey starting position is " + state.getCurrentNode().getId());
        System.out.print("The journey path list is [ ");
        for(int i = 0; i < journeyNodes.size(); i++){
            System.out.print((journeyNodes.get(i).getId()) + ", ");
        }
        System.out.println(" ]");

        // 1st element should be equal to startingNode
        if (state.getCurrentNode() != journeyNodes.get(journeyNodes.size()-1)) {
            System.out.println("Cannot make this journey as you are not at the right starting point");
            System.out.println("The currentNodeId is " + state.getCurrentNode().getId());
            System.out.println("The journey starting nodeId is " + journeyNodes.get(journeyNodes.size()-1).getId());
        } else {
            for (int i = journeyNodes.size()-2; i >= 0; i--) {
                System.out.print("   current pos:" + state.getCurrentNode().getId());
                System.out.print("..next intended move:" + journeyNodes.get(i).getId() + "  ");
                Set<Node> neighboursSet = state.getCurrentNode().getNeighbours();
                List<Node> neighboursList = Arrays.asList(neighboursSet.toArray(new Node[neighboursSet.size()]));
                Boolean containsNextMove = neighboursSet.contains(journeyNodes.get(i));
                if(!containsNextMove){
                    System.out.println("WARNING: next move is not a neighbour of current pos!!!");
                    System.out.print("The neighbours for the current node are: ");
                    System.out.print("[");
                    for(int j = 0; j < neighboursSet.size(); j++){
                        System.out.print(neighboursList.get(j).getId() + ", ");
                    }
                    System.out.println(" ]");

                    System.out.println("The separate paths for every node in the journey path are as follows: ");
                    for(int k = 0; k< journeyNodes.size(); k++){
                        List<Node> pathForThisNode = pathsForAllNodes.get(journeyNodes.get(k));
                        System.out.println("node(" + journeyNodes.get(k).getId() + ")");
                        System.out.print("Path(");
                        for(int n = 0; n < pathForThisNode.size(); n++){
                            System.out.print(pathForThisNode.get(n).getId() + ", ");
                        }
                        System.out.println(")");
                    }
                    System.out.println(" ]");
                    // print the path for each node in the path
                }
                state.moveTo(journeyNodes.get(i));
                System.out.println();
            }
        }
    }
}
