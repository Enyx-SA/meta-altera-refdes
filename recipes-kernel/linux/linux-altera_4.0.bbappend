PR = "r1"

FILESEXTRAPATHS_prepend := "${THISDIR}/config:"

SRC_URI += " \
		file://${KBRANCH}:type=kmeta:destsuffix=${KBRANCH} \
		"
