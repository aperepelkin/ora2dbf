package ru.ailabs.convert2dbf;

import nl.knaw.dans.common.dbflib.*;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DbfTable {

    private static Logger logger = Logger.getLogger(DbfTable.class);

    private long succeeded = 0;
    private long failed = 0;

    private Table table;
    private List<DbfFieldDescription> fields;

    protected DbfTable(List<DbfFieldDescription> fields) {
        this.fields = fields;
    }
    
    public List<DbfFieldDescription> getFields() {
    	return fields;
    }
    
    public Table getTable() {
    	return table;
    }

    public Table createTable(String fileName) throws DbfLibException, IOException {
        File file = new File(fileName);
        if (file.exists()) {
            file.delete();
        }

        List<Field> dbfFields = new ArrayList<Field>();
        for (DbfFieldDescription fieldDesc : fields) {
            dbfFields.add(new Field(fieldDesc.dbfName, fieldDesc.dbfType, fieldDesc.length, fieldDesc.precision));
        }
        table = new Table(file, Version.DBASE_5, dbfFields, "CP866");
        table.open(IfNonExistent.CREATE);

        succeeded = 0;
        failed = 0;

        return table;
    }

    public void recordRow(Map<String, Object> row) throws DbfLibException, IOException {

        List<Object> values = new ArrayList<Object>();
        for (DbfFieldDescription fieldDesc : fields) {
            values.add(fieldDesc.converter.createValue(row, fieldDesc.resultName));
        }

        try {
            table.addRecord(values.toArray());
            succeeded++;
        } catch (DbfLibException e) {
        	logger.error("Row stack: ");
        	for (DbfFieldDescription fieldDesc : fields) {
        		
        		if(fieldDesc.dbfType.equals(Type.CHARACTER)) {
        			String result = (String) row.get(fieldDesc.resultName);
        			if(result != null) {
        				if(result.length() > fieldDesc.length) {
            				logger.error("[WARNING]>>");
            			}
        			}
            	}
            	logger.error(fieldDesc.resultName + "=[" + row.get(fieldDesc.resultName) + "]");
            }
            logger.error("Table:" + table.getName() + ", Record: " + values, e);
            failed++;
            throw e;
        }
    }

    public void close() throws IOException {
        table.close();
        table = null;
    }

    public long getSucceeded() {
        return succeeded;
    }

    public long getFailed() {
        return failed;
    }
}
