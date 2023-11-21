export CC=gcc
export AR=gcc-ar
export RANLIB=gcc-ranlib
export CFLAGS="-I/usr/include/x86_64-linux-gnu"
export LIBS=-ldl
cd libxml2
sed -i 's/1.16.3/1.15/g' configure.ac
./autogen.sh --with-pic --disable-shared --without-iconv --without-http --without-ftp --without-legacy --without-c14n --without-catalog --without-writer --without-schematron --without-docbook --without-output --without-push --without-modules --without-tree --without-xptr --without-xinclude --without-xpath --without-schemas --without-sax1 --without-iso8859x --without-python --without-zlib --without-lzma --prefix="$(pwd)/out"
make && make install
