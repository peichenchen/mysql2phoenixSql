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

/**
 * Created by peichenchen on 17/9/29.
 */
public class Mysql2PhoenixSql {

    private static final String SCHEMA_NAME  = "DAIJIA_ORDER";                             // 类似mysql数据库的名称
    private static final int    SALT_BUCKETS = 64;                                         //分的region数量 （按数据量和读写量综合评估）
    //    private static final int    TTL          = 108000;                                     //数据保留时间,（单位 秒）
    private static final String sqlFilePath  = "/Users/peichenchen/Downloads/temp/testSql";

    public static void main(String[] args) throws IOException {

        String sql = FileUtils.readFileToString(new File(sqlFilePath), "utf-8");

        sql = toUpperCase(sql);

        sql = removeMysqlGrammar(sql);

        sql = addPhoenixConstraint(sql);

        sql = dealTableName(sql);

        sql = mysqlToPhoenixDataType(sql);

        List<String> indexFields = extractIndexFields(sql);
        String tableName = StringUtils.substringBetween(sql, "CREATE TABLE", "(").trim();
        List<String> indexSqls = generateIndexSql(indexFields, tableName);
        System.out.println(indexSqls);

        sql = dealPrimaryKey(sql);

        System.out.println(sql);

    }

    private static List<String> generateIndexSql(List<String> indexFields, String tableName) {
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

    private static List<String> extractIndexFields(String sql) {
        List<String> indexFields = Lists.newArrayList();
        Pattern p = Pattern.compile("KEY\\s+[A-Z_]+\\s+\\(([A-Z_,]+)\\)");
        Matcher m = p.matcher(sql);
        String primaryKey = "";
        while (m.find()) {
            indexFields.add(m.group(1));
        }

        return indexFields;
    }

    private static String addPhoenixConstraint(String sql) {
        sql = sql.replaceAll("\\)\\s*?ENGINE=.*;",
            ")DATA_BLOCK_ENCODING='FAST_DIFF',COMPRESSION ='SNAPPY',SALT_BUCKETS=" + SALT_BUCKETS + ";");
        return sql;
    }

    private static String toUpperCase(String sql) {
        return sql.toUpperCase();
    }

    private static String dealTableName(String sql) {
        return sql.replaceAll("CREATE\\s+TABLE\\s+", "CREATE TABLE " + SCHEMA_NAME + ".");
    }

    private static String mysqlToPhoenixDataType(String sql) {
        Set<Map.Entry<String, String>> entrySet = DataTypeMapping.mapping.entrySet();
        for (Map.Entry<String, String> entry : entrySet) {
            String mysqlDataType = entry.getKey();
            String phoenixDataType = entry.getValue();
            sql = sql.replaceAll("\\s+" + mysqlDataType + ",", " " + phoenixDataType + ",");
        }
        return sql;
    }

    private static String removeMysqlGrammar(String sql) throws IOException {
        sql = sql.replaceAll("`", "");
        sql = sql.replaceAll("COMMENT.*?',", ",");
        sql = sql.replaceAll("\\s*NOT\\s+NULL.*?,", ",");
        sql = sql.replaceAll("DEFAULT.*?,", ",");
        sql = sql.replaceAll("\\(\\d+\\)", "");
        return sql;
    }

    private static String dealPrimaryKey(String sql) {

        String primaryKey = extractPrimaryKey(sql);
        Assert.notNull(primaryKey, "extract Primary Key failed");
        String primaryKeyName = primaryKey.toUpperCase() + "_PK";
        sql = sql.replaceAll("PRIMARY[\\S\\s]*\\)",
            "CONSTRAINT " + primaryKeyName + " PRIMARY KEY (" + primaryKey + ")\n)");

        // 主键字段非空处理
        sql = sql.replaceAll("(" + primaryKey + "\\s+[A-Z]+),", "$1" + " NOT NULL,");

        return sql;
    }

    private static String extractPrimaryKey(String sql) {
        Pattern p = Pattern.compile("PRIMARY\\s+KEY \\((.*?)\\)");
        Matcher m = p.matcher(sql);
        String primaryKey = "";
        if (m.find()) {
            primaryKey = m.group(1);
        }

        return primaryKey;
    }

}
