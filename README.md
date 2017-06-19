Fsync Compare
===========

Writes a 1GB file to compare write strategies (mmap vs. channel io) for append. 

All times are in nano seconds. 

Build from source:
```
mvn clean package
java -Xmx1024m -Djava.io.tmpdir=<some_path_that_is_not_tmp_fs> -jar target/fsync_compare-0.1-SNAPSHOT.jar
```
For example (from the included binary):
```
wget https://github.com/jakelandis/fsync_compare/raw/master/bin/fsync_compare-0.1-SNAPSHOT.jar
java -Xmx1024m -Djava.io.tmpdir=/storage -jar fsync_compare-0.1-SNAPSHOT.jar 
```
The code uses Java's temp directory...but if that is a mapped to a temporary filesystem, then the results will be skewed badly.



