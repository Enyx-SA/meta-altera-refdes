require atlas-soc-console-image.bb

IMAGE_INSTALL += " \
	angstrom-gdm-xfce-hack \
	packagegroup-xfce-base \
	ttf-dejavu-sans ttf-dejavu-sans-mono ttf-dejavu-common \
	xterm \
	angstrom-x11vnc-xinit \
	x11vnc \
	xserver-xorg-xvfb \
	xkbcomp \
"

export IMAGE_BASENAME = "atlas-soc-image"

