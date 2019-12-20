### A JavaSpace Auction Lot System

Java application based on the Apache River (JavaSpaces) technology to provide a Simple Auction System. 


#### Requirements
The following libraries must be imported from Apache River:
```
apache-river-3.0.0/lib/jsk-lib.jar
apache-river-3.0.0/lib/jsk-platform.jar
apache-river-3.0.0/lib/outrigger.jar
apache-river-3.0.0/lib/reggie.jar
apache-river-3.0.0/lib-dl/reggie-dl.jar
```

#### How To Compile & Run:
```
cd src/
. jinicl
javac .java
java -Djava.security.policy=../policy.all -Djava.rmi.server.useCodebaseOnly=false StartAuctionSpace &
java -Djava.security.policy=../policy.all -Djava.rmi.server.useCodebaseOnly=false LoginRegisterUI &
```


NOTE - When using an IDE, if the run configurations are not imported correctly, the following MUST 
be passed as VM args to all run configs (LoginRegisterUI & StartAuctionSpace):

    -Djava.security.policy=policy.all -Djava.rmi.server.useCodebaseOnly=false

Updated to use Apache River 3.0
