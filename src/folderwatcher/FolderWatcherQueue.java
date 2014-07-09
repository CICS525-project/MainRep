package folderwatcher;

import java.io.File;
import java.util.Date;

public class FolderWatcherQueue implements Comparable<FolderWatcherQueue> {

    private String eventType;
    private String fullPath;
    private Date date;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getFullPath() {
        return fullPath;
    }

    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }

    public int compareTo(FolderWatcherQueue t1) {
        {
            if (date.before(t1.getDate())) {
                return -1;
            }
            if (date.after(t1.getDate())) {
                return 1;
            }
            return 0;
        }
    }
}
