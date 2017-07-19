package no.java.sleepingpill.core.session;

import java.util.EnumSet;

public enum SessionStatus {
    DRAFT,
    SUBMITTED,
    APPROVED,
    REJECTED,
    HISTORIC;

    private static final EnumSet<SessionStatus> publicStatuses = EnumSet.of(HISTORIC,APPROVED);

    public boolean wasPublished(SessionStatus oldStatus) {
        return  publicStatuses.contains(this) && !publicStatuses.contains(oldStatus);
    }

    public boolean isPublic() {
        return this == SessionStatus.APPROVED || this == SessionStatus.HISTORIC;
    }
}
