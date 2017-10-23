package com.pcc.phoenixsql.builder;

import java.util.List;

import com.google.common.collect.Lists;
import com.pcc.phoenixsql.utils.MyStringUtil;

import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.create.table.Index;

/**
 * @author peichenchen
 * @date 2017/10/22
 */
public class PhoenixCreateIndexBuilder {

    public List<String> build(CreateTable mysqlCreateTable, String schema, String columnPrefix) {

        List<Index> indexes = mysqlCreateTable.getIndexes();
        List<String> indexFields = Lists.newArrayList();
        // 第一个是主键，所以下标从1开始
        for (int i = 1; i < indexes.size(); i++) {
            indexFields.add(MyStringUtil.getStringList(indexes.get(i).getColumnsNames(), columnPrefix, true));
        }

        String tableName = schema + "." + mysqlCreateTable.getTable().getName();

        return generateIndexSql(indexFields, tableName);
    }

    private List<String> generateIndexSql(List<String> indexColumns, String tableName) {
        List<String> indexSqls = Lists.newArrayList();
        for (String indexColumnStr : indexColumns) {
            // 处理联合索引
            String indexName = "IDX_" + indexColumnStr;
            if (indexColumnStr.contains(",")) {
                indexName = indexName.replaceAll(",\\s*", "_");
            }
            //CREATE INDEX my_index ON my_schema.my_table (v1,v2)
            indexSqls.add("CREATE INDEX " + indexName + " ON " + tableName + "(" + indexColumnStr + ");");
        }

        return indexSqls;
    }
}
