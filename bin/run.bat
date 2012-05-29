@echo off

set CLASSPATH=%CLASSPATH%;ojdbc5-11.1.0.7.0.jar;orai18n.jar;orai18n-collation.jar;orai18n-mapping.jar;orai18n-utility.jar;convert-1.0-jar-with-dependencies.jar
java ru.ailabs.convert2dbf.convert.Main %*