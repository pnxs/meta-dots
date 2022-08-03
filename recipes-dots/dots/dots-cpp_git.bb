DESCRIPTION = "DOTS C++ Core"
LICENSE = "LGPL-3.0-only"
LIC_FILES_CHKSUM = "file://LICENSE.LESSER;md5=3000208d539ec061b899bce1d9ce9404"
DEPENDS += "boost dots-dcg-native rapidjson"

PV = "1.0-git${SRCREV}"
PR = "r1"

SRCREV = "a81708caf12bce55f1a7e5ff91111b380e26c615"
SRCBRANCH = "master"

SRC_URI="gitsm://github.com/pnxs/dots-cpp.git;protocol=https;branch=${SRCBRANCH} \
         file://dotsd.service \
        "

OECMAKE_EXTRA_ROOT_PATH = "${S}"
OECMAKE_GENERATOR = "Unix Makefiles"

S = "${WORKDIR}/git"

inherit cmake systemd python3native
    
EXTRA_OECMAKE = "-DBUILD_UNIT_TESTS=OFF \
                 -DDOTS_BUILD_UNIT_TESTS=OFF \
                 -DCMAKE_BUILD_TYPE=MINSIZEREL \
                 -DDOTS_BUILD_EXAMPLES=OFF \
                "

do_install:append () {
    if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
        install -d ${D}/${systemd_system_unitdir}
        install -m 644 ${WORKDIR}/dotsd.service                 ${D}/${systemd_system_unitdir}
    fi
}

FILES:${PN} += " \
                    ${systemd_system_unitdir}/*.service \
               "

FILES:${PN}-dev += "${datadir}/cmake/Modules/*.cmake \
                    /usr/cmake/* \
                    /usr/share/dots/*.cmake \
                    /usr/share/dots-cg-cpp/* \
                   "

SYSTEMD_SERVICE:${PN} = "dotsd.service"
