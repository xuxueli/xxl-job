package com.xxl.job.executor.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.http.client.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.druid.util.StringUtils;
import com.xxl.job.executor.service.pojo.RealTimeJob;

@Component
public class JobService {
	/**
	 * 数据库类型(0.MySQL|MariaDB,1.MongoDB)
	 */
	public static int DATABASE_TYPE = 0;
	@Autowired
	JdbcHandle jdbcHandle;
	@Autowired
	MongoDBHandle mongodbHandle;
	private static String TABLE_SQL_TEMPLATE = "table_sql_template.sql";
	private static Map<String, String> DEFAULT_COLUMNS = null;
	private String[] reflect(String table,RealTimeJob obj){
		Map<String, String> columns = DEFAULT_COLUMNS==null||DEFAULT_COLUMNS.isEmpty()?jdbcHandle.queryColumns(table):DEFAULT_COLUMNS;
		if(columns==null||columns.size()<1){
			createTable(table, true);
			columns = jdbcHandle.queryColumns(table);
		}
		DEFAULT_COLUMNS = columns;
		String keys = null;
		String values = null;
		if(obj!=null){
			Class<? extends RealTimeJob> clazz = obj.getClass();
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {
				String name = field.getName();
				if(name.equalsIgnoreCase("serialVersionUID")||name.contains("$this")||!columns.containsKey(name)){
					continue;
				}
				String type = field.getType().getSimpleName();
				try {
					Method method = clazz.getMethod((type.equalsIgnoreCase("boolean")?"is":"get")+name.substring(0, 1).toUpperCase()+ name.substring(1));
					Object value = method.invoke(obj);
					if(type.equalsIgnoreCase("boolean")||type.equalsIgnoreCase("Date")){
						if(type.equalsIgnoreCase("Date")){
							value = DateUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
						}
						value = value.toString();
					}
					if(value!=null){
						if(StringUtils.isEmpty(keys)){
							keys = name;
							values = (value instanceof String?"'"+value+"'":value+"");
						}else{
							keys +=","+ name;
							values +=","+ (value instanceof String?"'"+value+"'":value+"");
						}
					}
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return new String[]{keys,values};
	}
	private String readSqlFile(String fileName) throws Exception {
        try {
        	InputStream is = this.getClass().getClassLoader().getResourceAsStream(fileName);
        	BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuilder content = new StringBuilder();
            String data = null;
            while ((data = br.readLine()) != null) {
                content.append(data);
                content.append(System.getProperty("line.separator"));
            }
            return content.toString();
        } catch (Exception e) {
            throw new Exception("Read sql file : [" + fileName + "] error ", e);
        }
    }
	/**
	 * 描述: 创建表
	 * 时间: 2017年11月29日 上午10:18:42
	 * @author yi.zhang
	 * @param table	表名
	 * @param flag	存在表是否重新建表
	 */
	public void createTable(String table,boolean flag){
		try {
			if(DATABASE_TYPE==1){
				List<String> tables = mongodbHandle.queryTables();
				if(tables!=null&&tables.contains(table)&&flag){
					mongodbHandle.dropTable(table);
					tables.remove(table);
				}
				if(tables==null||!tables.contains(table)){
					mongodbHandle.createTable(table);
				}
			}else{
				List<String> tables = jdbcHandle.queryTables();
				if(tables!=null&&tables.contains(table)&&flag){
					jdbcHandle.dropTable(table);
					tables.remove(table);
				}
				if(tables==null||!tables.contains(table)){
					String sql = readSqlFile(TABLE_SQL_TEMPLATE);
					sql = sql.replace("{job_table}", table);
					jdbcHandle.createTable(sql);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public int insert(String table,RealTimeJob obj){
		int result = 0;
		if(DATABASE_TYPE==1){
			mongodbHandle.save(table, obj);
		}else{
			String[] data = reflect(table, obj);
			String keys = data[0];
			String values = data[1];
			String sql = "insert into "+table+"("+keys+")values("+values+")";
			result = jdbcHandle.executeUpdate(sql);
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public List<RealTimeJob> select(String table){
		if(DATABASE_TYPE==1){
			List<RealTimeJob> list = (List<RealTimeJob>) mongodbHandle.executeQuery(table, RealTimeJob.class,null);
			return list;
		}else{
			String sql = "select * from "+table;
			List<RealTimeJob> list = (List<RealTimeJob>) jdbcHandle.executeQuery(sql, RealTimeJob.class);
			return list;
		}
	}
}