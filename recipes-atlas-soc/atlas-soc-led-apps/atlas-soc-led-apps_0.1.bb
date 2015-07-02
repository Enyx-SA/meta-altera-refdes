SUMMARY = "Example application for Atlas SoC FPGA LED manipulation"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=12f884d2ae1ff87c09e5b7ccc2c4ca7e"

PR = "r0"

SRC_URI = "\
	file://COPYING \
	file://build_app.sh \
	file://toggle_fpga_leds.c \
	file://toggle_fpga_leds.sh \
"

LED_FILES  = "COPYING"
LED_FILES += "build_app.sh"
LED_FILES += "toggle_fpga_leds.c"
LED_FILES += "toggle_fpga_leds.sh"

S = "${WORKDIR}"

do_install () {
	
	cd ${S}

	install -d ${D}/examples/led/sandbox
	cp -a ${LED_FILES} ${D}/examples/led/sandbox

	rm -f led_sandbox.tgz
	tar czf led_sandbox.tgz ${LED_FILES}
	cp -a led_sandbox.tgz ${D}/examples/led
}

PACKAGES =+ "${PN}-src"
FILES_${PN}-src += "examples/led"
FILES_${PN}-src += "examples/led/sandbox"

