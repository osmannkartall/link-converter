CREATE TABLE link_conversions (
    id SERIAL PRIMARY KEY,
    url TEXT NOT NULL UNIQUE,
    deeplink TEXT,
    shortlink TEXT NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE link_conversion_failures (
    id SERIAL PRIMARY KEY,
    endpoint TEXT NOT NULL,
    request TEXT NOT NULL,
    message TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_link_conversions_shortlink ON link_conversions(shortlink);