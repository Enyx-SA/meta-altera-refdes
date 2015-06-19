SUMMARY = "Example application for Atlas SoC FPGA FFT Benchmarking"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://fftsw_apps/COPYING;md5=12f884d2ae1ff87c09e5b7ccc2c4ca7e"

PR = "r4"

SRCREV_fftsw_apps = "${AUTOREV}"
SRCREV_ne10 = "${AUTOREV}"

SRC_URI = " \
	git://github.com/dwesterg/atlas-soc-fftsw-apps.git;name=fftsw_apps;destsuffix=fftsw_apps \
	git://github.com/projectNe10/Ne10.git;name=ne10;destsuffix=ne10 \
	file://build_fftapps.sh \
"
S = "${WORKDIR}"

FFT_APPS = "create_real_short_sine32"
FFT_APPS += "create_real_short_square32"
FFT_APPS += "create_real_short_triangle32"
FFT_APPS += "real_short_to_ne10cpx_long"
FFT_APPS += "real_short_to_ne10cpx_short"
FFT_APPS += "ne10cpx_short_to_text"
FFT_APPS += "ne10cpx_long_to_text"
FFT_APPS += "c16_256"
FFT_APPS += "c32_256"
FFT_APPS += "fft_256"
FFT_APPS += "fftdma_256"
FFT_APPS += "neon16_256"
FFT_APPS += "neon32_256"
FFT_APPS += "c16_4096"
FFT_APPS += "c32_4096"
FFT_APPS += "fft_4096"
FFT_APPS += "fftdma_4096"
FFT_APPS += "neon16_4096"
FFT_APPS += "neon32_4096"
FFT_APPS += "c16_256x32"
FFT_APPS += "c32_256x32"
FFT_APPS += "fft_256x32"
FFT_APPS += "fftdma_256x32"
FFT_APPS += "neon16_256x32"
FFT_APPS += "neon32_256x32"
FFT_APPS += "c16_256x32x128"
FFT_APPS += "c32_256x32x128"
FFT_APPS += "fft_256x32x128"
FFT_APPS += "fftdma_256x32x128"
FFT_APPS += "neon16_256x32x128"
FFT_APPS += "neon32_256x32x128"
FFT_APPS += "stream_fpga_256x32x128"
FFT_APPS += "stream_neon32_256x32x128"
FFT_APPS += "stream_raw_256x32x128"

FFT_SCRIPTS = "create_input_waveforms.sh"
FFT_SCRIPTS += "duplicate_x128.sh"
FFT_SCRIPTS += "duplicate_x32.sh"
FFT_SCRIPTS += "duplicate_x8.sh"
FFT_SCRIPTS += "run_all.sh"
FFT_SCRIPTS += "run_fft_256.sh"
FFT_SCRIPTS += "run_fft_4096.sh"
FFT_SCRIPTS += "run_fft_256x32.sh"
FFT_SCRIPTS += "run_fft_256x32x128.sh"
FFT_SCRIPTS += "run_stream_256x32x128.sh"

do_configure_ne10 () {
	cd ${S}/ne10
	sed -i "s/arm-linux-gnueabihf-/arm-angstrom-linux-gnueabi-/g" GNUlinux_config.cmake
	rm -Rf build
	mkdir build
}

do_compile_ne10 () {
	cd ${S}/ne10/build
	cmake -DCMAKE_TOOLCHAIN_FILE=../GNUlinux_config.cmake \
		-DCMAKE_CXX_FLAGS="-mfloat-abi=hard" \
		-DCMAKE_C_FLAGS="-mfloat-abi=hard" \
		..
	make
}

do_compile_lib () {
	cd ${S}/fftsw_apps
	${CC} \
		-march=armv7-a \
		-mfloat-abi=hard \
		-mfpu=vfp3 \
		-mthumb-interwork \
		-mthumb \
		-O2 \
		-g \
		-feliminate-unused-debug-types  \
		-std=gnu99 \
		-W \
		-Wall \
		-Werror \
		-Wc++-compat \
		-Wwrite-strings \
		-Wstrict-prototypes \
		-pedantic \
		-I../ne10/inc \
		-o "overhead.o" \
		-c \
		"overhead.c"

	${AR} -r "liboverhead.a" "overhead.o"
}


do_configure_apps () {
	cd ${S}/fftsw_apps
	rm -Rf *.o *.a ${FFT_APPS}
}

do_compile_apps () {
	cd ${S}/fftsw_apps
	
	for i in ${FFT_APPS}; do
		${CC} -march=armv7-a -mfloat-abi=hard -mfpu=vfp3 -mthumb-interwork \
			-mthumb -O2 -g -feliminate-unused-debug-types -std=gnu99 -W \
			-Wall -Werror -Wc++-compat -Wwrite-strings \
			-Wstrict-prototypes -pedantic -I../ne10/inc \
			-o $i.o -c $i.c
		
		${CXX} -march=armv7-a -mfloat-abi=hard -mfpu=vfp3 -mthumb-interwork \
			-mthumb -O2 -g $i.o -o $i \
			-L${S}/fftsw_apps -loverhead \
			-rdynamic ../ne10/build/modules/libNE10.a \
			-lm
	done

}

do_configure () {
	do_configure_ne10
	do_configure_apps
}

do_compile () {
	do_compile_ne10
	do_compile_lib
	do_compile_apps

	cd ${S}/fftsw_apps
	tar -czvf ${S}/fftsw_apps/fft.tgz ${FFT_APPS} ${FFT_SCRIPTS} README_TARGET.TXT 
}

do_install () {
	
	install -d ${D}/examples/fft/bin
	cp -a ${S}/fftsw_apps/fft.tgz ${D}/examples/fft/bin/
	cp -a ${S}/fftsw_apps/README_TARGET.TXT ${D}/examples/fft/bin/
	cp -a ${S}/fftsw_apps/setup_target_fft_env.sh ${D}/examples/fft/bin/

	install -d ${D}/examples/fft/src
	cp -a build_fftapps.sh ${D}/examples/fft/src
	install -d ${D}/examples/fft/src/fftsw_app
	install -d ${D}/examples/fft/src/ne10
	
	for i in ${FFT_APPS}; do
		cp ${S}/fftsw_apps/$i.c ${D}/examples/fft/src/fftsw_app
	done

	for i in ${FFT_SCRIPTS}; do
		cp ${S}/fftsw_apps/$i ${D}/examples/fft/src/fftsw_app
	done

	cp ${S}/fftsw_apps/*.TXT ${D}/examples/fft/src/fftsw_app
	cp -a ${S}/fftsw_apps/images ${D}/examples/fft/src/fftsw_app

	wget -O ${S}/ne10.tgz https://github.com/projectNe10/Ne10/tarball/master

	tar -xvf ${S}/ne10.tgz --strip-components=1 -C ${D}/examples/fft/src/ne10

}

FILES_${PN} += "examples/fft/bin"

PACKAGES =+ "${PN}-src"
FILES_${PN}-src += "examples/fft/src"
