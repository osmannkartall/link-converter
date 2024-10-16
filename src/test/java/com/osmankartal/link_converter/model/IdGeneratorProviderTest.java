package com.osmankartal.link_converter.model;

import com.osmankartal.link_converter.domain.model.IdGeneratorProvider;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IdGeneratorProviderTest {

    static class IdGeneratorProviderForTest extends IdGeneratorProvider {

        String statefulsetPodName = "";

        @Override
        public String getStatefulsetPodName() {
            return statefulsetPodName;
        }
    }

    private final IdGeneratorProviderForTest idGeneratorProviderForTest = new IdGeneratorProviderForTest();

    @Test
    void getMachineIdForSingleInstance() {
        idGeneratorProviderForTest.statefulsetPodName = null;
        var idGenerator = idGeneratorProviderForTest.createIdGenerator();
        assertEquals(0, idGenerator.getMachinedId());
    }

    @Test
    void throwExceptionWhenPodOrdinalIndexIsNotFound() {
        idGeneratorProviderForTest.statefulsetPodName = "incorrectpodname";
        assertThrows(IllegalArgumentException.class, idGeneratorProviderForTest::createIdGenerator);
    }

    @Test
    void throwExceptionWhenPodOrdinalIndexIsNotNumber() {
        idGeneratorProviderForTest.statefulsetPodName = "link-converter-id1";
        assertThrows(IllegalArgumentException.class, idGeneratorProviderForTest::createIdGenerator);
    }

    @Test
    void getMachineIdForPod() {
        idGeneratorProviderForTest.statefulsetPodName = "link-converter-5";
        var idGenerator = idGeneratorProviderForTest.createIdGenerator();
        assertEquals(5, idGenerator.getMachinedId());
    }
}
