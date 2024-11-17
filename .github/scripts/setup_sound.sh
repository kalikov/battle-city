#!/bin/bash

sudo apt-get update
#sudo apt-get install -y libportaudio2 dbus-x11 libasound-dev
#sudo apt-get install -y kmod
sudo apt-get install -y alsa-utils
sudo apt-get install -y linux-modules-extra-`uname -r`
#sudo apt-get install -y pulseaudio libportaudio2 dbus-x11 libasound-dev
#systemctl --user restart pulseaudio.service
#systemctl --user restart pulseaudio.socket
#pactl list

sudo ls /lib/modules/`uname -r`/kernel/sound/drivers/
sudo modprobe snd-dummy
#pactl list
aplay -l

systemctl restart alsa