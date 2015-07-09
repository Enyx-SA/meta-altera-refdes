This readme describes the linux kernel gpio-keys framework as it deploys on the
Atlas target environment.  You may find the following references useful for more
information on this topic as well.

<linux-source-tree>/Documentation/gpio/gpio.txt
<linux-source-tree>/Documentation/gpio/sysfs.txt
<linux-source-tree>/Documentation/input/input.txt
<linux-source-tree>/Documentation/input/event-codes.txt
<linux-source-tree>/Documentation/input/input-programming.txt
<linux-source-tree>/Documentation/devicetree/bindings/gpio/gpio.txt
<linux-source-tree>/Documentation/devicetree/bindings/input/gpio-keys.txt
<linux-source-tree>/Documentation/devicetree/bindings/input/gpio-keys-polled.txt

If you cut and paste the following function into a console running on the Atlas
target you can extract the useful information contained in the run time device
tree maintained by the kernel in the procfs.

################################################################################
# find gpio-keys in device tree
################################################################################
function find_gpio_keys_dt () {
for NEXT in $(find /proc/device-tree -name "compatible" | sort)
do
cat ${NEXT} | grep -xz "gpio-keys" > /dev/null && {
KEYS_DIRNAME="$(dirname ${NEXT})"
KEYS_COMPATIBLE="$(cat ${KEYS_DIRNAME}/compatible)"
echo "${KEYS_DIRNAME}"
echo -e "\tcompatible = '${KEYS_COMPATIBLE}'"
for NEXT_KEY in $(find "${KEYS_DIRNAME}" -name "gpios" | sort)
do
NEXT_KEY_DIR="$(dirname ${NEXT_KEY})"
echo "${NEXT_KEY_DIR}"
KEYS_GPIOS="$(hexdump -v -e '"0x" 4/1 "%02x" " "' "${NEXT_KEY}")"
CONTROLLER_PHANDLE_HEX=$(echo ${KEYS_GPIOS} | cut -d ' ' -f 1)
GPIO_BIT_HEX=$(echo ${KEYS_GPIOS} | cut -d ' ' -f 2)
INVERTED_FLAG_HEX=$(echo ${KEYS_GPIOS} | cut -d ' ' -f 3)
printf "             gpios = ('%d', '%d', '%d') : ('%s', '%s', '%s')\n" \
"${CONTROLLER_PHANDLE_HEX}" "${GPIO_BIT_HEX}" "${INVERTED_FLAG_HEX}" \
"controller" "bit" "flag"
KEYS_CODE="$(hexdump -v -e '"0x" 4/1 "%02x"' "${NEXT_KEY_DIR}/linux,code")"
printf "              code = '%d'\n" "${KEYS_CODE}"
GPIO_CONTROLLER="unknown"
CONTROLLER_PHANDLE_DEC="$(printf "%d" "${CONTROLLER_PHANDLE_HEX}")"
for NEXT in $(find /proc/device-tree -name "phandle" | sort)
do
PHANDLE_HEX="$(hexdump -v -e '"0x" 4/1 "%02x"' "${NEXT}")"
PHANDLE_DEC="$(printf "%d" "${PHANDLE_HEX}")"
[ "${PHANDLE_DEC}" -eq "${CONTROLLER_PHANDLE_DEC}" ] && {
GPIO_CONTROLLER="$(dirname ${NEXT})"
}
done
printf "        controller = '%s'\n" "${GPIO_CONTROLLER}"
done
}
done
}
################################################################################

When we run the function above on the Atlas target it searches for nodes
containing the 'compatible' string 'gpio-keys', there should be only one node
located.  The function then prints the path to the node that it found and 
extracts the 'gpios' binding and the 'linux,code' binding for each key node and
prints these statistics.

root@cyclone5:~# find_gpio_keys_dt
/proc/device-tree/soc/keys
        compatible = 'gpio-keys'
/proc/device-tree/soc/keys/sw0
             gpios = ('45', '0', '1') : ('controller', 'bit', 'flag')
              code = '64'
        controller = '/proc/device-tree/soc/bridge@0xc0000000/gpio@0x100004000'
/proc/device-tree/soc/keys/sw1
             gpios = ('45', '1', '1') : ('controller', 'bit', 'flag')
              code = '65'
        controller = '/proc/device-tree/soc/bridge@0xc0000000/gpio@0x100004000'
