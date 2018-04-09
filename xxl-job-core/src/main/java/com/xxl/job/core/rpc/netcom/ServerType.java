package com.xxl.job.core.rpc.netcom;

/**
 * RPC server type
 *
 * @author zixiao
 * @date 18/4/9
 */
public enum ServerType {

    JETTY("jetty"),
    NETTY("netty");

    private String value;

    private ServerType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ServerType match(String value){
        for (ServerType item: ServerType.values()) {
            if (item.getValue().equals(value)) {
                return item;
            }
        }
        return null;
    }

}
