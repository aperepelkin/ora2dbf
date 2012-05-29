package ru.ailabs.convert2dbf.converter;

import java.util.Map;

public class AsIsConverter implements Converter<Object, Object> {

    public Object createValue(Map<String, Object> map, String resultName) {
        return map.get(resultName);
    }

    public Object convert(Object value) {
        return value;
    }

}
