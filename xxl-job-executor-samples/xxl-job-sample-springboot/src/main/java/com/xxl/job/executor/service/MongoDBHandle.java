package com.xxl.job.executor.service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.Filters;

@Component
public class MongoDBHandle {
	private static Logger logger = LoggerFactory.getLogger(MongoDBHandle.class);
	/**
	 * 主键ID是否处理(true:处理[id],false:不处理[_id])
	 */
	public static boolean ID_HANDLE=false;
	/**
	 * 批量数据大小
	 */
	public static int BATCH_SIZE = 10000;
	/**
	 * 最大时间(单位:毫秒)
	 */
	public static int MAX_WAIT_TIME = 24*60*60*1000;
	@Autowired
	MongoDatabase session;
	
	public void dropTable(String table){
		try {
			session.getCollection(table).drop();
		} catch (Exception e) {
			logger.error("--mongodb drop error!",e);
		}
	}
	public void createTable(String table){
		try {
			session.createCollection(table);
		} catch (Exception e) {
			logger.error("--mongodb create error!",e);
		}
	}
	/**
	 * @decription 保存数据
	 * @author yi.zhang
	 * @time 2017年6月2日 下午6:18:49
	 * @param table	文档名称(表名)
	 * @param obj
	 * @return
	 */
	public int save(String table, Object obj) {
		try {
			MongoCollection<Document> collection = session.getCollection(table);
			if (collection == null) {
				session.createCollection(table);
				collection = session.getCollection(table);
			}
			collection.insertOne(Document.parse(JSON.toJSONString(obj)));
			return 1;
		} catch (Exception e) {
			logger.error("--mongodb insert error!",e);
		}
		return -1;
	}

	/**
	 * @decription 更新数据
	 * @author yi.zhang
	 * @time 2017年6月2日 下午6:19:08
	 * @param table	文档名称(表名)
	 * @param obj
	 * @return
	 */
	public int update(String table, Object obj) {
		try {
			MongoCollection<Document> collection = session.getCollection(table);
			if (collection == null) {
				return 0;
			}
			JSONObject json = JSON.parseObject(JSON.toJSONString(obj));
			Document value = Document.parse(JSON.toJSONString(obj));
			collection.replaceOne(Filters.eq("_id", json.containsKey("_id")?json.get("_id"):json.get("id")), value);
			return 1;
		} catch (Exception e) {
			logger.error("--mongodb update error!",e);
		}
		return -1;
	}

	/**
	 * @decription 删除数据
	 * @author yi.zhang
	 * @time 2017年6月2日 下午6:19:25
	 * @param table	文档名称(表名)
	 * @param obj
	 * @return
	 */
	public int delete(String table, Object obj) {
		try {
			MongoCollection<Document> collection = session.getCollection(table);
			if (collection == null) {
				return 0;
			}
			JSONObject json = JSON.parseObject(JSON.toJSONString(obj));
			collection.findOneAndDelete(Filters.eq("_id", json.containsKey("_id")?json.get("_id"):json.get("id")));
			return 1;
		} catch (Exception e) {
			logger.error("--mongodb delete error!",e);
		}
		return -1;
	}

	/**
	 * @decription 数据库查询
	 * @author yi.zhang
	 * @time 2017年6月26日 下午4:12:59
	 * @param table	文档名称(表名)
	 * @param clazz		映射对象
	 * @param params	参数
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<?> executeQuery(String table, Class clazz, JSONObject params) {
		try {
			MongoCollection<Document> collection = session.getCollection(table);
			if (collection == null) {
				return null;
			}
			List<Object> list = new ArrayList<Object>();
			FindIterable<Document> documents = null;
			if (params != null) {
				List<Bson> filters = new ArrayList<Bson>();
				for (String key : params.keySet()) {
					Object value = params.get(key);
					filters.add(Filters.eq(key, value));
				}
				documents = collection.find(Filters.and(filters));
			} else {
				documents = collection.find();
			}
			MongoCursor<Document> cursor = documents.batchSize(BATCH_SIZE).noCursorTimeout(true).iterator();
			while (cursor.hasNext()) {
				JSONObject obj = new JSONObject();
				Document document = cursor.next();
				for (String column : document.keySet()) {
					Object value = document.get(column);
					if(value instanceof ObjectId){
						value = document.getObjectId(column).toHexString();
					}
					if (clazz == null) {
						obj.put(ID_HANDLE?column.replaceFirst("^(\\_?)", ""):column, value);
					} else {
						String tcolumn = column.replaceAll("_", "");
						Field[] fields = clazz.getDeclaredFields();
						for (Field field : fields) {
							String tfield = field.getName();
							if (column.equalsIgnoreCase(tfield) || tcolumn.equalsIgnoreCase(tfield)) {
								obj.put(tfield, value);
								break;
							}
						}
					}
				}
				Object object = obj;
				if (clazz != null) {
					object = JSON.parseObject(obj.toJSONString(), clazz);
				}
				list.add(object);
			}
			cursor.close();
			return list;
		} catch (Exception e) {
			logger.error("--mongodb select error!",e);
		}
		return null;
	}
	
	/**
	 * @decription 查询数据表字段名(key:字段名,value:字段类型名)
	 * @author yi.zhang
	 * @time 2017年6月30日 下午2:16:02
	 * @param table	表名
	 * @return
	 */
	public Map<String,String> queryColumns(String table){
		try {
			MongoCollection<Document> collection = session.getCollection(table);
			if (collection == null) {
				return null;
			}
			Map<String,String> reflect = new HashMap<String,String>();
			FindIterable<Document> documents = collection.find();
			Document document = documents.first();
			if(document==null){
				return reflect;
			}
			for (String column : document.keySet()) {
				Object value = document.get(column);
				String type = "string";
				if(value instanceof Integer){
					type = "int";
				}
				if(value instanceof Long){
					type = "long";
				}
				if(value instanceof Double){
					type = "double";
				}
				if(value instanceof Boolean){
					type = "boolean";
				}
				if(value instanceof Date){
					type = "date";
				}
				reflect.put(column, type);
			}
			return reflect;
		} catch (Exception e) {
			logger.error("--mongodb columns error!",e);
		}
		return null;
	}
	/**
	 * @decription 查询数据库表名
	 * @author yi.zhang
	 * @time 2017年6月30日 下午2:16:02
	 * @param table	表名
	 * @return
	 */
	public List<String> queryTables(){
		try {
			MongoIterable<String> collection = session.listCollectionNames();
			if (collection == null) {
				return null;
			}
			List<String> tables = new ArrayList<String>();
			MongoCursor<String> cursor = collection.iterator();
			while(cursor.hasNext()){
				String table = cursor.next();
				tables.add(table);
			}
			return tables;
		} catch (Exception e) {
			logger.error("--mongodb tables error!",e);
		}
		return null;
	}
}