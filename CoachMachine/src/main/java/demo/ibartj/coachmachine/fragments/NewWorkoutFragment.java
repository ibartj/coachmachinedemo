package demo.ibartj.coachmachine.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import com.codetroopers.betterpickers.hmspicker.HmsPickerBuilder;
import com.codetroopers.betterpickers.hmspicker.HmsPickerDialogFragment;
import demo.ibartj.coachmachine.AppContext;
import demo.ibartj.coachmachine.MainActivity;
import demo.ibartj.coachmachine.R;
import demo.ibartj.coachmachine.model.Origin;
import demo.ibartj.coachmachine.model.Workout;
import demo.ibartj.coachmachine.service.ResultCallback;
import demo.ibartj.coachmachine.util.DoneClickListener;
import demo.ibartj.coachmachine.util.Formatter;

/**
 * @author Jan Bartovský
 * @version %I%, %G%
 */
public class NewWorkoutFragment extends Fragment implements DoneClickListener, HmsPickerDialogFragment.HmsPickerDialogHandler {
    private EditText editTitle;
    private EditText editPlace;
    private EditText editDuration;
    private SwitchCompat switchOrigin;
    private TextView labelOriginTitle;
    private TextView labelOriginDesc;
    private Workout workout = new Workout();

    public static NewWorkoutFragment newInstance() {
        return new NewWorkoutFragment();
    }

    public void setWorkout(Workout workout) {
        this.workout = workout;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_new_workout, container, false);
        editTitle = (EditText) rootView.findViewById(R.id.new_workout_title);
        editPlace = (EditText) rootView.findViewById(R.id.new_workout_place);
        editDuration = (EditText) rootView.findViewById(R.id.new_workout_duration);
        switchOrigin = (SwitchCompat) rootView.findViewById(R.id.new_workout_origin);
        labelOriginTitle = (TextView) rootView.findViewById(R.id.new_workout_label_origin_title);
        labelOriginDesc = (TextView) rootView.findViewById(R.id.new_workout_label_origin_desc);

        if (workout != null) {
            editTitle.setText(workout.getTitle());
            editPlace.setText(workout.getPlace());
            editDuration.setText(Formatter.duration(workout.getDuration()));
            switchOrigin.setChecked(workout.getOrigin() == Origin.SYNERGY);
        }

        setupDurationListener();
        setupSwitchListener();

        return rootView;
    }

    private void setupDurationListener() {
        editDuration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HmsPickerBuilder hpb = new HmsPickerBuilder()
                        .setFragmentManager(getChildFragmentManager())
                        .setStyleResId(R.style.BetterPickersDialogFragment)
                        .setTargetFragment(NewWorkoutFragment.this)
                        .setTimeInSeconds(workout.getDuration());
                hpb.show();
            }
        });
    }

    @Override
    public void onDialogHmsSet(int reference, int hours, int minutes, int seconds) {
        if (minutes > 60) {
            minutes -= 60;
            hours += 1;
        }
        if (seconds > 60) {
            seconds -= 60;
            minutes += 1;
        }
        workout.setDuration(hours * 60 * 60 + minutes * 60 + seconds);
        editDuration.setText(Formatter.duration(hours, minutes, seconds));
        editDuration.setError(null);
    }

    private void setupSwitchListener() {
        switchOrigin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                checkNetwork();
                updateSwitchState(b);
            }
        });
        updateSwitchState(switchOrigin.isChecked());
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkNetwork()) {
                    switchOrigin.setChecked(!switchOrigin.isChecked());
                }
            }
        };
        labelOriginTitle.setOnClickListener(listener);
        labelOriginDesc.setOnClickListener(listener);
        switchOrigin.setOnClickListener(listener);
    }

    private boolean checkNetwork() {
        if (!AppContext.getInstance().getWorkoutService().isNetworkConnected()) {
            switchOrigin.setChecked(false);
            ((MainActivity) getActivity()).showErrorMessage(
                    R.string.new_workout_network_not_available_title,
                    getResources().getString(R.string.new_workout_network_not_available)
            );
            return false;
        }
        return true;
    }

    private void updateSwitchState(boolean b) {
        labelOriginTitle.setText(b ? R.string.new_workout_label_origin_cloud_title : R.string.new_workout_label_origin_db_title);
        labelOriginDesc.setText(b ? R.string.new_workout_label_origin_cloud_desc : R.string.new_workout_label_origin_db_desc);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(R.string.title_new_workout);
    }

    @Override
    public void onDoneButtonClicked() {
        boolean valid = true;
        if (editTitle.getText().length() == 0) {
            editTitle.setError(getResources().getString(R.string.new_workout_error_title_empty));
            valid = false;
        }
        if (editPlace.getText().length() == 0) {
            editPlace.setError(getResources().getString(R.string.new_workout_error_place_empty));
            valid = false;
        }
        if (workout.getDuration() == 0) {
            editDuration.setError(getResources().getString(R.string.new_workout_error_duration_empty));
            valid = false;
        }
        if (valid) {
            createWorkout();
        }
    }

    private void createWorkout() {
        workout.setTitle(editTitle.getText().toString());
        workout.setPlace(editPlace.getText().toString());
        workout.setOrigin(switchOrigin.isChecked() ? Origin.SYNERGY : Origin.DB);
        workout.setUser(((MainActivity) getActivity()).getUsername());
        AppContext.getInstance().getWorkoutService().createNewWorkout(workout, new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void object) {
                openWorkoutList();
            }

            @Override
            public void onFailure(Exception ex) {
                ((MainActivity) getActivity()).showErrorMessage(
                        R.string.error_creating_workout_title,
                        getResources().getString(R.string.error_creating_workout)
                );
            }
        });
    }

    private void openWorkoutList() {
        ((MainActivity) getActivity()).startWorkoutListFragment();
    }

    public Workout getWorkout() {
        workout.setTitle(editTitle.getText().toString());
        workout.setPlace(editPlace.getText().toString());
        workout.setOrigin(switchOrigin.isChecked() ? Origin.SYNERGY : Origin.DB);
        return workout;
    }
}
