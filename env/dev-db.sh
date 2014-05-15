#!/bin/bash
set -eu

./db-server.sh 192.168.50.51 ssh/dev_id_rsa.pub

# poistetaan esteet hostin ja guestin valilta
iptables -F
service iptables save
