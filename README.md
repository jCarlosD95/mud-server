mud-server
==========

A MUD server written in Java. Elements of the D&D/d20 system are used to implement the 'game' aspect of the
server code with regards to things like player and item stats, etc.

## Status

This code is very much in an unfinished state, there may be radical shifts in the inner workings
in the future. Try not to make too many assumptions about stability and backup the database if you0
play with this as future code may corrupt the data and/or utilize an alternate approach for data storage.

This code requires at least Java 7 (1.7)

## Usage

```
Usage: java -jar MUDServer.jar

    --port  <port number> specifiy port for the server to listen on
    --debug               enable debugging messages
```

## Help

For help using the code and miscellaneous documentation, see the [Wiki](https://github.com/jnharton/mud-server/wiki) for this project.

## Updates

see the [Wiki](https://github.com/jnharton/mud-server/wiki)

## Copyright
Copyright (c) 2012 Jeremy Harton. See LICENSE.txt for further details.

The license given basically applies to all files in the source (MUDServer/src) directory.
