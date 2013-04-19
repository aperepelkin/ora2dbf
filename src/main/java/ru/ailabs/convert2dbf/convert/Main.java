package ru.ailabs.convert2dbf.convert;

import ru.ailabs.convert2dbf.DbfFieldDescription;
import ru.ailabs.convert2dbf.DbfTable;
import ru.ailabs.text.ResourceStringReader;
import nl.knaw.dans.common.dbflib.DbfLibException;
import nl.knaw.dans.common.dbflib.Type;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class Main {


    private static Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args) throws ParseException, SQLException, IOException, DbfLibException {

        Options options = new Options();

        options.addOption("jdbc", true, "JDBC connection string");
        options.addOption("user", true, "Database User Name");
        options.addOption("pass", true, "Database User Password");
        options.addOption("sql", true, "SQL query file");
        options.addOption("convert2dbf", true, "DBF file name (optional)");

        CommandLineParser parser = new PosixParser();
        CommandLine cmd = parser.parse(options, args, true);

        String jdbcString = cmd.getOptionValue("jdbc");
        String username = cmd.getOptionValue("user");
        String password = cmd.getOptionValue("pass");
        String sqlfile = cmd.getOptionValue("sql");
        String dbffile = cmd.getOptionValue("convert2dbf");
        if (dbffile == null) {
            dbffile = sqlfile + ".convert2dbf";
        }

        StringBuilder query = ResourceStringReader.readToBuilder(new FileInputStream(sqlfile), "UTF-8");

        Connection conn = DriverManager.getConnection(jdbcString, username, password);

        QueryRunner run = new QueryRunner();

        class Handler extends MapListHandler {

            public List<DbfFieldDescription> fields = null;

            @Override
            protected Map<String, Object> handleRow(ResultSet rs) throws SQLException {
                if (fields == null) {
                    fields = new ArrayList<DbfFieldDescription>();
                    ResultSetMetaData meta = rs.getMetaData();
                    int cols = meta.getColumnCount();

                    for (int i = 0; i < cols; i++) {

                        String name = meta.getColumnName(i + 1);
                        String type = meta.getColumnTypeName(i + 1);
                        int precision = meta.getPrecision(i + 1);
                        precision = precision > 160 ? 160 : precision;
                        precision = precision == 0 ? 10 : precision;
                        int scale = meta.getScale(i + 1);
                        scale = scale < 0 ? 0 : scale;

                        logger.debug("Field Name " + name + ", Type: " + type + "(" + precision + "," + scale + ")");

                        if (type.equals("CHAR")) {
                            fields.add(new DbfFieldDescription(name, Type.CHARACTER, precision, scale));
                        } else if (type.equals("VARCHAR")) {
                            fields.add(new DbfFieldDescription(name, Type.CHARACTER, precision, scale));
                        } else if (type.equals("VARCHAR2")) {
                            fields.add(new DbfFieldDescription(name, Type.CHARACTER, precision, scale));
                        } else if (type.equals("NUMBER")) {
                            precision = precision > 20 ? 20 : precision;
                            fields.add(new DbfFieldDescription(name, Type.NUMBER, precision, scale));
                        } else if (type.equals("DATE")) {
                            fields.add(new DbfFieldDescription(name, Type.DATE, precision, scale));
                        } else {
                            logger.warn("No DBF field for " + name + " type: " + type);
                        }
                    }
                }
                return super.handleRow(rs);
            }
        }

        Handler h = new Handler();

        List<Map<String, Object>> result = run.query(
                conn, query.toString(), h);

        if (result != null) {

            DbfTable dbfTable = new Table(h.fields);
            dbfTable.createTable(dbffile);

            try {
                for (Map<String, Object> row : result) {
                    dbfTable.recordRow(row);
                }
            } finally {
                dbfTable.close();
                DbUtils.close(conn);
            }
        }
    }

    private static class Table extends DbfTable {

        public Table(List<DbfFieldDescription> fields) {
            super(fields);
        }
    }
}
