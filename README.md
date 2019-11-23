A JavaSpaces Print Queue Demo

Gary Allen, University of Huddersfield.

Animates a print queue system using a JavaSpace as a central repository of objects.  Illustrates how to create a queue of objects.

Updated to use Apache River 3.0

NOTE - if the run configurations are not imported correctly, the following MUST
be passed as VM args to all run configs (PrintJobAdder, PrintJobPrinter, and StartPrintQueue):

    -Djava.security.policy=policy.all -Djava.rmi.server.useCodebaseOnly=false
