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
        List<Long> visitedNodes = new ArrayList<Long>();
        Collection<NodeStatus> neighboursCollection;
        List<NodeStatus> neighbours;
        List<NodeStatus> unvisitedNeighbours;
        List<NodeStatus> visitedNeighbours;

        while (state.getDistanceToTarget() != 0) {
            System.out.println("starting a  new move........");
            System.out.println("The current node is: " + state.getCurrentLocation());
            System.out.println("This distance away from the orb is: " + state.getDistanceToTarget());
            neighboursCollection = state.getNeighbours();
            //convert the new neighbours collection into a new list
            neighbours = new ArrayList<NodeStatus>(neighboursCollection);
            //sort the list
            Collections.sort(neighbours);
            System.out.println("Here is the full list of neighbours for the current node");
            neighbours.forEach(node -> System.out.print("  node id: " + node.getId()));
            //get three empty lists for splitting up the neighbours
            System.out.println();
            unvisitedNeighbours = new ArrayList<NodeStatus>();
            visitedNeighbours = new ArrayList<NodeStatus>();

            // split the sorted list into three sublists
            for(int i = 0; i < neighbours.size(); i++){
                NodeStatus tempNode = neighbours.get(i);
                if (visitedNodes.contains(tempNode.getId())) {
                    visitedNeighbours.add(tempNode);
                } else{
                    unvisitedNeighbours.add(tempNode);
                }
            }
            System.out.println("Here are the 2 neighbour lists for the current node: ");
            System.out.print("unvisited: [ ");
            unvisitedNeighbours.forEach(node -> System.out.print("  node id: " + node.getId()));
            System.out.println(" ]");
            System.out.print("visited: [ ");
            visitedNeighbours.forEach(node -> System.out.print("  node id: " + node.getId()));
            System.out.println(" ]");

            // now need to check 2 lists and decide next move
            long nextNodeId;
            // move to the first node in unvisitedNeighbours, if such a node exists
            if (unvisitedNeighbours.size() > 0) {
                System.out.println("There is a neighbour not visited yet, and we move to it");
                nextNodeId = unvisitedNeighbours.get(0).getId();
                move(nextNodeId, visitedNodes, state);
            } else{
                System.out.print("All neighbours have been visited. ");
                System.out.println("We move to the neighbour closest to the orb, " +
                        "excluding the last node visited if possible.");
                // note: alternatively, I could make it go back to previous and keep doing this until
                //it finds an unvisited neighbour, but I did this before and ran into problems
                nextNodeId = visitedNeighbours.get(0).getId();
                //ensure it never goes back to the last node visited unless its the only neighbour
                System.out.println("Here is the list of visited Nodes: [ ");
                visitedNodes.forEach(nodeId -> System.out.print("  visited node id: " + nodeId));
                System.out.println(" ]");
                // if the nearest neighbour to orb is one of the previous three nodes, then rule it out if there
                // is another neighbour, in order to prevent a repetitive cycle between 2,3 or 4 nodes
                // I could increase this to last 4 or 5 nodes in case of a large repeated cycle
                long lastNode = visitedNodes.get(visitedNodes.size()-2);
                long secondLastNode = visitedNodes.get(visitedNodes.size()-3);
                long thirdLastNode = visitedNodes.get(visitedNodes.size()-4);

                if((nextNodeId == lastNode) || (nextNodeId == secondLastNode) || (nextNodeId == thirdLastNode) ){
                    if(visitedNeighbours.size() > 1){
                        System.out.println("NEIGHBOUR NEAREST THE ORB WAS RECENTLY VISITED. WE RULE IT OUT ");
                        nextNodeId = visitedNeighbours.get(1).getId();
                        // could also test if this was recently visited, but may not need to
                    }else{
                        System.out.println("NEIGHBOUR NEAREST THE ORB WAS RECENTLY VISITED, " +
                                "BUT THERE ARE NO MORE NEIGHBOURS SO WE GO BACK TO IT ");
                    }
                }
                move(nextNodeId, visitedNodes, state);
                // need an added part that identifies if it is stuck in a complex loop that will
                //never finish, due to a long wall blocking the path. In this case, it can follow teh opposite rule
                // ie it can actually take the furthest path from the orb, in order to get away from the wall.
                //alternatively, extend my recently visited list of squares that are ruled out, and/or put in
                // a safety loop that makes a random move if all else fails. or even a random set of moves, but
                // only if it is stuck. this could be identified by spotting a pattern ie if teh same square comes
                // up in a list four times, this triggers a random sequence of moves, or certain squares become banned
            }
        }
    }
    private void move(long nextNode, List<Long> visitedNodes, ExplorationState state){
        long nextNodeId;
        nextNodeId = nextNode;
        visitedNodes.add(nextNodeId);
        System.out.println("moving to node with id: " + nextNodeId);
        state.moveTo(nextNodeId);
        System.out.println("Check move: node should be: " + nextNodeId + " node is: " + state.getCurrentLocation());
        System.out.println();
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
                Node previousTopNode;
                //path for this neighbour, before updating
                List<Node> pathOfNeighbour = paths.get(neighbour);
                System.out.print(" Neighbour path: [");
                for(int i = 0; i < pathOfNeighbour.size(); i++){
                    System.out.print(pathOfNeighbour.get(i).getId());
                }
                System.out.println("]");

                //get the path of current node
                List<Node> pathOfCurrent = paths.get(current);
                System.out.print(" Current path: [");
                for(int i = 0; i < pathOfCurrent.size(); i++){
                    System.out.print(pathOfCurrent.get(i).getId() + ", ");
                }
                System.out.println("]");
                System.out.println("There are " + pathOfCurrent.size() + " nodes to add.");
                System.out.println(" CHECK!!! NEW PATH SIZE SHOULD BE " + (pathOfCurrent.size() + pathOfNeighbour.size()) );

                //add all nodes from current node's path into neighbour's path
                for(int i = 0; i < pathOfCurrent.size(); i++){
                    System.out.print("adding " + pathOfCurrent.get(i).getId() + " to path...");
                    pathOfNeighbour.add(pathOfCurrent.get(i));
                }
                System.out.println();
                System.out.println("Amended path for " + neighbour.getId() + " to: [");
                for(int i = 0; i < pathOfNeighbour.size(); i++){
                    System.out.print(pathOfNeighbour.get(i).getId() + ", ");
                }
                System.out.println("]");
                //replace the path of neighbour in paths
                paths.replace(neighbour, pathOfNeighbour);
                System.out.print("...UPDATE COMPLETE- ");
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
