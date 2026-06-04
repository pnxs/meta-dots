DESCRIPTION = "DOTS default object loader service"
LICENSE = "CLOSED"

PR = "r1"

SRC_URI = "file://dots-default-object-loader.service \
          "

S = "${UNPACKDIR}"

do_install() {
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/dots-default-object-loader.service ${D}${systemd_system_unitdir}

    install -d ${D}${sysconfdir}/dots/default-objects
}

FILES:${PN} += "${systemd_system_unitdir}/*"

