DESCRIPTION = "DOTS client utilities"
LICENSE = "CLOSED"

PV = "1.0+git${SRCPV}"
PR = "r1"

SRCREV ?= "69f5bd7854384184fce2729e72055e914e75e6bc"
SRCBRANCH ?= "main"

SRC_URI = "gitsm://github.com/pnxs/dots-cli.git;protocol=https;branch=${SRCBRANCH}"

inherit dots-cpp

