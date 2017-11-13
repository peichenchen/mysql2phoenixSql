package com.pcc.phoenixsql;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.pcc.phoenixsql.builder.PhoenixCreateIndexBuilder;
import com.pcc.phoenixsql.builder.PhoenixCreateTableBuilder;
import com.pcc.phoenixsql.builder.PhoenixSelectBuilder;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.create.table.CreateTable;

/**
 * 工具启动入口
 *
 * @author peichenchen
 * @version 17/10/19 下午9:35
 */
public class Mysql2PhoenixSqlLauncher {

    /**
     * 运行前请提供如下参数
     */
    private static final String  SQL_PATH           = "/Users/peichenchen/Downloads/temp/testSql";
    /**
     * phoenix schema名称，类似mysql数据库的名称
     */
    private static final String  SCHEMA_NAME        = "PCC_TEST";
    //表的字段前缀
    private static final String  COLUMN_PREFIX      = "";
    //分的region数量
    private static final int     SALT_BUCKETS       = 64;
    private static final boolean USING_FOR_TEST_ENV = false;

    public static void main(String[] args) throws IOException, JSQLParserException {
        CreateTable mysqlCreateTable = getCreateTableStatement(SQL_PATH);

        String createTableSql = new PhoenixCreateTableBuilder().build(mysqlCreateTable, SCHEMA_NAME, COLUMN_PREFIX,
            SALT_BUCKETS, USING_FOR_TEST_ENV);
        List<String> createIndexSqls = new PhoenixCreateIndexBuilder().build(mysqlCreateTable, SCHEMA_NAME,
            COLUMN_PREFIX);
        String selectSql = new PhoenixSelectBuilder().build(mysqlCreateTable, COLUMN_PREFIX);

        print(createTableSql);
        printList(createIndexSqls);
        print(selectSql);
    }

    private static CreateTable getCreateTableStatement(String createSqlPath) throws IOException, JSQLParserException {
        String sqlContent = FileUtils.readFileToString(new File(createSqlPath), "utf-8");
        sqlContent = sqlContent.replaceAll("`", "");
        sqlContent = sqlContent.toUpperCase();

        Statement statement = CCJSqlParserUtil.parse(sqlContent);
        return (CreateTable) statement;
    }

    private static void print(String str) {
        System.out.println();
        System.out.println(str);
    }

    private static void printList(List<String> stringLists) {
        System.out.println();
        for (String str : stringLists) {
            System.out.println(str);
        }
    }

}
