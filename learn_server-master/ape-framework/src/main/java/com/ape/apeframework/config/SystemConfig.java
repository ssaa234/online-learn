package com.ape.apeframework.config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import sun.plugin2.util.SystemUtil;

@Component
@ConfigurationProperties(prefix = "system")
public class SystemConfig {

    private static boolean requestLog;

    public static boolean isRequestLog() {
        return requestLog;
    }

    public void setRequestLog(boolean requestLog) {
        SystemConfig.requestLog = requestLog;
    }
}