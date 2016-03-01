package ransac3;

import java.util.*;

/**
 * @author tigerlihao
 *
 * @param <T>
 *            Class of the data
 * @param <S>
 *            Class of the parameter
 */
public class AnotherRansac<T, S> {
    private List<S> parameters = null;
    private ParameterEstimator<T, S> paramEstimator;
    private boolean[] bestVotes;
    private int numForEstimate;
    private double maximalOutlierPercentage;

    /**
     * @return The best inlier set.
     */
    public boolean[] getBestVotes() {
        return bestVotes;
    }

    /**
     * @return The best model parameters.
     */
    public List<S> getParameters() {
        return parameters;
    }

    /**
     * This is the constructor of the new ransac object.
     *
     * @param paramEstimator
     *            The parameter estimator to be use.
     * @param numForEstimate
     *            The minimal data record number needed to estimate model
     *            parameters.
     * @param maximalOutlierPercentage
     *            The maximal outlier percentage.
     */
    public AnotherRansac(ParameterEstimator<T, S> paramEstimator, int numForEstimate,
                  double maximalOutlierPercentage) {
        this.paramEstimator = paramEstimator;
        this.numForEstimate = numForEstimate;
        this.maximalOutlierPercentage = maximalOutlierPercentage;
    }

    /**
     * This method is used to run the RANSAC process.
     *
     * @param data
     *            The data sample.
     * @param desiredProbabilityForNoOutliers
     *            The probability needed in the estimation.
     * @return The percentage of data used to estimate the best result.
     */
    public double compute(List<T> data, double desiredProbabilityForNoOutliers) {
        int dataSize = data.size();
        if (dataSize < numForEstimate || maximalOutlierPercentage >= 1.0) {
            return 0.0;
        }
        List<T> exactedData = new ArrayList<T>();
        List<T> leastSqData;
        List<S> exactedParams;
        int bestSize, curSize, tryTimes;
        bestVotes = new boolean[dataSize];
        boolean[] curVotes = new boolean[dataSize];
        boolean[] notChosen = new boolean[dataSize];
        Set<int[]> chosenSubSets = new HashSet<int[]>();
        int[] curSubSetIndexes;
        double outlierPercentage = maximalOutlierPercentage;
        double numerator = Math.log(1.0 - desiredProbabilityForNoOutliers);
        double denominator = Math.log(1 - Math.pow(
                1 - maximalOutlierPercentage, numForEstimate));
        if (parameters != null) {
            parameters.clear();
        } else {
            parameters = new ArrayList<S>();
        }
        bestSize = -1;
        Random random = new Random(new Date().getTime());
        tryTimes = (int) Math.round(numerator / denominator);
        for (int i = 0; i < tryTimes; i++) {
            // initiate a new iterator
            for (int j = 0; j < notChosen.length; j++) {
                notChosen[j] = true;
            }
            curSubSetIndexes = new int[numForEstimate];
            exactedData.clear();
            // randomly select data
            for (int j = 0; j < numForEstimate; j++) {
                int selectedIndex = random.nextInt(dataSize - j);
                int k, l;
                for (k = 0, l = -1; k < dataSize && l < selectedIndex; k++) {
                    if (notChosen[k]) {
                        l++;
                    }
                }
                k--;
                exactedData.add(data.get(k));
                notChosen[k] = false;
            }
            for (int j = 0, k = 0; j < dataSize; j++) {
                if (!notChosen[j]) {
                    curSubSetIndexes[k] = j;
                    k++;
                }
            }
            // If the subset is not selected, test it.
            if (chosenSubSets.add(curSubSetIndexes)) {
                exactedParams = paramEstimator.estimate(exactedData);
                // see how many agree on this estimate
                curSize = 0;
                for (int j = 0; j < notChosen.length; j++) {
                    curVotes[j] = false;
                }
                for (int j = 0; j < dataSize; j++) {
                    if (paramEstimator.agree(exactedParams, data.get(j))) {
                        curVotes[j] = true;
                        curSize++;
                    }
                }
                if (curSize > bestSize) {
                    bestSize = curSize;
                    System.arraycopy(curVotes, 0, bestVotes, 0, dataSize);
                }
                // update the estimate of outliers and the number of iterations
                // we need
                outlierPercentage = 1.0 - (double) curSize / (double) dataSize;
                if (outlierPercentage < maximalOutlierPercentage) {
                    maximalOutlierPercentage = outlierPercentage;
                    denominator = Math.log(1 - Math.pow(
                            1 - maximalOutlierPercentage, numForEstimate));
                    tryTimes = (int) Math.round(numerator / denominator);
                }
            } else {
                i--;
            }
            System.out.print("\rFor 1: " + i + " of " + tryTimes);
        }
        System.out.println();
        chosenSubSets.clear();
        // compute the least squares estimate using the best subset
        leastSqData = new ArrayList<T>();
        for (int i = 0; i < dataSize; i++) {
            if (bestVotes[i]) {
                leastSqData.add(data.get(i));
            }
            System.out.print("\rFor 2: " + i + " of " + dataSize);
        }
        parameters = paramEstimator.leastSquaresEstimate(leastSqData);

        return (double) bestSize / (double) dataSize;
    }
}