package com.pcc.phoenixsql.builder;

import java.util.List;

import com.google.common.collect.Lists;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.util.SelectUtils;

/**
 * @author peichenchen
 * @date 2017/10/22
 */
public class PhoenixSelectBuilder {

    public String build(CreateTable mysqlCreateTable, String columnPrefix) throws JSQLParserException {

        String tableName = mysqlCreateTable.getTable().getName();

        List<String> columns = Lists.newArrayList();
        for (ColumnDefinition columnDefinition : mysqlCreateTable.getColumnDefinitions()) {
            columns.add(columnPrefix + "_" + columnDefinition.getColumnName());
        }

        Select select = SelectUtils.buildSelectFromTableAndExpressions(new Table(tableName),
            columns.toArray(new String[0]));

        return select.toString();
    }
}
