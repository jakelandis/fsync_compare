Fsync Compare
===========

Writes a 1GB file to compare write strategies (mmap vs. channel io) for append. 

All times are in nano seconds. 

```
mvn clean package
java -Xmx1024m  -jar target/fsync_compare-0.1-SNAPSHOT.jar
```


