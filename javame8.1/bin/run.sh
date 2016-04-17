#! /bin/sh
#
# Copyright (c) 1990, 2011, Oracle and/or its affiliates. All rights reserved.
#

cd ${0%/*}
./runMidlet runMidlet +UseProxy =VMAgentBufferSize4000 $*
