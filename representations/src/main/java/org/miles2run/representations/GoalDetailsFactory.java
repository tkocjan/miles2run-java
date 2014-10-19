package org.miles2run.representations;

import org.miles2run.domain.entities.Goal;

/**
 * TODO: can we use CDI for factory pattern
 */
public abstract class GoalDetailsFactory {

    public static GoalDetails toGoalType(Goal goal, double totalDistanceCoveredForGoal) {
        switch (goal.getGoalType()) {
            case DURATION_GOAL:
                return new DurationGoalDetails(goal);
            case DISTANCE_GOAL:
                return new DistanceGoalDetails(goal, totalDistanceCoveredForGoal);
            case COMMUNITY_RUN_GOAL:
                return new CommunityRunGoalDetails(goal);
            default:
                return null;
        }
    }

}