import javafx.util.Pair;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        /**
         * Part I: Using Policy Iteration and Value Iteration to calculate the optimal policy
         *         and the utility values for the non-walled states.
         */
        /**
         * The reward array given in the question.
         */
        double[] rewardArray = {1, Double.NaN, 1, -0.04, -0.04, 1,
                                -0.04, -1, -0.04, 1, Double.NaN, -1,
                                -0.04, -0.04, -1, -0.04, 1, -0.04,
                                -0.04, -0.04, -0.04, -1, -0.04, 1,
                                -0.04, Double.NaN, Double.NaN, Double.NaN, -1, -0.04,
                                -0.04, -0.04, -0.04, -0.04, -0.04, -0.04
                          };
        /**
         * Instantiate a GridWorld object.
         */
        GridWorld gridWorld = new GridWorld(6, rewardArray);

        /**
         * Invoke Policy Iteration on the GridWorld.
         */
        gridWorld.policyIteration(true, true, 0.1);

        /**
         * Invoke Value Iteration on the GridWorld.
         */
        gridWorld.valueIteration(true, true, 0.2);

        /**
         * Part II: Designing more complicated maze environments and
         *          re-running the algorithms designed in Part I on them.
         */

        /**
         * An ArrayList of pairs of precision values is constructed for.
         * The first value in the pair is used to check for utility convergence in policy evaluation.
         * The second value in the pair is used to check for utility convergence in value iteration.
         */

        /**
         * Get a list of pair of epsilon values to check for convergence in Policy Evaluation
         * and Value Iteration.
         */
        ArrayList<Pair<Double, Double>> epsilonPairList = getEpsilonPairList();

        /**
         * Try to find optimal policies in random and more complex GridWorlds.
         */
        findPolicyInRandomWorlds(epsilonPairList);
    }

    /**
     * Create random GridWorlds and try to find the optimal policies via both Policy Iteration and
     * Value Iteration.
     * @param epsilonPairList
     */
    private static void findPolicyInRandomWorlds(ArrayList<Pair<Double, Double>> epsilonPairList){

        GridWorld randomGridWorld;
        double[] randomRewardArray;

        ArrayList<String> solvedMDPs;
        ArrayList<String> unSolvedMDPs;

        int numPolicyIter;
        int numValueIter;

        for(Pair<Double, Double> epsilonPair : epsilonPairList){

            solvedMDPs = new ArrayList<String>();
            unSolvedMDPs = new ArrayList<String>();

            for(int numRows = 10; numRows <= 20; numRows += 2){
                for(int numCols = 10; numCols <= 20; numCols += 2){
                    randomRewardArray = getRandomRewardArray(numRows, numCols);
                    randomGridWorld = new GridWorld(numRows, numCols, randomRewardArray);

                    numPolicyIter = randomGridWorld.policyIteration(false, false, epsilonPair.getKey());
                    numValueIter = randomGridWorld.valueIteration(false, false, epsilonPair.getValue());

                    if(randomGridWorld.mPolicyIterationMap.equals(randomGridWorld.mValueIterationMap)){
                        solvedMDPs.add("(" + numRows + "x" + numCols + ") = " + numRows * numCols +
                                " Policy Iteration = " + numPolicyIter + " Value Iteration = " + numValueIter);
                    }
                    else{
                        unSolvedMDPs.add("(" + numRows + "x" + numCols + ") = " + numRows * numCols);
                    }

                }
            }

            System.out.println("Precision Values: (" + epsilonPair.getKey() + ", " + epsilonPair.getValue() + ") Solved = " +
                    solvedMDPs.size() + " Unsolved = " + unSolvedMDPs.size());

            if(solvedMDPs.size() != 0)
                System.out.println("Solved MDPs");

            for(String stringIterator : solvedMDPs)
                System.out.println(stringIterator);

            if(unSolvedMDPs.size() != 0)
                System.out.println("\nUnsolved MDPs");

            for(String stringIterator : unSolvedMDPs)
                System.out.print(stringIterator + " ");

            System.out.println("\n");
        }

    }

    /**
     * Returns a list of randomly selected state rewards for a GridWorld object.
     * @param numRows
     * @param numCols
     * @return an array of reward values for State objects.
     */
    private static double[] getRandomRewardArray(int numRows, int numCols){
        int numItems = numRows * numCols;
        double[] rewardArray = new double[numItems];

        for(int i = 0; i < numItems; ++i){
            rewardArray[i] = randomFill();
        }
        return rewardArray;
    }

    /**
     * Randomly returns a reward value for a State object.
     * @return
     */
    private static double randomFill(){
        int randNum = (int)(Math.random() * 4);

        switch(randNum){
            case 0:{
                return Double.NaN;
            }
            case 1:{
                return 1.0;
            }
            case 2:{
                return -1.0;
            }
            default: {
                return -0.04;
            }
        }
    }

    /**
     * Return a list of pairs of epsilon values to be used for convergence tests in Policy Evaluation Step
     * and Value Iteration Steps.
     * @return
     */
    private static ArrayList<Pair<Double, Double>> getEpsilonPairList(){
        ArrayList<Pair<Double, Double>> epsilonPairList = new ArrayList<Pair<Double, Double>>();
        epsilonPairList.add(new Pair<Double, Double>(0.1, 0.2));
        epsilonPairList.add(new Pair<Double, Double>(0.01, 0.01));
        epsilonPairList.add(new Pair<Double, Double>(0.001, 0.001));
        epsilonPairList.add(new Pair<Double, Double>(0.00001, 0.00001));
        epsilonPairList.add(new Pair<Double, Double>(0.000001, 0.000001));
        epsilonPairList.add(new Pair<Double, Double>(0.0000001, 0.0000001));
        epsilonPairList.add(new Pair<Double, Double>(0.000000000001, 0.000000000001));

        return epsilonPairList;
    }

}
