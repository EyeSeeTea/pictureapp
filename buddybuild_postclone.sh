#!/bin/sh

# Definitions
gitPath=$(git rev-parse --show-toplevel)
postCheckoutFile=${gitPath}/.git/hooks/post-checkout
${EST_FLAVOR:="ereferrals"}

# Generate last commit
sh ${gitPath}/generate_last_commit.sh

# Use the EST SDK branch
cd sdk
git checkout v1.2_la
cd -

cd EyeSeeTea-sdk
git checkout v1.2_la
cd -

mkdir app/src/"${EST_FLAVOR}"/res/raw
cp ${BUDDYBUILD_SECURE_FILES}/config_${EST_FLAVOR}.json app/src/${EST_FLAVOR}/res/raw/config.json
