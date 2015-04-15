#!/bin/sh

modprobe g_multi file=/dev/mmcblk0p1 cdrom=0 stall=0 removable=0 nofua=1 host_addr="00:07:ed:01:02:03"

sleep 5

/sbin/ifconfig usb0 hw ether 00:07:ed:01:02:03
/sbin/ifconfig usb0 192.168.7.1 netmask 255.255.255.0
/usr/sbin/udhcpd -fS -I 192.168.7.1 /etc/udhcpd.conf
