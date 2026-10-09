CREATE TABLE projects (
    id UUID PRIMARY KEY,
    workspace_id UUID NOT NULL REFERENCES workspaces (id),
    name VARCHAR(255) NOT NULL,
    key VARCHAR(50) NOT NULL,
    description VARCHAR(1000),
    archived_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);
