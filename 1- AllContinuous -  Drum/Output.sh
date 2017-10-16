#!/bin/sh

APPDIR=$(readlink -f "$0")
APPDIR=$(dirname "$APPDIR")
java -Djna.nosys=true -Djava.library.path="$APPDIR:$APPDIR/lib" -cp "$APPDIR:$APPDIR/lib/Output.jar:$APPDIR/lib/core.jar:$APPDIR/lib/jogl-all.jar:$APPDIR/lib/gluegen-rt.jar:$APPDIR/lib/jogl-all-natives-linux-amd64.jar:$APPDIR/lib/gluegen-rt-natives-linux-amd64.jar:$APPDIR/lib/tritonus_share.jar:$APPDIR/lib/jl1.0.1.jar:$APPDIR/lib/mp3spi1.9.5.jar:$APPDIR/lib/jsminim.jar:$APPDIR/lib/tritonus_aos.jar:$APPDIR/lib/minim.jar:$APPDIR/lib/oscP5.jar" Output "$@"