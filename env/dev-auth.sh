#!/bin/bash
set -eu

software/jre.sh

iptables -F
service iptables save

adduser --create-home tomcat
cd /home/tomcat
cp /env/auth-server/apache-tomcat-7.0.47.tar.gz .
tar -xzvf apache-tomcat-7.0.47.tar.gz
cp /env/auth-server/tomcat-initd.sh /etc/init.d/tomcat
cp /env/auth-server/server.xml apache-tomcat-7.0.47/conf

mkdir /home/tomcat/oph-configuration
cp /env/auth-server/*.properties /home/tomcat/oph-configuration

export CATALINA_HOME=/home/tomcat/apache-tomcat-7.0.47

# cas deploy
cp /env/auth-server/cas-server-webapp-3.5.2.war /home/tomcat/apache-tomcat-7.0.47/webapps

chown -R tomcat:tomcat *

service tomcat start

#ldap install

yum install -y openldap-servers
cp /env/auth-server/olcDatabase\=\{2\}bdb.ldif /etc/openldap/slapd.d/cn\=config
service slapd start

#Wait for LDAP to start, then load ldif
yum install -y openldap-clients
until ldapmodify -h localhost -p 389 -D "cn=aituserv,ou=People,dc=opintopolku,dc=fi" -w salasana -a -f /env/auth-server/aitu.ldif; do
  echo "Waiting for LDAP server..."
  sleep 10
done