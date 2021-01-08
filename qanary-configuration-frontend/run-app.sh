#!/bin/bash

if [ $# -ne 0 ]; then
    echo "-> overriding env with args ..."
    for var in "$@"
    do
        export "$var"
    done
fi

echo "-> ENV vars:"
env | sort

echo "-> building app ..."
npm run build --production

echo "=> running app ..."
exec serve -s build
