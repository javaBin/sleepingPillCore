package no.java.sleepingpill.core.event;

import java.util.List;
import java.util.UUID;

public class DummyArrangedEventHolder implements ArrangedEventHolder {
    @Override
    public List<ArrangedEvent> allArrangedEvents() {
        return null;
    }

    public static void main(String[] args) throws Exception {
        System.out.println(UUID.randomUUID().toString());
    }
}
