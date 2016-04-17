#!/bin/sh
#
# Copyright (c) 2014, Oracle and/or its affiliates. All rights reserved.
#

# Fix permissions for correct functionality without sudo

cd ${0%/*}
echo sets +x for all .sh
chmod +x ./*.sh

echo sets +x for runMidlet
chmod +x ./runMidlet*

echo change owner of runMidlet
sudo chown root:root ./runMidlet*

echo set suid flag for runMidlet
sudo chmod u+s ./runMidlet*
