PR = "r14"
FILESEXTRAPATHS_prepend := "${THISDIR}/config:"
SRC_URI_append += " \
			file://${KBRANCH};type=kmeta;destsuffix=${KBRANCH} \
			"

