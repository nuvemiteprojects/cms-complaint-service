package com.nuvemite.cms.complaints.web;

import com.nuvemite.cms.complaints.domain.Complaint;
import com.nuvemite.cms.complaints.domain.ComplaintPriority;
import com.nuvemite.cms.complaints.domain.ComplaintStatus;
import com.nuvemite.cms.complaints.security.ComplaintAccessService;
import com.nuvemite.cms.complaints.security.SecurityUtils;
import com.nuvemite.cms.complaints.service.ComplaintService;
import com.nuvemite.cms.complaints.web.dto.AddAttachmentRequest;
import com.nuvemite.cms.complaints.web.dto.AttachmentResponse;
import com.nuvemite.cms.complaints.web.dto.ComplaintResponse;
import com.nuvemite.cms.complaints.web.dto.CreateComplaintRequest;
import com.nuvemite.cms.complaints.web.dto.RejectComplaintRequest;
import com.nuvemite.cms.complaints.web.dto.RequestInfoRequest;
import com.nuvemite.cms.complaints.web.dto.ResolveComplaintRequest;
import com.nuvemite.cms.complaints.web.dto.TimelineEventResponse;
import com.nuvemite.cms.complaints.web.dto.TriageComplaintRequest;
import com.nuvemite.cms.complaints.web.dto.UpdateComplaintRequest;
import com.nuvemite.cms.complaints.web.mapper.ComplaintMapper;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/complaints")
public class ComplaintController {

    private final ComplaintService complaintService;
    private final ComplaintAccessService access;
    private final ComplaintMapper mapper;

    public ComplaintController(
            ComplaintService complaintService, ComplaintAccessService access, ComplaintMapper mapper) {
        this.complaintService = complaintService;
        this.access = access;
        this.mapper = mapper;
    }

    @GetMapping
    public Page<ComplaintResponse> list(
            @RequestParam(required = false) ComplaintStatus status,
            @RequestParam(required = false) ComplaintPriority priority,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) UUID primaryCompanyId,
            @RequestParam(required = false) UUID primaryPremiseId,
            @RequestParam(required = false) String assignedRegulator,
            @PageableDefault(size = 20) Pageable pageable) {
        return complaintService
                .list(status, priority, category, primaryCompanyId, primaryPremiseId, assignedRegulator, pageable)
                .map(mapper::toResponse);
    }

    @GetMapping("/{id}")
    public ComplaintResponse get(@PathVariable UUID id) {
        Complaint complaint = complaintService.get(id);
        access.requireReadAccess(complaint);
        return mapper.toResponse(complaint);
    }

    @GetMapping("/{id}/timeline")
    public List<TimelineEventResponse> timeline(@PathVariable UUID id) {
        Complaint complaint = complaintService.get(id);
        access.requireReadAccess(complaint);
        return complaintService.timeline(id).stream().map(mapper::toTimelineResponse).toList();
    }

    @GetMapping("/{id}/attachments")
    public List<AttachmentResponse> attachments(@PathVariable UUID id) {
        Complaint complaint = complaintService.get(id);
        access.requireReadAccess(complaint);
        return complaintService.attachments(id).stream().map(mapper::toAttachmentResponse).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ComplaintResponse create(@Valid @RequestBody CreateComplaintRequest request) {
        return mapper.toResponse(complaintService.create(request, SecurityUtils.currentSubject()));
    }

    @PutMapping("/{id}")
    public ComplaintResponse update(@PathVariable UUID id, @Valid @RequestBody UpdateComplaintRequest request) {
        Complaint complaint = complaintService.get(id);
        access.requireReporterDraftEdit(complaint);
        return mapper.toResponse(complaintService.updateDraft(id, request));
    }

    @PostMapping("/{id}/submit")
    public ComplaintResponse submit(@PathVariable UUID id) {
        Complaint complaint = complaintService.get(id);
        access.requireReporterSubmit(complaint);
        return mapper.toResponse(complaintService.submit(id, SecurityUtils.currentSubject()));
    }

    @PostMapping("/{id}/triage")
    public ComplaintResponse triage(@PathVariable UUID id, @Valid @RequestBody TriageComplaintRequest request) {
        access.requireRegulator();
        return mapper.toResponse(complaintService.triage(id, request, SecurityUtils.currentSubject()));
    }

    @PostMapping("/{id}/investigate")
    public ComplaintResponse investigate(@PathVariable UUID id) {
        access.requireRegulator();
        return mapper.toResponse(complaintService.startInvestigation(id, SecurityUtils.currentSubject()));
    }

    @PostMapping("/{id}/request-info")
    public ComplaintResponse requestInfo(@PathVariable UUID id, @Valid @RequestBody RequestInfoRequest request) {
        access.requireRegulator();
        return mapper.toResponse(complaintService.requestInfo(id, request, SecurityUtils.currentSubject()));
    }

    @PostMapping("/{id}/respond-info")
    public ComplaintResponse respondInfo(@PathVariable UUID id) {
        Complaint complaint = complaintService.get(id);
        access.requireReporterRespond(complaint);
        return mapper.toResponse(complaintService.respondInfo(id, SecurityUtils.currentSubject()));
    }

    @PostMapping("/{id}/reject")
    public ComplaintResponse reject(@PathVariable UUID id, @Valid @RequestBody RejectComplaintRequest request) {
        access.requireRegulator();
        return mapper.toResponse(complaintService.reject(id, request.notes(), SecurityUtils.currentSubject()));
    }

    @PostMapping("/{id}/resolve")
    public ComplaintResponse resolve(@PathVariable UUID id, @Valid @RequestBody ResolveComplaintRequest request) {
        access.requireRegulator();
        return mapper.toResponse(complaintService.resolve(id, request, SecurityUtils.currentSubject()));
    }

    @PostMapping("/{id}/close")
    public ComplaintResponse close(@PathVariable UUID id, @Valid @RequestBody RejectComplaintRequest request) {
        access.requireRegulator();
        return mapper.toResponse(complaintService.closeNoAction(id, request.notes(), SecurityUtils.currentSubject()));
    }

    @PostMapping("/{id}/attachments")
    @ResponseStatus(HttpStatus.CREATED)
    public AttachmentResponse addAttachment(@PathVariable UUID id, @Valid @RequestBody AddAttachmentRequest request) {
        Complaint complaint = complaintService.get(id);
        access.requireAttachmentAccess(complaint);
        return mapper.toAttachmentResponse(
                complaintService.addAttachment(id, request, SecurityUtils.currentSubject()));
    }
}
