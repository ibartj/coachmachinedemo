package demo.ibartj.coachmachine.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import demo.ibartj.coachmachine.AppContext;
import demo.ibartj.coachmachine.R;
import demo.ibartj.coachmachine.model.Origin;
import demo.ibartj.coachmachine.model.Workout;

import java.util.List;

/**
 * @author Jan Bartovský
 * @version %I%, %G%
 */
public class WorkoutArrayAdapter extends ArrayAdapter<Workout> {
    private List<Workout> items;

    public WorkoutArrayAdapter(Context context, List<Workout> items) {
        super(context, R.layout.workout_list_item, items);
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Workout getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RelativeLayout listItem;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            listItem = (RelativeLayout) inflater.inflate(R.layout.workout_list_item, null);
        } else {
            listItem = (RelativeLayout) convertView;
        }

        TextView titleText = (TextView) listItem.findViewById(R.id.workout_list_title);
        TextView durationText = (TextView) listItem.findViewById(R.id.workout_list_duration);
        TextView placeText = (TextView) listItem.findViewById(R.id.workout_list_place);

        Workout workout = items.get(position);
        titleText.setText(workout.getTitle());
        durationText.setText(Formatter.duration(workout.getDuration()));
        placeText.setText(workout.getPlace());

        listItem.setBackgroundColor(
                ContextCompat.getColor(
                        AppContext.getInstance().getContext(),
                        workout.getOrigin() == Origin.DB ? R.color.list_item_bg_db : R.color.list_item_bg_synergy
                )
        );

        return listItem;
    }
}
