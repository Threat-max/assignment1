-- Make UUID generator available (needed for gen_random_uuid())
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE IF NOT EXISTS students (
    id UUID PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name  VARCHAR(50) NOT NULL,
    major      VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS courses (
    id UUID PRIMARY KEY,
    code VARCHAR(20) UNIQUE NOT NULL,
    title VARCHAR(120) NOT NULL,
    capacity INT NOT NULL
);
