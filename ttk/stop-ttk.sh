#!/bin/bash

# Sammuttaa ajossa olevan TTK-prosessin. Ajettava käyttäjänä, 
# jolla on riittävät oikeudet TTK-prosessin tappamiseen.
#
# Käyttö:
#
#     ./stop-ttk.sh

set -eu

CURRENT_JARFILE='ttk.jar'
PIDFILE='ttk.pid'

if [ -a $PIDFILE ]
then
    echo "Stopping Aitu..."
    kill `cat $PIDFILE` && rm $PIDFILE
fi
