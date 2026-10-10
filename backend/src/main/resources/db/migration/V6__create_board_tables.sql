CREATE TABLE boards (
    id UUID PRIMARY KEY,
    project_id UUID NOT NULL REFERENCES projects (id),
    name VARCHAR(255) NOT NULL,
    CONSTRAINT uq_boards_project_id UNIQUE (project_id)
);

CREATE TABLE board_columns (
    id UUID PRIMARY KEY,
    board_id UUID NOT NULL REFERENCES boards (id),
    name VARCHAR(255) NOT NULL,
    order_index INT NOT NULL,
    wip_limit INT
);
