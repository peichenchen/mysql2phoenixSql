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