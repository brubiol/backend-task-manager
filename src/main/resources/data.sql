-- Seed data for local development
-- Loaded automatically via spring.sql.init.mode=always

-- Users are created via /api/auth/register (passwords are BCrypt-hashed at registration time)

-- Tags
INSERT INTO tags (id, name) VALUES (1, 'backend'), (2, 'frontend'), (3, 'urgent'), (4, 'bug');

-- Tasks
INSERT INTO tasks (id, title, description, status, priority, assignee, due_date, created_at, updated_at, deleted) VALUES
(1, 'Set up CI/CD pipeline',      'Configure GitHub Actions for automated testing', 'TODO',        'HIGH',   'john', DATEADD('DAY', 7, CURRENT_TIMESTAMP),  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
(2, 'Fix login bug',              'Users get 500 error on invalid credentials',     'IN_PROGRESS', 'URGENT', 'jane', DATEADD('DAY', 2, CURRENT_TIMESTAMP),  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
(3, 'Write API documentation',    'Document all REST endpoints with examples',      'TODO',        'MEDIUM', 'john', DATEADD('DAY', 14, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
(4, 'Refactor user service',      'Extract validation logic into separate class',   'BLOCKED',     'LOW',    NULL,   NULL,                                   CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
(5, 'Deploy to staging',          'Push latest build to staging environment',        'DONE',        'HIGH',   'jane', DATEADD('DAY', -1, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false);

-- Task-Tag associations
INSERT INTO task_tags (task_id, tag_id) VALUES (1, 1), (2, 3), (2, 4), (3, 1), (4, 1);

-- Comments
INSERT INTO comments (content, author, task_id, created_at) VALUES
('Started working on this, should be done by EOD', 'jane', 2, CURRENT_TIMESTAMP),
('Can you check the error logs?', 'john', 2, CURRENT_TIMESTAMP),
('Let me know if you need help with the YAML config', 'jane', 1, CURRENT_TIMESTAMP);
