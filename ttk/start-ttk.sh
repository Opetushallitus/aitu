#!/bin/bash

# Käynnistää TTK-version, johon ttk.jar-linkki osoittaa.
# Ajettava TTK-asennushakemistossa käyttäjänä, jolla TTK-prosessia halutaan 
# ajaa.
#
# Käyttö:
#
#     ./start-ttk.sh

set -eu

CURRENT_JARFILE='ttk.jar'
PIDFILE='ttk.pid'

echo "Starting Aitu... "
if [ -a $CURRENT_JARFILE ]
then
    nohup java -jar $CURRENT_JARFILE 1> ttk.out 2> ttk.err &
    echo -n $! > $PIDFILE
else
    echo "Tiedosto '$CURRENT_JARFILE' puuttuu"
    exit 1
fi
