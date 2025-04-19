package com.example.special_reads_t.Model;

import java.beans.Transient;

public class Challenge {
    private String title;
    private Long current;
    private long target;

    public Challenge(String title, Long current, long target) {
        this.title = title;
        this.current = current;
        this.target = target;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getCurrent() {
        return current;
    }

    public void setCurrent(Long current) {
        this.current = current;
    }

    public long getTarget() {
        return target;
    }

    public void setTarget(long target) {
        this.target = target;
    }

    @Transient
    public int getPercent() {
        if (target <= 0) return 0;
        return (int) Math.min(100, Math.round(current * 100.0 / target));
    }
}
