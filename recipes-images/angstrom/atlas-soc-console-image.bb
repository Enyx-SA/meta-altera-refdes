require recipes-images/angstrom/extended-console-image.bb

IMAGE_INSTALL += " \
	kernel-headers \
	kernel-modules \
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
	gator \
	fft_driver-mod \
"
export IMAGE_BASENAME = "atlas-soc-console-image"
