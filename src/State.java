public class State {

    /**
     * Every state has a row and column index to specify its
     * coordinates in the GridWorld.
     */

    private int mRowIndex;
    private int mColIndex;

    /**
     * A state can be of two types:
     *  (i) A walled state, or
     *  (ii) A non-walled state
     */

    private boolean mIsWall;

    /**
     * Every state has a reward associated to it.
     * If the state is a walled state, then its reward is NaN (Not a number).
     * If the state is a non-walled state, then its reward is a Double value.
     */
    private double mReward;

    /**
     * Constructor for the State object. It takes the row and column indices along with the
     * reward value as arguments.
     * @param reward
     * @param rowIndex
     * @param colIndex
     */
    public State(double reward, int rowIndex, int colIndex){
        /**
         * If the reward for the state is NaN (Not a number),
         * then it is a walled-state.
         */
        if(Double.isNaN(reward)){
            mIsWall = true;
        }
        else{
            mIsWall = false;
            mReward = reward;
        }
        mRowIndex = rowIndex;
        mColIndex = colIndex;
    }

    public int getRowIndex() {
        return mRowIndex;
    }

    public int getColIndex() {
        return mColIndex;
    }

    public boolean isWall(){
        return mIsWall;
    }

    public double getReward() {
        return mReward;
    }

    @Override
    public boolean equals(Object obj) {
        State otherState = (State) obj;
        return mRowIndex == otherState.getRowIndex() && mColIndex == otherState.getColIndex();
    }

    @Override
    public String toString() {
        if(mIsWall){
            return "Wall";
        }
        else{
            return Double.toString(mReward);
        }
    }
}
