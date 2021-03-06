package elw.vo;

import java.util.*;

import org.akraievoy.couch.Squab;

public class Enrollment extends Squab implements IdNamed, Cloneable {
    private String id;
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    private String name;
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    private String groupId;
    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }

    private String courseId;
    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }
    
    private String timeZone;
    public String getTimeZone() { return timeZone; }
    public void setTimeZone(String timeZone) { this.timeZone = timeZone; }

    private SortedMap<String, Class> classes = new TreeMap<String, Class>();
    public SortedMap<String, Class> getClasses() {
        return Collections.unmodifiableSortedMap(classes);
    }
    public void setClasses(SortedMap<String, Class> classes) {
        this.classes.clear();
        if (classes != null) {
            this.classes.putAll(classes);
        }
    }

    private SortedMap<String, IndexEntry> index = new TreeMap<String, IndexEntry>();
    public SortedMap<String, IndexEntry> getIndex() {
        return Collections.unmodifiableSortedMap(index);
    }
    public void setIndex(SortedMap<String, IndexEntry> index) {
        this.index.clear();
        if (index != null) {
            this.index.putAll(index);
        }
    }

    @Override
    protected String[] pathElems() {
        return new String[] { groupId, courseId, id };
    }

    public boolean checkOnTime(elw.vo.Stamped stamped) {
        for (Class aClass : classes.values()) {
            if (aClass.checkOnTime(stamped)) {
                return true;
            }
        }

        return false;
    }

    public int cmpTotalBudget() {
        int totalBudget = 0;
        for (IndexEntry ie : getIndex().values()) {
            totalBudget += ie.getScoreBudget();
        }
        return totalBudget;
    }

    @Override
    public Enrollment clone() throws CloneNotSupportedException {
        Enrollment clone = (Enrollment) super.clone();

        clone.index = new TreeMap<String, IndexEntry>();
        for (Map.Entry<String, IndexEntry> indexEntry : index.entrySet()) {
            clone.index.put(
                    indexEntry.getKey(),
                    indexEntry.getValue().clone()
            );
        }

        clone.classes = new TreeMap<String, Class>();
        for (Map.Entry<String, Class> classEntry : classes.entrySet()) {
            clone.classes.put(
                    classEntry.getKey(),
                    classEntry.getValue().clone()
            );
        }

        return clone;
    }
}
