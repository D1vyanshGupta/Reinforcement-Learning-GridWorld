import org.jfree.data.xy.XYSeries;

import java.util.HashMap;
import java.lang.Math;
import java.util.LinkedHashMap;

public class GridWorld {

    /**
     * This class represents the GridWorld i.e. the environment in which the agent operates.
     */

    /**
     * Discount factor used for computation of utility values of the states.
     */
    private static final double DISCOUNT_FACTOR = 0.99;

    /**
     * An array of all the possible actions that the agent can take in the environment.
     */
    private static final Action[] ALL_POSSIBLE_ACTIONS = {new Action("UP"), new Action("RIGHT"), new Action("DOWN"), new Action("LEFT")};

    /**
     * To specify the number of rows and columns in the GridWorld.
     */
    private int mNumRows;
    private int mNumCols;

    /**

     * Hash Map between state and the action to be taken in that state.
     * (Used for Policy Iteration.)
     */
    public HashMap<State, Action> mPolicyIterationMap;

    /**
     * Hash Map between state and the best action that cab be taken in that state.
     * (Used for Value Iteration.)
     */
    public HashMap<State, Action> mValueIterationMap;

    /**
     * Hash Map between state and the utility value for that state.
     */
    private HashMap<State, Double> mUtilityMap;

    /**
     * The GridWorld can be represented by a 2-D array of State Objects.
     */
    private State[][] mStateGrid;

    /**
     * Data structures to store data for graph plotting.
     */
    private LinkedHashMap<State, XYSeries> mPIDataMap;
    private LinkedHashMap<State, XYSeries> mVIDataMap;

    /**
     * Counter to keep track of number of iterations for Policy Evaluation.
     */
    private int numPolicyEvalIter;

    /**
     * Overloaded constructor for the class. Takes in the number of rows (assumes the GridWorld is a square)
     * and an array of rewards for the states in the GridWorld, as arguments.
     * @param numRows
     * @param rewardArray
     */
    public GridWorld(int numRows, double[] rewardArray){
        this(numRows, numRows, rewardArray);
    }

    /**
     * General constructor for the class. Takes in the number of rows and columns along with an array
     * of rewards for the states in the GridWorld, as arguments.
     * @param numRows
     * @param numCols
     * @param rewardArray
     */
    public GridWorld(int numRows, int numCols, double[] rewardArray){
        mNumRows = numRows;
        mNumCols = numCols;

        mStateGrid = new State[mNumRows][mNumCols];

        mPolicyIterationMap = new HashMap<State, Action>();
        mValueIterationMap = new HashMap<State, Action>();
        mUtilityMap = new HashMap<State, Double>();

        mVIDataMap = new LinkedHashMap<State, XYSeries>();
        mPIDataMap = new LinkedHashMap<State, XYSeries>();

        initializeWorld(rewardArray);
        initializeUtilityMap();
        initializePolicyMap();

        numPolicyEvalIter = 0;
    }

    /**
     * Set the rewards for the states in the GridWorld.
     * @param rewardArray
     */
    private void initializeWorld(double[] rewardArray){
        for(int i = 0; i < mNumRows; ++i){
            for(int j = 0; j < mNumCols; ++j){
                try{
                    State state = new State(rewardArray[i * mNumCols + j], i, j);
                    mStateGrid[i][j] = state;
                } catch (Exception e){
                    System.out.println("Exception");
                    System.out.println("i = " + i + ", j = " + j);
                }
            }
        }
    }

    /**
     * Initialize utility of all non-walled states in the GridWorld to 0.
     */
    private void initializeUtilityMap(){
        for(int i = 0; i < mNumRows; ++i){
            for(int j = 0; j < mNumCols; ++j){
                State state = mStateGrid[i][j];
                if(!state.isWall()){
                    mUtilityMap.put(state, 0.0);
                }
            }
        }
    }

