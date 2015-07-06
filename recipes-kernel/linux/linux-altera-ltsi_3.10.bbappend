PR = "r2"
FILESEXTRAPATHS_prepend := "${THISDIR}/config:"
SRC_URI_append += " \
			file://${KBRANCH};type=kmeta;destsuffix=${KBRANCH} \
			"

# 3.10 LTSi kernel uses older dts names
KERNEL_DEVICETREE_socfpga_cyclone5_socdk = "socfpga_cyclone5.dtb"
