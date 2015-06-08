SUMMARY = "Example application for Atlas SoC FPGA FFT Benchmarking"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "${WORKDIR}/git/COPYING;md5=12f884d2ae1ff87c09e5b7ccc2c4ca7e"

PR = "r0"

SRCREV = "${AUTOREV}"

SRC_URI = " \
	git://github.com/dwesterg/atlas-soc-fftsw-apps.git \
"

S = "${WORKDIR}/git"

do_compile () {
	source ${S}/build_ne10.sh
	source ${S}/build_all.sh
	source ${S}/archive_for_target.sh
}

do_install () {

}
