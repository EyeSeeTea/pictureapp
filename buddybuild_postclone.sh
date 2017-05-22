#!/bin/sh
set -x

# Definitions
gitPath=$(git rev-parse --show-toplevel)
postCheckoutFile=${gitPath}/.git/hooks/post-checkout
FLAVOR=${FLAVOR:="ereferrals"}

# Generate last commit
sh ${gitPath}/generate_last_commit.sh

# Use the EST SDK branch
cd sdk
git checkout 2.25_EyeSeeTea
cd -

mkdir app/src/"${FLAVOR}"/res/raw
cp ${BUDDYBUILD_SECURE_FILES}/config_${FLAVOR}.json app/src/${FLAVOR}/res/raw/config.json