    /**
     * Set the initial policy for all the non-walled states (cells) for the purposes of Policy Iteration.
     * The initial policy is as follows:
     *  (i) Given a state (cell) in the GridWorld, the list of possible actions
     *      in decreasing order of preference is: UP, RIGHT, DOWN, LEFT.
     *  (ii) If it is not possible to take an action, then check for the next action in the list.
     *  (iii) For example - If it is not possible to go UP (because either there is a wall above
     *        or the cell is in the first row of the GridWorld), then check whether it is possible
     *        to go RIGHT, and so on.
     *
     */
    private void initializePolicyMap(){
        for(int i = 0; i < mNumRows; ++i){
            for(int j = 0; j < mNumCols; ++j){
                State state = mStateGrid[i][j];

                /**
                 * No need to set actions for walled-states.
                 */
                if(state.isWall()){
                    continue;
                }
                /**
                 * Check if it is possible to move UP from the given state.
                 */
                if(canGoUp(state)){
                    mPolicyIterationMap.put(state, new Action("UP"));
                    continue;
                }
                /**
                 * Check if it is possible to move RIGHT from the given state.
                 */
                if(canGoRight(state)){
                    mPolicyIterationMap.put(state, new Action("RIGHT"));
                    continue;
                }
                /**
                 * Check if it is possible to move DOWN from the given state.
                 */
                if(canGoDown(state)){
                    mPolicyIterationMap.put(state, new Action("DOWN"));
                    continue;
                }
                /**
                 * If it is not possible to move UP, RIGHT and DOWN, then default to LEFT.
                 */
                mPolicyIterationMap.put(state, new Action("LEFT"));
            }
        }
    }

    /**
     * Initialize data structure for storing data for graph plotting, for policy iteration.
     */
    private void initializePIDataGrid(){
        for(int i = 0; i < mNumRows; ++i){
            for(int j = 0; j < mNumCols; ++j){
                State state = mStateGrid[i][j];
                if(!state.isWall()){
                    XYSeries dataSeries = new XYSeries("(" + i + ", " + j + ")");
                    dataSeries.add(0,0);
                    mPIDataMap.put(state, dataSeries);
                }

            }
        }
    }

    /**
     * Initialize data structure for storing data for graph plotting, for value iteration.
     */
    private void initializeVIDataGrid(){
        for(int i = 0; i < mNumRows; ++i){
            for(int j = 0; j < mNumCols; ++j){
                State state = mStateGrid[i][j];
                if(!state.isWall()){
                    XYSeries dataSeries = new XYSeries("(" + i + ", " + j + ")");
                    dataSeries.add(0,0);
                    mVIDataMap.put(state, dataSeries);
                }

            }
        }
    }

    /**
     * Policy Evaluation implementation for Policy Iteration.
     */
    private void policyEvaluation(boolean giveFeedback, double epsilon){
        /**
         * Precision value to determine when the utility values have converged.
         */

        int numIterations = 0;

        /**
         * Outer while loop to repeatedly calculate utility values until convergence.
         */
        while(true){
            numPolicyEvalIter++;
            numIterations++;
            double delta = 0.0;

            for(State stateIterator : mUtilityMap.keySet()){
                /**
                 * Get action for state, given the current policy.
                 */
                Action stateAction = mPolicyIterationMap.get(stateIterator);

                /**
                 * Get expected utility given current state and current action.
                 */
                double expectedUtility = getExpectedUtility(stateAction, stateIterator);

                /**
                 * Calculate new utility value for the state using Bellman Equation.
                 */
                double stateUtility = stateIterator.getReward() + DISCOUNT_FACTOR * expectedUtility;

                delta = Math.max(delta, Math.abs(stateUtility - mUtilityMap.get(stateIterator)));

                mUtilityMap.put(stateIterator, stateUtility);
            }

            /**
             * Check whether utility values have converged up to the specified precision value.
             */
            if(delta < epsilon){
                if(giveFeedback){
                    System.out.println("Number of Iterations for Policy Evaluation Step: " + numIterations);
                }
                break;
            }
        }
    }

    /**
     * Policy Improvement implementation for Policy Iteration.
     * Returns the new policy hash map based on newly calculated utility values.
     * @return HashMap<State, Action> newPolicyMap
     */
    private HashMap<State, Action> policyImprovement(){
        HashMap<State, Action> newPolicyMap = new HashMap<State, Action>();
        for(State stateIterator : mPolicyIterationMap.keySet()){
            newPolicyMap.put(stateIterator, getBestAction(stateIterator));
        }
        return newPolicyMap;
    }

