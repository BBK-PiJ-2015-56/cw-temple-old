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
        Map<Node, Stack<Node>> pathsToNodes = findPathsToNodes(start, exit, nodes, dstToNodes);

        //Make the journey from the state's currentNode to the exit, along the shortest path
        makeJourney(state, pathsToNodes.get(state.getExit()));
    }

    private Map<Node,Stack<Node>> findPathsToNodes(Node start, Node exit, List<Node> nodes, Map<Node, Integer> dstToNodes){
        //create the Map of stacks(ie paths) and set each path to contain it's own node
        Map<Node, Stack<Node>> pathsToNodes = new HashMap<>();
        for(int i = 0; i < nodes.size(); i++){
            Stack<Node> path = new Stack<>();
            path.push(nodes.get(i));
            pathsToNodes.put(nodes.get(i), path);
            System.out.println("UPDATE: top node in exit path now: " + pathsToNodes.get(exit).peek().getId());
        }
        //A List of all Nodes for which we don't know shortest dst - all of them to begin with
        List<Node> unopt = nodes;
        //A List of all nodes for which we do know shortest dst
        List<Node> opt = new ArrayList<>();

        // The lists of neighbours to be used for each node
        Collection<Node> neighboursSet;
        List<Node> neighbours;
        List<Node> unoptNeighbours;

        //The node we are optimizing next
        Node currentOptNode;
        while(unopt.size() > 0) {
            // get another node to optimize - this is 'start' the first time
            currentOptNode = getNextNode(unopt, dstToNodes);

            //take this node out of unoptimized and put into optimized
            opt.add(currentOptNode);
            unopt.remove(currentOptNode);

            //update the neighbours
            neighboursSet = currentOptNode.getNeighbours();
            neighbours = new ArrayList<>(neighboursSet);
            unoptNeighbours = new ArrayList<>();
            for(int i = 0; i < neighbours.size(); i++){
                if(unopt.contains(neighbours.get(i))) {
                    unoptNeighbours.add(neighbours.get(i));
                }
            }

            // update the shortestDst estimates and paths for these neighbours
            updateMaps(currentOptNode,unoptNeighbours, dstToNodes, pathsToNodes, exit);
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
    private void updateMaps(Node current, List<Node> nodes, Map<Node,Integer> shortestDst,
                           Map<Node,Stack<Node>> paths, Node exitNode){
        nodes.forEach(node -> {
            //sum the dst from start to current with dst from current to this neighbour
            int newPathDst = shortestDst.get(current) + current.getEdge(node).length();
            // check if this dst is shorter than current best estimate for neighbour
            if (newPathDst < shortestDst.get(node)) {
                //add the currentOptNode to this neighbours path stack, as a predecessor
                // and add its predecessors aswell
                Stack<Node> path = paths.get(node);
                path.push(current);
                paths.replace(node, path);
                System.out.print("top node in exit path: " + paths.get(exitNode).peek().getId());
                //update the shortest distance
                shortestDst.replace(node, newPathDst);
            }
        });
    }
    // method throws exception
    private void makeJourney(EscapeState state, Stack<Node> journeyNodes) {
        // 1st element should be equal to startingNode
        if (state.getCurrentNode() != journeyNodes.peek()) {
            System.out.println("Cannot make this journey as you are not at the right starting point");
            System.out.println("The currentNodeId is " + state.getCurrentNode().getId());
            System.out.println("The journey starting nodeId is " + journeyNodes.peek().getId());
        } else {
            for (int i = 1; i < journeyNodes.size(); i++) {
                state.moveTo(journeyNodes.pop());
            }
        }
    }
}

