package no.java.sleepingpill.core.event;

import java.util.Collections;
import java.util.List;

public class DummyArrangedEventHolder implements ArrangedEventHolder {
    @Override
    public List<ArrangedEvent> allArrangedEvents() {
        return Collections.singletonList(new ArrangedEvent("6c599656fdd846468bbcab66cfffbbc0","JavaZone 2017","javazone2017"));
    }

}
