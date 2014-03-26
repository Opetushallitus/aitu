#!/bin/bash
# description: Aituhaku Start Stop Restart
# processname: aituhaku
# chkconfig: 234 20 80
aituhaku_home=/data00/aituhaku

case $1 in
start)
  sudo -u tomcat sh -c "cd $aituhaku_home; ./start-aituhaku.sh"
;;
stop)
  sudo -u tomcat sh -c "cd $aituhaku_home; ./stop-aituhaku.sh"
;;
restart)
  sudo -u tomcat sh -c "cd $aituhaku_home; ./stop-aituhaku.sh"
  sleep 1
  sudo -u tomcat sh -c "cd $aituhaku_home; ./start-aituhaku.sh"
;;
status)
  if [ -f $aituhaku_home/aituhaku.pid ] && ps -p `cat $aituhaku_home/aituhaku.pid` > /dev/null; then
    echo "aituhaku (pid  `cat $aituhaku_home/aituhaku.pid`) is running..."
  else
    echo "aituhaku is stopped"
  fi
;;
esac
exit 0
