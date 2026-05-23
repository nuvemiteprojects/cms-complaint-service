package com.nuvemite.cms.complaints.repository;

import com.nuvemite.cms.complaints.domain.ComplaintAttachment;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComplaintAttachmentRepository extends JpaRepository<ComplaintAttachment, UUID> {

    List<ComplaintAttachment> findByComplaintIdOrderByUploadedAtAsc(UUID complaintId);
}
