CREATE TABLE complaint_attachment (
  id              UUID PRIMARY KEY,
  complaint_id    UUID NOT NULL REFERENCES complaint (id) ON DELETE CASCADE,
  document_name   VARCHAR(255) NOT NULL,
  document_type   VARCHAR(64),
  storage_ref     VARCHAR(512) NOT NULL,
  uploaded_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
  uploaded_by     VARCHAR(128)
);

CREATE INDEX idx_complaint_attachment_complaint ON complaint_attachment (complaint_id);
