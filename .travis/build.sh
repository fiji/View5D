#!/bin/sh
curl -fsLO https://raw.githubusercontent.com/scijava/scijava-scripts/master/travis-build.sh
sh travis-build.sh $encrypted_bc8bdf24d4b3_key $encrypted_bc8bdf24d4b3_iv
