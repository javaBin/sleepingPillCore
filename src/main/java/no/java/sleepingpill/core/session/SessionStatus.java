package no.java.sleepingpill.core.session;

import java.util.EnumSet;

public enum SessionStatus {
    DRAFT,
    SUBMITTED,
    APPROVED,
    REJECTED,
    HISTORIC;

    private static final EnumSet<SessionStatus> publicStatuses = EnumSet.of(HISTORIC,APPROVED);
    private static final EnumSet<SessionStatus> finalStatuses = EnumSet.of(HISTORIC,APPROVED,REJECTED);


    public boolean wasPublished(SessionStatus oldStatus) {
        return  publicStatuses.contains(this) && !publicStatuses.contains(oldStatus);
    }

    public boolean isPublic() {
        return publicStatuses.contains(this);
    }

    public SessionStatus findNewStatus(SessionStatus proposedStatus) {
        if (finalStatuses.contains(this) && !finalStatuses.contains(proposedStatus)) {
            return this;
        }
        return proposedStatus;
    }
}
