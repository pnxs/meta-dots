SUMMARY = "DOTS implementation in Rust"
DESCRIPTION = "Stages the dots-rust workspace sources into the sysroot so that \
other Rust recipes can use the dots-* crates as a compile (cargo) dependency. \
A consumer recipe adds 'dots-rust' to DEPENDS and references the crates from \
${datadir}/dots-rust, e.g. by appending that path to EXTRA_OECARGO_PATHS or \
by adding a [patch]/path entry in its Cargo.toml. dots-rust is the workspace \
root, the individual crates live under ${datadir}/dots-rust/crates/."
HOMEPAGE = "https://github.com/pnxs/dots-rust"
LICENSE = "LGPL-3.0-only"

# dots-rust ships no dedicated license file; the SPDX identifier is declared in
# the workspace manifest, so the license is pinned against that line.
LIC_FILES_CHKSUM = "file://Cargo.toml;beginline=8;endline=8;md5=b488c92c1188c26e394ba1d28c78bbdf"

PV = "0.1.0+git${SRCPV}"
PR = "r0"

SRCREV = "a0445306e06d6e2758fe7181ad32f9aff4beab4f"
SRCBRANCH = "master"

SRC_URI = "git://github.com/pnxs/dots-rust.git;protocol=https;branch=${SRCBRANCH}"

# Location the crate sources are exposed at, both in the sysroot (for other
# recipes to build against) and on a target rootfs (should it ever be installed).
DOTS_RUST_CRATE_DIR = "${datadir}/dots-rust"

# This recipe only provides Rust sources for other recipes to compile against.
# It is architecture independent and does not build anything itself.
inherit allarch
INHIBIT_DEFAULT_DEPS = "1"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install () {
    install -d ${D}${DOTS_RUST_CRATE_DIR}
    # Copy the whole workspace, including the checked-in Cargo.lock which
    # consumers need for reproducible (--frozen) cargo builds. Do not preserve
    # host ownership; the files must be owned by root in the package.
    cp -R --no-preserve=ownership ${S}/. ${D}${DOTS_RUST_CRATE_DIR}/
    rm -rf ${D}${DOTS_RUST_CRATE_DIR}/.git
}

FILES:${PN} = "${DOTS_RUST_CRATE_DIR}"

# ${datadir} is part of SYSROOT_DIRS by default, so the staged sources become
# available to any recipe that has dots-rust in DEPENDS.
