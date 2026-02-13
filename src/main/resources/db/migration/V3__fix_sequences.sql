-- Reset sequences after seed data inserts with explicit IDs
SELECT setval('tasks_id_seq', (SELECT COALESCE(MAX(id), 0) FROM tasks));
SELECT setval('tags_id_seq', (SELECT COALESCE(MAX(id), 0) FROM tags));
