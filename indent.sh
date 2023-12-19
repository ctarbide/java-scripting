#!/bin/sh
set -eu
die(){ ev=$1; shift; for msg in "$@"; do echo "${msg}"; done; exit "${ev}"; }
command -v astyle >/dev/null || die 1 "Error, command 'astyle' not found."
# https://astyle.sourceforge.net/astyle.html
exec astyle -n -s4 --style=java -j -xb -xB -H -p -U "$@"
