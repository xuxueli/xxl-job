package com.xxl.job.admin.platform.pageable.data;

import java.util.Objects;

/**
 * @author Ice2Faith
 * @date 2024/9/26 20:38
 * @desc
 */
public class PageDto {
    public static final int DEFAULT_NUMBER = 1;
    public static final int DEFAULT_SIZE = 100;
    protected int number = DEFAULT_NUMBER;
    protected int size = DEFAULT_SIZE;
    protected long offsetBegin;
    protected long offsetEnd;
    protected long rowBegin;
    protected long rowEnd;

    public PageDto() {
        this.number = DEFAULT_NUMBER;
        this.size = DEFAULT_SIZE;
        this.complete();
    }

    public PageDto(int size) {
        this.number = DEFAULT_NUMBER;
        this.size = size;
        this.complete();
    }

    public PageDto(int number, int size) {
        this.number = number;
        this.size = size;
        this.complete();
    }

    public void setPage(int number, int size) {
        this.number = number;
        this.size = size;
        this.complete();
    }

    public static PageDto of(int number, int size) {
        return new PageDto(number, size);
    }

    public static PageDto of(int size) {
        return new PageDto(size);
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void complete() {
        if (this.number <= 0) {
            this.number = DEFAULT_NUMBER;
        }
        if (this.size <= 0) {
            this.size = DEFAULT_SIZE;
        }
        this.offsetBegin = (this.number - 1) * (long) this.size;
        this.offsetEnd = (this.number) * (long) this.size;
        this.rowBegin = this.offsetBegin + 1;
        this.rowEnd = this.offsetEnd + 1;
    }

    public int getNumber() {
        return number;
    }

    public int getSize() {
        return size;
    }

    public long getOffsetBegin() {
        return offsetBegin;
    }

    public long getOffsetEnd() {
        return offsetEnd;
    }

    public long getRowBegin() {
        return rowBegin;
    }

    public long getRowEnd() {
        return rowEnd;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        PageDto pageDto = (PageDto) object;
        return number == pageDto.number && size == pageDto.size && offsetBegin == pageDto.offsetBegin && offsetEnd == pageDto.offsetEnd && rowBegin == pageDto.rowBegin && rowEnd == pageDto.rowEnd;
    }

    @Override
    public int hashCode() {
        return Objects.hash(number, size, offsetBegin, offsetEnd, rowBegin, rowEnd);
    }

    @Override
    public String toString() {
        return "PageDto{" +
                "number=" + number +
                ", size=" + size +
                ", offsetBegin=" + offsetBegin +
                ", offsetEnd=" + offsetEnd +
                ", rowBegin=" + rowBegin +
                ", rowEnd=" + rowEnd +
                '}';
    }
}
