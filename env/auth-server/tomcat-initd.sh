#!/bin/bash
# description: Tomcat Start Stop Restart  
# processname: tomcat  
# chkconfig: 234 20 80
CATALINA_HOME=/home/tomcat/apache-tomcat-7.0.47
cd $CATALINA_HOME

export LANG=en_US.UTF-8
  
case $1 in  
start)  
sudo -u tomcat sh $CATALINA_HOME/bin/startup.sh  
;;   
stop)     
sudo -u tomcat sh $CATALINA_HOME/bin/shutdown.sh  
;;   
restart)  
sudo -u tomcat sh $CATALINA_HOME/bin/shutdown.sh  
sudo -u tomcat sh $CATALINA_HOME/bin/startup.sh  
;;   
esac      
exit 0

