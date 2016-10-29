package no.java.sleepingpill.core.event;

import java.util.Collections;
import java.util.List;

public class DummyConferenceHolder implements ConferenceHolder {
    @Override
    public List<Conference> allConferences() {
        return Collections.singletonList(new Conference("6c599656fdd846468bbcab66cfffbbc0","JavaZone 2017","javazone2017"));
    }

}
