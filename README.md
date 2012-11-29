EyePad
======

A visual scratchpad for Clojure code.

Usage
-----
EyePad needs a [MongoDB](http://www.mongodb.org/) installation. Make sure you have a running MongoDB instance. On Mac, you can install it using [Homebrew](http://mxcl.github.com/homebrew/):

    brew update
    brew install mongodb 

Once you have the database set up, run EyePad using the standard:

    lein run


EyePad will be running on [http://localhost:8080/](http://localhost:8080/).

Security
--------
It is important to realize that evaluated code does **not** run in a sandbox. In other words, **executed code has full access to the server**. EyePad scripts have full access to the filesystem, network, etc. Do not run this in production environments.

License
-------
EyePad is copyright (C) 2012 Frederik De Bleser. It is distributed under the Eclipse Public License, the same as Clojure.

