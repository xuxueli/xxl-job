package com.xxl.job.core.constant;

/**
 * Created by xuxueli on 17/5/9.
 */
public enum ExecutorBlockStrategyEnum {


    /**
     * serial execution
     */
    SERIAL_EXECUTION("Serial execution"),

    /**
     * concurrent execution
     */
    /*CONCURRENT_EXECUTION("并行"),*/

    /**
     * discard later
     */
    DISCARD_LATER("Discard Later"),

    /**
     * cover early
     */
    COVER_EARLY("Cover Early");


    private String title;
    ExecutorBlockStrategyEnum (String title) {
        this.title = title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public String getTitle() {
        return title;
    }

    /**
     * match by name
     *
     * @param name              enum name
     * @param defaultItem       default item
     * @return                  match item
     */
    public static ExecutorBlockStrategyEnum match(String name, ExecutorBlockStrategyEnum defaultItem) {
        if (name != null) {
            for (ExecutorBlockStrategyEnum item:ExecutorBlockStrategyEnum.values()) {
                if (item.name().equals(name)) {
                    return item;
                }
            }
        }
        return defaultItem;
    }
}
