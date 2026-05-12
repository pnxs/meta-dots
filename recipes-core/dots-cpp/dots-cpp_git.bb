DESCRIPTION = "DOTS C++ Core"
LICENSE = "CLOSED"
DEPENDS += "boost dots-dcg-native rapidjson"

#PV = "1.0-git${@d.getVar('SRCPV', True).replace('+','-')}"
PV = "1.8.1-git${SRCREV}"
PR = "r1"

# DOTS 1.8.1
SRCREV = "4baddf66420db0da8674e62fcb36c963d22e613c"
SRCBRANCH = "update-boost-and-gcc"

SRC_URI = "gitsm://github.com/pnxs/dots-cpp.git;protocol=https;branch=${SRCBRANCH} \
         file://dotsd.service \
        "

OECMAKE_EXTRA_ROOT_PATH = "${S}"
OECMAKE_GENERATOR = "Unix Makefiles"

inherit cmake systemd python3native

EXTRA_OECMAKE = "-DBUILD_UNIT_TESTS=OFF \
                 -DDOTS_BUILD_UNIT_TESTS=OFF \
                 -DCMAKE_BUILD_TYPE=MINSIZEREL \
                 -DDOTS_BUILD_EXAMPLES=OFF \
                 -DBUILD_DOTS_SHARED=ON \
                 -DENABLE_CHANNEL_WEBSOCKET=OFF \
                 -DENABLE_DOTS_IO=ON \
                 -DBoost_USE_STATIC_LIBS=OFF \
                "
#-DCMAKE_SKIP_RPATH=TRUE

do_install:append () {
    if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
        install -d ${D}/${systemd_system_unitdir}
        install -m 644 ${UNPACKDIR}/dotsd.service                 ${D}/${systemd_system_unitdir}
    fi
}

PACKAGES =+ "${PN}-dotsd"

FILES:${PN}-dotsd = "${sbindir}/dotsd"

FILES:${PN} += " \
                    ${systemd_system_unitdir}/*.service \
               "

FILES:${PN}-dev += "${datadir}/cmake/Modules/*.cmake \
                    /usr/cmake/* \
                    /usr/share/dots/*.cmake \
                    /usr/share/dots-cg-cpp/* \
                   "

SYSTEMD_SERVICE:${PN} = "dotsd.service"
SYSTEMD_AUTO_ENABLE = "enable"
