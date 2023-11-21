export TOOLCHAIN="$(pwd)/android-ndk-r20b/toolchains/llvm/prebuilt/linux-x86_64"
export AR=$TOOLCHAIN/bin/i686-linux-android-ar
export AS=$TOOLCHAIN/bin/i686-linux-android-as
export CC=$TOOLCHAIN/bin/i686-linux-android21-clang
export CXX=$TOOLCHAIN/bin/i686-linux-android21-clang++
export LD=$TOOLCHAIN/bin/i686-linux-android-ld
export RANLIB=$TOOLCHAIN/bin/i686-linux-android-ranlib
export STRIP=$TOOLCHAIN/bin/i686-linux-android-strip
export LIBS=-ldl
cd libxml2
sed -i 's/1.16.3/1.15/g' configure.ac
./autogen.sh --host=i686-linux-android --target=i686-linux-android --with-pic --disable-shared --without-iconv --without-ftp --without-legacy --without-c14n --without-catalog --without-http --without-writer --without-schematron --without-docbook --without-output --without-push --without-modules --without-tree --without-xptr --without-xinclude --without-xpath --without-schemas --without-sax1 --without-iso8859x --without-python --without-zlib --without-lzma --prefix="$(pwd)/out"
make && make install
