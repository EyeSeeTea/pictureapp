#!/bin/bash
echo "start travis before script"
openssl aes-256-cbc -K $encrypted_7829ffbf9381_key -iv $encrypted_7829ffbf9381_iv -in app.tar.enc -out app.tar -d
echo "decrypted file"
tar -xvzf app.tar
echo "continue"
