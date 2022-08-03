DESCRIPTION = "DOTS Code generator"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=84dcc94da3adb52b53ae4fa38fe49e5d"
DEPENDS += "python3-jinja2"

PR = "r1"
SRCREV="208a88ca3895a329a1967182eba6626f91c78f7d"

SRC_URI="git://github.com/pnxs/dots-code-generator.git;protocol=https;nobranch=1"

S = "${WORKDIR}/git"

inherit setuptools3

do_install:append () {
    #install -d ${D}${bindir}
    #install -m 755 ${S}/bin/dcg.py ${D}${bindir}

    # Replace python with nativepython as interpreter
    sed -i -e 's/env python/env nativepython/' ${D}${bindir}/dcg.py
}

BBCLASSEXTEND = "native nativesdk"
