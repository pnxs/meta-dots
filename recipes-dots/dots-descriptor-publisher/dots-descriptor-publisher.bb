DESCRIPTION = "DOTS descriptor publisher"
LICENSE = "CLOSED"

PR = "r3"

SRC_URI = "file://dots-descriptor-publisher.service \
          "

S = "${WORKDIR}/${PN}"

do_install() {
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/dots-descriptor-publisher.service ${D}${systemd_system_unitdir}
    
    install -d ${D}${sysconfdir}/dots/descriptors
}

FILES:${PN} += "${systemd_system_unitdir}/*"