    /**
     * Policy Iteration to calculate optimal values and utility values.
     */
    public int policyIteration(boolean displayUI, boolean giveFeedback, double epsilon){
        initializePIDataGrid();

        HashMap<State, Action> newPolicy;

        int numIterations = 0;
        while(true){
            numIterations++;
            /**
             * Policy Evaluation step to calculate utility values.
             */
            policyEvaluation(giveFeedback, epsilon);

            /**
             * Store data for graph plotting.
             */
            for(State stateIterator : mUtilityMap.keySet()){
                XYSeries dataSeries = mPIDataMap.get(stateIterator);
                dataSeries.add(numPolicyEvalIter, mUtilityMap.get(stateIterator));
                mPIDataMap.put(stateIterator, dataSeries);
            }

            /**
             * Calculate new policy based on new utility values.
             */
            newPolicy = policyImprovement();

            /**
             * Compare old policy and new policy.
             */
            if(newPolicy.equals(mPolicyIterationMap)){
                if(giveFeedback){
                    System.out.println("Old and new policy match");
                }
                mPolicyIterationMap = newPolicy;
                break;
            }
            else{
                if(giveFeedback){
                    System.out.println("Old and new do not policy match");
                }
                mPolicyIterationMap = newPolicy;
            }
        }

        if(displayUI){
            GridUIUtils.displayWorld("Final State (Policy Iteration)", mPolicyIterationMap,
                    this, 0, 0);
            GridUIUtils.displayLineChart("Policy Iteration", mPIDataMap);
        }

        return numPolicyEvalIter;
    }

    /**
     * Value Iteration implementation to calculate utility values and optimal values.
     */
    public int valueIteration(boolean displayUI, boolean giveFeedback, double epsilon){
        initializeVIDataGrid();

        /**
         * Reset utilities of all states to 0.
         */
        initializeUtilityMap();

        HashMap<State, Action> newPolicyMap = new HashMap<State, Action>();

        int numIterations = 0;
        while(true){
            double delta = 0.0;
            numIterations++;

            for(State stateIterator : mUtilityMap.keySet()){
                /**
                 * Get best action given current state, i.e. the action with the maximum expected utility.
                 */
                Action stateAction = getBestAction(stateIterator);

                /**
                 * Calculate new utility value for the state using the Bellman Equation.
                 */
                double stateUtility = stateIterator.getReward() +
                                    DISCOUNT_FACTOR * getExpectedUtility(stateAction, stateIterator);

                delta = Math.max(delta, Math.abs(stateUtility - mUtilityMap.get(stateIterator)));

                mUtilityMap.put(stateIterator, stateUtility);

                mValueIterationMap.put(stateIterator, stateAction);

                /**
                 * Store data for graph plotting.
                 */
                XYSeries dataSeries = mVIDataMap.get(stateIterator);
                dataSeries.add(numIterations, stateUtility);
                mVIDataMap.put(stateIterator, dataSeries);
            }

            /**
             * Check for convergence of utility values.
             */
            if(delta < epsilon){
                if(giveFeedback){
                    System.out.println("Number of iterations for Value Iteration: " + numIterations);
                }
                break;
            }
        }

        if(displayUI){
            GridUIUtils.displayWorld("Final State (Value Iteration)", mValueIterationMap, this, 600, 0);
            GridUIUtils.displayLineChart("Value Iteration", mVIDataMap);
        }
        return numIterations;
    }

    /**
     * Method to check if the agent can go to the state (cell) above from the current state (cell).
     * @param state
     * @return
     */
    private boolean canGoUp(State state){
        int rowIndex = state.getRowIndex();
        int colIndex = state.getColIndex();

        /**
         * Check if the cell is in the first row.
         */
        if(rowIndex == 0)
            return false;

        State upperState = mStateGrid[rowIndex - 1][colIndex];
        /**
         * Check if the state above is a wall.
         */
        return !upperState.isWall();
    }

