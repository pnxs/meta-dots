SUMMARY = "DOTS utility tools (inspect, tui, trace)"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1803fa9c2c3ce8cb06b4861d75310742"

SRC_URI = "git://github.com/pnxs/dots-utils-rs.git;protocol=https;branch=main"
SRCREV = "0d06bd9fbcaf29d40987632f6f1f6e42ad2f7cd6"

inherit cargo cargo-update-recipe-crates
require ${BPN}-crates.inc

PACKAGES =+ "${PN}-inspect ${PN}-trace ${PN}-tui"

SUMMARY:dots-utils-inspect = "Statically inspect a compiled dots-rs ELF binary"
DESCRIPTION:dots-utils-inspect = "Reports which DOTS types a compiled dots-rs binary publishes and subscribes to."
FILES:dots-utils-inspect = "${bindir}/dots-inspect"

SUMMARY:dots-utils-tui = "Terminal UI for connected DOTS clients"
DESCRIPTION:dots-utils-tui = "Shows connected DOTS clients and per-client statistics in a terminal UI."
FILES:dots-utils-tui = "${bindir}/dots-tui"

SUMMARY:dots-utils-trace = "Trace DOTS transmissions"
DESCRIPTION:dots-utils-trace = "Connects to a broker and prints every transmission as it arrives."
FILES:dots-utils-trace = "${bindir}/dots-trace"

BBCLASSEXTEND = "native nativesdk"
