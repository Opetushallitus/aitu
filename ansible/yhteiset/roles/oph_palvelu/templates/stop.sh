#!/bin/bash

# {{ ansible_managed }}

# Sammuttaa ajossa olevan palveluprosessin. Ajettava käyttäjänä, jolla on
# riittävät oikeudet palveluprosessin tappamiseen.
#
# Käyttö:
#
#     ./stop-{{ palvelu }}.sh

set -eu

current_jarfile='{{ palvelu }}.jar'
pidfile='{{ palvelu }}.pid'

if [ -a $pidfile ]
then
    echo "Stopping {{ palvelu }}..."
    kill `cat $pidfile` && rm $pidfile
fi
