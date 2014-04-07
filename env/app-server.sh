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

software/jre.sh
software/httpd.sh

mkdir -p $install_dir

useradd aituadmin

# Aitua ajetaan tomcat-käyttäjänä
useradd -r -s /bin/false tomcat

# aituadmin-käyttäjälle oikeudet ajaa rootina asennukseen tarvittavat komennot
# ilman salasanaa
echo 'aituadmin ALL = NOPASSWD: /bin/cp * /data00/aitu, /bin/ln -sf * ttk.jar, /bin/chown tomcat\:tomcat -R /data00/aitu, /sbin/service aitu *' >> /etc/sudoers

#init.d-skripti
cp app-server/aitu-init.d.sh /etc/init.d/aitu
chmod 755 /etc/init.d/aitu

# Palvelimen asetukset
cp app-server/ttk.properties $install_dir
mkdir "$install_dir/resources"
chmod a+rx "$install_dir/resources"
cp app-server/log4j.properties "$install_dir/resources"
mkdir "$install_dir/logs"
chmod a+rwx "$install_dir/logs"
sed -i -e "s|\\\$DB_HOST|$db_host|g" $install_dir/ttk.properties
sed -i -e "s|\\\$BASE_URL|$base_url|g" $install_dir/ttk.properties
sed -i -e "s|\\\$CAS_URL|https://$cas_ip:8443/cas-server-webapp-3.5.2|g" $install_dir/ttk.properties

# Migraatioiden asetukset
cp app-server/ttk-db.properties $install_dir
sed -i -e "s|\\\$DB_HOST|$db_host|g" $install_dir/ttk-db.properties


# Aituhaku
aituhaku_install_dir=/data00/aituhaku

mkdir -p $aituhaku_install_dir

echo 'aituadmin ALL = NOPASSWD: /bin/cp * /data00/aituhaku, /bin/ln -sf * aituhaku.jar, /bin/chown tomcat\:tomcat -R /data00/aituhaku, /sbin/service aituhaku *' >> /etc/sudoers

cp app-server/aituhaku/aituhaku-init.d.sh /etc/init.d/aituhaku
chmod 755 /etc/init.d/aituhaku

cp app-server/aituhaku/aituhaku.properties $aituhaku_install_dir
mkdir "$aituhaku_install_dir/resources"
chmod a+rx "$aituhaku_install_dir/resources"
cp app-server/aituhaku/logback.xml "$aituhaku_install_dir/resources"
mkdir "$aituhaku_install_dir/logs"
chmod a+rwx "$aituhaku_install_dir/logs"
sed -i -e "s|\\\$DB_HOST|$db_host|g" $aituhaku_install_dir/aituhaku.properties

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