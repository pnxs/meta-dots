DESCRIPTION = "DOTS descriptor publisher"
LICENSE = "CLOSED"

PR = "r1"

SRC_URI = "file://dots-descriptor-publisher.service \
          "

S = "${UNPACKDIR}"

do_install() {
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/dots-descriptor-publisher.service ${D}${systemd_system_unitdir}

    install -d ${D}${sysconfdir}/dots/descriptors
}

FILES:${PN} += "${systemd_system_unitdir}/*"

