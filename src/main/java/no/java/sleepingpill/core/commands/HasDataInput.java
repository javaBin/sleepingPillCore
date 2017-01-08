package no.java.sleepingpill.core.commands;

import no.java.sleepingpill.core.session.DataField;

public interface HasDataInput {
    HasDataInput addData(String key,DataField dataField);
}
