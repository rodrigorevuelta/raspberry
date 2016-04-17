#!/bin/sh
#
# Copyright (c) 2013, Oracle and/or its affiliates. All rights reserved.
#
if [ $# -lt 1 -o $# -gt 2 ]; then
  echo "usage: runSuite.sh <suite name or suite ID> [MIDlet class or MIDlet number in the suite]"
  exit
fi

cd ${0%/*}
./run.sh -1 com.sun.midp.appmanager.AmsLauncher -wdogenable -launcherMode $1 $2
