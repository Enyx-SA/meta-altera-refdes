FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}-${PV}:"

KERNEL_DEVICETREE = "socfpga_vt socfpga_cyclone5 socfpga_arria5 socfpga_cyclone5_atlas_socdk"

SRC_URI += "file://leds.cfg"
SRC_URI += "file://usb_gadget.cfg"
SRC_URI += "file://uio.cfg"
SRC_URI += "file://net.cfg"
SRC_URI += "file://input_misc.cfg"
SRC_URI += "file://gpio-keys.cfg"
SRC_URI += "file://0001-fix_usbnet_large_transfers_v_3_10_ltsi.patch"
SRC_URI += "file://0001-Add-atlas-dts.patch"
