package com.pcc.phoenixsql.launch;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.pcc.phoenixsql.builder.PhoenixCreateIndexBuilder;
import com.pcc.phoenixsql.builder.PhoenixCreateTableBuilder;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.create.table.CreateTable;

/**
 * 
 * @author peichenchen
 * @version 17/10/19 下午9:35
 */
public class Mysql2PhoenixSql {

    private static final String SQL_PATH      = "/Users/peichenchen/Downloads/temp/testSql";
    private static final String SCHEMA_NAME   = "DAIJIA_ORDER";
    private static final String COLUMN_PREFIX = "VO";
    private static final int    SALT_BUCKETS  = 64;

    public static void main(String[] args) throws IOException, JSQLParserException {
        // TODO 3.支持生成导入数据时需要用到的select sql

        CreateTable mysqlCreateTable = getCreateTableStatement(SQL_PATH);

        String createTableSql = new PhoenixCreateTableBuilder().build(mysqlCreateTable, SCHEMA_NAME, COLUMN_PREFIX,
            SALT_BUCKETS, true);
        List<String> createIndexSqls = new PhoenixCreateIndexBuilder().build(mysqlCreateTable, SCHEMA_NAME,
            COLUMN_PREFIX);
        print(createTableSql, createIndexSqls);
    }

    private static CreateTable getCreateTableStatement(String createSqlPath) throws IOException, JSQLParserException {
        String sqlContent = FileUtils.readFileToString(new File(createSqlPath), "utf-8");
        sqlContent = sqlContent.replaceAll("`", "");
        sqlContent = sqlContent.toUpperCase();

        Statement statement = CCJSqlParserUtil.parse(sqlContent);
        return (CreateTable) statement;
    }

    private static void print(String sql, List<String> indexSqls) {
        System.out.println(sql);
        System.out.println();
        for (String indexSql : indexSqls) {
            System.out.println(indexSql);
        }
        System.out.println("\n");
    }

}
