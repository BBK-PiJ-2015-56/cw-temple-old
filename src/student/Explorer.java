package student;

import game.EscapeState;
import game.ExplorationState;

import java.util.*;
import java.util.concurrent.SynchronousQueue;

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

        // https://www.google.co.uk/webhp?sourceid=chrome-instant&ion=1&espv=2&ie=UTF-8#q=dijkstra%27s+algorithm+java
    }
}
