# TAPIR Captain #

**Tapir Captain** captures SIP from UDP/TCP/IP2IP packets and sends it to **Tapir Salto**. 

![](https://cloud.githubusercontent.com/assets/1871737/23656419/b084699e-034a-11e7-852f-329eac1291a5.png)

### Build & Install ###

Follow next steps to build **Captain** on your local machine and to install it to your Linux:

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

The best way to install **Captain** on your Linux is to run ```./package/tapir-captain``` script to create RMP and install it with ```rpm``` utility:
```
# cd </path/to/Tapir/package>
# ./tapir-captain rpm
```

Alternatively, you can run ```./package/tapir-captain``` script to automatically install **Captain** to local/remote Linux:
```
# cd </path/to/Tapir/package>
# ./tapir-captain install 127.0.0.1
```

_**Hint:** Run ```./package/tapir-captain``` script to update/remove **Captain**._

### Configure & Run ###

Make sure you have installed:
* Oracle JRE HotSpot, version 1.8+
* libpcap, version 1.4.0+. 

#### 1. Configure ####

Make sure you have presented:
* /etc/init.d/tapir-captain
* /etc/tapir-captain/tapir-captain.properties
* /etc/tapir-captain/logback.xml
* /var/log/tapir-captain/default.log

Use [this](https://github.com/sip3io/tapir/tree/master/package/etc/tapir-captain/tapir-captain.properties.changes) property description to configure ```/etc/tapir-captain/tapir-captain.properties```

_**Hint:** To run **Captain** you have to set ```pcap.device```/```pcap.directory```/```pcap.file``` and ```packet.host```, ```packet.port```. Use other properties only for tunning_

By default, **Captain** writes logs to ```/var/log/tapir-captain/default.log```, rotates it every day and keeps history for 15 days.
Use [this](https://logback.qos.ch) description to configure ```/etc/tapir-captain/logback.xml```

#### 2.  Run ####
```
# service tapir-captain start

```
