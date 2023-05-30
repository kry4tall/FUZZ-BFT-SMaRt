# FUZZ-BFT-SMaRt

# Quick start
To run the counter demonstration by executing the following commands, from within the main directory across four different consoles (4 replicas, to tolerate 1 fault):

./smartrun.sh bftsmart.demo.counter.CounterServer 0
./smartrun.sh bftsmart.demo.counter.CounterServer 1
./smartrun.sh bftsmart.demo.counter.CounterServer 2
./smartrun.sh bftsmart.demo.counter.CounterServer 3
