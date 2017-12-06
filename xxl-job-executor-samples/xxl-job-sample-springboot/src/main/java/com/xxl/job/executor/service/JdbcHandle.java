package com.xxl.job.executor.service;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
@Component
public class JdbcHandle {
	private static Logger logger = LoggerFactory.getLogger(JdbcHandle.class);
	@Autowired
	DruidPooledConnection connection;
	
	public void dropTable(String table){
		try {
			String sql = "drop table if exists "+table;
			connection.prepareStatement(sql).execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void createTable(String sql){
		try {
			String _sql = sql.toLowerCase().trim();
			if(_sql.startsWith("create")&&_sql.contains("table")){
				connection.prepareStatement(sql).execute();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 描述: 数据操作(Insert|Update|Delete)
	 * 时间: 2017年11月15日 上午11:27:52
	 * @author yi.zhang
	 * @param sql	sql语句
	 * @param params	参数
	 * @return	返回值
	 */
	public int executeUpdate(String sql,Object...params ){
		try {
			PreparedStatement ps = connection.prepareStatement(sql);
			if(params!=null&&params.length>0){
				for(int i=1;i<=params.length;i++){
					Object value = params[i-1];
					ps.setObject(i, value);
				}
			}
			int result = ps.executeUpdate();
			return result;
		} catch (Exception e) {
			logger.error("-----SQL excute update Error-----", e);
		}
		return -1;
	}
	/**
	 * 描述: 数据库查询(Select)
	 * 时间: 2017年11月15日 上午11:28:42
	 * @author yi.zhang
	 * @param sql	sql语句
	 * @param clazz	映射对象
	 * @param params	占位符参数
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<?> executeQuery(String sql,Class clazz,Object...params){
		try {
			List<Object> list=new ArrayList<Object>();
			PreparedStatement ps = connection.prepareStatement(sql);
			if(params!=null&&params.length>0){
				for(int i=1;i<=params.length;i++){
					Object value = params[i-1];
					ps.setObject(i, value);
				}
			}
			ResultSet rs = ps.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int count = rsmd.getColumnCount();
			Map<String,String> reflect = new HashMap<String,String>();
			for(int i=1;i<=count;i++){
				String column = rsmd.getColumnName(i);
				String tcolumn = column.replaceAll("_", "");
				if(clazz==null){
					reflect.put(column, column);
				}else{
					Field[] fields = clazz.getDeclaredFields();
					for (Field field : fields) {
						String tfield = field.getName();
						if(tcolumn.equalsIgnoreCase(tfield)){
							reflect.put(column, tfield);
							break;
						}
					}
				}
			}
			while(rs.next()){
				JSONObject obj = new JSONObject();
				for(String column:reflect.keySet()){
					String key = reflect.get(column);
					Object value = rs.getObject(column);
					obj.put(key, value);
				}
				Object object = obj;
				if(clazz!=null){
					object = JSON.parseObject(obj.toJSONString(), clazz);
				}
				list.add(object);
			}
			rs.close();
			ps.close();
			return list;
		} catch (Exception e) {
			logger.error("-----SQL excute query Error-----", e);
		}
		return null;
	}
	/**
	 * 描述: 查询数据表字段名(key:字段名,value:字段类型名)
	 * 时间: 2017年11月15日 上午11:29:32
	 * @author yi.zhang
	 * @param table	表名
	 * @return
	 */
	public Map<String,String> queryColumns(String table){
		try {
			String sql = "select * from "+table;
			PreparedStatement ps = connection.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int count = rsmd.getColumnCount();
			Map<String,String> reflect = new HashMap<String,String>();
			for(int i=1;i<=count;i++){
				String column = rsmd.getColumnName(i);
				String type = rsmd.getColumnTypeName(i);
				reflect.put(column, type);
			}
			rs.close();
			ps.close();
			return reflect;
		} catch (Exception e) {
			logger.error("-----Columns excute query Error-----", e);
		}
		return null;
	}
	/**
	 * 描述: 查询数据库表名
	 * 时间: 2017年11月15日 上午11:29:59
	 * @author yi.zhang
	 * @return 返回表
	 */
	public List<String> queryTables(){
		try {
			String sql = "show tables";
			PreparedStatement ps = connection.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			List<String> tables = new ArrayList<String>();
			while(rs.next()){
				String table = rs.getString(1);
				tables.add(table);
			}
			rs.close();
			ps.close();
			return tables;
		} catch (SQLException e) {
			logger.error("-----Tables excute query Error-----", e);
		}
		return null;
	}
}