/proc/device-tree/soc/keys/sw2
             gpios = ('45', '2', '1') : ('controller', 'bit', 'flag')
              code = '66'
        controller = '/proc/device-tree/soc/bridge@0xc0000000/gpio@0x100004000'
/proc/device-tree/soc/keys/sw3
             gpios = ('45', '3', '1') : ('controller', 'bit', 'flag')
              code = '67'
        controller = '/proc/device-tree/soc/bridge@0xc0000000/gpio@0x100004000'

For more information on the gpio controllers framework, please read the
README_gpio.txt document.  The 'gpio@0x100004000' controller identified above
maps to the 'dipsw_pio' controller that provides a 4-bit input, fpga based gpio,
registered as 'gpio-keys' in the device tree to be used in the gpio-keys
framework to receive input events from switches SW0-SW3 on the Atlas board.

The gpio-keys framework will register an input event device that will post input
events when the gpios above change their state.  The 'code' associated with each
gpio above will be encoded in the input event message along with the state of
the switch.  We can see the input device in the devfs like this:

root@cyclone5:~# ls -lR /dev/input
/dev/input:
total 0
drwxr-xr-x    2 root     root            80 Jan  1  1970 by-path
crw-rw----    1 root     input      13,  64 Jan  1  1970 event0
crw-rw----    1 root     input      13,  65 Jan  1  1970 event1
crw-rw----    1 root     input      13,  63 Jan  1  1970 mice
crw-rw----    1 root     input      13,  32 Jan  1  1970 mouse0

/dev/input/by-path:
total 0
lrwxrwxrwx    1 root     root             9 Jan  1  1970 platform-ffc04000.i2c-event -> ../event0
lrwxrwxrwx    1 root     root             9 Jan  1  1970 platform-keys.8-event -> ../event1

In this case the event1 device is the device for our gpio-keys input, we can
tell this from the '/dev/input/by-path' links that have more descriptive names.

The sysfs also describes the input device environment for us in a useful way.

root@cyclone5:~# ls -l /sys/class/input/
total 0
lrwxrwxrwx    1 root     root             0 Jan  1  1970 event0 -> ../../devices/soc.0/ffc04000.i2c/i2c-0/0-0053/input/input0/event0
lrwxrwxrwx    1 root     root             0 Jul  8 01:35 event1 -> ../../devices/soc.0/keys.8/input/input1/event1
lrwxrwxrwx    1 root     root             0 Jan  1  1970 input0 -> ../../devices/soc.0/ffc04000.i2c/i2c-0/0-0053/input/input0
lrwxrwxrwx    1 root     root             0 Jul  8 01:35 input1 -> ../../devices/soc.0/keys.8/input/input1
lrwxrwxrwx    1 root     root             0 Jan  1  1970 mice -> ../../devices/virtual/input/mice
lrwxrwxrwx    1 root     root             0 Jan  1  1970 mouse0 -> ../../devices/soc.0/ffc04000.i2c/i2c-0/0-0053/input/input0/mouse0

And from the above we can see that the 'keys.8' device exists in the sysfs.

root@cyclone5:~# ls /sys/devices/soc.0/keys.8
disabled_keys      input              power              uevent
disabled_switches  keys               subsystem
driver             modalias           switches
root@cyclone5:~# ls /sys/devices/soc.0/keys.8/input/
input1
root@cyclone5:~# ls /sys/devices/soc.0/keys.8/input/input1/
capabilities  event1        modalias      phys          properties    uevent
device        id            name          power         subsystem     uniq
root@cyclone5:~# ls /sys/devices/soc.0/keys.8/input/input1/event1/
dev        device     power      subsystem  uevent
root@cyclone5:~# cat /sys/devices/soc.0/keys.8/input/input1/name
keys.8

So now that we know what our input event device is, we can simply read from it
to capture the input events as they arrive.  The device will block until the
next event message is received.  We can do this like so:

