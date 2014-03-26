#!/bin/bash
set -eu

software/postgresql.sh

# poistetaan esteet hostin ja guestin valilta
iptables -F
service iptables save

# alustetaan ttk tietokanta ilman tauluja
sudo -u postgres psql --file=db/dev.sql
