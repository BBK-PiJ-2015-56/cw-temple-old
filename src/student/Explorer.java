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
        List<NodeStatus> unvisited;
        List<NodeStatus> visitedButHasUnvisitedNeighbour;
        List<NodeStatus> visitedAndAllNeighboursVisited;

        while (state.getDistanceToTarget() != 0) {
            System.out.println("starting a  new move........");
            System.out.println("The current node is: " + state.getCurrentLocation());
            System.out.println("This distance away from the orb is: " + state.getDistanceToTarget());
            neighboursCollection = state.getNeighbours();
            //convert the new neighbours collection into a new list
            neighbours = new ArrayList<NodeStatus>(neighboursCollection);
            //sort the list
            Collections.sort(neighbours);
            //get three empty lists for splitting up the neighbours
            unvisited = new ArrayList<NodeStatus>();
            visitedButHasUnvisitedNeighbour = new ArrayList<NodeStatus>();
            visitedAndAllNeighboursVisited = new ArrayList<NodeStatus>();

            // split the sorted list into three sublists
            for(int i = 0; i < neighbours.size(); i++){
                NodeStatus tempNode = neighbours.get(i);
                if (visitedNodes.contains(tempNode.getId())) {
                    if(allNeighboursVisited(tempNode)){
                        visitedAndAllNeighboursVisited.add(tempNode);
                    }else{
                        visitedButHasUnvisitedNeighbour.add(tempNode);
                    }
                } else{
                    unvisited.add(tempNode);
                }
            }
            // now need to check 3 lists 
            long nextNodeId;
            // move to the first node in neighboursNotVisited, if such a node exists
            if (unvisited.size() > 0) {
                //nextNodeId = visited.get(0).getId();
                System.out.println("There is a neighbour not visited yet, and we move to it");
                move(nextNodeId, visitedNodes, state);
            } else {

                nextNodeId = neighboursVisited.get(0).getId();
                System.out.println("All neighbours have been visited so we move to another one");
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
    private boolean allNeighboursVisited(NodeStatus node){
        //get neighbours for this node, and check if visited
        return true;
    }



      /*
            // an int to keep track of which neighbours have been checked
            int count = 0;
            // check a move had not been made, and there are still other neighbours to check
            while ((!(moveMade)) && (count < neighbours.size()) ) {
                // get the next neighbour to check
                long tempId = neighbours.get(count).getId();
                System.out.println("checking neighbour with id: " + tempId);
                //check if this node has been visited yet, including in recent wiped history
                if (!(visitedNodes.contains(tempId))) {
                    System.out.println("This neighbour has not been visited yet");
                    //move to this node, add it to visitedNodes and partialVisits
                    visitedNodes.add(tempId);
                    partialVisitedNodes.add(tempId);
                    System.out.println("moving to node with id: " + tempId);
                    state.moveTo(tempId);
                    moveMade = true;
                    System.out.println("Check move success: node should be: " + tempId + " node is: " + state.getCurrentLocation());
                    System.out.println();
                } else {
                        System.out.println("This neighbour has already been visited.");
                }
                    count++;
            }
            int newCount = 0;
            while ((!(moveMade)) && (newCount < neighbours.size()) ) {
                    System.out.println("NO MOVE YET, SO NOW CHECKING THE PARTIALVISITS LIST... ");
                // get the next neighbour to check
                long tempId = neighbours.get(newCount).getId();
                System.out.println("checking neighbour with id: " + tempId);
                //check if this node has been visited yet, including in recent wiped history
                if (!(partialVisitedNodes.contains(tempId))) {
                    System.out.println("This neighbour has not been visited recently");
                    //move to this node
                    // add it to both visitedNodes and partialVisitedNotes
                    partialVisitedNodes.add(tempId);
                    visitedNodes.add(tempId);
                    System.out.println("moving to node with id: " + tempId);
                    state.moveTo(tempId);
                    moveMade = true;
                    System.out.println("Check move success: node should be: " + tempId + " node is: " + state.getCurrentLocation());
                    System.out.println();
                } else {
                    System.out.println("This neighbour is on the partialVisits list and the visits list.");
                    newCount++;
                }
            }
            /*
            //If still no move, then George is stuck so need to wipe recent visits
            if(!(moveMade)){
                // all neighbours have been visited
                System.out.print("all the neighbours have been checked on visited list and wipedVisited list: ");
                neighbours.forEach(nodeStatus -> System.out.println("  neighbour checked: " + nodeStatus.getId()));
                System.out.println();
                // will need to do a wipe of the recently visited nodes to get George moving
                // we still want george to favour a completely unvisited square, so we update the wiped list with a copy
                //reset partialVisits to visits, and then wipe last 20 from partialVisits
                partialVisitedNodes = visitedNodes;
                System.out.println("WIPING LAST 20 NODES FROM VISITED LIST!!!");
                System.out.print("current list of visited nodes:(total = " + visitedNodes.size() + "):  ");
                visitedNodes.forEach(nodeId -> System.out.print("  nodeId visited: " + nodeId));
                System.out.println();
                //remove the last 3 moves from list so George can move, or all of them if less than 4 nodes visited
                int origSize = visitedNodes.size();
                // need to optimize number of removals: 8 too small, 20 too small
                for(int i = 1; i < 50; i++) {   //need to check it works when size < 30
                    if (origSize >= i) {
                        partialVisitedNodes.remove((origSize) - i);
                    }
                }
                System.out.println("ABOUT TO WIPE THE LIST");
                System.out.println("There are now " + partialVisitedNodes.size() + " nodes on the partialVisits list");
                System.out.println("There are now " + visitedNodes.size() + " nodes on the visits list");
                System.out.print("new list of partialVisitedNodes(last 20 visits wiped): ");
                partialVisitedNodes.forEach(nodeId -> System.out.print("  nodeId visited: " + nodeId));
                System.out.println();
                System.out.println("There are now " + partialVisitedNodes.size() + " nodes on the partialVisits list");
                System.out.println("There are now " + visitedNodes.size() + " nodes on the visits list");
                //add current node to the new visited list so it doesn't return here in future
                partialVisitedNodes.add(state.getCurrentLocation());
                //start a new move from current location
                count = 0;
            }
        }
    }
                    //move back to previous visited squares until we get one with a neighbour not visited
                    boolean unvisitedNeighbourExists = false;
                    int n = 2;
                    NodeStatus prevTempNode;
                    while(!(unvisitedNeighbourExists)){
                        System.out.println("moving back to previous node");
                        prevTempNode = visitedNodes.get((visitedNodes.size()-n ));
                        System.out.println("The previous node had id: " + prevTempNode.getId());
                        //check if this node has an unvisited neighbour
                        neighboursCollection = state.getNeighbours();
                        //convert the new neighbours collection into a new list
                        List<NodeStatus> tempNodeNeighbours = new ArrayList<NodeStatus>(neighboursCollection);
                        System.out.println("There are " + tempNodeNeighbours.size() + " neighbours to check for this previous node");
                        //sort the list
                        Collections.sort(tempNodeNeighbours);
                        //Look through the neighbours, in distance order, to check if an unvisited neighbour exists
                        int tempCount = 0;
                        while(tempCount < tempNodeNeighbours.size() && !(unvisitedNeighbourExists)) {
                            // get the neighbour at that count
                            NodeStatus tempNeighbour = tempNodeNeighbours.get(tempCount);
                            System.out.println("now checking neighbour number: " + tempCount);
                            // check if this neighbour has been visited
                            if (!(visitedNodes.contains(tempNeighbour))) {
                                System.out.println("This neighbour has not been visited, so moving to it");
                                visitedNodes.add(tempNeighbour);
                                state.moveTo(tempNeighbour.getId());
                                unvisitedNeighbourExists = true;
                                moveMade = true;
                            }
                            tempCount++;
                        }
                        n++;
                    }*/



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
