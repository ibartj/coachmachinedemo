package demo.ibartj.coachmachine.util;

import demo.ibartj.coachmachine.model.Workout;

import java.util.Comparator;

/**
 * Created by Jan on 20.12.2015.
 */
public class WorkoutSortComparator implements Comparator<Workout> {
    @Override
    public int compare(Workout w1, Workout w2) {
        return -((Long) w1.getCreatedAt()).compareTo((Long) w2.getCreatedAt());
    }
}