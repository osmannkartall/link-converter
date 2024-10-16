package com.osmankartal.link_converter.domain.model;


import lombok.Getter;
import lombok.Setter;

public class IdGenerator {
    // This code is adapted from https://github.com/twitter-archive/snowflake/releases/tag/snowflake-2010

    public static final long START_EPOCH = 1288834974657L;

    // top zero bit(1) - timestamp bits(41) - datacenter id bits(5) - machined id bits(5) - sequence bits(12)
    private static final int DATACENTER_ID_BITS = 5;
    private static final int MACHINE_ID_BITS = 5;
    private static final int SEQUENCE_BITS = 12;

    public static final long MAX_DATACENTER_ID = (1L << DATACENTER_ID_BITS) - 1;
    public static final long MAX_MACHINE_ID = (1L << MACHINE_ID_BITS) - 1;
    public static final long MAX_SEQUENCE = (1L << SEQUENCE_BITS) - 1;

    private static final int NUM_BIT_SHIFT_TO_EXTRACT_TIMESTAMP = DATACENTER_ID_BITS + MACHINE_ID_BITS + SEQUENCE_BITS;
    private static final int NUM_BIT_SHIFT_TO_EXTRACT_DATACENTER_ID = MACHINE_ID_BITS + SEQUENCE_BITS;
    private static final int NUM_BIT_SHIFT_TO_EXTRACT_MACHINE_ID = SEQUENCE_BITS;

    // define length:
    //      Create the binary number consisting of only 1s, with the given number of bits
    //      (Or, simply set to the largest unsigned integer with this bit length).
    // define position:
    //      Then, perform the necessary bit shift to extract the specific part.
    private static final long DATACENTER_ID_MASK = MAX_DATACENTER_ID << NUM_BIT_SHIFT_TO_EXTRACT_DATACENTER_ID;         // 0b0001111100000000000000000
    private static final long MACHINE_ID_MASK = MAX_MACHINE_ID << NUM_BIT_SHIFT_TO_EXTRACT_MACHINE_ID;                  // 0b00011111000000000000
    private static final long SEQUENCE_MASK = MAX_SEQUENCE;                                                             // 0b111111111111

    private long lastTimestamp = -1L;

    @Getter
    private final long machinedId;

    @Getter
    private final long datacenterId;

    @Setter
    @Getter
    private long sequence;

    public IdGenerator(long datacenterId, long machinedId, long sequence) {
        if (machinedId > MAX_MACHINE_ID || machinedId < 0) {
            throw new IllegalArgumentException(String.format("Invalid machineId: %d. It must be in [%d and %d)", machinedId, 0, MAX_MACHINE_ID));
        }

        if (datacenterId > MAX_DATACENTER_ID || datacenterId < 0) {
            throw new IllegalArgumentException(String.format("Invalid datacenterId: %d. It must be in [%d and %d)", datacenterId, 0, MAX_DATACENTER_ID));
        }

        this.datacenterId = datacenterId;
        this.machinedId = machinedId;
        this.sequence = sequence;
    }

    public synchronized long nextId() {
        var timestamp = timeGen();

        if (timestamp < lastTimestamp) {
            throw new IllegalStateException(String.format("System clock moved backward. timestamp: %d, lastTimeStamp: %d", timestamp, lastTimestamp));
        }

        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if (sequence == 0) {
                timestamp = tillNextMillis(timestamp);
            }
        } else {
            sequence = 0;
        }

        lastTimestamp = timestamp;

        return ((timestamp - START_EPOCH) << NUM_BIT_SHIFT_TO_EXTRACT_TIMESTAMP)
                | (datacenterId << NUM_BIT_SHIFT_TO_EXTRACT_DATACENTER_ID)
                | (machinedId << NUM_BIT_SHIFT_TO_EXTRACT_MACHINE_ID)
                | sequence;
    }

    public long retrieveTimestamp(long id) {
        return (id >> NUM_BIT_SHIFT_TO_EXTRACT_TIMESTAMP) + START_EPOCH;
    }

    public long retrieveDatacenterId(long id) {
        return (id & DATACENTER_ID_MASK) >> NUM_BIT_SHIFT_TO_EXTRACT_DATACENTER_ID;
    }

    public long retrieveMachineId(long id) {
        return (id & MACHINE_ID_MASK) >> NUM_BIT_SHIFT_TO_EXTRACT_MACHINE_ID;
    }

    public long retrieveSequence(long id) {
        return id & SEQUENCE_MASK;
    }

    protected long timeGen() {
        return System.currentTimeMillis();
    }

    protected long tillNextMillis(long lastTimestamp) {
        var timestamp = timeGen();
        while (timestamp == lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    public long getTimestamp() {
        return System.currentTimeMillis();
    }

}
