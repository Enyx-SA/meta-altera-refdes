DESCRIPTION = "Atlas SoC web content"
AUTHOR = "Dalon Westergreen <dwesterg@gmail.com>"
SECTION = "atlas-soc"

LICENSE = "MIT & LGPLv3 & others"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f02920251cbdc9b014dc1cbdb2bb95c4"

PV = "1.0"
PR = "r1"

SRCREV = "${AUTOREV}"
#SRC_URI = "git://sj-swip-nx2.altera.com/data/dwesterg/git/atlas-soc-webcontent.git;protocol=ssh"
SRC_URI = "git://github.com/dwesterg/atlas-soc-webcontent.git"
S = "${WORKDIR}/git"

inherit allarch

do_install() {
	install -d ${D}${datadir}/${PN}
	cp -a ${S}/* ${D}${datadir}/${PN}
}

FILES_${PN} += "${datadir}/${PN}"
