DESCRIPTION = "DOTS client utilities"
LICENSE = "CLOSED"

PV = "1.0+git${SRCPV}"
PR = "r1"

SRCREV ?= "83ba230fb225fdc225d33fef18fdebf55d9da12d"
SRCBRANCH ?= "main"

SRC_URI = "gitsm://github.com/pnxs/dots-cli.git;protocol=https;branch=${SRCBRANCH}"

inherit dots-cpp

