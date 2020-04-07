package io.jenkins.plugins.grading;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Stores the scores of an autograding run. Persists the configuration and the scores for each metric.
 *
 * @author Eva-Maria Zeintl
 * @author Ullrich Hafner
 */
public class Score {
    static final String EXCELLENT = "progress-bg-excellent";
    static final String GOOD = "progress-bg-good";
    static final String FAILURE = "progress-bg-failure";

    private int total;
    private int achieved;

    private AnalysisConfiguration analysisConfiguration;
    private final List<AnalysisScore> analysisScores = new ArrayList<>();
    private TestConfiguration testsConfiguration;
    private TestScore testsScore;
    private CoverageConfiguration coverageConfiguration;
    private CoverageScore coverageScore;
    private PitConfiguration pitConfiguration;
    private PitScore pitScore;

    /**
     * Returns the number of achieved points.
     *
     * @return the number of achieved points
     */
    public int getAchieved() {
        return achieved;
    }

    /**
     * Returns the total number of points that could be achieved.
     *
     * @return the total number of points that could be achieved
     */
    public int getTotal() {
        return total;
    }

    /**
     * Returns the success ratio, i.e. number of achieved points divided by total points.
     *
     * @return the success ration
     */
    public int getRatio() {
        return achieved * 100 / total;
    }

    /**
     * Returns a styling class that will be used to render the success progress bar.
     *
     * @return a styling class
     */
    public String getStyle() {
        if (getRatio() < 50) {
            return FAILURE;
        }
        else if (getRatio() < 75) {
            return GOOD;
        }
        return EXCELLENT;
    }

    public AnalysisConfiguration getAnalysisConfiguration() {
        return analysisConfiguration;
    }

    public List<AnalysisScore> getAnalysisScores() {
        return analysisScores;
    }

    public TestConfiguration getTestConfiguration() {
        return testsConfiguration;
    }

    public List<TestScore> getTestScores() {
        return Collections.singletonList(testsScore);
    }

    public CoverageConfiguration getCoverageConfiguration() {
        return coverageConfiguration;
    }

    public List<CoverageScore> getCoverageScores() {
        return Collections.singletonList(coverageScore);
    }

    public PitConfiguration getPitConfiguration() {
        return pitConfiguration;
    }

    public List<PitScore> getPitScores() {
        return Collections.singletonList(pitScore);
    }

    /**
     * Adds the specified collection of analysis grading scores.
     *
     * @param configuration
     *         the grading configuration
     * @param scores
     *         the scores to take into account
     *
     * @return the total score impact (limited by the {@code maxScore} parameter of the configuration)
     */
    public int addAnalysisTotal(final AnalysisConfiguration configuration, final List<AnalysisScore> scores) {
        analysisScores.addAll(scores);
        analysisConfiguration = configuration;

        int delta = 0;
        for (AnalysisScore score : scores) {
            delta = delta + score.getTotalImpact();
        }

        return updateScore(analysisConfiguration.getMaxScore(), delta);
    }

    /**
     * Adds a test grading score.
     *
     * @param configuration
     *         the grading configuration
     * @param score
     *         the score to take into account
     *
     * @return the total score impact (limited by the {@code maxScore} parameter of the configuration)
     */
    public int addTestsTotal(final TestConfiguration configuration, final TestScore score) {
        testsScore = score;
        testsConfiguration = configuration;

        return updateScore(configuration.getMaxScore(), testsScore.getTotalImpact());
    }

    /**
     * Adds a coverage grading score.
     *
     * @param configuration
     *         the grading configuration
     * @param score
     *         the score to take into account
     *
     * @return the total score impact (limited by the {@code maxScore} parameter of the configuration)
     */
    // TODO: replace with a concept similar to the analysis configuration (line and branch coverage)
    public int addCoverageTotal(final CoverageConfiguration configuration, final CoverageScore score) {
        this.coverageScore = score;
        this.coverageConfiguration = configuration;

        return updateScore(configuration.getMaxScore(), score.getTotalImpact());
    }

    /**
     * Adds a PIT mutation testing grading score.
     *
     * @param configuration
     *         the grading configuration
     * @param score
     *         the score to take into account
     *
     * @return the total score impact (limited by the {@code maxScore} parameter of the configuration)
     */
    public int addPitTotal(final PitConfiguration configuration, final PitScore score) {
        this.pitConfiguration = configuration;
        this.pitScore = score;

        return updateScore(configuration.getMaxScore(), score.getTotalImpact());
    }

    private int updateScore(final int maxScore, final int totalChange) {
        total += maxScore;

        int actual;
        if (totalChange <= 0) {
            actual = Math.max(0, maxScore + totalChange);
        }
        else {
            actual = Math.min(maxScore, totalChange);
        }
        achieved += actual;

        return actual;
    }
}
