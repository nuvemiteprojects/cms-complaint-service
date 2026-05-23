CREATE TABLE company_reference (
  company_id    UUID PRIMARY KEY,
  legal_name    VARCHAR(255),
  created_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE premise_reference (
  premise_id    UUID PRIMARY KEY,
  company_id    UUID NOT NULL,
  name          VARCHAR(255),
  created_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_premise_reference_company ON premise_reference (company_id);
