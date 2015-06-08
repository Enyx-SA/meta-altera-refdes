SUMMARY = "Example application for Atlas SoC FPGA FFT Benchmarking"

PR = "r0"

SRCREV = "${AUTOREV}"

SRC_URI = " \
	git://github.com/dwesterg/atlas-soc-fftsw-apps.git \
	file://Makefile \
"

S = "${WORKDIR}"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "${S}/git/COPYING;md5=12f884d2ae1ff87c09e5b7ccc2c4ca7e"
