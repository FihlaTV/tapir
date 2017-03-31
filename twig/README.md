# TAPIR Twig #

**Tapir Twig** provides REST API.

![](https://cloud.githubusercontent.com/assets/16978841/23796439/608e7030-05b4-11e7-9fe9-dcb8eac9334b.png)

### Build & Install ###

Follow next steps to build **Twig** on your local machine and to install it to your Linux:

#### 1. Build ####

Make sure you have installed:
* Oracle JRE HotSpot, version 1.8+.
* Apache Maven, version 3.0.0+.

Build **Tapir** with Apache Maven:
```
# cd </path/to/Tapir>
# mvn clean package
```

#### 2. Install ####

The best way to install **Twig** on your Linux is to run ```./package/tapir-twig``` script to create RPM and install it with ```rpm``` utility:
```
# cd </path/to/Tapir/package>
# ./tapir-twig rpm
```
RPM requires Oracle JDK HotSpot 1.8+ (use RPM option --nodeps, if you sure use Oracle JRE HotSpot 1.8+).

Using Sip3.io YUM repository:
```
# <Download Oracle JDK HotSpot 1.8, e.g. jdk-8u121-linux-x64.rpm to /tmp>
# yum localinstall /tmp/jdk-8u121-linux-x64.rpm -y
# curl http://sip3.io/repo/yum/sip3io.repo > /etc/yum.repos.d/sip3io.repo
# yum install tapir-twig -y
```

Alternatively, you can run ```./package/tapir-twig``` script to automatically install **Twig** to local/remote Linux:
```
# cd </path/to/Tapir/package>
# ./tapir-twig install 127.0.0.1
```

_**Hint:** Run ```./package/tapir-twig``` script to update/remove **Twig**._

### Configure & Run ###

Make sure you have installed:
* [Oracle JRE HotSpot, version 1.8+.](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
* Mongo, version 3.0.0+. 
* Redis, version 2.8.9+.

#### 1. Configure ####

Make sure you have presented:
* /etc/init.d/tapir-twig
* /etc/tapir-twig/tapir-twig.properties
* /etc/tapir-twig/logback.xml
* /var/log/tapir-twig/default.log

Use [this](https://github.com/sip3io/tapir/tree/master/package/etc/tapir-twig/tapir-twig.properties.changes) property description to configure ```/etc/tapir-twig/tapir-twig.properties```

_**Hint:** To run **Twig** you have to set ```server.port```, ```mongo.uri```, ```redis.host``` and ```redis.port```. Use other properties only for tunning_

By default, **Twig** writes logs to ```/var/log/tapir-twig/default.log```, rotates it every day and keeps history for 15 days.
Use [this](https://logback.qos.ch) description to configure ```/etc/tapir-twig/logback.xml```

#### 2.  Run ####
```
# service tapir-twig start

```
