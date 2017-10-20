package com.pcc.phoenixsql.cfg;

import java.util.Map;

import com.google.common.collect.Maps;

/**
 * Created by peichenchen on 17/10/10.
 */
public class DataTypeMapping {

    /**
     * key:mysql data type, value:phoenix data type
     */
    public static final Map<String, String> mapping = Maps.newHashMap();

    static {
        mapping.put("INT", "INTEGER");
        mapping.put("DATETIME", "TIME");
        mapping.put("TEXT", "VARCHAR");
    }

}
