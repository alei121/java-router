export ROOT_PATH=../..
export JAVA_HOME=../../../../jdk1.7.0_02


export PATH=${JAVA_HOME}/bin:$PATH

export LD_LIBRARY_PATH=${ROOT_PATH}/build/deploy/lib
export CLASSPATH=${ROOT_PATH}/build/deploy/lib/JavaRouter.jar
export CLASSPATH=${CLASSPATH}:${ROOT_PATH}/build/deploy/lib/JythonRouter.jar
export CLASSPATH=${CLASSPATH}:${ROOT_PATH}/build/deploy/lib/RawSocket.jar
export CLASSPATH=${CLASSPATH}:${ROOT_PATH}/ThirdParty/jython2.5.2/jython.jar

java -Djava.nio.channels.spi.SelectorProvider=code.messy.net.ip.spi.MessySelectorProvider code.messy.script.JythonReader preconfig.py $1
