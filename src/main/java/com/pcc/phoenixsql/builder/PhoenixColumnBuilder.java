package com.pcc.phoenixsql.builder;

import java.util.List;

import com.google.common.collect.Lists;
import com.pcc.phoenixsql.cfg.DataTypeMapping;
import com.pcc.phoenixsql.utils.MyStringUtil;

import net.sf.jsqlparser.statement.create.table.ColDataType;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;

/**
 * phoenix建表的列语句构建器
 *
 * @author peichenchen
 * @date 2017/10/20
 */
public class PhoenixColumnBuilder {

    private static final String DATE_TYPE_DECIMAL = "DECIMAL";

    public List<ColumnDefinition> buildColumns(CreateTable mysqlCreateTable, String columnPrefix) {
        List<ColumnDefinition> columnDefinitions = Lists.newArrayList();

        List<ColumnDefinition> mysqlColumnDefinitions = mysqlCreateTable.getColumnDefinitions();
        List<String> primaryKeyColumnsNames = mysqlCreateTable.getIndexes().get(0).getColumnsNames();
        for (ColumnDefinition mysqlColumnDefinition : mysqlColumnDefinitions) {
            ColumnDefinition phoenixColumnDefinition = new ColumnDefinition();
            phoenixColumnDefinition.setColumnName(buildColumnName(columnPrefix, mysqlColumnDefinition));
            phoenixColumnDefinition
                .setColumnSpecStrings(buildColumnSpecStrings(primaryKeyColumnsNames, mysqlColumnDefinition));
            phoenixColumnDefinition.setColDataType(buildColDataType(mysqlColumnDefinition));

            columnDefinitions.add(phoenixColumnDefinition);
        }
        ColumnDefinition pkColumnDefinition = dealPrimaryKeyAsSpecialColumn(columnPrefix, primaryKeyColumnsNames);
        columnDefinitions.add(pkColumnDefinition);

        return columnDefinitions;
    }

    private ColumnDefinition dealPrimaryKeyAsSpecialColumn(String columnPrefix, List<String> primaryKeyColumnsNames) {
        ColumnDefinition phoenixColumnDefinition = new ColumnDefinition();
        String primaryKeyName = MyStringUtil.getStringList(primaryKeyColumnsNames, columnPrefix, "_");
        phoenixColumnDefinition.setColumnName("CONSTRAINT " + primaryKeyName + "_PK");
        ColDataType colDataType = new ColDataType();
        String primaryKeyStr = MyStringUtil.getStringList(primaryKeyColumnsNames, columnPrefix, ",");
        colDataType.setDataType("PRIMARY KEY (" + primaryKeyStr + ")");
        phoenixColumnDefinition.setColDataType(colDataType);
        return phoenixColumnDefinition;
    }

    private String buildColumnName(String columnPrefix, ColumnDefinition mysqlColumnDefinition) {
        return MyStringUtil.addPrefixIfNotBlank(mysqlColumnDefinition.getColumnName(), columnPrefix);
    }

    private List<String> buildColumnSpecStrings(List<String> primaryKeyColumnsNames,
                                                ColumnDefinition mysqlColumnDefinition) {
        List<String> columnSpecStrings = Lists.newArrayList();
        if (isPrimaryKeyColumn(primaryKeyColumnsNames, mysqlColumnDefinition)) {
            columnSpecStrings.add("NOT NULL");
        }
        return columnSpecStrings;
    }

    private ColDataType buildColDataType(ColumnDefinition mysqlColumnDefinition) {
        String mysqlDataType = mysqlColumnDefinition.getColDataType().getDataType();
        String phoenixDataType = DataTypeMapping.MAPPING_CFG.get(mysqlDataType);
        ColDataType phoenixCloDataType = new ColDataType();
        phoenixCloDataType.setDataType(mysqlDataType);
        if (phoenixDataType != null) {
            phoenixCloDataType.setDataType(phoenixDataType);
        }
        if (DATE_TYPE_DECIMAL.equalsIgnoreCase(mysqlDataType)) {
            phoenixCloDataType.setArgumentsStringList(mysqlColumnDefinition.getColDataType().getArgumentsStringList());
        }
        return phoenixCloDataType;
    }

    private boolean isPrimaryKeyColumn(List<String> primaryKeyColumns, ColumnDefinition mysqlColumnDefinition) {
        return primaryKeyColumns.contains(mysqlColumnDefinition.getColumnName());
    }

    private String buildPrimaryKeyName(List<String> primaryKeyColumns) {
        String primaryKeyName = null;
        for (String primaryKeyColumn : primaryKeyColumns) {
            primaryKeyName += primaryKeyColumn + "_";
        }

        return primaryKeyName;
    }

}
