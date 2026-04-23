DESCRIPTION = "DOTS Code generator"
LICENSE = "CLOSED"
DEPENDS += "python3-jinja2"

#PV = "1.0-git${@d.getVar('SRCPV', True).replace('+','-')}"
#PV = "1.0+git${SRCPV}"
PR = "r1"
SRCREV = "015dbe07f09aef09109a6ac83fd57af01ecd4d92"
SRCBRANCH = "master"

SRC_URI = "git://github.com/pnxs/dots-code-generator.git;protocol=https;branch=${SRCBRANCH}"

#S = "${WORKDIR}/git"

inherit setuptools3

#RDEPENDS:${PN} = "\
#    python-core \
#"

do_install:prepend () {
    #install -d ${D}${bindir}
    #install -m 755 ${S}/bin/dcg.py ${D}${bindir}

    # Replace python with nativepython as interpreter
    #sed -i -e 's/env python/env nativepython/' ${D}${bindir}/dcg.py

    #install -d ${D}${datadir}/dots
    #cp ${WORKDIR}/git/model/template/*.dotsT ${D}${datadir}/dots
    #cp ${WORKDIR}/git/model/config*.py ${D}${datadir}/dots
    #chmod 644 ${D}${datadir}/dots/*
}

BBCLASSEXTEND = "native nativesdk"
