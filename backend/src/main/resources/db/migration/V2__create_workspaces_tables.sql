CREATE TABLE workspaces (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(255) NOT NULL UNIQUE,
    owner_id UUID NOT NULL REFERENCES users (id),
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE workspace_members (
    id UUID PRIMARY KEY,
    workspace_id UUID NOT NULL REFERENCES workspaces (id),
    user_id UUID NOT NULL REFERENCES users (id),
    role VARCHAR(50) NOT NULL,
    joined_at TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT uq_workspace_members_workspace_user UNIQUE (workspace_id, user_id)
);

CREATE TABLE invitations (
    id UUID PRIMARY KEY,
    workspace_id UUID NOT NULL REFERENCES workspaces (id),
    email VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    token VARCHAR(255) NOT NULL UNIQUE,
    status VARCHAR(50) NOT NULL,
    expires_at TIMESTAMP NOT NULL
);
