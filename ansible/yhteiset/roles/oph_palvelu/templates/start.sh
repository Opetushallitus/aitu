#!/bin/bash

# {{ ansible_managed }}

# Käynnistää palvelun version, johon {{ palvelu }}.jar-linkki osoittaa.
# Ajettava palvelun asennushakemistossa käyttäjänä, jolla palveluprosessia
# halutaan ajaa.
#
# Käyttö:
#
#     ./start-{{ palvelu }}.sh

set -eu

current_jarfile='{{ palvelu }}.jar'
pidfile='{{ palvelu }}.pid'

echo "Starting {{ palvelu }}... "
if [ -a $current_jarfile ]
then
    nohup java -jar $current_jarfile 1> {{ palvelu }}.out 2> {{ palvelu }}.err &
    echo -n $! > $pidfile
else
    echo "Tiedosto '$current_jarfile' puuttuu"
    exit 1
fi
