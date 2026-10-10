CREATE TABLE user_stories (
    id UUID PRIMARY KEY,
    project_id UUID NOT NULL REFERENCES projects (id),
    sprint_id UUID REFERENCES sprints (id),
    status_id UUID NOT NULL REFERENCES board_columns (id),
    assignee_id UUID,
    created_by UUID NOT NULL REFERENCES users (id),
    title VARCHAR(255) NOT NULL,
    description VARCHAR(2000),
    story_points INT,
    priority VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);
