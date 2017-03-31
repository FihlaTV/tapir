# TAPIR Salto #

**Tapir Salto** receives parsed SIP data from **Tapir Captain** and saves it to database. Since March 2017 **Salto** can process HEPv3 packets too.

![salto](https://cloud.githubusercontent.com/assets/16978841/24553230/25aa2dcc-1632-11e7-9799-739200f5088d.png)

### Build & Install ###

Follow next steps to build **Salto** on your local machine and to install it to your Linux:

#### 1. Build ####

Make sure you have installed:
* Oracle JRE HotSpot, version 1.8+
* Apache Maven, version 3.0.0+.

Build **Tapir** with Apache Maven:
```
# cd </path/to/Tapir>
# mvn clean package
```

#### 2. Install ####

The best way to install **Salto** on your Linux is to run ```./package/tapir-salto``` script to create RMP and install it with ```rpm``` utility:
```
# cd </path/to/Tapir/package>
# ./tapir-salto rpm
```

Alternatively, you can run ```./package/tapir-salto``` script to automatically install **Salto** to local/remote Linux:
```
# cd </path/to/Tapir/package>
# ./tapir-salto install 127.0.0.1
```

_**Hint:** Run ```./package/tapir-salto``` script to update/remove **Salto**._

### Configure & Run ###

Make sure you have installed:
* Oracle JRE HotSpot, version 1.8+
* Mongo, version 3.0.0+. 
* Redis, version 2.8.9+.

#### 1. Configure ####

Make sure you have presented:
* /etc/init.d/tapir-salto
* /etc/tapir-salto/tapir-salto.properties
* /etc/tapir-salto/logback.xml
* /var/log/tapir-salto/default.log

Use [this](https://github.com/sip3io/tapir/tree/master/package/etc/tapir-salto/tapir-salto.properties.changes) property description to configure ```/etc/tapir-salto/tapir-salto.properties```

_**Hint:** To run **Salto** you have to set ```mongo.uri```, ```redis.host``` and ```redis.port```. Use ```staistic.enabled``` property if you want to see statistic data on dashboard. Use other properties only for tunning_

_**Hint:** **Salto** can give a name to any SIP node. Use property ```hosts.yml``` to define hosts file in [current](https://github.com/sip3io/tapir/tree/master/package/etc/tapir-salto/hosts.yml.example) format. This feature is very useful if your SIP node has different network interfaces for input and output SIP traffic_

By default, **Salto** writes logs to ```/var/log/tapir-salto/default.log```, rotates it every day and keeps history for 15 days.
Use [this](https://logback.qos.ch) description to configure ```/etc/tapir-salto/logback.xml```

#### 2.  Run ####
```
# service tapir-salto start

```
