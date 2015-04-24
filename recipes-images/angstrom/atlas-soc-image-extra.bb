require atlas-soc-image.bb

IMAGE_INSTALL += " \
	gateone \
	packagegroup-core-x11-xserver \
	angstrom-gnome-icon-theme-enable gtk-engine-clearlooks gtk-theme-clearlooks angstrom-clearlooks-theme-enable \
	xserver-nodm-init \
	xserver-common \
	ttf-dejavu-sans ttf-dejavu-sans-mono ttf-dejavu-common \
	angstrom-packagegroup-gnome gimp abiword gedit midori epiphany firefox matchbox-terminal \
	xterm \
"

export IMAGE_BASENAME = "atlas-soc-image-extra"

