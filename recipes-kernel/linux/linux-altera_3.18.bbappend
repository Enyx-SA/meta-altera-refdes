FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}-${PV}:"

PR = "r1"

SRC_URI += "file://leds.cfg"
SRC_URI += "file://usb_gadget.cfg"
SRC_URI += "file://uio.cfg"
SRC_URI += "file://net.cfg"
SRC_URI += "file://input_misc.cfg"
SRC_URI += "file://gpio-keys.cfg"
SRC_URI += "file://0001-fix_usbnet_large_transfers_v_3_18.patch"
SRC_URI += "file://0001-Add-atlas-dts.patch"
