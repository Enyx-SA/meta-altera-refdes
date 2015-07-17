#!/bin/sh

# create this environment setup file for the shell in the x11vnc session

cat <<EOF > =/home/root/.x11vnc_env

export PS1='\u@\h:\w$ '

EOF

# then execute this command to start the x11vnc session
FD_PROG=/usr/bin/xfce4-session /usr/bin/x11vnc -loop -repeat -env ENV=/home/root/.x11vnc_env -display WAIT:cmd=FINDCREATEDISPLAY-Xvf
