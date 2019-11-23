package top.kpromise.dynamicdatasource.dynamicdatasource;

import java.util.ArrayList;
import java.util.List;

class DynamicDataSourceContextHolder {

    private static final ThreadLocal<String> contextHolder = new ThreadLocal<>();
    static List<String> dataSourceIds = new ArrayList<>();

    static void setDataSourceType(String dataSourceType) {
        contextHolder.set(dataSourceType);
    }

    static String getDataSourceType() {
        return contextHolder.get();
    }

    static void clearDataSourceType() {
        contextHolder.remove();
    }

    static boolean isContainsDataSource(String dataSourceId) {
        return dataSourceIds.contains(dataSourceId);
    }
}
