# ID Generator

Each unique short URL is a **Base62** encoded version of **Snowflake IDs**.

In this approach, the longest possible short URL is 11 characters. The largest Snowflake ID value, without extracting the start epoch, is **2^63 - 1**. The Base62 encoded value of **2^63 - 1** becomes **AzL8n0Y58m7**.

## Snowflake ID

[Snowflake ID](https://en.wikipedia.org/wiki/Snowflake_ID) is a method used to generate unique IDs in distributed systems. It was created by X.

With the Snowflake ID approach, there’s no need to use a persistent counter to generate unique IDs or check if the generated ID already exists in the database.

- It achieves this by using a millisecond-level timestamp.
- IDs generated within the same millisecond are made unique using sequence bits.
- Machine bits and datacenter bits ensure different machines do not generate the same IDs.
- On the same machine and in the same datacenter, up to 4095 unique IDs can be generated within a single millisecond. If more than 4095 IDs are generated in one millisecond, the system will sleep and wait for the next millisecond.

However, it is important to ensure that machine IDs are not duplicated. Every machine created for generating Snowflake IDs must be assigned a unique ID. Furthermore, when a machine is destroyed, the ID assigned to it must be freed.

**Using Zookeeper for Machine ID Mapping**

In X's [own implementation](https://github.com/twitter-archive/snowflake/releases/tag/snowflake-2010), **Zookeeper** is used to manage the mapping.

`SnowflakeServer.scala` coordinates machine IDs and ensuring uniqueness across distributed systems through `ZooKeeperClient`.

- Before creating the IdWorker instance, it calls the `registerWorkerId()` method to register the corresponding worker ID in Zookeeper.
    - For instance, if the worker ID = 1, it creates metadata that holds the worker ID (1) and the host and port information of the worker at the path `workerIdZkPath/1`. This prevents another IdWorker instance from being created with worker ID = 1.
- Additionally, it performs a `sanityCheck` to verify if the IdWorker machines are up and running. To do this, it transfers the metadata in **Zookeeper** to a HashMap called `peers`. The structure of the HashMap is as follows: the key is the machine ID, and the value is a pair of hostname and port of the machine. Then, it traverses this HashMap and tries to establish a network connection with each machine to check their status.

**Terms**

- worker ID = machine ID
- `IdWorker` = (`IdGenerator` in Link Converter): Code that handles Snowflake ID generation
- `workerIdZkPath`: The parent path where the Zookeeper records are kept
- `<workerIdZkPath>/1`: The metadata file/folder that holds the host and port corresponding to worker ID = 1

## Snowflake ID in Link Converter

A Spring Boot instance in the Link Converter app creates short URLs and retrieves them.

The datacenter ID is always 0. **Note:** Datacenter and machine ID bits can be combined and used as the machine ID in 10 bits.

`IdGenerator` is the class that implements the Snowflake ID approach. It creates unique IDs.

`IdGeneratorProvider.createIdGenerator()` assigns a machine ID to the app(in other words, the machine) and creates an IdGenerator instance with that ID.

### Snowflake ID Generator on a Single Spring Boot Instance

If the application is running locally or in docker-compose environment, the machine id is assigned as 0 because there will be only one instance.

Since the environment variable named `STATEFULSET_POD_NAME` is not defined, the application cannot find it and sets the machine ID to 0.

### Snowflake ID Generator on a Kubernetes Cluster

A pod that runs a Spring Boot app represents a single machine.

In a cluster, Spring Boot instances are dynamically created or destroyed by the HPA. Each of these instances must be uniquely mapped to numbers between [0, 31]. 5 bits are used to represent machine IDs, so the highest machine ID is **2^5 - 1 = 31**.

**Using StatefulSets for Machine ID Mapping**

Can this mapping be done by leveraging Kubernetes without using an additional tool like Zookeeper?

Pod names are passed as environment variables to each Spring Boot instance. However, this is difficult to do when Deployment workloads are used because pod names are randomized. This is where StatefulSets come into play. Each pod in the StatefulSet has a unique ID, which is based on the [ordinal index](https://kubernetes.io/docs/tutorials/stateful-application/basic-stateful-set/#pods-in-a-statefulset) set by the StatefulSet controller.

Therefore, one can expect pods running Spring Boot instances to have the following names each time:

```text
link-converter-app-0
link-converter-app-1
link-converter-app-2
```

When HPA adds a new one, it will be `link-converter-app-3`:

```text
link-converter-app-0
link-converter-app-1
link-converter-app-2
link-converter-app-3
```

When HPA deletes two of these pods, `link-converter-app-2` and `link-converter-app-3` are deleted.

As a result, thanks to the StatefulSet, the name of each created pod can be passed to Spring Boot instances. Then, each application can extract the corresponding ordinal index and assign it as the machine ID in `IdGenerator`.

**Note**: This is a simple and minimal approach. It has not been extensively tested. For example, scenarios like updating StatefulSets have not been tested. Additionally, for more comprehensive management, solutions like Zookeeper, as implemented in X's repository, may be required.