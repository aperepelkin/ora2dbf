package ru.ailabs.convert2dbf;

import ru.ailabs.convert2dbf.converter.Converter;
import ru.ailabs.convert2dbf.converter.Converters;
import nl.knaw.dans.common.dbflib.Type;

public class DbfFieldDescription {

    public final String dbfName;
    public final String resultName;
    public final Type dbfType;
    public final int length;
    public final int precision;
    public final Converter<?, ?> converter;

    public DbfFieldDescription(String dbfName, String resultName, Type dbfType, int length, int precision,
            Converter<?, ?> converter) {
        this.dbfName = dbfName;
        this.resultName = resultName;
        this.dbfType = dbfType;
        this.length = length;
        this.precision = precision;
        this.converter = converter;
    }
    
    public DbfFieldDescription(String dbfName, String resultName, Type dbfType, int length, int precision) {
    	this(dbfName, resultName, dbfType, length, precision, Converters.AS_IS.toConverter());
    }

    public DbfFieldDescription(String dbfName, Type dbfType, int length, int precision) {
    	this(dbfName, dbfName, dbfType, length, precision);
    }

    public DbfFieldDescription(String dbfName, Type dbfType, int length, int precision, Converter<?, ?> converter) {
    	this(dbfName, dbfName, dbfType, length, precision, converter);
    }
}
