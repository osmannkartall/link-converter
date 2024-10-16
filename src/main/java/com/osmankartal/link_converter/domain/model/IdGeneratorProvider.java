package com.osmankartal.link_converter.domain.model;


import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class IdGeneratorProvider {

    public IdGenerator createIdGenerator() throws IllegalArgumentException {
        long machineId = assignMachineId();
        System.out.println("IdGeneratorProvider - machine id: " + machineId);
        return new IdGenerator(0, machineId, 0);
    }

    private long assignMachineId() {
        String podName = getStatefulsetPodName();

        if (Objects.isNull(podName)) {
            System.out.println("IdGeneratorProvider - running for only single instance");
            return 0;
        }

        String[] parts = podName.split("-");

        if (parts.length < 2) {
            throw new IllegalArgumentException("cannot extract machine id");
        }

        try {
            long machinedId = Long.parseLong(parts[parts.length-1]);

            if (machinedId < 0 || machinedId >= IdGenerator.MAX_MACHINE_ID) {
                throw new IllegalArgumentException(String.format("Invalid machineId: %d . It must be in [%d and %d)%n", machinedId, 0, IdGenerator.MAX_MACHINE_ID));
            }

            return machinedId;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("machine id must be number");
        }
    }

    protected String getStatefulsetPodName() {
        return System.getenv("STATEFULSET_POD_NAME");
    }

}
