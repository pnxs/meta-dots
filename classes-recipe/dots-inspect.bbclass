# Class: dots-inspect
#
# Records, at build time, which DOTS types the binaries built by a recipe
# publish and subscribe to. The information is read statically out of the
# linkme ELF sections of the compiled binary by dots-inspect (from
# dots-utils), so no target execution is involved and the class works for
# any target architecture.
#
# One YAML file per binary is written to ${DOTS_INSPECT_DIR} and packaged
# into a separate ${PN}-dots-meta package (like -dev / -doc), so it never
# bloats the runtime package. An image that wants the DOTS interface of
# everything it ships simply installs the -dots-meta packages, e.g.
#
#     IMAGE_INSTALL += "container-os-netcfgd-dots-meta"
#
# The generated /usr/share/dots/meta/<binary>.yaml looks like:
#
#     binary: /usr/bin/netcfgd
#     recipe: container-os-netcfgd
#     machine: gsm-vm
#     published:
#       - ConfigCommit
#       - NetworkInterface
#     subscribed:
#       - ConfigChange
#     subscribed_filtered: []
#
# Usage:
#
#     inherit dots-inspect
#
# By default every regular, non-symlink executable below DOTS_INSPECT_DIRS
# is offered to dots-inspect; binaries without dots-rs registration
# sections are silently skipped, so plain shell scripts or non-DOTS helpers
# in the same recipe do not need to be excluded by hand. Set
# DOTS_INSPECT_BINARIES to restrict the scan to an explicit list of
# installed paths (relative to the rootfs, e.g. "${bindir}/netcfgd").

# Where the YAML files end up on the target.
DOTS_INSPECT_DIR ?= "${datadir}/dots/meta"

# Directories scanned when DOTS_INSPECT_BINARIES is empty.
DOTS_INSPECT_DIRS ?= "${bindir} ${sbindir} ${libexecdir}"

# Explicit list of installed binaries (rootfs-relative paths). Empty means
# "scan DOTS_INSPECT_DIRS".
DOTS_INSPECT_BINARIES ?= ""

DEPENDS += "dots-utils-native"

# The reports describe the target binaries; nothing builds against them, so
# keep them out of other recipes' sysroots (${datadir} is staged by default).
SYSROOT_DIRS_IGNORE += "${DOTS_INSPECT_DIR}"

def dots_inspect_parse(text):
    """Turn the dots-inspect summary report into {group: [type names]}.

    The report is a sequence of 'label (n):' headers followed by two-space
    indented type names, or 'label: none' for an empty group."""
    import re

    groups = {}
    current = None
    for line in text.splitlines():
        if not line.strip():
            continue
        header = re.match(r'^(\S.*?) \(\d+\):$', line)
        if header:
            current = header.group(1)
            groups[current] = []
            continue
        empty = re.match(r'^(\S.*?): none$', line)
        if empty:
            groups[empty.group(1)] = []
            current = None
            continue
        if line.startswith('  ') and current is not None:
            groups[current].append(line.strip())
            continue
        # Anything else means the output is not what we parsed against.
        raise ValueError("unexpected dots-inspect output line: %r" % line)
    return groups

def dots_inspect_yaml_scalar(value):
    """Quote a YAML scalar unless it is safely plain."""
    import re

    # YAML 1.1 readers turn these into booleans/null when left unquoted.
    reserved = ('y', 'n', 'yes', 'no', 'true', 'false', 'on', 'off', 'null', '~')
    if value.lower() not in reserved and re.match(r'^[A-Za-z0-9_/][A-Za-z0-9_./+-]*$', value):
        return value
    return '"%s"' % value.replace('\\', '\\\\').replace('"', '\\"')

