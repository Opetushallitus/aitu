#!/bin/bash
# description: {{ palvelu }} Start Stop Restart
# processname: {{ palvelu }}
# chkconfig: 234 20 80

# {{ ansible_managed }}

set -eu

case $1 in
start)
  sudo -u tomcat sh -c "cd {{ asennushakemisto }}; ./start-{{ palvelu }}.sh"
;;
stop)
  sudo -u tomcat sh -c "cd {{ asennushakemisto }}; ./stop-{{ palvelu }}.sh"
;;
restart)
  sudo -u tomcat sh -c "cd {{ asennushakemisto }}; ./stop-{{ palvelu }}.sh"
  sleep 1
  sudo -u tomcat sh -c "cd {{ asennushakemisto }}; ./start-{{ palvelu }}.sh"
;;
status)
  sudo -u tomcat sh -c "cd {{ asennushakemisto }}; ./status-{{ palvelu }}.sh"
;;
esac
