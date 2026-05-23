CREATE TABLE complaint (
  id                          UUID PRIMARY KEY,
  reference_number            VARCHAR(32) NOT NULL UNIQUE,
  status                      VARCHAR(32) NOT NULL DEFAULT 'DRAFT',
  title                       VARCHAR(255) NOT NULL,
  description                 TEXT NOT NULL,
  category                    VARCHAR(128),
  priority                    VARCHAR(16) NOT NULL DEFAULT 'MEDIUM',
  location_title              VARCHAR(255),
  country_code                CHAR(2),
  address_line                VARCHAR(512),
  postal_code                 VARCHAR(32),
  location                    GEOGRAPHY(POINT, 4326),
  primary_company_id          UUID,
  primary_premise_id          UUID,
  primary_company_name        VARCHAR(255),
  primary_premise_name        VARCHAR(255),
  reporter_user_sub           VARCHAR(128) NOT NULL,
  reporter_name               VARCHAR(255),
  reporter_email              VARCHAR(255),
  reporter_phone              VARCHAR(64),
  assigned_regulator_sub      VARCHAR(128),
  resolution_outcome          VARCHAR(32),
  resolution_notes            TEXT,
  request_special_inspection  BOOLEAN NOT NULL DEFAULT false,
  submitted_at                TIMESTAMPTZ,
  resolved_at                 TIMESTAMPTZ,
  created_at                  TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at                  TIMESTAMPTZ NOT NULL DEFAULT now(),
  created_by                  VARCHAR(128),
  version                     BIGINT NOT NULL DEFAULT 0,
  CONSTRAINT chk_complaint_status CHECK (status IN (
    'DRAFT', 'SUBMITTED', 'TRIAGED', 'UNDER_INVESTIGATION', 'REQUIRES_MORE_INFO',
    'RESOLVED', 'CLOSED_NO_ACTION', 'REJECTED'
  )),
  CONSTRAINT chk_complaint_priority CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH', 'URGENT'))
);

CREATE INDEX idx_complaint_status ON complaint (status);
CREATE INDEX idx_complaint_priority ON complaint (priority);
CREATE INDEX idx_complaint_reporter ON complaint (reporter_user_sub);
CREATE INDEX idx_complaint_primary_company ON complaint (primary_company_id);
CREATE INDEX idx_complaint_primary_premise ON complaint (primary_premise_id);
CREATE INDEX idx_complaint_location ON complaint USING GIST (location);

CREATE TABLE complaint_related_subject (
  id              UUID PRIMARY KEY,
  complaint_id    UUID NOT NULL REFERENCES complaint (id) ON DELETE CASCADE,
  company_id      UUID NOT NULL,
  premise_id      UUID,
  company_name    VARCHAR(255),
  premise_name    VARCHAR(255),
  created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_complaint_related_complaint ON complaint_related_subject (complaint_id);
CREATE INDEX idx_complaint_related_company ON complaint_related_subject (company_id);
CREATE INDEX idx_complaint_related_premise ON complaint_related_subject (premise_id);
