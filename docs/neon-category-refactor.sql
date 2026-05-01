-- Neon/PostgreSQL migration for per-user JPA categories.
-- Run duplicate checks first before creating the unique index.

-- 1) Make sure category columns exist.
ALTER TABLE categories
    ADD COLUMN IF NOT EXISTS description TEXT;

ALTER TABLE categories
    ADD COLUMN IF NOT EXISTS user_id BIGINT;

UPDATE categories
SET name = 'Kategori ' || id
WHERE name IS NULL OR btrim(name) = '';

ALTER TABLE categories
    ALTER COLUMN name SET NOT NULL;

ALTER TABLE categories
    ALTER COLUMN name TYPE VARCHAR(100);

-- 2) Check duplicate category names per user.
-- If this returns rows, rename/delete duplicates before running step 3.
SELECT user_id, lower(name) AS normalized_name, COUNT(*) AS total
FROM categories
GROUP BY user_id, lower(name)
HAVING COUNT(*) > 1;

-- 3) Add indexes and constraints.
CREATE INDEX IF NOT EXISTS idx_categories_user_id
    ON categories(user_id);

CREATE UNIQUE INDEX IF NOT EXISTS uk_categories_user_name_lower
    ON categories(user_id, lower(name));

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_categories_user'
    ) THEN
        ALTER TABLE categories
            ADD CONSTRAINT fk_categories_user
            FOREIGN KEY (user_id)
            REFERENCES users(id)
            ON DELETE CASCADE;
    END IF;
END $$;

-- Only run this after every category row has a valid user_id.
ALTER TABLE categories
    ALTER COLUMN user_id SET NOT NULL;

-- 4) Product relation to categories.
ALTER TABLE products
    ADD COLUMN IF NOT EXISTS category_id BIGINT;

CREATE INDEX IF NOT EXISTS idx_products_category_id
    ON products(category_id);

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_products_category'
    ) THEN
        ALTER TABLE products
            ADD CONSTRAINT fk_products_category
            FOREIGN KEY (category_id)
            REFERENCES categories(id)
            ON DELETE SET NULL;
    END IF;
END $$;
