package com.xxl.job.core.util;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

/**
 * Created by xuxueli on 16/9/30.
 */
public class DBUtil {

    private static Connection getConn(DataSource dataSource) {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * update
     *
     * @param dataSource
     * @param sql
     * @param params
     */
    public static int update(DataSource dataSource, String sql, Object params[]) {
        Connection connection = getConn(dataSource);
        PreparedStatement preparedStatement = null;
        int ret = 0;
        try {
            preparedStatement = connection.prepareStatement(sql);
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    preparedStatement.setObject(i + 1, params[i]);
                }
            }
            ret = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            release(connection, preparedStatement, null);
        }
        return ret;
    }

    /**
     * query
     *
     * @param dataSource
     * @param sql
     * @param params
     * @return
     */
    public static List<Map<String, Object>> query(DataSource dataSource, String sql, Object[] params) {
        Connection connection = getConn(dataSource);
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(sql);
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    preparedStatement.setObject(i + 1, params[i]);
                }
            }
            resultSet = preparedStatement.executeQuery();

            List<Map<String, Object>> ret = resultSetToList(resultSet);
            return ret;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            release(connection, preparedStatement, resultSet);
        }
        return null;
    }

    private static List<Map<String, Object>> resultSetToList(ResultSet resultSet) throws SQLException {
        if (resultSet == null) {
            return Collections.EMPTY_LIST;
        }

        ResultSetMetaData resultSetMetaData = resultSet.getMetaData(); // 得到结果集(rs)的结构信息，比如字段数、字段名等
        int columnCount = resultSetMetaData.getColumnCount(); // 返回此 ResultSet 对象中的列数

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        while (resultSet.next()) {
            Map<String, Object> rowData = new HashMap<String, Object>(columnCount);
            for (int i = 1; i <= columnCount; i++) {
                rowData.put(resultSetMetaData.getColumnName(i), resultSet.getObject(i));
            }
            list.add(rowData);
        }
        return list;
    }

    /**
     * release
     * @param connection
     * @param preparedStatement
     * @param resultSet
     */
    public static void release(Connection connection, PreparedStatement preparedStatement, ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (preparedStatement != null) {
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
