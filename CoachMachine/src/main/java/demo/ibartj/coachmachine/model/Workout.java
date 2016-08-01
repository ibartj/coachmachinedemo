package demo.ibartj.coachmachine.model;

import com.google.gson.annotations.Expose;
import com.letsgood.synergykitsdkandroid.resources.SynergykitObject;

import java.io.Serializable;
import java.util.Calendar;

/**
 * @author Jan Bartovský
 * @version %I%, %G%
 */
public class Workout extends SynergykitObject implements Serializable {
    @Expose
    private String title;
    @Expose
    private String place;
    @Expose
    private Integer duration = 0;
    @Expose
    private String user;

    private Origin origin = Origin.DB;

    public Workout() {
        super();
        setCreatedAt(Calendar.getInstance().getTimeInMillis());
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Origin getOrigin() {
        return origin;
    }

    public void setOrigin(Origin origin) {
        this.origin = origin;
    }

    @SuppressWarnings("unused")
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @SuppressWarnings("unused")
    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }
}
