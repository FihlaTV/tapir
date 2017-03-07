# TAPIR #

**Tapir** is open source solution for troubleshooting and real-time monitoring VoIP-network based systems. 
It has easy scalable architecture, fast and clear WEB UI and, as a result, provides best way to analyze end-to-end 
call correlation. Using **Tapir** you reduce operational costs, prevent voice fraud, increase system availability 
time.

### ###

![](https://cloud.githubusercontent.com/assets/1871737/23656420/b089fe5e-034a-11e7-8244-4a2a7594ddd3.gif)

### Tapir includes four modules: ###

1. **Tapir Captain** network sniffer for capturing and parsing traffic.
2. **Tapir Salto** aggregates traffic from **Captain** and stores it to DB-engine.
3. **Tapir Twig** provides REST API for **Hoof** to get data from database.
4. **Tapir Hoof** convenient and easy web-based user interface to search and display call data.

**Captain** can capture data directly from network interface (UDP/TCP/encapsulated IP2IP), from single file 
or check directory for new incoming file and process it.

**Salto** receives parsed data from **Captain**, validates it structure and stores the data in database. 
Of course, to achieve the desired performance can be easily scaled. 

**Twig** offers REST API for user interface queries and presents statistical data.

**Hoof** includes dashboard with widgets and SIP search page. Each call can be displayed as graphic callflow, 
raw message view or can be exported to pcap-file.

**Tapir** modules powered by high-performance Java code and use fast and reliable MongoDB database for signaling data. 
So scaling of any module will be just clear. Redis is used for storing statistical metrics and operating on it. 
**Tapir Hoof** based on ReactJS which provides simple way for creating custom web interfaces.

**Tapir** solution used by telecom operators and service providers, who note the excellent stability, fast search 
and significant time savings in solving problems on trunk and access connections.

### Tapir architecture ###

![](https://cloud.githubusercontent.com/assets/1871737/23656460/ddd32c28-034a-11e7-90a8-a1ba4e55c079.png)

### Installation ###

To deploy **Tapir** modules, follow build and installation instructions: [Captain](https://github.com/sip3io/tapir/tree/master/captain "Captain Installation guide"), 
[Salto](https://github.com/sip3io/tapir/tree/master/salto "Salto Installation guide"), [Twig](https://github.com/sip3io/tapir/tree/master/twig "Twig Installation guide"), 
[Hoof](https://github.com/sip3io/tapir-hoof "Hoof Installation guide").

At the moment, we described build and installation process only for Linux users. Let us know if you want to deploy Tapir on Windows machine.

### Versions ###

**Tapir** distributed in two different versions: Community and Enterprise. Summary comparison table is given below.

| Functionality              | Community | Enterprise |
|----------------------------|-----------|------------|
| Unlimited sessions         | Yes       | No         |
| Сall-legs join             | Yes       | Yes        |
| Authorization/Roles        | No        | Yes        |
| Statistical metrics        | Limited   | Full       |
| SIP KPI                    | Limited   | Full       |
| Source SPAN                | Yes       | Yes        |
| Source pcap/pcap.gz files  | Yes       | Yes        |
| SIP-I support              | No        | Yes        |
| Scalability                | Full      | Full       |
| Extended search            | No        | Yes        |
| Technical assistance       | No        | Yes        |
| Quality dashboards         | Limited   | Full       |

### License & Contributing ###

This project is available under the Apache License 2.0. We welcome any contributions submitted as pull requests, 
wiki edits or issues under the terms of the project's license.

### Support ###

If you have a question about **Tapir**, please contact us at 
[support@sip3.io](mailto:support@sip3.io "send mail to tapir team"). We are always ready to help you.