root@cyclone5:~# hexdump -C /dev/input/event1
00000000  cd 80 9c 55 ad 4d 0a 00  01 00 40 00 00 00 00 00  |...U.M....@.....|
00000010  cd 80 9c 55 ad 4d 0a 00  00 00 00 00 00 00 00 00  |...U.M..........|
00000020  d1 80 9c 55 5a a8 04 00  01 00 40 00 01 00 00 00  |...UZ.....@.....|
00000030  d1 80 9c 55 5a a8 04 00  00 00 00 00 00 00 00 00  |...UZ...........|
00000040  d5 80 9c 55 58 37 02 00  01 00 41 00 00 00 00 00  |...UX7....A.....|
00000050  d5 80 9c 55 58 37 02 00  00 00 00 00 00 00 00 00  |...UX7..........|
00000060  d9 80 9c 55 78 67 07 00  01 00 41 00 01 00 00 00  |...Uxg....A.....|
00000070  d9 80 9c 55 78 67 07 00  00 00 00 00 00 00 00 00  |...Uxg..........|
00000080  dd 80 9c 55 0b 38 0b 00  01 00 42 00 00 00 00 00  |...U.8....B.....|
00000090  dd 80 9c 55 0b 38 0b 00  00 00 00 00 00 00 00 00  |...U.8..........|
000000a0  e0 80 9c 55 88 ac 02 00  01 00 42 00 01 00 00 00  |...U......B.....|
000000b0  e0 80 9c 55 88 ac 02 00  00 00 00 00 00 00 00 00  |...U............|
000000c0  e3 80 9c 55 88 70 0c 00  01 00 43 00 00 00 00 00  |...U.p....C.....|
000000d0  e3 80 9c 55 88 70 0c 00  00 00 00 00 00 00 00 00  |...U.p..........|
000000e0  e9 80 9c 55 c9 48 03 00  01 00 43 00 01 00 00 00  |...U.H....C.....|
000000f0  e9 80 9c 55 c9 48 03 00  00 00 00 00 00 00 00 00  |...U.H..........|
^C

The output above results after we start hexdump reading from the input event1
device, and then we toggle SW1 on and off, then we toggle SW2 on and off, then
we toggle SW3 on and off, and then we toggle SW4 on and off.  In 16 byte event
messages that appear above, we can see the first 4 bytes that represent the
second of the event, followed by the next 4 bytes that represent the millisecond
of the event, followed by the next 2 bytes that represent the event type,
followed by the next 2 bytes that represent the event code, followed by the
last 4 bytes that represent the event value.  So we can see the first line 0x00
is an EV_KEY event for SW0 with code 0x40 and value 0.  Then line 0x10 is an
EV_SYN event, followed by line 0x20 which is the next EV_KEY event for SW0 with
code 0x40 and value 1.  Then line 0x30 is another EV_SYN event.  This pattern
repeats itself for the SW1 events with code 0x41, then the SW2 events with code
0x42 and finally the SW3 events with code 0xx43.

--------------------------------------------------------------------------------
Example programs and scripts
--------------------------------------------------------------------------------
This directory contains a few examples to demonstrate how to interact with the
switches on the Atlas board that have been registered in the gpio-keys
framework.  There is a shell script called 'watch_switch_events.sh' and a C
program called 'watch_switch_events.c'.  Each of these examples monitor the FPGA
gpio-keys input event in exactly the same way.  Then there is a C program called
'watch_switch_events_ioctl.c' which simply adds an ioctl() call to the
'watch_switch_events.c' program so that it can detect the current state of all
the switches, which can only be accomplished with the ioctl() call.

To build the 'watch_switch_events.c' application simply run the
'build_watch_switch_events.sh' shell script.  That will compile the
'watch_switch_events.c' source file and produce the executable
'watch_switch_events' application.  Refer to the 'build_watch_switch_events.sh'
script to see how the application is actually compiled and refer to the C
program source file for more details on how it actually works.

To build the 'watch_switch_events_ioctl.c' application simply run the
'build_watch_switch_events_ioctl.sh' shell script.  That will compile the
'watch_switch_events_ioctl.c' source file and produce the executable
'watch_switch_events_ioctl' application.  Refer to the
'build_watch_switch_events_ioctl.sh' script to see how the application is
actually compiled and refer to the C program source file for more details on how
it actually works.

Refer to the 'watch_switch_events.sh' source file for more details on how it
actually works.

Once you've built the applications, you can run both the script and the
applications like this:

./watch_switch_events.sh	<<< to run the script
./watch_switch_events		<<< to run the program
./watch_switch_events_ioctl	<<< to run the program with ioctl()

The programs and script will monitor the input events for the gpio-keys device
registered in the system.  To generate an input event slide the switches SW0,
SW1, SW2 and SW3 on the Atlas board.  As you slide the switches you will see the
programs and script print out the input events that they receive from the
system.  The ioctl version of the program will additionally print out the
current state of all the switches at each input event as well.  To terminate the
script or programs just type CTRL-C on the console that you launched them from.

Both the programs and the script monitor the switches by interacting with the
input event device node provided by the gpio-keys framework which leverages the
linux gpio controller framework.