python do_dots_inspect() {
    import os
    import stat

    imgroot = d.getVar('D')
    metadir = imgroot + d.getVar('DOTS_INSPECT_DIR')
    tool = os.path.join(d.getVar('STAGING_BINDIR_NATIVE'), 'dots-inspect')

    # dots-inspect labels the subscribed-with-filter group 'subscribed
    # (filtered)'; the YAML keys are plain identifiers.
    labels = [
        ('published', 'published'),
        ('subscribed', 'subscribed'),
        ('subscribed (filtered)', 'subscribed_filtered'),
    ]

    targets = (d.getVar('DOTS_INSPECT_BINARIES') or '').split()
    scanned = not targets
    if scanned:
        for scandir in (d.getVar('DOTS_INSPECT_DIRS') or '').split():
            absdir = imgroot + scandir
            if not os.path.isdir(absdir):
                continue
            for name in sorted(os.listdir(absdir)):
                path = os.path.join(absdir, name)
                # Symlinks would report the same binary twice.
                if os.path.islink(path) or not os.path.isfile(path):
                    continue
                if not os.stat(path).st_mode & stat.S_IXUSR:
                    continue
                targets.append(scandir + '/' + name)

    count = 0
    for target in targets:
        binary = imgroot + target
        if not os.path.isfile(binary):
            bb.warn("dots-inspect: %s is listed in DOTS_INSPECT_BINARIES "
                    "but was not installed" % target)
            continue

        try:
            report, _ = bb.process.run([tool, binary])
        except bb.process.ExecutionError:
            # Not an ELF object, so not a DOTS binary.
            if scanned:
                continue
            raise
        if report.startswith('no dots-rs registration sections'):
            continue

        groups = dots_inspect_parse(report)
        unknown = set(groups) - set(label for label, _ in labels)
        if unknown:
            bb.warn("dots-inspect: ignoring unknown group(s) %s in the report "
                    "for %s" % (', '.join(sorted(unknown)), target))

        lines = [
            '# DOTS interface of %s' % target,
            '# generated by dots-inspect during the build -- do not edit',
            'binary: %s' % dots_inspect_yaml_scalar(target),
            # No version here: PKGV is not final yet at this point in the
            # build (the +git AUTOINC revision is only resolved during
            # do_package), and the package metadata records it anyway.
            'recipe: %s' % dots_inspect_yaml_scalar(d.getVar('PN')),
            'machine: %s' % dots_inspect_yaml_scalar(d.getVar('MACHINE')),
        ]
        for label, key in labels:
            types = groups.get(label, [])
            if not types:
                lines.append('%s: []' % key)
                continue
            lines.append('%s:' % key)
            lines.extend('  - %s' % dots_inspect_yaml_scalar(t) for t in types)

        bb.utils.mkdirhier(metadir)
        yamlfile = os.path.join(metadir, os.path.basename(target) + '.yaml')
        with open(yamlfile, 'w') as f:
            f.write('\n'.join(lines) + '\n')
        os.chmod(yamlfile, 0o644)

        count += 1
        bb.note("dots-inspect: wrote DOTS metadata for %s" % target)

    if not count:
        bb.note("dots-inspect: no DOTS binaries found in %s, no metadata "
                "packaged" % d.getVar('PN'))
}
addtask dots_inspect after do_install before do_package
# Writes into ${D}, so it has to run under pseudo like do_install does --
# otherwise the reports end up owned by the build user instead of root.
do_dots_inspect[fakeroot] = "1"
do_dots_inspect[umask] = "022"

PACKAGE_BEFORE_PN += "${PN}-dots-meta"
FILES:${PN}-dots-meta = "${DOTS_INSPECT_DIR}"
SUMMARY:${PN}-dots-meta = "DOTS type metadata for ${PN}"
DESCRIPTION:${PN}-dots-meta = "YAML files listing which DOTS types the binaries \
of ${PN} publish and subscribe to, extracted from the compiled binaries with \
dots-inspect at build time."
# Pure data, and deliberately installable without the binaries it describes.
RDEPENDS:${PN}-dots-meta = ""
# ... but it should follow its binaries into an image by default. A
# recommendation rather than a dependency, so an image that does not want
# the metadata can drop it via BAD_RECOMMENDATIONS / NO_RECOMMENDATIONS.
RRECOMMENDS:${PN} += "${PN}-dots-meta"
