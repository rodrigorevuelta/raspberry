#!/bin/sh
#
# Copyright (c) 1990, 2011, Oracle and/or its affiliates. All rights reserved.
#
if [ "x$1" = "x" ]; then
  echo "usage: removeMidlet <suite ID>"
  exit
fi

cd ${0%/*}
./run.sh -1 com.sun.midp.scriptutil.SuiteRemover $1
