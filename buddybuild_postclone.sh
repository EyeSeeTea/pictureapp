#!/bin/sh

# Definitions
gitPath=$(git rev-parse --show-toplevel)
postCheckoutFile=${gitPath}/.git/hooks/post-checkout

# Generate last commit
sh ${gitPath}/generate_last_commit.sh

# Use the EST SDK branch
cd sdk
git checkout 2.25_EyeSeeTea
cd -
mkdir app/src/cambodia/res/raw
cp ${BUDDYBUILD_SECURE_FILES}/config_laos.json app/src/laos/res/raw/config.json
