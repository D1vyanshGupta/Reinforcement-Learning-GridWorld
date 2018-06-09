import java.util.HashMap;

public class Action {

    /**
     * Every Action object has an "Intended Direction".
     * However, according to the model of the world,
     * the agent can go along the "directions at either right angles"
     * to the intended directions.
     */

    /**
     * Hash Map between intended direction and a list of other possible
     * directions that the agent can choose.
     */
    private static final HashMap<String, String[]> ACTION_MAP = new HashMap<String, String[]>();

    /**
     * Initialize the hash map between intended direction and the other
     * possible directions that the agent can choose.
     */
    static{
        ACTION_MAP.put("UP", new String[]{"LEFT", "RIGHT"});
        ACTION_MAP.put("RIGHT", new String[]{"UP", "DOWN"});
        ACTION_MAP.put("DOWN", new String[]{"LEFT", "RIGHT"});
        ACTION_MAP.put("LEFT", new String[]{"UP", "DOWN"});
    }

    /**
     * Intended direction for the action.
     */
    private String mIntendedDirection;

    /**
     * Hash map between all possible directions that the agent can take,
     * and the probabilities of agent choosing these directions.
     */
    private HashMap<String, Double> mDirectionProbMap;

    /**
     * Constructor for the Action object. It takes the intended direction as argument.
     * @param direction
     */
    public Action(String direction){
        mIntendedDirection = direction;
        initializeActionProbMap();
    }

    public void initializeActionProbMap(){
        mDirectionProbMap = new HashMap<String, Double>();

        /**
         * Intended direction is chosen with a probability of 0.8.
         */
        mDirectionProbMap.put(mIntendedDirection, 0.8);

        /**
         * Unintended directions at either right angles can be chosen with a probability of 0.1 each.
         */
        for(String possibleDirection : ACTION_MAP.get(mIntendedDirection)){
            mDirectionProbMap.put(possibleDirection, 0.1);
        }
    }

    public String getIntendedAction() {
        return mIntendedDirection;
    }

    public HashMap<String, Double> getDirectionProbMap() {
        return mDirectionProbMap;
    }

    @Override
    public String toString() {
        return mIntendedDirection;
    }
}
