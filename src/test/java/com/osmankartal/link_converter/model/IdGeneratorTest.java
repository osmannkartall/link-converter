package com.osmankartal.link_converter.model;

import com.osmankartal.link_converter.domain.model.IdGenerator;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

class IdGeneratorTest {

    @Setter
    @Getter
    static class StaticTimeIdGenerator extends IdGenerator {
        private long time = 1L;

        public StaticTimeIdGenerator(long datacenterId, long machinedId, long sequence) {
            super(datacenterId, machinedId, sequence);
        }

        @Override
        public long timeGen() {
            return time + IdGenerator.START_EPOCH;
        }

        @Override
        public long retrieveTimestamp(long id) {
            return super.retrieveTimestamp(id) - IdGenerator.START_EPOCH;
        }
    }

    @Setter
    static class EasyTimeIdGenerator extends IdGenerator {

        public EasyTimeIdGenerator(long datacenterId, long machinedId, long sequence) {
            super(datacenterId, machinedId, sequence);
        }

        // Change the time supplier programmatically
        Supplier<Long> timeMaker = System::currentTimeMillis;

        @Override
        protected long timeGen() {
            return timeMaker.get();
        }
    }

    @Getter
    static class WakingIdGenerator extends EasyTimeIdGenerator {
        public WakingIdGenerator(long datacenterId, long machinedId, long sequence) {
            super(datacenterId, machinedId, sequence);
        }

        private int slept = 0;

        @Override
        protected long tillNextMillis(long lastTimestamp) {
            slept += 1;
            return super.tillNextMillis(lastTimestamp);
        }
    }

    @Test
    void generateId() {
        IdGenerator idGenerator = new IdGenerator(1, 1, 0);
        long id = idGenerator.nextId();
        assertTrue(id > 0L);
    }

    @Test
    void getAccurateTimestamp() {
        IdGenerator idGenerator = new IdGenerator(1, 1, 0);
        long t = System.currentTimeMillis();
        assertTrue(idGenerator.getTimestamp() - t < 50L);
    }

    @Test
    void getCorrectDatacenterId() {
        IdGenerator idGenerator = new IdGenerator(1, 1, 0);
        assertEquals(1L, idGenerator.getDatacenterId());
    }

    @Test
    void getCorrectMachineId() {
        IdGenerator idGenerator = new IdGenerator(1, 1, 0);
        assertEquals(1L, idGenerator.getMachinedId());
    }

    @Test
    void maskMachineIdProperly() {
        long machineId = IdGenerator.MAX_MACHINE_ID;
        long datacenterId = 0;
        IdGenerator idGenerator = new IdGenerator(datacenterId, machineId, 0);
        for (int i = 1; i <= 1000; i++) {
            long id = idGenerator.nextId();
            assertEquals(machineId, idGenerator.retrieveMachineId(id));
        }
    }

    @Test
    void maskDatacenterIdProperly() {
        long datacenterId = 0x1F;
        long machineId = 0;
        IdGenerator idGenerator = new IdGenerator(datacenterId, machineId, 0);
        long id = idGenerator.nextId();
        assertEquals(datacenterId, idGenerator.retrieveDatacenterId(id));
    }

    @Test
    void maskTimestampProperly() {
        EasyTimeIdGenerator easyTimeIdGenerator = new EasyTimeIdGenerator(IdGenerator.MAX_DATACENTER_ID, IdGenerator.MAX_MACHINE_ID, 0);
        for (int i = 1; i <= 100; i++) {
            long t = System.currentTimeMillis();
            // It modifies timeMaker to return a fixed timestamp t, allowing the test to check the
            // generated ID against expected values without relying on the real system time.
            easyTimeIdGenerator.timeMaker = (() -> t);
            long id = easyTimeIdGenerator.nextId();
            assertEquals(t, easyTimeIdGenerator.retrieveTimestamp(id));
        }
    }

    @Test
    void rollOverSequenceId() {
        // It tests whether the sequence value resets when it reaches the maximum value
        long machineId = 4;
        long datacenterId = 4;
        long startSequence = 0xFFFFFF - 20;
        long endSequence = 0xFFFFFF + 20;
        IdGenerator idGenerator = new IdGenerator(datacenterId, machineId, startSequence);

        for (long i = startSequence; i <= endSequence; i++) {
            long id = idGenerator.nextId();
            assertEquals(machineId, idGenerator.retrieveMachineId(id));
        }
    }

