package demo.ibartj.coachmachine.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import demo.ibartj.coachmachine.AppContext;
import demo.ibartj.coachmachine.MainActivity;
import demo.ibartj.coachmachine.R;
import demo.ibartj.coachmachine.model.Workout;
import demo.ibartj.coachmachine.service.ResultCallback;
import demo.ibartj.coachmachine.util.WorkoutArrayAdapter;

import java.util.List;

/**
 * @author Jan Bartovský
 * @version %I%, %G%
 */
public class WorkoutListFragment extends Fragment {
    private ListView workoutList;
    private ArrayAdapter<Workout> workoutListAdapter;
    private TextView workoutListEmpty;
    private FloatingActionButton newWorkoutFab;

    public static WorkoutListFragment newInstance() {
        return new WorkoutListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_workout_list, container, false);
        if (rootView != null) {
            setupFABListener(rootView);
            workoutList = (ListView) rootView.findViewById(R.id.workout_list);
            workoutListEmpty = (TextView) rootView.findViewById(R.id.workout_list_empty);
        }
        setupWorkouts();
        return rootView;
    }

    private void setupFABListener(View v) {
        newWorkoutFab = (FloatingActionButton) v.findViewById(R.id.new_workout_fab);
        newWorkoutFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).startNewWorkoutFragment();
            }
        });
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(R.string.title_workout_list);
    }

    private void showNoUsername() {
        if (workoutList != null) {
            workoutList.setVisibility(View.INVISIBLE);
            WorkoutListFragment.this.workoutListEmpty.setText(R.string.workout_list_nouser);
            WorkoutListFragment.this.workoutListEmpty.setVisibility(View.VISIBLE);
            newWorkoutFab.setVisibility(View.GONE);
            WorkoutListFragment.this.workoutListEmpty.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((MainActivity) getActivity()).initUsername();
                }
            });
        }
    }

    private void setupWorkouts() {
        if (newWorkoutFab == null) {
            return;
        }
        String username = ((MainActivity) getActivity()).getUsername();
        if (username == null) {
            showNoUsername();
            return;
        } else {
            newWorkoutFab.setVisibility(View.VISIBLE);
        }
        ((MainActivity) getActivity()).showProgressDialog(R.string.loading);
        AppContext.getInstance().getWorkoutService().getMyWorkouts(username, new ResultCallback<List<Workout>>() {
            @Override
            public void onSuccess(List<Workout> workouts) {
                if (getActivity() != null) {
                    ((MainActivity) getActivity()).hideProgressDialog();
                    if (workoutListAdapter == null) {
                        workoutListAdapter = new WorkoutArrayAdapter(AppContext.getInstance().getContext(), workouts);
                        //workoutList.setOnItemClickListener(...);
                        workoutList.setAdapter(workoutListAdapter);
                    } else {
                        workoutListAdapter.clear();
                        workoutListAdapter.addAll(workouts);
                        workoutListAdapter.notifyDataSetChanged();
                    }
                    boolean emptyList = workouts.isEmpty();
                    workoutList.setVisibility(emptyList ? View.INVISIBLE : View.VISIBLE);
                    WorkoutListFragment.this.workoutListEmpty.setText(
                            AppContext.getInstance().getWorkoutService().isNetworkConnected()
                                    ? R.string.workout_list_empty
                                    : R.string.workout_list_not_connected
                    );
                    WorkoutListFragment.this.workoutListEmpty.setVisibility(emptyList ? View.VISIBLE : View.GONE);
                    WorkoutListFragment.this.workoutListEmpty.setOnClickListener(null);
                }
            }

            @Override
            public void onFailure(Exception ex) {
                if (getActivity() != null) {
                    ((MainActivity) getActivity()).hideProgressDialog();
                }
            }
        });
    }
}