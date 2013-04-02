#
# This shell script is for running the standalone validator from within the code, no pre-packaging required 
# (just run it from the directory this shell script is placed in)
# 
# Note: there are VM args to be able to run a debugger on the validator. If you intend to use one, change suspend=n to suspend=y (but don't check it in, it's probably better to leave suspend=n as a default)
#
java -showversion -Xms1024M -Xmx1024M -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005 -jar validator.jar $*