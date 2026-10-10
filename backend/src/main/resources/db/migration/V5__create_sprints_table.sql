CREATE TABLE sprints (
    id UUID PRIMARY KEY,
    project_id UUID NOT NULL REFERENCES projects (id),
    name VARCHAR(255) NOT NULL,
    goal VARCHAR(1000),
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status VARCHAR(50) NOT NULL
);
