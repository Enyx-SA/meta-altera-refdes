#!/bin/bash
CC=gcc
CXX=g++
CMAKE=cmake

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

# compile ne10 library
echo "Compiling ne10 library"
pushd ne10
	sed -i "s/arm-angstrom-linux-gnueabi-//g" GNUlinux_config.cmake
	rm -Rf build
	mkdir build
popd
pushd ne10/build
	cmake -DCMAKE_TOOLCHAIN_FILE=../GNUlinux_config.cmake \
		-DCMAKE_CXX_FLAGS="-mfloat-abi=hard" \
		-DCMAKE_C_FLAGS="-mfloat-abi=hard" \
		..
	make
popd	

# compile overhead library
echo "Compiling overhead library"
pushd fftsw_apps
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
		I../ne10/inc \
		-o "overhead.o" \
		-c \
		"overhead.c"
popd

# Compile fftsw apps
echo "Compiling fft applications"
pushd fftsw_apps
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
popd
