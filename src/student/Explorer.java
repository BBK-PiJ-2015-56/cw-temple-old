package student;

import game.EscapeState;
import game.ExplorationState;

import java.util.*;

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
        List<NodeStatus> visitedNodes = new ArrayList<NodeStatus>();
        Collection<NodeStatus> neighboursCollection;

        while(state.getDistanceToTarget() != 0) {
            System.out.println("starting a  new move........");
            System.out.println("The current node is: " + state.getCurrentLocation());
            System.out.println("This distance away from the orb is: " + state.getDistanceToTarget());
            neighboursCollection = state.getNeighbours();
            //convert the new neighbours collection into a new list
            List<NodeStatus> neighbours = new ArrayList<NodeStatus>(neighboursCollection);
            System.out.println("There are " + neighbours.size() + " neighbours to check");
            //sort the list
            //System.out.println("The neighbours of node: " + state.getCurrentLocation() + "  are" + neighbours);
            Collections.sort(neighbours);
            System.out.println("These neighbours in order are" + neighbours);
            System.out.println("The id of the neighbour closest to orb is: " + neighbours.get(0).getId());
            // a flag to come out of next while loop once a move has been made
            boolean moveMade = false;
            // an int to keep track of which neighbours have been checked
            int count = 0;
            while (moveMade == false) {
                //check there are still potential neighbours to move to
                System.out.println("checking neighbour number: " + count);
                if(count < neighbours.size()) {
                    // get the next neighbour to check
                    System.out.println("This neighbour exists");
                    NodeStatus temp = neighbours.get(count);
                    //check if this node has been visited yet
                    if (!(visitedNodes.contains(temp))){
                        System.out.println("This neighbour has not been visited yet");
                        //move to this node, add it to visitedNodes and come out of while loop
                        visitedNodes.add(temp);
                        System.out.println("moving to node with id: " + temp.getId());
                        state.moveTo(temp.getId());
                        moveMade = true;
                        System.out.println("The new node is: " + state.getCurrentLocation());
                    }else{
                        System.out.println("This neighbour has already been visited.");
                    }
                    count++;
                }
                else{
                    System.out.println("This neighbour does not exist as we have checked all the neighbours");
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
                    }
                }
            }
            //add nearest neighbour to visitedNodes, and move to it
            //old code state.moveTo(neighbours.get(0).getId());
            //         System.out.println("The new node is: " + state.getCurrentLocation());

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
    }
}
