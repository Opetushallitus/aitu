#!/bin/bash
set -eu

if [ $# -ne 1 ]
then
    echo "$0 <sovelluspalvelimen-ip>"
    exit 1
fi

APP_HOST=$1

software/postgresql.sh

# Sovelluspalvelin
iptables -I INPUT 1 -p tcp -s $APP_HOST --dport 5432 -j ACCEPT

service iptables save

# alustetaan ttk tietokanta ilman tauluja

until service postgresql-9.2 status > /dev/null; do
  echo "Waiting for postgresql...\n"
  sleep 1
done
su postgres -c 'psql --file=db/dev.sql'
