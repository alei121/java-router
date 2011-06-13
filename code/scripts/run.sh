export ROOT_PATH=../..
export JAVA_HOME=../../../../../jdk1.6.0_20


export PATH=${JAVA_HOME}/bin:$PATH

export LD_LIBRARY_PATH=${ROOT_PATH}/build/deploy/lib
export CLASSPATH=${ROOT_PATH}/build/deploy/lib/JavaRouter.jar
export CLASSPATH=${CLASSPATH}:${ROOT_PATH}/build/deploy/lib/JythonRouter.jar
export CLASSPATH=${CLASSPATH}:${ROOT_PATH}/build/deploy/lib/RawSocket.jar
export CLASSPATH=${CLASSPATH}:${ROOT_PATH}/ThirdParty/jython2.5.1/jython.jar

java Main $1 $2
