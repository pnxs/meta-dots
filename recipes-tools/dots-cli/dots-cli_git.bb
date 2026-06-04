DESCRIPTION = "DOTS client utilities"
LICENSE = "CLOSED"

PV = "1.0+git${SRCPV}"
PR = "r1"

SRCREV ?= "8df9294eb3f2ab2917eb1802cd181604ab6831f3"
SRCBRANCH ?= "main"

SRC_URI = "gitsm://github.com/pnxs/dots-cli.git;protocol=https;branch=${SRCBRANCH}"

inherit dots-cpp

