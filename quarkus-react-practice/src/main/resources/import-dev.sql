INSERT INTO "users" ("id", "name", "password", "created", "version")
VALUES (0, 'admin', '$2a$10$7b.9iLgXFVh.r1u9HEbMv.EDL3JcJgldsWHUg4etSUh4wCNGuExye', NOW(), 0)
    ON CONFLICT DO NOTHING;
INSERT INTO "user_roles" ("id", "role") VALUES (0, 'admin')
    ON CONFLICT DO NOTHING;
INSERT INTO "user_roles" ("id", "role") VALUES (0, 'user')
    ON CONFLICT DO NOTHING;
    
INSERT INTO "users" ("id", "name", "password", "created", "version")
VALUES (1, 'user', '$2a$10$7b.9iLgXFVh.r1u9HEbMv.EDL3JcJgldsWHUg4etSUh4wCNGuExye', NOW(), 0)
    ON CONFLICT DO NOTHING;
INSERT INTO "user_roles" ("id", "role") VALUES (1, 'user')
    ON CONFLICT DO NOTHING;

INSERT INTO "projects" ("id", "name", "user_id", "created", "version")
VALUES (0, 'Work', 1, NOW(), 0)
    ON CONFLICT DO NOTHING;

INSERT INTO "projects" ("id", "name", "user_id", "created", "version")
VALUES (1, 'Work2', 0, NOW(), 0)
    ON CONFLICT DO NOTHING;

INSERT INTO "tasks" ("id", "title", "description", "priority", "user_id", "complete", "project_id", "created", "version")
VALUES (0, 'Task', 'Initial data for testing', 1, 0, null, 1, NOW(), 0)
    ON CONFLICT DO NOTHING;

ALTER SEQUENCE users_seq RESTART WITH 2;
ALTER SEQUENCE projects_seq RESTART WITH 2;
