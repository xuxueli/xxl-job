package com.xxl.job.admin.platform.pageable;

import java.util.Objects;

/**
 * @author Ice2Faith
 * @date 2024/7/5 11:12
 * @desc
 */
public class DatabasePageable {
    private int start;
    private int length;

    public DatabasePageable() {
    }

    public DatabasePageable(int start, int length) {
        this.start = start;
        this.length = length;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DatabasePageable that = (DatabasePageable) o;
        return start == that.start && length == that.length;
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, length);
    }

    @Override
    public String toString() {
        return "DatabasePageable{" +
                "start=" + start +
                ", length=" + length +
                '}';
    }
}
