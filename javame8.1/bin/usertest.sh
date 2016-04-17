#!/bin/sh
#
# Copyright (c) 1990, 2013, Oracle and/or its affiliates. All rights reserved.
#
cd ${0%/*}

return_code=16
additional_flags=

while [ $return_code -eq 16 ]
do
    ./run.sh -1 com.sun.midp.appmanager.AmsLauncher -debugger -wdogenable $additional_flags;
    return_code=$?
    additional_flags="-wdogreboot true"
done
