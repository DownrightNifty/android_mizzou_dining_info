# arm64 is used by most devices, but compiling for all architectures provides support for
# the Android Emulator (and it's easy)

# the build scripts are adapted from https://github.com/djp952/prebuilt-libxml2

# install build deps
sudo apt install autoconf pkgconf libtool make

# download the Android NDK if it's not present
if [ ! -d android-ndk-r20b/ ]; then
	wget https://dl.google.com/android/repository/android-ndk-r20b-linux-x86_64.zip
	unzip android-ndk-r20b-linux-x86_64.zip
fi

# download libxml2 if it's not present
if [ ! -d libxml2/ ]; then
	git clone https://gitlab.gnome.org/GNOME/libxml2 -b v2.10.3 --depth=1
fi

rm -r out/ 2> /dev/null
mkdir out/
mkdir out/android/
mkdir out/gnulinux/

# build for Android
./build_android_arm64.sh
mv libxml2/out/ out/android/arm64/
./build_android_arm.sh
mv libxml2/out/ out/android/arm/
./build_android_x86.sh
mv libxml2/out/ out/android/x86/
./build_android_x64.sh
mv libxml2/out/ out/android/x64

# also build for GNU/Linux (for testing)
./build_gnulinux_x64.sh
mv libxml2/out/ out/gnulinux/x64/
