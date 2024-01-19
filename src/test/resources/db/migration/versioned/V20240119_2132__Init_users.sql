-- Insert users
INSERT INTO users(id, name, email, password, role)
VALUES (100001, 'Test English', 'english@test.com', '$2a$10$QSWrk2qHW14yy0H/1Ou5teCT3ZF0qUr1e916eR.aWt3xb9uLlyRxG', 'USER_ENGLISH'),
       (100002, 'Test Chinese', 'chinese@test.com', '$2a$10$QSWrk2qHW14yy0H/1Ou5teCT3ZF0qUr1e916eR.aWt3xb9uLlyRxG', 'USER_CHINESE'),
       (100003, 'Test English Chinese', 'english.chinese@test.com', '$2a$10$QSWrk2qHW14yy0H/1Ou5teCT3ZF0qUr1e916eR.aWt3xb9uLlyRxG', 'USER_ENGLISH'),
       (100004, 'Test Chinese English', 'chinese.english@test.com', '$2a$10$QSWrk2qHW14yy0H/1Ou5teCT3ZF0qUr1e916eR.aWt3xb9uLlyRxG', 'USER_CHINESE');

-- Insert role statistics
INSERT INTO role_statistics(id, role_name, current_streak, date_of_last_streak, record_streak)
VALUES (200001, 'USER_ENGLISH', 0, '2024-01-18', 0),
       (200002, 'USER_CHINESE', 0, '2024-01-18', 0),
       (200003, 'USER_ENGLISH', 0, '2024-01-18', 0),
       (200004, 'USER_CHINESE', 0, '2024-01-18', 0);

-- Insert users_role_statistics
INSERT INTO users_role_statistics(users_id, role_statistics_id)
VALUES (100001, 200001),
       (100002, 200002),
       (100003, 200003),
       (100004, 200004);

-- Insert notifications
INSERT INTO notifications(notification_id, to_user_id, to_user_email, sender, subject, message, sent_at, is_read)
VALUES (400001, 100001, 'english@test.com', 'Daily Lexika', 'Welcome to Daily Lexika', 'Welcome message', '2024-01-19 21:21:07.300585', false),
       (400002, 100002, 'chinese@test.com', 'Daily Lexika', 'Welcome to Daily Lexika', 'Welcome message', '2024-01-19 21:21:07.300585', false),
       (400003, 100003, 'english.chinese@test.com', 'Daily Lexika', 'Welcome to Daily Lexika', 'Welcome message', '2024-01-19 21:21:07.300585', false),
       (400004, 100004, 'chinese.english@test.com', 'Daily Lexika', 'Welcome to Daily Lexika', 'Welcome message', '2024-01-19 21:21:07.300585', false);
