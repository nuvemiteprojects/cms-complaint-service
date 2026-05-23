package com.nuvemite.cms.complaints.security;

import com.nuvemite.cms.complaints.domain.Complaint;
import com.nuvemite.cms.complaints.domain.ComplaintRelatedSubject;
import com.nuvemite.cms.complaints.domain.ComplaintStatus;
import com.nuvemite.cms.complaints.exception.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class ComplaintAccessService {

    public void requireRegulator() {
        if (!SecurityUtils.currentUser().isRegulator()) {
            throw new AccessDeniedException("Regulator role required");
        }
    }

    public void requireReadAccess(Complaint complaint) {
        CmsUserPrincipal user = SecurityUtils.currentUser();
        if (user.isRegulator()) {
            return;
        }
        if (complaint.getReporterUserSub().equals(user.subject())) {
            return;
        }
        if (hasSubjectCompanyAccess(complaint, user)) {
            return;
        }
        throw new AccessDeniedException("No access to this complaint");
    }

    public void requireReporter(Complaint complaint) {
        if (!complaint.getReporterUserSub().equals(SecurityUtils.currentSubject())) {
            throw new AccessDeniedException("Only the reporter can perform this action");
        }
    }

    public void requireReporterDraftEdit(Complaint complaint) {
        requireReporter(complaint);
        if (complaint.getStatus() != ComplaintStatus.DRAFT) {
            throw new AccessDeniedException("Only draft complaints can be edited by the reporter");
        }
    }

    public void requireReporterSubmit(Complaint complaint) {
        requireReporter(complaint);
        if (complaint.getStatus() != ComplaintStatus.DRAFT) {
            throw new AccessDeniedException("Complaint is not in draft status");
        }
    }

    public void requireReporterRespond(Complaint complaint) {
        requireReporter(complaint);
        if (complaint.getStatus() != ComplaintStatus.REQUIRES_MORE_INFO) {
            throw new AccessDeniedException("Complaint is not awaiting reporter response");
        }
    }

    public void requireAttachmentAccess(Complaint complaint) {
        CmsUserPrincipal user = SecurityUtils.currentUser();
        if (user.isRegulator()) {
            return;
        }
        if (complaint.getReporterUserSub().equals(user.subject())
                && (complaint.getStatus() == ComplaintStatus.DRAFT
                        || complaint.getStatus() == ComplaintStatus.SUBMITTED
                        || complaint.getStatus() == ComplaintStatus.REQUIRES_MORE_INFO)) {
            return;
        }
        requireReadAccess(complaint);
        if (!user.isRegulator()) {
            throw new AccessDeniedException("Cannot add attachments in current status");
        }
    }

    private boolean hasSubjectCompanyAccess(Complaint complaint, CmsUserPrincipal user) {
        if (complaint.getPrimaryCompanyId() != null
                && user.isCompanyMember(complaint.getPrimaryCompanyId())) {
            if (complaint.getPrimaryPremiseId() == null
                    || user.canAccessPremise(
                            complaint.getPrimaryCompanyId(), complaint.getPrimaryPremiseId())) {
                return true;
            }
        }
        for (ComplaintRelatedSubject subject : complaint.getRelatedSubjects()) {
            if (user.isCompanyMember(subject.getCompanyId())) {
                if (subject.getPremiseId() == null
                        || user.canAccessPremise(subject.getCompanyId(), subject.getPremiseId())) {
                    return true;
                }
            }
        }
        return false;
    }
}
