package com.pcc.phoenixsql.builder;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pcc.phoenixsql.utils.MyStringUtil;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.util.deparser.CreateTableDeParser;

/**
 * phoenix建表语句构建器
 *
 * @author peichenchen
 * @date 2017/10/20
 */
public class PhoenixCreateTableBuilder {

    /**
     * 根据mysql的建表语句构建phoenix的建表语句
     *
     * @param mysqlCreateTable mysql的建表语句封装对象
     * @param schema          类似mysql数据库的名称
     * @param columnPrefix    表的字段前缀
     * @param saltBuckets     分的region数量
     * @param usingForTestEnv 建表语句是否用于测试环境下的
     * @return
     * @throws JSQLParserException
     */
    public String build(CreateTable mysqlCreateTable, String schema, String columnPrefix, int saltBuckets,
                        boolean usingForTestEnv) throws JSQLParserException {

        CreateTable phoenixCreateTable = new CreateTable();
        phoenixCreateTable.setTable(new Table(schema, mysqlCreateTable.getTable().getName()));
        phoenixCreateTable
            .setColumnDefinitions(new PhoenixColumnBuilder().buildColumns(mysqlCreateTable, columnPrefix));
        phoenixCreateTable
            .setTableOptionsStrings(buildTableOptionsStrings(mysqlCreateTable, saltBuckets, usingForTestEnv));

        CreateTableDeParser createTableDeParser = new CreateTableDeParser(new StringBuilder());
        createTableDeParser.deParse(phoenixCreateTable);
        String phoenixCreateSql = createTableDeParser.getBuffer().toString();
        //        System.out.println(phoenixCreateSql);
        //        System.out.println(phoenixCreateTable.toString());

        phoenixCreateSql = addPhoenixComment(columnPrefix, mysqlCreateTable, phoenixCreateSql);

        return phoenixCreateSql;
    }

    private String addPhoenixComment(String columnPrefix, CreateTable mysqlCreateTable, String phoenixCreateSql) {
        Map<String, String> columnName2comment = buildColumnName2Comment(columnPrefix, mysqlCreateTable);
        for (String columnName : columnName2comment.keySet()) {
            String comment = columnName2comment.get(columnName).replaceAll("'", "");
            phoenixCreateSql = phoenixCreateSql.replaceAll("(" + columnName + "\\s+DECIMAL.*?\\),)",
                "$1" + " //" + comment + "\n");
            phoenixCreateSql = phoenixCreateSql.replaceAll("(" + columnName + "\\s+[^(]*?,)",
                "$1" + " //" + comment + "\n");
        }
        return phoenixCreateSql;
    }

    private Map<String, String> buildColumnName2Comment(String columnPrefix, CreateTable mysqlCreateTable) {
        Map<String, String> columnName2comment = Maps.newHashMap();
        List<ColumnDefinition> mysqlColumnDefinitions = mysqlCreateTable.getColumnDefinitions();
        for (ColumnDefinition mysqlColumnDefinition : mysqlColumnDefinitions) {
            String columnName = MyStringUtil.addPrefixIfNotBlank(mysqlColumnDefinition.getColumnName(), columnPrefix);
            columnName2comment.put(columnName, getColumnComment(mysqlColumnDefinition));
        }

        return columnName2comment;
    }

    private String getColumnComment(ColumnDefinition mysqlColumnDefinition) {
        List<String> columnSpecStrings = mysqlColumnDefinition.getColumnSpecStrings();
        for (int i = 0; i < columnSpecStrings.size(); i++) {
            if (columnSpecStrings.get(i).equalsIgnoreCase("COMMENT")) {
                return columnSpecStrings.get(i + 1);
            }
        }

        return "";
    }

    private List<String> buildTableOptionsStrings(CreateTable mysqlCreateTable, int saltBuckets, boolean isTestEnv) {
        List<String> tableOptionsStrings = Lists.newArrayList();
        if (!isTestEnv) {
            tableOptionsStrings.add("DATA_BLOCK_ENCODING = 'FAST_DIFF',COMPRESSION = 'SNAPPY',");
        }
        tableOptionsStrings.add("SALT_BUCKETS = " + saltBuckets + ";" + " //" + getTableComment(mysqlCreateTable));

        return tableOptionsStrings;
    }

    private String getTableComment(CreateTable mysqlCreateTable) {
        List<String> tableOptionsStrings = (List<String>) mysqlCreateTable.getTableOptionsStrings();
        for (int i = 0; i < tableOptionsStrings.size(); i++) {
            if (tableOptionsStrings.get(i).equalsIgnoreCase("COMMENT")) {
                return tableOptionsStrings.get(i + 2);
            }
        }

        return "";
    }

    private String getTableComment(ColumnDefinition mysqlColumnDefinition) {
        List<String> columnSpecStrings = mysqlColumnDefinition.getColumnSpecStrings();
        for (int i = 0; i < columnSpecStrings.size(); i++) {
            if (columnSpecStrings.get(i).equalsIgnoreCase("COMMENT")) {
                return columnSpecStrings.get(i + 1);
            }
        }

        return "";
    }

}
