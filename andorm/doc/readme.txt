
1.数据库配置(可选，默认数据库路径/data/data/{packagename}/andorm/andorm_default.db)
	创建属性文件*.properties，配置数据库参数:
	1)db.path=xxx，路径任意(可选，默认值/data/data/{packagename}/andorm/)，可以是sd卡，如db.path=/sdcard/Andorm/
	2)db.name=xxx.xx，文件名和扩展名任意(可选，默认值是andorm_default.db)
	以上配置可参照文件doc/andorm_db.properties
	
2.创建模型对象，参照示例Book类，类似于Hibernate对象模型

3.创建Dao接口，需要事务处理的方法加上@Transaction注解

4.实现Dao接口，需要继承DaoSupport，并且实现上一步创建的接口

5.使用DaoFactory工厂类得到Dao实例，通过Dao实例和模型对象进行数据库操作。

其中第一步数据库配置可以省略，会有默认值，创建Dao类中的@Database注解也可以省略，使用默认数据库配置

