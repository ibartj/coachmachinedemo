package demo.ibartj.coachmachine.service;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import com.letsgood.synergykitsdkandroid.Synergykit;
import com.letsgood.synergykitsdkandroid.builders.UriBuilder;
import com.letsgood.synergykitsdkandroid.builders.uri.Resource;
import com.letsgood.synergykitsdkandroid.config.SynergykitConfig;
import com.letsgood.synergykitsdkandroid.listeners.RecordsResponseListener;
import com.letsgood.synergykitsdkandroid.listeners.ResponseListener;
import com.letsgood.synergykitsdkandroid.resources.SynergykitError;
import com.letsgood.synergykitsdkandroid.resources.SynergykitObject;
import com.letsgood.synergykitsdkandroid.resources.SynergykitUri;
import demo.ibartj.coachmachine.AppContext;
import demo.ibartj.coachmachine.dao.WorkoutSqliteDbDao;
import demo.ibartj.coachmachine.exceptions.SynergyKitException;
import demo.ibartj.coachmachine.model.Origin;
import demo.ibartj.coachmachine.model.Workout;
import demo.ibartj.coachmachine.util.WorkoutSortComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Jan Bartovský
 * @version %I%, %G%
 */
public class WorkoutService {
    private WorkoutSqliteDbDao workoutSqliteDbDao = new WorkoutSqliteDbDao();

    public void getMyWorkouts(final String username, final ResultCallback<List<Workout>> callback) {
        new AsyncRequest<List<Workout>>() {
            @Override
            protected List<Workout> doInBackground(Void... params) {
                return workoutSqliteDbDao.getByUsername(username);
            }
        }.fire(new ResultCallback<List<Workout>>() {
            @Override
            public void onSuccess(final List<Workout> dbWorkouts) {
                getWorkoutsFromDbOnComplete(dbWorkouts, callback);
            }

            @Override
            public void onFailure(Exception ex) {
                getWorkoutsFromDbOnComplete(new ArrayList<Workout>(), callback);
            }
        });
    }

    private void getWorkoutsFromDbOnComplete(final List<Workout> dbWorkouts, final ResultCallback<List<Workout>> callback) {
        if (AppContext.getInstance().getWorkoutService().isNetworkConnected()) {
            getWorkoutsFromSynergyKit(new ResultCallback<List<Workout>>() {
                @Override
                public void onSuccess(List<Workout> synWorkouts) {
                    dbWorkouts.addAll(synWorkouts);
                    Collections.sort(dbWorkouts, new WorkoutSortComparator());
                    callback.onSuccess(dbWorkouts);
                }

                @Override
                public void onFailure(Exception ex) {
                    Collections.sort(dbWorkouts, new WorkoutSortComparator());
                    callback.onSuccess(dbWorkouts);
                }
            });
        } else {
            Collections.sort(dbWorkouts, new WorkoutSortComparator());
            callback.onSuccess(dbWorkouts);
        }
    }

    private void getWorkoutsFromSynergyKit(final ResultCallback<List<Workout>> callback) {
        SynergykitUri synergykitUri = UriBuilder
                .newInstance()
                .setResource(Resource.RESOURCE_DATA)
                .setCollection("Workouts")
                .build();

        SynergykitConfig config = SynergykitConfig
                .newInstance()
                .setParallelMode(false)
                .setType(Workout[].class)
                .setUri(synergykitUri);

        Synergykit.getRecords(config, new RecordsResponseListener() {
            @Override
            public void doneCallback(int statusCode, SynergykitObject[] objects) {
                ArrayList<Workout> workouts = new ArrayList<>();
                for (SynergykitObject object : objects) {
                    Workout workout = (Workout) object;
                    workout.setOrigin(Origin.SYNERGY);
                    workouts.add(workout);
                }
                callback.onSuccess(workouts);
            }

            @Override
            public void errorCallback(int statusCode, SynergykitError errorObject) {
                callback.onFailure(new SynergyKitException(errorObject));
            }
        });
    }

    public void createNewWorkout(final Workout workout, final ResultCallback<Void> callback) {
        if (workout.getOrigin() == Origin.SYNERGY) {
            Synergykit.createRecord("Workouts", workout, new ResponseListener() {
                @Override
                public void doneCallback(int statusCode, SynergykitObject object) {
                    callback.onSuccess(null);
                }

                @Override
                public void errorCallback(int statusCode, SynergykitError errorObject) {
                    callback.onFailure(new SynergyKitException(errorObject));
                }
            }, true);
        } else {
            new AsyncRequest<Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    workoutSqliteDbDao.insert(workout);
                    return null;
                }
            }.fire(callback);
        }
    }

    public boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) AppContext.getInstance().getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Network[] networks = connectivityManager.getAllNetworks();
            NetworkInfo networkInfo;
            for (Network network : networks) {
                networkInfo = connectivityManager.getNetworkInfo(network);
                if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                    return true;
                }
            }
        } else {
            //noinspection deprecation
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            if (info != null) {
                for (NetworkInfo anInfo : info) {
                    if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
