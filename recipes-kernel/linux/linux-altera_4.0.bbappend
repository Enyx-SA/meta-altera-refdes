PR = "r1"

FILESEXTRAPATHS_prepend := "${THISDIR}/config:"

SRC_URI += " \
		file://${PN}_{${PV}:type=kmeta:destsuffix=${PN}_{${PV} \
		"
