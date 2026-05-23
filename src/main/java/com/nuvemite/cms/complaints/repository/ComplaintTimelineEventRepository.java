package com.nuvemite.cms.complaints.repository;

import com.nuvemite.cms.complaints.domain.ComplaintTimelineEvent;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComplaintTimelineEventRepository extends JpaRepository<ComplaintTimelineEvent, UUID> {

    List<ComplaintTimelineEvent> findByComplaintIdOrderByOccurredAtAsc(UUID complaintId);
}
