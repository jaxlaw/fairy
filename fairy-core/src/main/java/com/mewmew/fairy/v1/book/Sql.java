package com.mewmew.fairy.v1.book;

import com.mewmew.fairy.v1.cli.Param;
import com.mewmew.fairy.v1.json.JsonOutput;
import com.mewmew.fairy.v1.json.JsonRegistry;
import com.mewmew.fairy.v1.json.OutputFormat;
import com.mewmew.fairy.v1.pipe.Output;
import com.mewmew.fairy.v1.spell.Spell;
import com.mewmew.fairy.v1.spell.Help;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.sql.Driver;
import java.util.HashMap;
import java.util.Map;

@Help(desc = "run sql query")
public class Sql extends Spell
{
    @Param(option = "O", name = "format", desc = "PRETTY, LINE, COMPACT, TAB, CSV, WIKI", defaultValue = "LINE")
    private OutputFormat outputFormat;
    @Param(desc = "specify column order for TAB and CSV, comma sepearted list of column names")
    private String[] columnOrder;

    @Param (option="D", desc="optional, driver class name")
    String driver = "oracle.jdbc.driver.OracleDriver";
    @Param (desc ="the query to run")
    String query;
    @Param (desc ="the jdbc url")
    String jdbcUrl;
    @Param
    String user;
    @Param
    String password;
    @Param(desc = "register as <arg> db")
    String register;
    @Param(desc = "use registered db <arg>")
    String db;
    @Param (desc = "list all registered dbs")
    boolean list;

    JsonRegistry registry = new JsonRegistry("sqldbs.json");

    @Override
    public void cast()
    {
        if (list) {
            Output<Map<String, Object>> output = null ;
            try {
                output = JsonOutput.createOutput(getOutputStream(), OutputFormat.PRETTY);
                Map<String, Map<String, Object>> map = registry.loadAsMap("name");
                for (Map<String, Object> objectMap : map.values()) {
                    output.output(objectMap);
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                if (output != null) {
                    try {
                        output.close();
                    }
                    catch (IOException e) {
                    }
                }
            }
            return ;
        }
        if (register != null) {
            if (jdbcUrl == null || user == null || password == null) {
                System.err.printf("mssing jdbcUrl/user/password !\n");
                return;
            }
            Map<String, Object> json = new HashMap<String, Object>();
            json.put("jdbcUrl", jdbcUrl);
            json.put("driver", driver);
            json.put("user", user);
            json.put("password", password);
            json.put("name", register);
            try {
                registry.add(json);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        try {
            if (db != null) {
                Map<String, Map<String, Object>> map = registry.loadAsMap("name");
                Map<String, Object> dbInfo = map.get(db);
                if (dbInfo != null) {
                    runSql((String) dbInfo.get("driver"), (String) dbInfo.get("jdbcUrl"), (String) dbInfo.get("user"), (String) dbInfo.get("password"));
                }
                else {
                    System.err.printf("db name %s is not registered !", db);
                }
            }
            else {
                if (jdbcUrl == null || user == null || password == null) {
                    System.err.printf("mssing jdbcUrl/user/password !\n");
                    return;
                }
                runSql(driver, jdbcUrl, user, password);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void runSql(String driver, String jdbcUrl, String user, String password) throws SQLException, IOException
    {
        if (!StringUtils.isEmpty(driver)) {
            try {
                DriverManager.registerDriver((Driver) Class.forName(driver).newInstance());
            }
            catch (Exception e) {
            }
        }
        Connection connection = DriverManager.getConnection(jdbcUrl, user, password);
        runSql(connection, query);
    }

    private void runSql(Connection conn, String query) throws SQLException, IOException
    {
        if (StringUtils.isEmpty(query)) {
            throw new IllegalArgumentException("empty query");
        }
        Statement stmt = conn.createStatement();
        ResultSet rs = null;
        Output<Map<String, Object>> output = null;
        try {
            rs = stmt.executeQuery(query);
            output = JsonOutput.createOutput(getOutputStream(), outputFormat, columnOrder);
            Column[] columns = null;
            int columnCount = 0;
            while (rs.next()) {
                if (columns == null) {
                    columnCount = rs.getMetaData().getColumnCount();
                    columns = new Column[columnCount];
                    for (int j = 1; j <= columnCount; j++) {
                        columns[j-1] = new Column(j, rs.getMetaData().getColumnName(j), rs.getMetaData().getColumnType(j));
                    }
                }
                Map<String, Object> json = new HashMap<String, Object>();
                for (int j = 0; j < columnCount; j++) {
                    json.put(columns[j].name, getObject(rs, columns[j].index, columns[j].sqltype));
                }
                output.output(json);
            }
        }
        catch (SQLException e) {
            throw e;
        }
        finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
            }
            catch (Exception e) {
            }
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (Exception e) {
            }
            try {
                if (output != null) {
                    output.close();
                }
            }
            catch (Exception e) {
            }
        }
    }

    private static class Column
    {
        int index ;
        String name ;
        int sqltype ;

        private Column(int index, String name, int sqltype)
        {
            this.index = index;
            this.name = name;
            this.sqltype = sqltype;
        }
    }

    private static Object getObject(ResultSet rs, int i, Integer sqlType) throws SQLException
	{
		switch(sqlType) {
			case Types.CLOB :
				return rs.getString(i) ;
			case Types.BLOB :
				return rs.getBytes(i) ;
            case Types.TIMESTAMP :
                return rs.getTimestamp(i);
            case Types.VARBINARY : case Types.BINARY :
                byte[] b = rs.getBytes(i) ;
                if ( b!= null ) {
                    return new String(Hex.encodeHex(b)) ;
                }
                return null;
			default :
				return rs.getObject(i) ;
		}
	}

}
