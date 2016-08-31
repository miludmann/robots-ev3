[![Build Status](https://travis-ci.org/orwell-int/robots-ev3.svg?branch=master)](https://travis-ci.org/orwell-int/robots-ev3) [![Stories in Ready](https://badge.waffle.io/orwell-int/robots-ev3.png?label=ready&title=Ready)](https://waffle.io/orwell-int/robots-ev3) [![Coverage Status](https://coveralls.io/repos/orwell-int/robots-ev3/badge.svg?branch=master)](https://coveralls.io/r/orwell-int/robots-ev3?branch=master)
robots-ev3
============

This is the code running on the EV3 robots in the ORWELL project.

Checkout the code
-----------------
Get the sources
```
git clone git@github.com:orwell-int/robots-ev3.git
```

Get the submodules
```
git submodule update --init --recursive
```

local setup for coveralls
-------------------------
Run with maven
--------------
Prerequiste: have jdk-7+ installed on your machine
```
javac -version
>javac 1.7.xxx

java -version                                                         
>java version "1.7.xxx"
```

Install maven:
```
sudo apt-get install maven
```

Download leJOS tar.gz
```
wget -nc --no-check-certificate http://sourceforge.net/projects/lejos/files/lejos-NXJ/0.9.1beta/leJOS_NXJ_0.9.1beta-3.tar.gz/download -O ./leJOS_NXJ_0.9.1beta-3.tar.gz
tar -xvf leJOS_NXJ_0.9.1beta-3.tar.gz
export NXJ_HOME=leJOS_NXJ_0.9.1beta-3
```

TEMPORARY DIRTY HACK to configure your setup
```
Provide your Proxy-Robots IP in the RemoteRobot.java class
Provide the IP of your robot in the robots-ev3-module/pom.xml 
```

Run maven for install (download code on robot) and run code on robot
```
mvn package ???
mvn install antrun:run
```

To update the coveralls status, export your repo token in the following environment variable:
(You will find it on https://coveralls.io/r/orwell-int/robots-ev3)
```
export COVERALLS_REPO_TOKEN=yourToken
```

To update the coveralls status, export your repo token in the following environment variable:
(You will find it on https://coveralls.io/r/orwell-int/robots-ev3)
```
mvn clean cobertura:cobertura coveralls:report
```

You can also run the jar created by the install to start the application from the command line on the robot itself (by ssh)
```
Check in the pom file the actual command to launch on the robot itself
```

