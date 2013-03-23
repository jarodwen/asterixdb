ZK_HOME=$1
ZK_ID=$2
mkdir $ZK_HOME/data
echo $2 > $ZK_HOME/data/myid
CLASSPATH=$ZK_HOME/lib/zookeeper-3.4.5.jar:$ZK_HOME/lib/log4j-1.2.15.jar:$ZK_HOME/lib/slf4j-api-1.6.1.jar:$ZK_HOME/conf:$ZK_HOME/conf/log4j.properties
ZK_CONF=$ZK_HOME/zk.cfg
export JAVA_OPTS="-Xdebug -Xrunjdwp:transport=dt_socket,address=8400,server=y,suspend=n"
java $JAVA_OPTS -Dlog4j.configuration="file:$ZK_HOME/conf/log4j.properties" -cp $CLASSPATH org.apache.zookeeper.server.quorum.QuorumPeerMain $ZK_CONF