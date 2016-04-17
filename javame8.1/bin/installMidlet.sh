#!/bin/sh
#
# Copyright (c) 1990, 2011, Oracle and/or its affiliates. All rights reserved.
#
if [ $1X = X ]; then
  echo "Usage: installMidlet <URL> [<URL label>]"
  exit
fi

cd ${0%/*}
./run.sh -1 com.sun.midp.scriptutil.CommandLineInstaller I $1 $2;
