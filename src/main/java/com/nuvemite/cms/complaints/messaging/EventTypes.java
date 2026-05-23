package com.nuvemite.cms.complaints.messaging;

public final class EventTypes {

    public static final String CONSUMER_GROUP = "cms-complaints";

    public static final String COMPLAINT_INSPECTION_REQUESTED = "cms.complaint.inspection.requested.v1";
    public static final String COMPLAINT_SUBMITTED = "cms.complaint.submitted.v1";
    public static final String COMPLAINT_RESOLVED = "cms.complaint.resolved.v1";

    private EventTypes() {}
}
