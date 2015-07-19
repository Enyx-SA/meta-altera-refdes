require recipes-images/angstrom/extended-console-image.bb

IMAGE_INSTALL += " \
	kernel-modules \
	kernel-dev \
	linux-firmware \
	usbutils \
	libusbg \
	iw \
        systemd-analyze \
        vim vim-vimrc \
        procps \
        screen minicom \
        cronie-systemd ntpdate \
        tar \
        packagegroup-sdk-target \
	gcc \
        gdb gdbserver \
        iproute2 \
	lighttpd \
        lighttpd-module-cgi \
	atlas-soc-lighttpd-conf \
	atlas-soc-101 \
	atlas-soc-usb-gadget \
	atlas-soc-fftdriver-mod \
	atlas-soc-fftsw-apps \
	atlas-soc-fftsw-apps-src \
	atlas-soc-gpio-apps \
	atlas-soc-adxl-apps \
	gnuplot \
	cmake \
"
export IMAGE_BASENAME = "atlas-soc-console-image"

# Hack to adjust timestamp forward and ensure timestamp format matches load-timestamp.sh in meta-angstrom/systemd

rootfs_update_timestamp() {
        date -u +%4Y%2m%2d%2H%2M -d "+1 days" >${IMAGE_ROOTFS}/etc/timestamp
}

EXPORT_FUNCTIONS rootfs_update_timestamp
