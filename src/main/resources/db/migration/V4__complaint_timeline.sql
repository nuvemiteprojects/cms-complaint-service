CREATE TABLE complaint_timeline_event (
  id              UUID PRIMARY KEY,
  complaint_id    UUID NOT NULL REFERENCES complaint (id) ON DELETE CASCADE,
  event_type      VARCHAR(64) NOT NULL,
  actor_ref       VARCHAR(128),
  notes           TEXT,
  occurred_at     TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_complaint_timeline_complaint ON complaint_timeline_event (complaint_id);
