#!/bin/sh

# Definitions
gitPath=$(git rev-parse --show-toplevel)
postCheckoutFile=${gitPath}/.git/hooks/post-checkout
${EST_FLAVOR:="ereferrals"}

# Generate last commit
sh ${gitPath}/generate_last_commit.sh

# Use the EST SDK branch
cd sdk
git checkout 2.25_EyeSeeTea
cd -

mkdir app/src/"${EST_FLAVOR}"/res/raw
cp ${BUDDYBUILD_SECURE_FILES}/config_${EST_FLAVOR}.json app/src/${EST_FLAVOR}/res/raw/config.json