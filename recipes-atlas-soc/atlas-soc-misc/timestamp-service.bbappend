do_initial_timestamp() {
	date -d "+1 day" -u "+%4Y%2m%2d%2H%2M" > ${WORKDIR}/timestamp
	date -d "+1 day" -u "+%4Y%2m%2d%2H%2M" > ${WORKDIR}/timestamp_dalon
	install -d 0644 ${D}/${sysconfdir}
	install -m 0644 ${WORKDIR}/timestamp ${D}${sysconfdir}/timestamp
	install -m 0644 ${WORKDIR}/timestamp_dalon ${D}${sysconfdir}/timestamp_dalon
}
addtask initial_timestamp after compile before install

FILES_${PN} += "etc/timestamp"
FILES_${PN} += "etc/timestamp_dalon"
