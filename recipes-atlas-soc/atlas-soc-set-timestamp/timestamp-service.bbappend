do_install_append() {
	echo $(date -u "+%4Y%2m%2d%2H%2M") > ${WORKDIR}/timestamp
	install -d 0644 ${D}/${sysconfdir}}
	install -m 0644 ${WORKDIR}/timestamp ${D}/${sysconfdir}/timestamp	
}

FILES_${PN} += "etc/timestamp"
