Usage : java -jar openttd-conf-minifier.jar custom_conf [base_conf]
custom_conf	your server configuration file
base_conf	Optional: base configuration file

By default, the base_conf is openttd.cfg from openttd 1.3.0
The output file will be prefixed with : 'mini_'

Example:	java -jar openttd-conf-minifier.jar my_server.cfg
will produce:	mini_my_server.cfg

