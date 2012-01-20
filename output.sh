#!/bin/bash
# Massages the INFO sent to log.log and outputs the chord currently being played
# use:  tail -f  ~/.overtone/log/log.log  | src/pitch/output.sh

while read data; do
    clear
    echo Chord
    echo "$data" | sed -n 's/INFO: chord//p' | sed 's/://g'
done