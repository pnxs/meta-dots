DESCRIPTION = "DOTS client utilities"
LICENSE = "CLOSED"
DEPENDS += "fmt"

PV = "1.0+git${SRCPV}"
PR = "r1"

SRCREV ?= "fe71b715ef1e485c5cbb77289e212d0dbc1bb9df"
SRCBRANCH ?= "main"

SRC_URI = "gitsm://github.com/pnxs/dots-web-connector.git;protocol=https;branch=${SRCBRANCH} \
         file://dots-web-connector.service \
        "

do_install:append() {
    if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
        install -d ${D}/${systemd_system_unitdir}
        install -m 644 ${UNPACKDIR}/dots-web-connector.service    ${D}/${systemd_system_unitdir}
    fi
}

inherit dots-cpp pkgconfig systemd

SYSTEMD_SERVICE:${PN} = "dots-web-connector.service"

