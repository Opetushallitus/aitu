#!/bin/bash

# Julkaisee annetun TTK-version etäpalvelimelle.
#
# Käyttö:
#
#     ./deploy.sh [-c] [-t] <ttk.jar> <ttk-db.jar> <user@host>
#
# Parametrit:
#     -c              Jos annettu, tyhjentää tietokannan.
#
#     -t              Jos annettu, luo testikäyttäjät T-1001 ja T-800.
#
#     <ttk.jar>       Polku ttk.jar:iin.
#
#     <ttk-db.jar>    Polku ttk-db.jar:iin.
#
#     <user@host>     Käyttäjätunnus palvelimella ja palvelimen nimi.
#                     Käyttäjällä tulee olla sudo-oikeudet.

set -eu

db_extra_args=''
while getopts 'ct' o; do
    case $o in
        c)
            db_extra_args+=' --clear'
            ;;
        t)
            db_extra_args+=' -t'
            ;;
    esac
    shift
    ((OPTIND-=1))
done

if [ $# -lt 3 ]
then
    echo "$0 [-t] <ttk.jar> <ttk-db.jar> <user@host>"
    exit 1
fi

db_user=${AITU_DB_USER:-aitu}
aituhaku_db_user=${AITUHAKU_DB_USER:-aituhaku}
service=${AITU_SERVICE:-aitu}

version_jarfile=$1
version_dbjarfile=$2
user_host=$3
aitu_home=${AITU_HOME:-/data00/aitu}
ssh_key=${AITU_SSH_KEY:-~/.ssh/id_rsa}

set -x

# Ei tarkisteta isäntäavaimia, koska testiajoihin käytettävien
# virtuaalipalvelinten IP:t vaihtuvat, kun ne tuhotaan ja luodaan uudelleen
echo "kopioidaan uusi versio etäpalvelimelle $user_host"
scp -i $ssh_key -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no -p start-ttk.sh stop-ttk.sh $version_jarfile $version_dbjarfile $user_host:~

echo "päivitetään tietokanta ja sovellus"

ssh -t -t -i $ssh_key -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no $user_host "sudo cp start-ttk.sh stop-ttk.sh `basename $version_dbjarfile` `basename $version_jarfile` $aitu_home && cd $aitu_home && sudo chown tomcat:tomcat -R $aitu_home && sudo /sbin/service $service stop && java -jar `basename $version_dbjarfile` -u $db_user --aituhaku-username $aituhaku_db_user $db_extra_args && sudo ln -sf `basename $version_jarfile` ttk.jar; sudo /sbin/service $service start && sleep 2"
