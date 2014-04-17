#!/bin/bash
# description: Aitu Start Stop Restart
# processname: aitu
# chkconfig: 234 20 80
aitu_home=/data00/aitu

case $1 in
start)
  sudo -u tomcat sh -c "cd $aitu_home; ./start-ttk.sh"
;;
stop)
  sudo -u tomcat sh -c "cd $aitu_home; ./stop-ttk.sh"
;;
restart)
  sudo -u tomcat sh -c "cd $aitu_home; ./stop-ttk.sh"
  sleep 1
  sudo -u tomcat sh -c "cd $aitu_home; ./start-ttk.sh"
;;
status)
  if [ -f $aitu_home/ttk.pid ] && ps -p `cat $aitu_home/ttk.pid` > /dev/null; then
    echo "aitu (pid `cat $aitu_home/ttk.pid`) is running..."
  else
    echo "aitu is stopped"
    exit 1
  fi
;;
esac
exit 0
