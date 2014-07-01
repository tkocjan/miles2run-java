package org.miles2run.business.vo;

import org.miles2run.business.domain.GoalUnit;
import org.miles2run.business.domain.Share;

import java.util.Date;
import java.util.Map;

/**
 * Created by shekhargulati on 11/03/14.
 */
public class ActivityDetails {
    private Long goalId;
    private Long id;

    private String status;

    private long distanceCovered;

    private GoalUnit goalUnit;

    private Date activityDate;

    private Share share;

    private String fullname;

    private String username;

    private String profilePic;

    private long duration;

    private Date postedAt;

    private String durationStr;

    public ActivityDetails(Long id, String status, long distanceCovered, GoalUnit goalUnit, Date activityDate, Share share, String fullname, long duration, String username, String profilePic, Date postedAt) {
        this.id = id;
        this.status = status;
        this.goalUnit = goalUnit;
        this.distanceCovered = distanceCovered / goalUnit.getConversion();
        this.activityDate = activityDate;
        this.share = share;
        this.fullname = fullname;
        this.username = username;
        this.profilePic = profilePic;
        this.duration = duration;
        this.postedAt = postedAt;
        this.durationStr = toDurationText(duration);
    }

    private String toDurationText(long duration) {
        long secondsInMinute = 60;
        long minutesInHour = 60;
        long secondsInHour = minutesInHour * secondsInMinute;
        long hours = duration / secondsInHour;
        long minutes = (duration - (hours * secondsInHour)) / secondsInMinute;
        long seconds = duration - (hours * secondsInHour) - (minutes * secondsInMinute);
        String hoursText = toText(hours);
        String minutesText = toText(minutes);
        String secondsText = toText(seconds);
        return new StringBuilder(hoursText).append(":")
                .append(minutesText).append(":")
                .append(secondsText)
                .toString();
    }

    private String toText(long val) {
        return val < 10 ? "0" + val : String.valueOf(val);
    }


    public ActivityDetails(Map<String, String> hash) {
        this.id = Long.valueOf(hash.get("id"));
        this.username = hash.get("username");
        this.activityDate = new Date(Long.valueOf(hash.get("posted")));
        this.goalUnit = GoalUnit.fromStringToGoalUnit(hash.get("goalUnit"));
        this.distanceCovered = Long.valueOf(hash.get("distanceCovered")) / this.goalUnit.getConversion();
        this.fullname = hash.get("fullname");
        this.profilePic = hash.get("profilePic");
        this.status = hash.get("status");
        this.goalId = Long.valueOf(hash.get("goalId"));
    }


    public Long getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public long getDistanceCovered() {
        return distanceCovered;
    }

    public GoalUnit getGoalUnit() {
        return goalUnit;
    }

    public Date getActivityDate() {
        return activityDate;
    }

    public Share getShare() {
        return share;
    }

    public String getFullname() {
        return fullname;
    }

    public String getUsername() {
        return username;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public long getDuration() {
        return duration;
    }


    public Long getGoalId() {
        return goalId;
    }


    public Date getPostedAt() {
        return postedAt;
    }

    public String getDurationStr() {
        return durationStr;
    }

    @Override
    public String toString() {
        return "ActivityDetails{" +
                "id=" + id +
                ", status='" + status + '\'' +
                ", distanceCovered=" + distanceCovered +
                ", goalUnit=" + goalUnit +
                ", activityDate=" + activityDate +
                ", share=" + share +
                ", fullname='" + fullname + '\'' +
                ", username='" + username + '\'' +
                ", profilePic='" + profilePic + '\'' +
                ", duration=" + duration +
                '}';
    }
}
