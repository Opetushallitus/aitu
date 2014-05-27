#!/bin/bash

# Jos vboxnet0 ei ole olemassa niin luodaan se 
ifconfig vboxnet0 |grep -q inet
if [ $? != 0 ]; then
    VBoxManage hostonlyif create
    VBoxManage hostonlyif ipconfig vboxnet0 --ip 192.168.50.1
fi

# korjataan puuttuva reitti virtuaalikoneeseen. Liittyy tilanteisiin joissa wlan ja VPN sotkevat reititystaulut
sudo route -n add -net 192.168.50.0 -netmask 255.255.255.0 -interface vboxnet1

# cisco vpn purkkaa. http://www.petefreitag.com/item/753.cfm
for RULE in `sudo ipfw list | grep deny | awk '{print $1}' | xargs`; do sudo ipfw delete $RULE; done

