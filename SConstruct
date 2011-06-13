import os

def build_jar(project, class_files, env):
	denv = env.Copy()
	project_build_dir = os.path.join(build_dir, project)
	target_jar = os.path.join(deploy_lib_dir, project + '.jar')
	denv['JARCHDIR'] = project_build_dir
	return denv.Jar(target = target_jar, source = project_build_dir)

def build_javac(project, env):
	project_build_dir = os.path.join(build_dir, project)
	src_dir = os.path.join(code_dir, project, 'src')
	return env.Java(target = project_build_dir, source = src_dir)

# Take javac from system environment
env = Environment(ENV = os.environ)

# Define paths
root_dir = env.Dir('.').abspath
build_dir = os.path.join(root_dir, 'build')
code_dir = os.path.join(root_dir, 'code')
thirdparty_dir = os.path.join(root_dir, 'ThirdParty')
deploy_dir = os.path.join(root_dir, 'build', 'deploy')
deploy_lib_dir = os.path.join(root_dir, 'build', 'deploy', 'lib')

# Jars for building
env['JAVACLASSPATH'].append(os.path.join(deploy_lib_dir, 'RawSocket.jar'))
env['JAVACLASSPATH'].append(os.path.join(deploy_lib_dir, 'JavaRouter.jar'))
env['JAVACLASSPATH'].append(os.path.join(thirdparty_dir, 'jython2.5.1', 'jython.jar'))

# Java classes
RawSocket_class_files = build_javac('RawSocket', env)
JavaRouter_class_files = build_javac('JavaRouter', env)
JythonRouter_class_files = build_javac('JythonRouter', env)

# Jar files
RawSocket_jar_file = build_jar('RawSocket', RawSocket_class_files, env)
JavaRouter_jar_file = build_jar('JavaRouter', JavaRouter_class_files, env)
JythonRouter_jar_file = build_jar('JythonRouter', JythonRouter_class_files, env)

# Java dependencies
Depends(JavaRouter_class_files, RawSocket_jar_file)
Depends(JythonRouter_class_files, JavaRouter_jar_file)

# Native code
env['CPPPATH'] = [];
env['CPPPATH'].append(os.path.join(env['ENV']['JAVA_HOME'], 'include'))
env['CPPPATH'].append(os.path.join(env['ENV']['JAVA_HOME'], 'include', 'linux'))
target_lib_file = os.path.join(deploy_lib_dir, 'RawSocket')
env.SharedLibrary(target_lib_file, 'code/RawSocketC/src/code_messy_net_RawSocket.c')
