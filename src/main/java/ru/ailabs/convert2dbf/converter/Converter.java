package ru.ailabs.convert2dbf.converter;

import java.util.Map;

// T - "to" type, F - "from" Type
public interface Converter<T, F> {

    T createValue(Map<String, Object> map, String resultName);

    T convert(F value);

}
