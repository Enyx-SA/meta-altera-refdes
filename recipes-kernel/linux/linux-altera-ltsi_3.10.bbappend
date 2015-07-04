PR = "r1"

FILESEXTRAPATHS_prepend := "${THISDIR}/config:"

SRC_URI += " \
		file://socfpga-3.10-ltsi:type=kmeta:destsuffix=socfpga-3.10-ltsi \
		"
