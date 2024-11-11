#!/bin/bash

java -jar acdc.jar packages_packages_dependencies.rsf c.rsf
java -jar mojo.jar c.rsf modules_packages_dependencies.rsf -fm

