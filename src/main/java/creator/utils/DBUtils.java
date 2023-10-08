package creator.utils;

/**
 * 
 */

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.postgresql.util.PSQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.pgvector.PGvector;

/**
 * @author Anurag
 *
 */
public class DBUtils {
	private static final Logger logger = LoggerFactory.getLogger(DBUtils.class);
	static DBUtils db;
	static ComboPooledDataSource dataSource;

	private synchronized static ComboPooledDataSource getDataSource() throws PropertyVetoException {
		ComboPooledDataSource cpds = new ComboPooledDataSource();
		try {
			cpds.setJdbcUrl("jdbc:postgresql://localhost:5432/contentcreator");
			cpds.setUser("postgres");
			cpds.setPassword("root");
			cpds.setDriverClass("org.postgresql.Driver");
			// Optional Settings
			cpds.setInitialPoolSize(10);
			cpds.setMinPoolSize(10);
			cpds.setAcquireIncrement(10);
			cpds.setMaxPoolSize(20);
			cpds.setMaxStatements(100);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception Occur {}", e.getMessage());
		}

		return cpds;
	}

	public static synchronized DBUtils getInstance() {
		try {
			if (db == null) {
				db = new DBUtils();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return db;
	}

	private DBUtils() throws PropertyVetoException {
		dataSource = getDataSource();
	}

	public int insertIntoDB(String sqlQuery) {
		logger.info(sqlQuery);
		int retrunIndex = 0;
		Connection connection = null;
		Statement pstmt = null;
		try {
			connection = dataSource.getConnection();
			pstmt = connection.createStatement();

			pstmt.executeUpdate(sqlQuery);
		} catch (SQLException se) {
			logger.error(sqlQuery);
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstmt != null)
					connection.close();
			} catch (SQLException se) {
			}
			try {
				if (connection != null)
					connection.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}
		return retrunIndex;
	}

	public int insertIntoDBWithGeneratedKey(String sqlQuery) {
		logger.info(sqlQuery);
		int retrunIndex = 0;
		Connection connection = null;
		Statement pstmt = null;
		ResultSet resultSet = null;
		try {
			connection = dataSource.getConnection();
			pstmt = connection.createStatement();
			resultSet = pstmt.executeQuery(sqlQuery);
			resultSet.next();
			retrunIndex = resultSet.getInt(1);
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstmt != null)
					connection.close();
			} catch (SQLException se) {
			}
			try {
				if (connection != null)
					connection.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}
		System.err.println(retrunIndex);
		return retrunIndex;
	}

	public ArrayList<HashMap<String, String>> executeQuery(StackTraceElement[] stackTraceElements, String sqlQuery) {
		long now = System.currentTimeMillis();
		ArrayList<HashMap<String, String>> table = new ArrayList<HashMap<String, String>>();
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet resultSet = null;

		try {
			connection = dataSource.getConnection();
			pstmt = connection.prepareStatement(sqlQuery);
			resultSet = pstmt.executeQuery();
			ResultSetMetaData rsmd = resultSet.getMetaData();

			ArrayList<String> columnnames = new ArrayList<String>();
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {

				columnnames.add(rsmd.getColumnName(i));
			}

			while (resultSet.next()) {
				HashMap<String, String> row = new HashMap<String, String>();
				for (String columnName : columnnames) {
					String first = resultSet.getString(columnName);
					row.put(columnName, first);
				}
				table.add(row);
			}
		} catch (PSQLException psqe) {
			logger.error("Problem running this query ->" + sqlQuery);
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstmt != null)
					connection.close();
			} catch (SQLException se) {
			}
			try {
				if (connection != null)
					connection.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}
		if ((System.currentTimeMillis() - now) > 5000)
			logger.error("Exxceptionally long time (" + (System.currentTimeMillis() - now) + ") taken for query ->"
					+ sqlQuery);
		logger.info(System.currentTimeMillis() - now + "ms - [" + stackTraceElements[1].getClassName() + "."
				+ stackTraceElements[1].getMethodName() + ":" + stackTraceElements[1].getLineNumber() + "]--->"
				+ sqlQuery);
		return table;
	}

	public int updateObject(String sqlQuery, HashMap<Integer, Object> data) throws SQLException {
		int retrunIndex = 0;

		logger.info(sqlQuery);
		Connection connection = null;
		PreparedStatement pstmt = null;
		try {
			connection = dataSource.getConnection();
			pstmt = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);

			for (Integer index : data.keySet()) {
				if (data.get(index) != null) {
					switch (data.get(index).getClass().getName().toString()) {
					case "java.lang.Integer":
						pstmt.setInt(index, Integer.parseInt(data.get(index).toString()));
						break;
					case "java.lang.String":
						pstmt.setString(index, data.get(index).toString());
						break;
					case "java.lang.Float":
						pstmt.setFloat(index, Float.parseFloat(data.get(index).toString()));
						break;
					case "java.lang.Boolean":
						pstmt.setBoolean(index, Boolean.parseBoolean(data.get(index).toString()));
						break;
					case "java.sql.Timestamp":
						pstmt.setTimestamp(index, (Timestamp) data.get(index));
						break;
					case "com.pgvector.PGvector":
						pstmt.setObject(index, (PGvector) data.get(index));
						break;
					default:
						pstmt.setString(index, data.get(index).toString());
						break;
					}
				} else {
					pstmt.setObject(index, null);
				}

			}
			retrunIndex = pstmt.executeUpdate();

			if (sqlQuery.toLowerCase().contains("insert") && retrunIndex > 0) {
				java.sql.ResultSet generatedKeys = pstmt.getGeneratedKeys();
				if (generatedKeys.next()) {
					retrunIndex = generatedKeys.getInt(1);
				}
			} else {
				retrunIndex = 0;
			}

		} catch (SQLException se) {
			logger.error(sqlQuery);
			se.printStackTrace();
			throw se;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstmt != null)
					connection.close();
			} catch (SQLException se) {
			}
			try {
				if (connection != null)
					connection.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}
		return retrunIndex;

	}

	public int[] updateInBatch(String sqlQuery, List<HashMap<Integer, Object>> dataList) throws SQLException {
		int[] retrunIndex = null;

		logger.info(sqlQuery);
		Connection connection = null;
		PreparedStatement pstmt = null;
		try {
			connection = dataSource.getConnection();
			pstmt = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);

			for (HashMap<Integer, Object> data : dataList) {
				for (Integer index : data.keySet()) {
					if (data.get(index) != null) {
						switch (data.get(index).getClass().getName().toString()) {
						case "java.lang.Integer":
							pstmt.setInt(index, Integer.parseInt(data.get(index).toString()));
							break;
						case "java.lang.String":
							pstmt.setString(index, data.get(index).toString());
							break;
						case "java.lang.Float":
							pstmt.setFloat(index, Float.parseFloat(data.get(index).toString()));
							break;
						case "java.lang.Boolean":
							pstmt.setBoolean(index, Boolean.parseBoolean(data.get(index).toString()));
							break;

						default:
							pstmt.setString(index, data.get(index).toString());
							break;
						}
					} else {
						pstmt.setObject(index, null);
					}

				}
				System.out.println(pstmt.toString());
				pstmt.addBatch();
			}
			retrunIndex = pstmt.executeBatch();

		} catch (SQLException se) {
			logger.error(sqlQuery);
			throw se;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstmt != null)
					connection.close();
			} catch (SQLException se) {
			}
			try {
				if (connection != null)
					connection.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}
		return retrunIndex;

	}

}