package com.xxl.job.admin.platform;

/**
 * @author Ice2Faith
 * @date 2024/5/22 9:28
 * @desc
 */
public enum DatabasePlatformType {
    MYSQL("mysql"),
    ORACLE("oracle"),
    POSTGRE("postgre"),
    GBASE("gbase"),
    H2("h2"),
    DM("dm"),
    KINGBASE("kingbase"),
    UNKNOWN("unknown");

    private String type;

    private DatabasePlatformType(String type) {
        this.type = type;
    }

    public String type() {
        return this.type;
    }

    public static DatabasePlatformType of(String name){
        if(name==null){
            return DatabasePlatformType.UNKNOWN;
        }
        name=name.toLowerCase();
        for (DatabasePlatformType value : DatabasePlatformType.values()) {
            if(value.type().equals(name)){
                return value;
            }
        }
        return DatabasePlatformType.UNKNOWN;
    }

}
