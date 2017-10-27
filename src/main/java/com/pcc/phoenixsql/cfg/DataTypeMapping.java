package com.pcc.phoenixsql.cfg;

import java.util.Map;

import com.google.common.collect.Maps;

/**
 * mysql datatype 到 phoenix datatype的映射
 * @author peichenchen
 * @version 17/10/27 上午11:23
 */
public class DataTypeMapping {

    /**
     * key:mysql data type, value:phoenix data type
     */
    public static final Map<String, String> MAPPING_CFG = Maps.newHashMap();

    static {
        MAPPING_CFG.put("INT", "INTEGER");
        MAPPING_CFG.put("DATETIME", "TIME");
        MAPPING_CFG.put("TEXT", "VARCHAR");
    }

}
