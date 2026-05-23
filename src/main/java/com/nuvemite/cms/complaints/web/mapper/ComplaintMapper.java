package com.nuvemite.cms.complaints.web.mapper;

import com.nuvemite.cms.complaints.domain.Complaint;
import com.nuvemite.cms.complaints.domain.ComplaintAttachment;
import com.nuvemite.cms.complaints.domain.ComplaintRelatedSubject;
import com.nuvemite.cms.complaints.domain.ComplaintTimelineEvent;
import com.nuvemite.cms.complaints.web.dto.AttachmentResponse;
import com.nuvemite.cms.complaints.web.dto.ComplaintResponse;
import com.nuvemite.cms.complaints.web.dto.LocationDto;
import com.nuvemite.cms.complaints.web.dto.RelatedSubjectResponse;
import com.nuvemite.cms.complaints.web.dto.TimelineEventResponse;
import org.springframework.stereotype.Component;

@Component
public class ComplaintMapper {

    public ComplaintResponse toResponse(Complaint complaint) {
        LocationDto location = null;
        if (complaint.getLatitude() != null && complaint.getLongitude() != null) {
            location = new LocationDto(complaint.getLatitude(), complaint.getLongitude());
        }
        return new ComplaintResponse(
                complaint.getId(),
                complaint.getReferenceNumber(),
                complaint.getStatus(),
                complaint.getTitle(),
                complaint.getDescription(),
                complaint.getCategory(),
                complaint.getPriority(),
                complaint.getLocationTitle(),
                complaint.getCountryCode(),
                complaint.getAddressLine(),
                complaint.getPostalCode(),
                location,
                complaint.getPrimaryCompanyId(),
                complaint.getPrimaryPremiseId(),
                complaint.getPrimaryCompanyName(),
                complaint.getPrimaryPremiseName(),
                complaint.getRelatedSubjects().stream().map(this::toRelatedSubject).toList(),
                complaint.getReporterUserSub(),
                complaint.getReporterName(),
                complaint.getReporterEmail(),
                complaint.getReporterPhone(),
                complaint.getAssignedRegulatorSub(),
                complaint.getResolutionOutcome(),
                complaint.getResolutionNotes(),
                complaint.isRequestSpecialInspection(),
                complaint.getSubmittedAt(),
                complaint.getResolvedAt(),
                complaint.getCreatedAt(),
                complaint.getUpdatedAt(),
                complaint.getVersion());
    }

    public TimelineEventResponse toTimelineResponse(ComplaintTimelineEvent event) {
        return new TimelineEventResponse(
                event.getId(), event.getEventType(), event.getActorRef(), event.getNotes(), event.getOccurredAt());
    }

    public AttachmentResponse toAttachmentResponse(ComplaintAttachment attachment) {
        return new AttachmentResponse(
                attachment.getId(),
                attachment.getComplaintId(),
                attachment.getDocumentName(),
                attachment.getDocumentType(),
                attachment.getStorageRef(),
                attachment.getUploadedAt(),
                attachment.getUploadedBy());
    }

    private RelatedSubjectResponse toRelatedSubject(ComplaintRelatedSubject subject) {
        return new RelatedSubjectResponse(
                subject.getId(),
                subject.getCompanyId(),
                subject.getPremiseId(),
                subject.getCompanyName(),
                subject.getPremiseName());
    }
}
