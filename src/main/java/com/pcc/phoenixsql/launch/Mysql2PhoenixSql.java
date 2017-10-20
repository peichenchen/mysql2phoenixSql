package com.pcc.phoenixsql.launch;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.pcc.phoenixsql.builder.PhoenixCreateTableBuilder;

import net.sf.jsqlparser.JSQLParserException;

/**
 * 
 * @author peichenchen
 * @version 17/10/19 下午9:35
 */
public class Mysql2PhoenixSql {

    public static void main(String[] args) throws IOException, JSQLParserException {
        //        new SingleTableConvertor().convert("/Users/peichenchen/Downloads/temp/testSql", "DAIJIA_ORDER", "", 64);

        // TODO 2.保留表和字段的注释
        // TODO 3.支持生成导入数据时需要用到的select sql

        String sqlContent = FileUtils.readFileToString(new File("/Users/peichenchen/Downloads/temp/testSql"), "utf-8");
        sqlContent = sqlContent.replaceAll("`", "");

        System.out.println(new PhoenixCreateTableBuilder().build(sqlContent, "DAIJIA_ORDER", "VO", 64, true));

    }

}
