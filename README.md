#Goal
The goal of the project is to expose the network router as a *programmable* platform. It will provide all the necessary components for basic routing. Users have the flexibility to insert their own functionality if desired.

The code is mostly written in Java. The sending and receiving of raw packets requires native code and currently supporting Linux only. The use of Jython is employed in configuration and scripting.


#Background
The project was originally intended to prove that Java is just as fast as C as most, if not all, routers software are written in C.

##Component
Traditionally, router software was a big piece of code with tons of features, where most users would actually use only a very small part of it. This introduced unnecessary complexity to the otherwise, very simple flow of usage. One of the goals of this project is to implement components that can be replaced easily. Components like checksum calculation, routing algorithm or even TCP stack can be replaced by importing a different library.

##Experimental networking
Another interesting aspect of the project is that it provides a platform that is easy to test out experimental technologies. For example, hierarchical IP address routing instead of OSPF, networking other than IP...etc.


#Architecture
The Java Router uses a callback approach to process packets. The network packets are read at the Port classes continuously. The classes provide registration interface for others to obtain the packets. For example, !EthernetPort provides registration to listen to a particular ethertype.

Similar to a realtime system, if possible, each packet is processed until it is being routed out of the box.

##Raw socket
In order to receive all packets from the network, a native raw socket module is implemented. The module is now written for Linux only and for ethernet only. It sets the ethernet port to promiscuous mode and open a raw socket to receive packets.

##Configuration
Jython is simple enough to be embedded as a configuration interface to run Java Router in anyway the users need. Certain templates are provided in order to simplify the main configuration. The future goal is to achieve the simplicity of a command line interface, but still abides to Python syntax.

#Current project status
Current project supports a few fundamental element in a router: Bridging and Routing. A learning bridge is implemented to demonstrate simple bridging. However, spanning tree is not implemented yet. ICMP and UDP are implemented. Static IP routing and partial RIP protocol is implemented.
The plan is to have TCP and OSPF available also.

#Build
The code uses scons to build. Testing has been done with VMWare using multiple VMs and custom networks.

###Environment
A small amount of native is required for raw socket. This native code is currently written for Linux only.
For reference my build environment is Ubuntu 14.04 running in VMWare Fusion.
  * VMWare Fusion 6.0.3
  * 1G Ram 20GB disk
  * In settings, added multiple custom networks vmnet2, vmnet3 and vmnet4
  * Ubuntu 14.04 mini 64-bit
  ** basic Ubuntu server
  ** openssh server

Additional package to install:

    sudo apt-get install subversion
    sudo apt-get install scons
    sudo apt-get install openjdk-7-jdk 
    sudo apt-get install jython
    sudo apt-get install gcc

###Build

    svn checkout http://java-router.googlecode.com/svn/trunk/ java-router-read-only
    cd java-router-read-only
    scons


#Samples
 * "Learning Bridge" shows a minimal use case
 * "Bridge With Filter" extends the bridge by intercepting packets
 * "RIP Router" routes IP packets and learns routes from other routers

##Learning Bridge
 * Very small code to show basic functionality
 * Bridges and learns mac addresses from each port
 * Simplified, so does not handle loops

###Run

    sudo LD_LIBRARY_PATH=build/deploy/lib java -cp "build/deploy/lib/*" code.messy.sample.LearningBridge eth1 eth2

###Flow
![](https://github.com/alei121/java-router/blob/master/images/LearningBridge.png)


##Bridge With Filter
 * Based on LearningBridge
 * Shows how to intercept packets
 * Prints all broadcast packets

###Run

    sudo LD_LIBRARY_PATH=build/deploy/lib java -cp "build/deploy/lib/*" code.messy.sample.BridgeWithFilter eth1 eth2

###Flow
![](https://github.com/alei121/java-router/blob/master/images/BridgeWithFilter.png)


##RIP Router
 * !RipRouter sample can talk to other !RipRouters to build up routing table
 * !RipProcessor simplified the RIP protocol by just sending and receiving periodic update messages
 * !RipProcessor does not handle loop current
 * DHCP and ICMP are also included for easier testing from network clients

###Run

    sudo LD_LIBRARY_PATH=build/deploy/lib java -cp "build/deploy/lib/*" code.messy.sample.RipRouter eth1 10.1.0.1 24 eth2 10.2.0.1 24

###Flow diagram
![](https://github.com/alei121/java-router/blob/master/images/RipRouter.png)