    /**
     * Method to check if the agent can go to the state (cell) right of the current state (cell).
     * @param state
     * @return
     */

    private boolean canGoRight(State state){
        int rowIndex = state.getRowIndex();
        int colIndex = state.getColIndex();

        /**
         * Check if the cell is in the rightmost column.
         */
        if(colIndex == mNumCols - 1)
            return false;

        State rightState = mStateGrid[rowIndex][colIndex + 1];
        /**
         * Check if the state to the right is a wall.
         */
        return !rightState.isWall();
    }

    /**
     * Method to check if the agent can go to the state (cell) below the current state (cell).
     * @param state
     * @return
     */
    private boolean canGoDown(State state){
        int rowIndex = state.getRowIndex();
        int colIndex = state.getColIndex();

        /**
         * Check if the cell is in the last row.
         */
        if(rowIndex == mNumRows - 1)
            return false;

        State belowState = mStateGrid[rowIndex + 1][colIndex];
        /**
         * Check if the cell below is a wall.
         */
        return !belowState.isWall();
    }

    /**
     * Method to check if the agent can go to the state (cell) left of the current state (cell).
     * @param state
     * @return
     */
    private boolean canGoLeft(State state){
        int rowIndex = state.getRowIndex();
        int colIndex = state.getColIndex();

        /**
         * Check if the cell is in the leftmost column.
         */
        if(colIndex == 0)
            return false;

        State leftState = mStateGrid[rowIndex][colIndex - 1];
        /**
         * Check if the state to the left is a wall.
         */
        return !leftState.isWall();
    }

    /**
     * Return action with the maximum expected utility given the current state.
     * @param state
     * @return
     */
    private Action getBestAction(State state){
        Action bestAction = null;
        double maximumUtility = Double.NEGATIVE_INFINITY;

        /**
         * Iterate through all possible actions and find the state with the
         * maximum expected utility.
         */
        for(Action actionIterator : ALL_POSSIBLE_ACTIONS) {
            if (maximumUtility < getExpectedUtility(actionIterator, state)) {
                maximumUtility = getExpectedUtility(actionIterator, state);
                bestAction = actionIterator;
            }
        }
        return bestAction;
    }

    /**
     * Given a state and an action to be taken, return the expected utility of the given action.
     * @param action
     * @param state
     * @return
     */
    private double getExpectedUtility(Action action, State state){
        double expectedUtility = 0.0;
        HashMap<String, Double> directionProbMap = action.getDirectionProbMap();

        double prob;
        State nextState;
        /**
         * Iterate through all possible directions for an action and calculate expected utility
         * of this action.
         */
        for(String directionIterator : directionProbMap.keySet()){
            prob = directionProbMap.get(directionIterator);
            nextState = getNextState(state, directionIterator);
            expectedUtility += prob * mUtilityMap.get(nextState);
        }
        return expectedUtility;
    }

    /**
     * Given a state and direction, get the next state.
     * @param state
     * @param direction
     * @return
     */
    private State getNextState(State state, String direction){
        int rowIndex = state.getRowIndex();
        int colIndex = state.getColIndex();
        State nextState = state;

        switch (direction){
            case "UP":{
                if(canGoUp(state)){
                    nextState = mStateGrid[rowIndex - 1][colIndex];
                }
                break;
            }
            case "RIGHT":{
                if(canGoRight(state)){
                    nextState = mStateGrid[rowIndex][colIndex + 1];
                }
                break;
            }
            case "DOWN":{
                if(canGoDown(state)){
                    nextState = mStateGrid[rowIndex + 1][colIndex];
                }
                break;
            }
            case "LEFT":{
                if(canGoLeft(state)){
                    nextState = mStateGrid[rowIndex][colIndex - 1];
                }
                break;
            }
        }
        return nextState;
    }

    public int getNumRows() {
        return mNumRows;
    }

    public int getNumCols() {
        return mNumCols;
    }

    public State[][] getStateGrid() {
        return mStateGrid;
    }

    public HashMap<State, Double> getUtilityMap() {
        return mUtilityMap;
    }
}
