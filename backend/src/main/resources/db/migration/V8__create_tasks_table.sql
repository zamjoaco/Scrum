CREATE TABLE tasks (
    id UUID PRIMARY KEY,
    user_story_id UUID NOT NULL REFERENCES user_stories (id),
    status_id UUID NOT NULL REFERENCES board_columns (id),
    assignee_id UUID,
    title VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);
