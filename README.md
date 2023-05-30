# FUZZ-BFT-SMaRt
This is a tool that implements FUZZ and message dropping on BFT-SMaRt v1.2.

# How to FUZZ
This tool currently statically implements FUZZ inside the tool, and nodes defined as Byzantine nodes will randomly mutate messages sent by themselves.

# How to drop message
Write a file with multiple lines in the following format:

```
<epoch> <number of round> <message sender> <message receiver>
```
eg: Drop the Write message from node 2 and node 3 to node 1 in epoch 0.
```
0 44782 2 1
0 44782 3 1
```

# Reproduce bug
To run the counter demonstration by executing the following commands, from within the main directory across four different consoles (4 replicas, to tolerate 1 fault):

```
./runscripts/smartrun.sh bftsmart.demo.counter.CounterServer 0
./runscripts/smartrun.sh bftsmart.demo.counter.CounterServer 1
./runscripts/smartrun.sh bftsmart.demo.counter.CounterServer 2
./runscripts/smartrun.sh bftsmart.demo.counter.CounterServer 3
```

Once all replicas are ready, the client can be launched as follows:

```
./runscripts/smartrun.sh bftsmart.demo.counter.CounterClient 1001 <increment> [<number of operations>]
```