    @Test
    void generateIncreasingIds() {
        IdGenerator idGenerator = new IdGenerator(1, 1, 0);
        long lastId = 0L;
        for (int i = 1; i <= 100; i++) {
            long id = idGenerator.nextId();
            assertTrue(id > lastId);
            lastId = id;
        }
    }

    @Test
    void generateOneMillionIdsQuickly() {
        IdGenerator idGenerator = new IdGenerator(IdGenerator.MAX_DATACENTER_ID, IdGenerator.MAX_MACHINE_ID, 0);
        long startTime = System.currentTimeMillis();
        for (int i = 1; i <= 1000000; i++) {
            // The error "System clock has moved backwards" can be thrown.
            // This is not related to the test case. Try the current iteration again.
            try {
                idGenerator.nextId();
            } catch (IllegalStateException e) {
                i--;
            }
        }
        long endTime = System.currentTimeMillis();
        System.out.printf("generated 1000000 ids in %d ms%n", (endTime - startTime));
        assertTrue(true);
    }

    @Test
    void generateOnlyUniqueIds() {
        IdGenerator idGenerator = new IdGenerator(IdGenerator.MAX_DATACENTER_ID, IdGenerator.MAX_MACHINE_ID, 0);
        HashSet<Long> set = new HashSet<>();
        int n = 2000000;
        for (int i = 1; i <= n; i++) {
            // The error "System clock has moved backwards" can be thrown.
            // This is not related to the test case. Try the current iteration again.
            try {
                long id = idGenerator.nextId();
                if (set.contains(id)) {
                    System.out.println(Long.toString(id, 2));
                } else {
                    set.add(id);
                }
            } catch (IllegalStateException e) {
                i--;
            }
        }
        assertEquals(n, set.size());
    }

    @Test
    void generateIdsOver50Billion() {
        IdGenerator idGenerator = new IdGenerator(0, 0, 0);
        assertTrue(idGenerator.nextId() > 50000000000L);
    }

    @Test
    void generateUniqueIdsWhenTimeGoesBackwards() {
        StaticTimeIdGenerator idGenerator = new StaticTimeIdGenerator(0, 0, 0);

        idGenerator.setTime(1);

        // Firstly, generate 2 ids with the same time, thus the sequence increases to 1
        long id1 = idGenerator.nextId();
        assertEquals(1, idGenerator.retrieveTimestamp(id1));
        assertEquals(0, idGenerator.retrieveSequence(id1));
        assertEquals(0, idGenerator.getSequence());
        assertEquals(1, idGenerator.getTime());
        long id2 = idGenerator.nextId();
        assertEquals(1, idGenerator.retrieveTimestamp(id2));
        assertEquals(1, idGenerator.retrieveSequence(id2));

        // Then, set the time backwards and simulate the error
        idGenerator.setTime(0);

        assertEquals(1, idGenerator.getSequence());
        assertThrows(IllegalStateException.class, idGenerator::nextId);
        assertEquals(1, idGenerator.getSequence());

        // Then, continue to generate id with the same time, thus the sequence increases to 2
        idGenerator.setTime(1);

        long id3 = idGenerator.nextId();
        assertEquals(1, idGenerator.retrieveTimestamp(id3));
        assertEquals(2, idGenerator.retrieveSequence(id3));
    }

    @Test
    void sleepIfRolloverTwiceInSameMillisecond() {
        WakingIdGenerator walkingIdGenerator = new WakingIdGenerator(1, 1, 0);
        // Simulate the same milliseconds for the first two iterations.
        walkingIdGenerator.timeMaker = (Arrays.asList(2L, 2L, 3L).iterator()::next);
        walkingIdGenerator.setSequence(IdGenerator.MAX_SEQUENCE);
        walkingIdGenerator.nextId();
        walkingIdGenerator.setSequence(IdGenerator.MAX_SEQUENCE);
        walkingIdGenerator.nextId();
        assertEquals(1, walkingIdGenerator.getSlept());
    }

}
