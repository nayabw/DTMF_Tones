# DTMF Tones

## What?
A simple Java project to take a .txt file with a list of DTMF tone codes, present the names to the user, and "dial" them when selected.

## Why?
This is based on the [W5AC](http://w5ac.tamu.edu) repeater controller configuration files. It's a fun and semi-useful project to keep me occupied over break.

The repeater controller can be "remote controlled" through a series of DTMF tones, much like a telephone system. For example, here is a code that could ask the repeater to automatically adjust for DST.
``012345 63 0002 1 * ;Enable auto DST Adjustment`` (012345 represents the master password)

The program should read everything after the ``;`` as the code name and exectute all the preceding DTMF tones if selected. This is a LOT easier than dialing it yourself on a Handi-Talkie!

## Who?
[W5AC](http://w5ac.tamu.edu) is Texas A&M University's Amateur Radio Club, and I am an amateur radio operator serving on the executive committee (as of writing).

## How?
This project uses [TarosDSP](https://github.com/JorenSix/TarsosDSP), which is licensed under GNU General Public License v3.0.
