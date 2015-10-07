#!/bin/bash
set -eu

if [ $# -lt 1 ]
then
    echo "$0 <tietokantapalvelimen-ip> [base-url] [id_rsa.pub]"
    exit 1
fi

ip=`hostname -I | cut -f1 -d' '`
db_host=$1
base_url=${2:-"http://$ip/aitu"}
id_rsa_pub=${3:-'ssh/ci_id_rsa.pub'}
cas_ip=${4:-'192.168.50.53'}
install_dir=/data00/aitu

set -x

sed -ri 's/(debuglevel)=[0-9]*/\1=10/' /etc/yum.conf
export URLGRABBER_DEBUG=1

software/jre.sh
software/httpd.sh

mkdir -p $install_dir

useradd aituadmin

# aituadmin-käyttäjälle oikeudet ajaa rootina asennukseen tarvittavat komennot
# ilman salasanaa
echo 'aituadmin ALL = NOPASSWD: ALL' >> /etc/sudoers

# Sallitaan asennusten pääsy ssh:lla
mkdir /home/aituadmin/.ssh
cat $id_rsa_pub >> /home/aituadmin/.ssh/authorized_keys

chown -R aituadmin:aituadmin /home/aituadmin/.ssh
chmod 700 /home/aituadmin/.ssh
chmod 644 /home/aituadmin/.ssh/authorized_keys

# Vagrant-isäntäkone
iptables -I INPUT 1 -p tcp -s 192.168.50.1 --dport 80 -j ACCEPT

# CAS-server
iptables -I INPUT 1 -p tcp -s $cas_ip --dport 80 -j ACCEPT

service iptables save
