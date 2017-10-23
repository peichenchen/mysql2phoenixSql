package com.pcc.phoenixsql.builder;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;

import com.google.common.collect.Lists;
import com.pcc.phoenixsql.cfg.DataTypeMapping;
import com.pcc.phoenixsql.utils.MyStringUtil;

/**
 *
 * @author peichenchen
 * @version 17/10/12 下午9:52
 */
@Deprecated
public class SingleTableConvertor {

    /**
     * @param sqlFilePath
     * @param schema      类似mysql数据库的名称
     * @param saltBuckets 分的region数量 （按数据量和读写量综合评估）
     * @throws IOException
     */
    public void convert(String sqlFilePath, String schema, String tablePrefix, int saltBuckets) throws IOException {

        String sqlContent = FileUtils.readFileToString(new File(sqlFilePath), "utf-8");

        sqlContent = toUpperCase(sqlContent);

        List<String> createTableSqls = splitByTable(sqlContent);

        for (String createTableSql : createTableSqls) {
            createTableSql = removeMysqlGrammar(createTableSql);

            createTableSql = addPhoenixConstraint(createTableSql, saltBuckets);

            createTableSql = dealTableName(createTableSql, schema);

            createTableSql = mysqlToPhoenixDataType(createTableSql);

            List<String> indexSqls = createIndexSqls(createTableSql);

            createTableSql = dealPrimaryKey(createTableSql);

            print(createTableSql, indexSqls);
        }
    }

    private List<String> splitByTable(String sqlContent) {
        return MyStringUtil.extractMatchItems(sqlContent, "(CREATE\\s+TABLE[\\s\\S]*?ENGINE.*?;)");
    }

    private void print(String sql, List<String> indexSqls) {
        System.out.println(sql);
        System.out.println();
        for (String indexSql : indexSqls) {
            System.out.println(indexSql);
        }
        System.out.println("\n");
    }

    private List<String> createIndexSqls(String sql) {
        List<String> indexFields = MyStringUtil.extractMatchItems(sql, "KEY\\s+[A-Z_\\d]+\\s+\\(([A-Z_,\\d]+)\\)");
        String tableName = StringUtils.substringBetween(sql, "CREATE TABLE", "(").trim();
        return generateIndexSql(indexFields, tableName);
    }

    private List<String> generateIndexSql(List<String> indexFields, String tableName) {
        List<String> indexSqls = Lists.newArrayList();
        for (String indexFieldStr : indexFields) {
            // 处理联合索引
            String indexName = "IDX_" + indexFieldStr.toUpperCase();
            if (indexFieldStr.contains(",")) {
                indexName = indexName.replace(",", "_");
            }
            //CREATE INDEX my_index ON my_schema.my_table (v1,v2)
            indexSqls.add("CREATE INDEX " + indexName + " ON " + tableName + "(" + indexFieldStr + ");");
        }

        return indexSqls;
    }

    private String addPhoenixConstraint(String sql, int saltBuckets) {
        sql = sql.replaceAll("\\)\\s*?ENGINE=.*;",
            ")DATA_BLOCK_ENCODING='FAST_DIFF',COMPRESSION ='SNAPPY',SALT_BUCKETS=" + saltBuckets + ";");
        return sql;
    }

    private String toUpperCase(String sql) {
        return sql.toUpperCase();
    }

    private String dealTableName(String sql, String schema) {
        return sql.replaceAll("CREATE\\s+TABLE\\s+", "CREATE TABLE " + schema + ".");
    }

    private String mysqlToPhoenixDataType(String sql) {
        Set<Map.Entry<String, String>> entrySet = DataTypeMapping.mapping.entrySet();
        for (Map.Entry<String, String> entry : entrySet) {
            String mysqlDataType = entry.getKey();
            String phoenixDataType = entry.getValue();
            sql = sql.replaceAll("\\s+" + mysqlDataType + ",", " " + phoenixDataType + ",");
        }
        return sql;
    }

    private String removeMysqlGrammar(String sql) throws IOException {
        sql = sql.replaceAll("`", "");
        sql = sql.replaceAll("COMMENT.*?',", ",");
        sql = sql.replaceAll("\\s*NOT\\s+NULL.*?,", ",");
        //        sql = sql.replaceAll("DEFAULT.*?,", ",");
        sql = sql.replaceAll("\\(\\d+\\)", "");
        return sql;
    }

    private String dealPrimaryKey(String sql) {

        String primaryKey = extractPrimaryKey(sql);
        Assert.notNull(primaryKey, "extract Primary Key failed");
        String primaryKeyName = primaryKey.toUpperCase() + "_PK";
        sql = sql.replaceAll("PRIMARY[\\S\\s]*\\)",
            "CONSTRAINT " + primaryKeyName + " PRIMARY KEY (" + primaryKey + ")\n)");

        // 主键字段非空处理
        sql = sql.replaceAll("(\\s" + primaryKey + "\\s+[A-Z]+),", "$1" + " NOT NULL,");

        return sql;
    }

    private String extractPrimaryKey(String sql) {
        Pattern p = Pattern.compile("PRIMARY\\s+KEY \\((.*?)\\)");
        Matcher m = p.matcher(sql);
        String primaryKey = "";
        if (m.find()) {
            primaryKey = m.group(1);
        }

        return primaryKey;
    }
}
