#!/bin/bash

# {{ ansible_managed }}

# Kertoo, onko palvelu {{ palvelu }} käynnissä.
#
# Käyttö:
#
#     ./status-{{ palvelu }}.sh

set -eu

pidfile='{{ palvelu }}.pid'

if [ -f $pidfile ] && ps -p `cat $pidfile` > /dev/null; then
  echo "{{ palvelu }} (pid `cat $pidfile`) is running..."
else
  echo "{{ palvelu }} is stopped"
  exit 1
fi
