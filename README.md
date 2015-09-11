flex
======

Flex is a simple protocol/framework with client and server jar libraries to
transfer low-latency high-throughput data between applications by using
simple Map (get/put) interface. Logically divides data in to storages, each storage has a name.


### Specs

* Fully asynchronous, based on Netty (NIO), can handle up to 30000 client connections per server
* Fully protobuf, can support heterogeneous C++/C#/Nodejs/etc clients
* Synchronous and asynchronous interfaces in Java client
* Supports batching and group operations: getAll, putAll, removeAll
* Supports synchronous and asynchronous data storages on server side
* All operations have timeout for SLA
* Keys are always Strings, values are always ByteString (byte[])
* Supports heart breath protocol to detect alive connection
* Reconnects automatically 

### Example

Client side example:

```
    ByteString value = ByteString.copyFrom("bytes".getBytes());

    FlexClient client = FlexClient.newBuilder().setHost("localhost").setPort(3333).build();
    
    client.start();

    while(!client.isActive()) {
      Thread.sleep(1);
    }

    System.out.println("connected, available stores=" + client.getStoreNames());
    
    FlexStore store = client.getStore("test");
    
    store.put("key", value).sync();
    
    // get result synchronous
    ByteString result = store.get("key").sync();
    
    // get result asynchronous
    final ListenableFuture<ByteString> future = store.get("key").async();
	
    // get with listener
    future.addListener(new Runnable() {

	    @Override
		public void run() {
				
			try {
				ByteString val = future.get();
				System.out.println("async result = " + val.toStringUtf8());

			} catch (Exception e) {
				e.printStackTrace();
			}
				
		}
			
	}, client.getDefaultExecutor());    
    
    // cancel operation on client and server side
    future.cancel(true);
```

Server side example:
```
    FlexServer server = FlexServer.newBuilder().setPort(3333).build();
    
    server.addStore("test", new SimpleHeapStore());
    
    server.start();
    
    server.join();
```



