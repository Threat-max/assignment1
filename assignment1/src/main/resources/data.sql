-- Seed data (runs every start; ignore duplicates by checking)
INSERT INTO students (id, first_name, last_name, major)
SELECT gen_random_uuid(), 'Ainur', 'K', 'CS'
WHERE NOT EXISTS (SELECT 1 FROM students WHERE first_name='Ainur' AND last_name='K');

INSERT INTO courses (id, code, title, capacity)
SELECT gen_random_uuid(), 'OOP101', 'Object-Oriented Programming', 3
WHERE NOT EXISTS (SELECT 1 FROM courses WHERE code='OOP101');
