# Example script to start Java Router using JythonReader
# May need to modify ROOT_PATH and JAVA_HOME

export ROOT_PATH=../..
#export JAVA_HOME=../../../../jdk1.7.0_02
export JAVA_HOME=/usr/lib/jvm/java-7-openjdk-amd64

export PATH=${JAVA_HOME}/bin:$PATH
export LD_LIBRARY_PATH=${ROOT_PATH}/build/deploy/lib
export CLASSPATH=${ROOT_PATH}/build/deploy/lib/JavaRouter.jar
export CLASSPATH=${CLASSPATH}:${ROOT_PATH}/build/deploy/lib/JythonRouter.jar
export CLASSPATH=${CLASSPATH}:${ROOT_PATH}/build/deploy/lib/RawSocket.jar
#export CLASSPATH=${CLASSPATH}:${ROOT_PATH}/ThirdParty/jython2.5.2/jython.jar
export CLASSPATH=${CLASSPATH}:/usr/share/java/*

java code.messy.script.JythonReader preconfig.py $1
