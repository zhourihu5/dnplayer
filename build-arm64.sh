#!/bin/bash

API=21
#armv7-a
#ARCH=armv8
ARCH=aarch64

PREFIX=./SO/$ARCH
NDK_ROOT=/Users/huzhou/Library/Android/sdk/ndk/21.4.7075529
TOOLCHAIN=$NDK_ROOT/toolchains/llvm/prebuilt/darwin-x86_64
FLAGS="-isystem $NDK_ROOT/sysroot/usr/include/aarch64-linux-android -D__ANDROID_API__=21 -g -DANDROID -fdata-sections -ffunction-sections -funwind-tables -fstack-protector-strong -no-canonical-prefixes -D_FORTIFY_SOURCE=2 -Wformat -Werror=format-security  -O0 -fno-limit-debug-info  -fPIC"
INCLUDES=" -isystem $TOOLCHAIN/lib64/clang/9.0.9/include -isystem $NDK_ROOT/sources/android/support/include -isystem $NDK_ROOT/sources/cxx-stl/llvm-libc++/include -isystem $NDK_ROOT/sources/cxx-stl/llvm-libc++abi/include -isystem $TOOLCHAIN/sysroot/usr/include -isystem $TOOLCHAIN/sysroot/usr/include/c++/v1"


build()
{
./configure \
--prefix=$PREFIX \
--disable-shared \
--enable-static \
--enable-small \
--disable-doc \
--disable-programs \
--disable-avdevice \
--disable-encoders \
--disable-muxers \
--enable-cross-compile \
--target-os=android \
--sysroot=$NDK_ROOT/platforms/android-21/arch-arm64 \
--extra-cflags="$FLAGS $INCLUDES" \
--extra-cflags="-isysroot $NDK_ROOT/sysroot/" \
--arch=$ARCH \
--cc=$TOOLCHAIN/bin/aarch64-linux-android$API-clang \
--cross-prefix=$TOOLCHAIN/bin/aarch64-linux-android-

make clean
make -j4
make install

}

build

