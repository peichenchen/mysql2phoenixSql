### 将mysql的create table sql语句转化为phoenix sql语句

虽然phoenix支持sql语法，但是和mysql的语法还是有一些差别，主要体现在：
- create table不支持comment
- 建表时只有主键不为空即可
- 字段不支持默认值
- 创建主键的语法
- phoenix建表的一些约束语法
- create index语法
- 数据类型不完全一致

每次手工转换基本都是重复的字符串替换、整理工作，浪费时间还容易出错，咨询过hbase团队的同学目前还没有这样的工具，网上也搜索了，确实没有搜索到，mysql2phoenixSql转换工具因此诞生。

 #### 使用方法
下载完工程，直接运行程序入口Mysql2PhoenixSqlLauncher的main方法，但是必须提供如下参数：
 运行前请提供如下参数
 - SQL_PATH:sql文件的路径，如/Users/peichenchen/Downloads/temp/testSql
 - SCHEMA_NAME:phoenix schema名称，类似mysql数据库的名称，如PCC_TEST
 -  COLUMN_PREFIX:表的字段前缀，默认为空
 -  SALT_BUCKETS:habse分的region数量
 -  USING_FOR_TEST_ENV:建表语句是否用于测试环境,测试环境生成的建表语句没有DATA_BLOCK_ENCODING = 'FAST_DIFF',COMPRESSION = 'SNAPPY约束
