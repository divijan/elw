package elw.vo;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.regex.Pattern;

//  LATER think of some other name for this entity, collision with java.lang smells
public class Class implements Cloneable, IdNamed {
    private static final DateTimeFormatter FMT_DATE = DateTimeFormat.forPattern("yyyy-MM-dd");
    private static final DateTimeFormatter FMT_DATE_NICE = DateTimeFormat.forPattern("EEE MMM dd");
    private static final DateTimeFormatter FMT_TIME = DateTimeFormat.forPattern("HH:mm");

    private String id;
    private String name;
    private String date;
    private String fromTime;
    private String toTime;
    private Pattern[] onSitePatterns;
    private String[] onSite;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() {
        return name == null ? getDate() + " " + getFromTime() : name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String id() {
        return getDate() + " " + getFromTime();
    }

    @JsonIgnore
    public boolean isCurrent() {
        return isStarted() && !isPassed();
    }

    @JsonIgnore
    public boolean isStarted() {
        return isStarted(new DateTime());
    }

    private boolean isStarted(final DateTime beforeTime) {
        final DateTime fromDateTime = getFromDateTime();
        return fromDateTime.isBefore(beforeTime);
    }

    @JsonIgnore
    public DateTime getFromDateTime() {
        return parseDateTime(getDate(), getFromTime());
    }

    @JsonIgnore
    public DateTime getToDateTime() {
        return parseDateTime(getDate(), getToTime());
    }

    public static DateTime parseDateTime(String dateStr, String timeStr) {
        final DateTime date = FMT_DATE.parseDateTime(dateStr);
        final DateTime time = FMT_TIME.parseDateTime(timeStr);
        final DateTime dateExact = new DateTime(
                date.getYear(), date.getMonthOfYear(), date.getDayOfMonth(),
                time.getHourOfDay(), time.getMinuteOfHour(),
                0, 0
        );

        return dateExact;
    }

    @JsonIgnore
    public boolean isPassed() {
        return isPassed(new DateTime());
    }

    private boolean isPassed(final DateTime beforeTime) {
        final DateTime toDateTime = getToDateTime();

        return toDateTime.isBefore(beforeTime);
    }

    @JsonIgnore
    public boolean isToday() {
        final DateTime fromDateTime = getFromDateTime();
        return fromDateTime.isBeforeNow() && fromDateTime.plusDays(1).isAfterNow();
    }

    public String getDate() {
        return date;
    }

    @JsonIgnore
    public String getNiceDate() {
        return FMT_DATE_NICE.print(getFromDateTime());
    }

    @JsonIgnore
    public int getDayDiff() {
        return getDayDiff(new DateTime());
    }

    public int computeToDiffStamp(Stamped stamped) {
        final DateTime time = stamped == null ? new DateTime() : new DateTime(stamped.getStamp());
        return computeToDiff(time);
    }

    public int computeToDiff(DateTime time) {
        final DateTime toDateTime = getToDateTime();
        final DateTime toMidnight = new DateTime(
                toDateTime.getYear(), toDateTime.getMonthOfYear(), toDateTime.getDayOfMonth(),
                0, 0, 0, 0
        );
        if (toMidnight.isAfter(time)) {
            return -Days.daysBetween(time, toMidnight).getDays() - 1;
        } else if (toMidnight.plusDays(1).isAfter(time)) {
            return 0;
        } else {
            return Days.daysBetween(toMidnight, time).getDays();
        }
    }
    
    private int getDayDiff(final DateTime toDate) {
        final DateTime date = getFromDateTime();
        final DateTime dateMidnight = new DateTime(
                date.getYear(), date.getMonthOfYear(), date.getDayOfMonth(),
                0, 0,
                0, 0
        );
        if (dateMidnight.isAfter(toDate)) {
            return Days.daysBetween(toDate, dateMidnight).getDays() + 1;
        } else if (dateMidnight.plusDays(1).isAfter(toDate)) {
            return 0;
        } else {
            return Days.daysBetween(dateMidnight, toDate).getDays();
        }
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFromTime() {
        return fromTime;
    }

    public void setFromTime(String fromTime) {
        this.fromTime = fromTime;
    }

    public String[] getOnSite() {
        return onSite;
    }

    public void setOnSite(String[] ipMasks) {
        this.onSite = ipMasks;
    }

    public String getToTime() {
        return toTime;
    }

    public void setToTime(String toTime) {
        this.toTime = toTime;
    }

    public int computeDaysOverdue(final Stamped stamped) {
        final DateTime stamp = stamped == null ? new DateTime() : new DateTime(stamped.getStamp());
        return getDaysOverdue(stamp);
    }

    private int getDaysOverdue(DateTime uploadStamp) {
        final int overdue;
        if (isPassed(uploadStamp)) {
            overdue = getDayDiff(uploadStamp);
        } else {
            overdue = 0;
        }
        return overdue;
    }

    public boolean checkOnSite(String sourceAddress) {
        synchronized (this) {
            if (onSitePatterns == null) {
                onSitePatterns = new Pattern[onSite.length];
                for (int i = 0; i < onSite.length; i++) {
                    onSitePatterns[i] = Pattern.compile(onSite[i]);
                }
            }
        }

        for (Pattern onSitePattern : onSitePatterns) {
            if (onSitePattern.matcher(sourceAddress).matches()) {
                return true;
            }
        }

        return false;
    }

    public boolean checkOnTime(Stamped stamed) {
        final long instant = stamed.getStamp();
        final long min = getFromDateTime().getMillis();
        final long max = getToDateTime().getMillis();
        final int lateTolerance = 30 * 60 * 1000;

        return min <= instant && instant <= max + lateTolerance;
    }

    @Override
    public Class clone() throws CloneNotSupportedException {
        Class clone = (Class) super.clone();

        clone.onSite = this.onSite.clone();

        return clone;
    }
}
