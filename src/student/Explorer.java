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
        List<NodeStatus> visitedNodes = new ArrayList<NodeStatus>();
        while(state.getDistanceToTarget() != 0) {
            System.out.println("The current node is: " + state.getCurrentLocation());
            Collection<NodeStatus> neighboursCollection = state.getNeighbours();
            List<NodeStatus> neighbours = new ArrayList<NodeStatus>(neighboursCollection);
            System.out.println("The neighbours of node: " + state.getCurrentLocation() + "  are" + neighbours);
            Collections.sort(neighbours);
            System.out.println("These neighbours in order are" + neighbours);
            System.out.println("The id of the neighbour closest to orb is: " + neighbours.get(0).getId());
            boolean moveMade = false;
            while (moveMade == false) {
                int count = 0;
                //check there are still potential neighbours to move to
                if(count < neighbours.size()) {
                    NodeStatus temp = neighbours.get(count);
                    //check if this node has not been visited yet
                    if (visitedNodes.contains(temp) == false) {
                        visitedNodes.add(temp);
                        state.moveTo(temp.getId());
                        moveMade = true;
                        System.out.println("The new node is: " + state.getCurrentLocation());
                    }
                    count++;
                }
                else{
                    //move back to previous visited square
                    state.moveTo((visitedNodes.get( (visitedNodes.size())-1 ) ).getId());
                    moveMade = true;
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
