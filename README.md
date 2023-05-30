# FUZZ-BFT-SMaRt
This is a tool that implements FUZZ and message dropping on BFT-SMaRt v1.2.

# Baseline
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

# How to FUZZ
This tool currently statically implements FUZZ inside the tool. Node 0 (CounterServer 0) is defined as Byzantine node and randomly mutate messages sent by itself.

# How to drop message
Write a file with multiple lines in the following format (PROPOSE = 44781; WRITE = 44782; ACCEPT = 44783):

```
<epoch> <number of round> <message sender> <message receiver>
```
eg: Drop the Write message from node 2 and node 3 to node 1 in epoch 0.
```
0 44782 2 1
0 44782 3 1
```
Modify attribute "scenario-file-path" to the path of this file in test.properties.

```
scenario-file-path=.../scenarios.txt
```

# Reproduce the bug
Fuzz the message send by node 0 and drop the Write message from node 2 and node 3 to node 1 in epoch 0. For the counter client, set "increment" to 1, "number of operations" to 10. We can find that:

1.The cluster is unable to process all 10 increment requests correctly and the return value is discontinuous.

2.The cluster is unable to select a leader within a limited time after request processing fails and ultimately stop.

Example standard output of counter client:
```
E:\Java\jdk1.8.0_361\bin\java.exe "-javaagent:E:\jetbrain\IntelliJ IDEA 2022.3.2\lib\idea_rt.jar=63963:E:\jetbrain\IntelliJ IDEA 2022.3.2\bin" -Dfile.encoding=UTF-8 -classpath E:\java\jdk1.8.0_361\jre\lib\charsets.jar;E:\java\jdk1.8.0_361\jre\lib\deploy.jar;E:\java\jdk1.8.0_361\jre\lib\ext\access-bridge-64.jar;E:\java\jdk1.8.0_361\jre\lib\ext\cldrdata.jar;E:\java\jdk1.8.0_361\jre\lib\ext\dnsns.jar;E:\java\jdk1.8.0_361\jre\lib\ext\jaccess.jar;E:\java\jdk1.8.0_361\jre\lib\ext\jfxrt.jar;E:\java\jdk1.8.0_361\jre\lib\ext\localedata.jar;E:\java\jdk1.8.0_361\jre\lib\ext\nashorn.jar;E:\java\jdk1.8.0_361\jre\lib\ext\sunec.jar;E:\java\jdk1.8.0_361\jre\lib\ext\sunjce_provider.jar;E:\java\jdk1.8.0_361\jre\lib\ext\sunmscapi.jar;E:\java\jdk1.8.0_361\jre\lib\ext\sunpkcs11.jar;E:\java\jdk1.8.0_361\jre\lib\ext\zipfs.jar;E:\java\jdk1.8.0_361\jre\lib\javaws.jar;E:\java\jdk1.8.0_361\jre\lib\jce.jar;E:\java\jdk1.8.0_361\jre\lib\jfr.jar;E:\java\jdk1.8.0_361\jre\lib\jfxswt.jar;E:\java\jdk1.8.0_361\jre\lib\jsse.jar;E:\java\jdk1.8.0_361\jre\lib\management-agent.jar;E:\java\jdk1.8.0_361\jre\lib\plugin.jar;E:\java\jdk1.8.0_361\jre\lib\resources.jar;E:\java\jdk1.8.0_361\jre\lib\rt.jar;E:\workspace\library-1.2\out\production\library-1.2;E:\workspace\library-1.2\lib\logback-classic-1.2.3.jar;E:\workspace\library-1.2\lib\logback-core-1.2.3.jar;E:\workspace\library-1.2\lib\core-0.1.4.jar;E:\workspace\library-1.2\lib\commons-codec-1.11.jar;E:\workspace\library-1.2\lib\netty-all-4.1.30.Final.jar;E:\workspace\library-1.2\lib\slf4j-api-1.7.25.jar bftsmart.demo.counter.CounterClient 1001 1 10
00:53:31.882 [main] DEBUG io.netty.util.internal.logging.InternalLoggerFactory - Using SLF4J as the default logging framework
00:53:31.888 [main] DEBUG io.netty.util.internal.InternalThreadLocalMap - -Dio.netty.threadLocalMap.stringBuilder.initialSize: 1024
00:53:31.888 [main] DEBUG io.netty.util.internal.InternalThreadLocalMap - -Dio.netty.threadLocalMap.stringBuilder.maxSize: 4096
00:53:31.915 [main] DEBUG io.netty.channel.MultithreadEventLoopGroup - -Dio.netty.eventLoopThreads: 12
00:53:31.939 [main] DEBUG io.netty.channel.nio.NioEventLoop - -Dio.netty.noKeySetOptimization: false
00:53:31.939 [main] DEBUG io.netty.channel.nio.NioEventLoop - -Dio.netty.selectorAutoRebuildThreshold: 512
00:53:31.961 [main] DEBUG io.netty.util.internal.PlatformDependent - Platform: Windows
00:53:31.963 [main] DEBUG io.netty.util.internal.PlatformDependent0 - -Dio.netty.noUnsafe: false
00:53:31.963 [main] DEBUG io.netty.util.internal.PlatformDependent0 - Java version: 8
00:53:31.964 [main] DEBUG io.netty.util.internal.PlatformDependent0 - sun.misc.Unsafe.theUnsafe: available
00:53:31.965 [main] DEBUG io.netty.util.internal.PlatformDependent0 - sun.misc.Unsafe.copyMemory: available
00:53:31.965 [main] DEBUG io.netty.util.internal.PlatformDependent0 - java.nio.Buffer.address: available
00:53:31.965 [main] DEBUG io.netty.util.internal.PlatformDependent0 - direct buffer constructor: available
00:53:31.966 [main] DEBUG io.netty.util.internal.PlatformDependent0 - java.nio.Bits.unaligned: available, true
00:53:31.966 [main] DEBUG io.netty.util.internal.PlatformDependent0 - jdk.internal.misc.Unsafe.allocateUninitializedArray(int): unavailable prior to Java9
00:53:31.966 [main] DEBUG io.netty.util.internal.PlatformDependent0 - java.nio.DirectByteBuffer.<init>(long, int): available
00:53:31.966 [main] DEBUG io.netty.util.internal.PlatformDependent - sun.misc.Unsafe: available
00:53:31.966 [main] DEBUG io.netty.util.internal.PlatformDependent - -Dio.netty.tmpdir: C:\Users\kry4t\AppData\Local\Temp (java.io.tmpdir)
00:53:31.966 [main] DEBUG io.netty.util.internal.PlatformDependent - -Dio.netty.bitMode: 64 (sun.arch.data.model)
00:53:31.968 [main] DEBUG io.netty.util.internal.PlatformDependent - -Dio.netty.maxDirectMemory: 3799515136 bytes
00:53:31.968 [main] DEBUG io.netty.util.internal.PlatformDependent - -Dio.netty.uninitializedArrayAllocationThreshold: -1
00:53:31.969 [main] DEBUG io.netty.util.internal.CleanerJava6 - java.nio.ByteBuffer.cleaner(): available
00:53:31.970 [main] DEBUG io.netty.util.internal.PlatformDependent - -Dio.netty.noPreferDirect: false
00:53:31.978 [main] DEBUG io.netty.util.internal.PlatformDependent - org.jctools-core.MpscChunkedArrayQueue: available
00:53:32.594 [main] DEBUG io.netty.channel.DefaultChannelId - -Dio.netty.processId: 7328 (auto-detected)
00:53:32.596 [main] DEBUG io.netty.util.NetUtil - -Djava.net.preferIPv4Stack: false
00:53:32.597 [main] DEBUG io.netty.util.NetUtil - -Djava.net.preferIPv6Addresses: false
00:53:32.707 [main] DEBUG io.netty.util.NetUtil - Loopback interface: lo (Software Loopback Interface 1, 127.0.0.1)
00:53:32.707 [main] DEBUG io.netty.util.NetUtil - Failed to get SOMAXCONN from sysctl and file \proc\sys\net\core\somaxconn. Default: 200
00:53:32.821 [main] DEBUG io.netty.channel.DefaultChannelId - -Dio.netty.machineId: 0c:9d:92:ff:fe:c6:94:0e (auto-detected)
00:53:32.838 [main] DEBUG io.netty.util.ResourceLeakDetector - -Dio.netty.leakDetection.level: simple
00:53:32.838 [main] DEBUG io.netty.util.ResourceLeakDetector - -Dio.netty.leakDetection.targetRecords: 4
00:53:32.866 [main] DEBUG io.netty.buffer.PooledByteBufAllocator - -Dio.netty.allocator.numHeapArenas: 12
00:53:32.866 [main] DEBUG io.netty.buffer.PooledByteBufAllocator - -Dio.netty.allocator.numDirectArenas: 12
00:53:32.866 [main] DEBUG io.netty.buffer.PooledByteBufAllocator - -Dio.netty.allocator.pageSize: 8192
00:53:32.866 [main] DEBUG io.netty.buffer.PooledByteBufAllocator - -Dio.netty.allocator.maxOrder: 11
00:53:32.866 [main] DEBUG io.netty.buffer.PooledByteBufAllocator - -Dio.netty.allocator.chunkSize: 16777216
00:53:32.866 [main] DEBUG io.netty.buffer.PooledByteBufAllocator - -Dio.netty.allocator.tinyCacheSize: 512
00:53:32.866 [main] DEBUG io.netty.buffer.PooledByteBufAllocator - -Dio.netty.allocator.smallCacheSize: 256
00:53:32.866 [main] DEBUG io.netty.buffer.PooledByteBufAllocator - -Dio.netty.allocator.normalCacheSize: 64
00:53:32.866 [main] DEBUG io.netty.buffer.PooledByteBufAllocator - -Dio.netty.allocator.maxCachedBufferCapacity: 32768
00:53:32.866 [main] DEBUG io.netty.buffer.PooledByteBufAllocator - -Dio.netty.allocator.cacheTrimInterval: 8192
00:53:32.867 [main] DEBUG io.netty.buffer.PooledByteBufAllocator - -Dio.netty.allocator.useCacheForAllThreads: true
00:53:32.874 [main] DEBUG io.netty.buffer.ByteBufUtil - -Dio.netty.allocator.type: pooled
00:53:32.874 [main] DEBUG io.netty.buffer.ByteBufUtil - -Dio.netty.threadLocalDirectBufferSize: 0
00:53:32.875 [main] DEBUG io.netty.buffer.ByteBufUtil - -Dio.netty.maxThreadLocalCharBufferSize: 16384
00:53:32.887 [main] INFO bftsmart.communication.client.netty.NettyClientServerCommunicationSystemClientSide - Connecting to replica 0 at /127.0.0.1:11000
00:53:32.888 [nioEventLoopGroup-2-1] DEBUG bftsmart.communication.client.netty.NettyTOMMessageDecoder - new NettyTOMMessageDecoder!!, isClient=true
00:53:32.894 [nioEventLoopGroup-2-1] INFO bftsmart.communication.client.netty.NettyClientServerCommunicationSystemClientSide - Channel active
00:53:32.897 [main] INFO bftsmart.communication.client.netty.NettyClientServerCommunicationSystemClientSide - Connecting to replica 1 at /127.0.0.1:11010
00:53:32.898 [nioEventLoopGroup-2-2] DEBUG bftsmart.communication.client.netty.NettyTOMMessageDecoder - new NettyTOMMessageDecoder!!, isClient=true
00:53:32.899 [nioEventLoopGroup-2-2] INFO bftsmart.communication.client.netty.NettyClientServerCommunicationSystemClientSide - Channel active
00:53:32.901 [main] INFO bftsmart.communication.client.netty.NettyClientServerCommunicationSystemClientSide - Connecting to replica 2 at /127.0.0.1:11020
00:53:32.902 [nioEventLoopGroup-2-3] DEBUG bftsmart.communication.client.netty.NettyTOMMessageDecoder - new NettyTOMMessageDecoder!!, isClient=true
00:53:32.902 [nioEventLoopGroup-2-3] INFO bftsmart.communication.client.netty.NettyClientServerCommunicationSystemClientSide - Channel active
00:53:32.904 [main] INFO bftsmart.communication.client.netty.NettyClientServerCommunicationSystemClientSide - Connecting to replica 3 at /127.0.0.1:11030
00:53:32.904 [nioEventLoopGroup-2-4] DEBUG bftsmart.communication.client.netty.NettyTOMMessageDecoder - new NettyTOMMessageDecoder!!, isClient=true
00:53:32.905 [nioEventLoopGroup-2-4] INFO bftsmart.communication.client.netty.NettyClientServerCommunicationSystemClientSide - Channel active
Invocation 000:53:32.907 [main] DEBUG bftsmart.communication.client.netty.NettyClientServerCommunicationSystemClientSide - All channel operations completed or timed out
00:53:32.907 [main] DEBUG bftsmart.communication.client.netty.NettyClientServerCommunicationSystemClientSide - Sending request from 1001 with sequence number 0 to [0, 1, 2, 3]
00:53:32.910 [main] DEBUG io.netty.util.Recycler - -Dio.netty.recycler.maxCapacityPerThread: 4096
00:53:32.910 [main] DEBUG io.netty.util.Recycler - -Dio.netty.recycler.maxSharedCapacityFactor: 2
00:53:32.910 [main] DEBUG io.netty.util.Recycler - -Dio.netty.recycler.linkCapacity: 16
00:53:32.910 [main] DEBUG io.netty.util.Recycler - -Dio.netty.recycler.ratio: 8
00:53:32.912 [main] DEBUG bftsmart.tom.ServiceProxy - Sending request (ORDERED_REQUEST) with reqId=0
00:53:32.913 [main] DEBUG bftsmart.tom.ServiceProxy - Expected number of matching replies: 3
00:53:32.921 [nioEventLoopGroup-2-4] DEBUG io.netty.buffer.AbstractByteBuf - -Dio.netty.buffer.checkAccessible: true
00:53:32.921 [nioEventLoopGroup-2-4] DEBUG io.netty.buffer.AbstractByteBuf - -Dio.netty.buffer.checkBounds: true
00:53:32.922 [nioEventLoopGroup-2-4] DEBUG io.netty.util.ResourceLeakDetectorFactory - Loaded default ResourceLeakDetector: io.netty.util.ResourceLeakDetector@6f74be0a
00:53:32.928 [nioEventLoopGroup-2-1] DEBUG bftsmart.communication.client.netty.NettyClientServerCommunicationSystemClientSide - 2 channel operations remaining to complete
00:53:32.929 [nioEventLoopGroup-2-3] DEBUG bftsmart.communication.client.netty.NettyClientServerCommunicationSystemClientSide - 1 channel operations remaining to complete
00:53:32.929 [nioEventLoopGroup-2-4] DEBUG bftsmart.communication.client.netty.NettyClientServerCommunicationSystemClientSide - 0 channel operations remaining to complete
00:53:32.929 [nioEventLoopGroup-2-2] DEBUG bftsmart.communication.client.netty.NettyClientServerCommunicationSystemClientSide - -1 channel operations remaining to complete
00:53:37.031 [nioEventLoopGroup-2-1] DEBUG bftsmart.communication.client.netty.NettyTOMMessageDecoder - Decoded reply from 0 with sequence number 0
00:53:37.031 [nioEventLoopGroup-2-1] DEBUG bftsmart.tom.ServiceProxy - Synchronously received reply from 0 with sequence number 0
00:53:37.031 [nioEventLoopGroup-2-1] DEBUG bftsmart.tom.ServiceProxy - Receiving reply from 0 with reqId:0. Putting on pos=0
00:53:37.031 [nioEventLoopGroup-2-2] DEBUG bftsmart.communication.client.netty.NettyTOMMessageDecoder - Decoded reply from 1 with sequence number 0
00:53:37.031 [nioEventLoopGroup-2-2] DEBUG bftsmart.tom.ServiceProxy - Synchronously received reply from 1 with sequence number 0
00:53:37.031 [nioEventLoopGroup-2-2] DEBUG bftsmart.tom.ServiceProxy - Receiving reply from 1 with reqId:0. Putting on pos=1
00:53:37.031 [nioEventLoopGroup-2-3] DEBUG bftsmart.communication.client.netty.NettyTOMMessageDecoder - Decoded reply from 2 with sequence number 0
00:53:37.031 [nioEventLoopGroup-2-3] DEBUG bftsmart.tom.ServiceProxy - Synchronously received reply from 2 with sequence number 0
00:53:37.031 [nioEventLoopGroup-2-3] DEBUG bftsmart.tom.ServiceProxy - Receiving reply from 2 with reqId:0. Putting on pos=2
00:53:37.032 [main] DEBUG bftsmart.tom.ServiceProxy - Response extracted = [2:868366897:0]
, returned value: 1
Invocation 100:53:37.032 [main] DEBUG bftsmart.communication.client.netty.NettyClientServerCommunicationSystemClientSide - All channel operations completed or timed out
00:53:37.032 [main] DEBUG bftsmart.communication.client.netty.NettyClientServerCommunicationSystemClientSide - Sending request from 1001 with sequence number 1 to [0, 1, 2, 3]
00:53:37.033 [nioEventLoopGroup-2-4] DEBUG bftsmart.communication.client.netty.NettyClientServerCommunicationSystemClientSide - 2 channel operations remaining to complete
00:53:37.033 [main] DEBUG bftsmart.tom.ServiceProxy - Sending request (ORDERED_REQUEST) with reqId=1
00:53:37.033 [main] DEBUG bftsmart.tom.ServiceProxy - Expected number of matching replies: 3
00:53:37.034 [nioEventLoopGroup-2-3] DEBUG bftsmart.communication.client.netty.NettyClientServerCommunicationSystemClientSide - 1 channel operations remaining to complete
00:53:37.034 [nioEventLoopGroup-2-1] DEBUG bftsmart.communication.client.netty.NettyClientServerCommunicationSystemClientSide - 0 channel operations remaining to complete
00:53:37.035 [nioEventLoopGroup-2-2] DEBUG bftsmart.communication.client.netty.NettyClientServerCommunicationSystemClientSide - -1 channel operations remaining to complete
00:53:37.051 [nioEventLoopGroup-2-4] DEBUG bftsmart.communication.client.netty.NettyTOMMessageDecoder - Decoded reply from 3 with sequence number 0
00:53:37.051 [nioEventLoopGroup-2-4] DEBUG bftsmart.tom.ServiceProxy - Synchronously received reply from 3 with sequence number 0
00:53:37.051 [nioEventLoopGroup-2-4] DEBUG bftsmart.tom.ServiceProxy - Ignoring reply from 3 with reqId:0. Currently wait reqId= 1
00:53:37.068 [nioEventLoopGroup-2-1] DEBUG bftsmart.communication.client.netty.NettyTOMMessageDecoder - Decoded reply from 0 with sequence number 1
00:53:37.069 [nioEventLoopGroup-2-1] DEBUG bftsmart.tom.ServiceProxy - Synchronously received reply from 0 with sequence number 1
00:53:37.069 [nioEventLoopGroup-2-1] DEBUG bftsmart.tom.ServiceProxy - Receiving reply from 0 with reqId:1. Putting on pos=0
00:53:37.069 [nioEventLoopGroup-2-3] DEBUG bftsmart.communication.client.netty.NettyTOMMessageDecoder - Decoded reply from 2 with sequence number 1
00:53:37.069 [nioEventLoopGroup-2-3] DEBUG bftsmart.tom.ServiceProxy - Synchronously received reply from 2 with sequence number 1
00:53:37.069 [nioEventLoopGroup-2-3] DEBUG bftsmart.tom.ServiceProxy - Receiving reply from 2 with reqId:1. Putting on pos=2
00:53:37.070 [nioEventLoopGroup-2-4] DEBUG bftsmart.communication.client.netty.NettyTOMMessageDecoder - Decoded reply from 3 with sequence number 1
00:53:37.070 [nioEventLoopGroup-2-4] DEBUG bftsmart.tom.ServiceProxy - Synchronously received reply from 3 with sequence number 1
00:53:37.070 [nioEventLoopGroup-2-4] DEBUG bftsmart.tom.ServiceProxy - Receiving reply from 3 with reqId:1. Putting on pos=3
00:53:37.071 [main] DEBUG bftsmart.tom.ServiceProxy - Response extracted = [3:868366897:1]
, returned value: 2
Invocation 200:53:37.071 [main] DEBUG bftsmart.communication.client.netty.NettyClientServerCommunicationSystemClientSide - All channel operations completed or timed out
00:53:37.071 [main] DEBUG bftsmart.communication.client.netty.NettyClientServerCommunicationSystemClientSide - Sending request from 1001 with sequence number 2 to [0, 1, 2, 3]
00:53:37.071 [main] DEBUG bftsmart.tom.ServiceProxy - Sending request (ORDERED_REQUEST) with reqId=2
00:53:37.071 [main] DEBUG bftsmart.tom.ServiceProxy - Expected number of matching replies: 3
00:53:37.072 [nioEventLoopGroup-2-3] DEBUG bftsmart.communication.client.netty.NettyClientServerCommunicationSystemClientSide - 2 channel operations remaining to complete
00:53:37.072 [nioEventLoopGroup-2-4] DEBUG bftsmart.communication.client.netty.NettyClientServerCommunicationSystemClientSide - 1 channel operations remaining to complete
00:53:37.072 [nioEventLoopGroup-2-1] DEBUG bftsmart.communication.client.netty.NettyClientServerCommunicationSystemClientSide - 0 channel operations remaining to complete
00:53:37.072 [nioEventLoopGroup-2-2] DEBUG bftsmart.communication.client.netty.NettyClientServerCommunicationSystemClientSide - -1 channel operations remaining to complete
00:53:37.072 [nioEventLoopGroup-2-2] DEBUG bftsmart.communication.client.netty.NettyTOMMessageDecoder - Decoded reply from 1 with sequence number 1
00:53:37.072 [nioEventLoopGroup-2-2] DEBUG bftsmart.tom.ServiceProxy - Synchronously received reply from 1 with sequence number 1
00:53:37.072 [nioEventLoopGroup-2-2] DEBUG bftsmart.tom.ServiceProxy - Ignoring reply from 1 with reqId:1. Currently wait reqId= 2
00:53:37.091 [nioEventLoopGroup-2-1] DEBUG bftsmart.communication.client.netty.NettyTOMMessageDecoder - Decoded reply from 0 with sequence number 2
00:53:37.091 [nioEventLoopGroup-2-1] DEBUG bftsmart.tom.ServiceProxy - Synchronously received reply from 0 with sequence number 2
00:53:37.091 [nioEventLoopGroup-2-1] DEBUG bftsmart.tom.ServiceProxy - Receiving reply from 0 with reqId:2. Putting on pos=0
00:53:41.098 [nioEventLoopGroup-2-1] DEBUG bftsmart.communication.client.netty.NettyTOMMessageDecoder - Decoded reply from 0 with sequence number 2
00:53:41.098 [nioEventLoopGroup-2-1] DEBUG bftsmart.tom.ServiceProxy - Synchronously received reply from 0 with sequence number 2
00:53:41.099 [nioEventLoopGroup-2-1] DEBUG bftsmart.tom.ServiceProxy - Receiving reply from 0 with reqId:2. Putting on pos=0
00:53:41.128 [nioEventLoopGroup-2-4] DEBUG bftsmart.communication.client.netty.NettyTOMMessageDecoder - Decoded reply from 3 with sequence number 2
00:53:41.128 [nioEventLoopGroup-2-4] DEBUG bftsmart.tom.ServiceProxy - Synchronously received reply from 3 with sequence number 2
00:53:41.128 [nioEventLoopGroup-2-4] DEBUG bftsmart.tom.ServiceProxy - Receiving reply from 3 with reqId:2. Putting on pos=3
00:53:41.134 [nioEventLoopGroup-2-2] DEBUG bftsmart.communication.client.netty.NettyTOMMessageDecoder - Decoded reply from 1 with sequence number 2
00:53:41.135 [nioEventLoopGroup-2-2] DEBUG bftsmart.tom.ServiceProxy - Synchronously received reply from 1 with sequence number 2
00:53:41.135 [nioEventLoopGroup-2-2] DEBUG bftsmart.tom.ServiceProxy - Receiving reply from 1 with reqId:2. Putting on pos=1
00:53:41.135 [main] DEBUG bftsmart.tom.ServiceProxy - Response extracted = [1:868366897:2]
, returned value: 3
Invocation 300:53:41.136 [main] DEBUG bftsmart.communication.client.netty.NettyClientServerCommunicationSystemClientSide - All channel operations completed or timed out
00:53:41.136 [main] DEBUG bftsmart.communication.client.netty.NettyClientServerCommunicationSystemClientSide - Sending request from 1001 with sequence number 3 to [0, 1, 2, 3]
00:53:41.136 [main] DEBUG bftsmart.tom.ServiceProxy - Sending request (ORDERED_REQUEST) with reqId=3
00:53:41.137 [main] DEBUG bftsmart.tom.ServiceProxy - Expected number of matching replies: 3
00:53:41.138 [nioEventLoopGroup-2-3] DEBUG bftsmart.communication.client.netty.NettyClientServerCommunicationSystemClientSide - 2 channel operations remaining to complete
00:53:41.138 [nioEventLoopGroup-2-4] DEBUG bftsmart.communication.client.netty.NettyClientServerCommunicationSystemClientSide - 1 channel operations remaining to complete
00:53:41.138 [nioEventLoopGroup-2-1] DEBUG bftsmart.communication.client.netty.NettyClientServerCommunicationSystemClientSide - 0 channel operations remaining to complete
00:53:41.138 [nioEventLoopGroup-2-2] DEBUG bftsmart.communication.client.netty.NettyClientServerCommunicationSystemClientSide - -1 channel operations remaining to complete
00:53:41.141 [nioEventLoopGroup-2-3] DEBUG bftsmart.communication.client.netty.NettyTOMMessageDecoder - Decoded reply from 2 with sequence number 2
00:53:41.141 [nioEventLoopGroup-2-3] DEBUG bftsmart.tom.ServiceProxy - Synchronously received reply from 2 with sequence number 2
00:53:41.141 [nioEventLoopGroup-2-3] DEBUG bftsmart.tom.ServiceProxy - Ignoring reply from 2 with reqId:2. Currently wait reqId= 3
00:53:41.147 [nioEventLoopGroup-2-1] DEBUG bftsmart.communication.client.netty.NettyTOMMessageDecoder - Decoded reply from 0 with sequence number 2
00:53:41.147 [nioEventLoopGroup-2-1] DEBUG bftsmart.tom.ServiceProxy - Synchronously received reply from 0 with sequence number 2
00:53:41.147 [nioEventLoopGroup-2-1] DEBUG bftsmart.tom.ServiceProxy - Ignoring reply from 0 with reqId:2. Currently wait reqId= 3
00:53:41.147 [nioEventLoopGroup-2-4] DEBUG bftsmart.communication.client.netty.NettyTOMMessageDecoder - Decoded reply from 3 with sequence number 2
00:53:41.148 [nioEventLoopGroup-2-4] DEBUG bftsmart.tom.ServiceProxy - Synchronously received reply from 3 with sequence number 2
00:53:41.148 [nioEventLoopGroup-2-4] DEBUG bftsmart.tom.ServiceProxy - Ignoring reply from 3 with reqId:2. Currently wait reqId= 3
00:53:41.149 [nioEventLoopGroup-2-2] DEBUG bftsmart.communication.client.netty.NettyTOMMessageDecoder - Decoded reply from 1 with sequence number 2
00:53:41.149 [nioEventLoopGroup-2-3] DEBUG bftsmart.communication.client.netty.NettyTOMMessageDecoder - Decoded reply from 2 with sequence number 2
00:53:41.149 [nioEventLoopGroup-2-2] DEBUG bftsmart.tom.ServiceProxy - Synchronously received reply from 1 with sequence number 2
00:53:41.149 [nioEventLoopGroup-2-3] DEBUG bftsmart.tom.ServiceProxy - Synchronously received reply from 2 with sequence number 2
00:53:41.149 [nioEventLoopGroup-2-2] DEBUG bftsmart.tom.ServiceProxy - Ignoring reply from 1 with reqId:2. Currently wait reqId= 3
00:53:41.149 [nioEventLoopGroup-2-3] DEBUG bftsmart.tom.ServiceProxy - Ignoring reply from 2 with reqId:2. Currently wait reqId= 3
00:53:41.167 [nioEventLoopGroup-2-1] DEBUG bftsmart.communication.client.netty.NettyTOMMessageDecoder - Decoded reply from 0 with sequence number 3
00:53:41.167 [nioEventLoopGroup-2-1] DEBUG bftsmart.tom.ServiceProxy - Synchronously received reply from 0 with sequence number 3
00:53:41.167 [nioEventLoopGroup-2-1] DEBUG bftsmart.tom.ServiceProxy - Receiving reply from 0 with reqId:3. Putting on pos=0
00:53:45.164 [nioEventLoopGroup-2-1] DEBUG bftsmart.communication.client.netty.NettyTOMMessageDecoder - Decoded reply from 0 with sequence number 3
00:53:45.164 [nioEventLoopGroup-2-1] DEBUG bftsmart.tom.ServiceProxy - Synchronously received reply from 0 with sequence number 3
00:53:45.164 [nioEventLoopGroup-2-1] DEBUG bftsmart.tom.ServiceProxy - Receiving reply from 0 with reqId:3. Putting on pos=0
00:53:45.175 [nioEventLoopGroup-2-4] DEBUG bftsmart.communication.client.netty.NettyTOMMessageDecoder - Decoded reply from 3 with sequence number 3
00:53:45.176 [nioEventLoopGroup-2-4] DEBUG bftsmart.tom.ServiceProxy - Synchronously received reply from 3 with sequence number 3
00:53:45.176 [nioEventLoopGroup-2-4] DEBUG bftsmart.tom.ServiceProxy - Receiving reply from 3 with reqId:3. Putting on pos=3
00:53:47.181 [nioEventLoopGroup-2-1] DEBUG bftsmart.communication.client.netty.NettyTOMMessageDecoder - Decoded reply from 0 with sequence number 3
00:53:47.182 [nioEventLoopGroup-2-1] DEBUG bftsmart.tom.ServiceProxy - Synchronously received reply from 0 with sequence number 3
00:53:47.182 [nioEventLoopGroup-2-1] DEBUG bftsmart.tom.ServiceProxy - Receiving reply from 0 with reqId:3. Putting on pos=0
00:53:47.182 [nioEventLoopGroup-2-1] DEBUG bftsmart.communication.client.netty.NettyTOMMessageDecoder - Decoded reply from 0 with sequence number 3
00:53:47.182 [nioEventLoopGroup-2-1] DEBUG bftsmart.tom.ServiceProxy - Synchronously received reply from 0 with sequence number 3
00:53:47.182 [nioEventLoopGroup-2-1] DEBUG bftsmart.tom.ServiceProxy - Receiving reply from 0 with reqId:3. Putting on pos=0
00:53:49.206 [nioEventLoopGroup-2-1] DEBUG bftsmart.communication.client.netty.NettyTOMMessageDecoder - Decoded reply from 0 with sequence number 3
00:53:49.206 [nioEventLoopGroup-2-1] DEBUG bftsmart.tom.ServiceProxy - Synchronously received reply from 0 with sequence number 3
00:53:49.206 [nioEventLoopGroup-2-1] DEBUG bftsmart.tom.ServiceProxy - Receiving reply from 0 with reqId:3. Putting on pos=0
00:53:49.206 [nioEventLoopGroup-2-1] DEBUG bftsmart.communication.client.netty.NettyTOMMessageDecoder - Decoded reply from 0 with sequence number 3
00:53:49.206 [nioEventLoopGroup-2-1] DEBUG bftsmart.tom.ServiceProxy - Synchronously received reply from 0 with sequence number 3
00:53:49.206 [nioEventLoopGroup-2-1] DEBUG bftsmart.tom.ServiceProxy - Receiving reply from 0 with reqId:3. Putting on pos=0
00:53:49.219 [nioEventLoopGroup-2-2] DEBUG bftsmart.communication.client.netty.NettyTOMMessageDecoder - Decoded reply from 1 with sequence number 3
00:53:49.219 [nioEventLoopGroup-2-2] DEBUG bftsmart.tom.ServiceProxy - Synchronously received reply from 1 with sequence number 3
00:53:49.219 [nioEventLoopGroup-2-2] DEBUG bftsmart.tom.ServiceProxy - Receiving reply from 1 with reqId:3. Putting on pos=1
00:53:49.221 [main] DEBUG bftsmart.tom.ServiceProxy - Response extracted = [1:868366897:3]
, returned value: 5
Invocation 400:53:49.221 [main] DEBUG bftsmart.communication.client.netty.NettyClientServerCommunicationSystemClientSide - All channel operations completed or timed out
00:53:49.221 [main] DEBUG bftsmart.communication.client.netty.NettyClientServerCommunicationSystemClientSide - Sending request from 1001 with sequence number 4 to [0, 1, 2, 3]
00:53:49.223 [nioEventLoopGroup-2-4] DEBUG bftsmart.communication.client.netty.NettyClientServerCommunicationSystemClientSide - 2 channel operations remaining to complete
00:53:49.223 [main] DEBUG bftsmart.tom.ServiceProxy - Sending request (ORDERED_REQUEST) with reqId=4
00:53:49.223 [main] DEBUG bftsmart.tom.ServiceProxy - Expected number of matching replies: 3
00:53:49.224 [nioEventLoopGroup-2-2] DEBUG bftsmart.communication.client.netty.NettyClientServerCommunicationSystemClientSide - 1 channel operations remaining to complete
00:53:49.224 [nioEventLoopGroup-2-1] DEBUG bftsmart.communication.client.netty.NettyClientServerCommunicationSystemClientSide - 0 channel operations remaining to complete
00:53:49.224 [nioEventLoopGroup-2-3] DEBUG bftsmart.communication.client.netty.NettyClientServerCommunicationSystemClientSide - -1 channel operations remaining to complete
00:53:53.249 [nioEventLoopGroup-2-1] DEBUG bftsmart.communication.client.netty.NettyTOMMessageDecoder - Decoded reply from 0 with sequence number 3
00:53:53.249 [nioEventLoopGroup-2-1] DEBUG bftsmart.tom.ServiceProxy - Synchronously received reply from 0 with sequence number 3
00:53:53.249 [nioEventLoopGroup-2-1] DEBUG bftsmart.tom.ServiceProxy - Ignoring reply from 0 with reqId:3. Currently wait reqId= 4
00:53:53.249 [nioEventLoopGroup-2-1] DEBUG bftsmart.communication.client.netty.NettyTOMMessageDecoder - Decoded reply from 0 with sequence number 3
00:53:53.250 [nioEventLoopGroup-2-1] DEBUG bftsmart.tom.ServiceProxy - Synchronously received reply from 0 with sequence number 3
00:53:53.250 [nioEventLoopGroup-2-1] DEBUG bftsmart.tom.ServiceProxy - Ignoring reply from 0 with reqId:3. Currently wait reqId= 4
00:53:53.250 [nioEventLoopGroup-2-1] DEBUG bftsmart.communication.client.netty.NettyTOMMessageDecoder - Decoded reply from 0 with sequence number 3
00:53:53.250 [nioEventLoopGroup-2-1] DEBUG bftsmart.tom.ServiceProxy - Synchronously received reply from 0 with sequence number 3
00:53:53.250 [nioEventLoopGroup-2-1] DEBUG bftsmart.tom.ServiceProxy - Ignoring reply from 0 with reqId:3. Currently wait reqId= 4
00:53:53.260 [nioEventLoopGroup-2-3] DEBUG bftsmart.communication.client.netty.NettyTOMMessageDecoder - Decoded reply from 2 with sequence number 3
00:53:53.260 [nioEventLoopGroup-2-3] DEBUG bftsmart.tom.ServiceProxy - Synchronously received reply from 2 with sequence number 3
00:53:53.260 [nioEventLoopGroup-2-3] DEBUG bftsmart.tom.ServiceProxy - Ignoring reply from 2 with reqId:3. Currently wait reqId= 4
00:54:29.224 [main] INFO bftsmart.tom.ServiceProxy - ###################TIMEOUT#######################
00:54:29.224 [main] INFO bftsmart.tom.ServiceProxy - Reply timeout for reqId=4, Replies received: 0
, ERROR! Exiting.
```
