-- SipTrack Beverage Database Schema
-- PostgreSQL 15+

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS pg_trgm;  -- for full-text search

-- ── Enums ─────────────────────────────────────────────────────────────
CREATE TYPE beverage_category AS ENUM (
    'beer', 'wine', 'spirit', 'cocktail', 'sake', 'cider', 'mead', 'other'
);

CREATE TYPE product_status AS ENUM ('active', 'discontinued', 'limited');
CREATE TYPE ingredient_role AS ENUM ('base_spirit', 'liqueur', 'mixer', 'garnish', 'modifier', 'other');

-- ── Brands ────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS brands (
    id             UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name           TEXT NOT NULL,
    parent_company TEXT,
    country        TEXT,
    website        TEXT,
    created_at     TIMESTAMPTZ DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_brands_name ON brands USING GIN (name gin_trgm_ops);

-- ── Products ──────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS products (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name            TEXT NOT NULL,
    brand_id        UUID REFERENCES brands(id) ON DELETE SET NULL,
    category        beverage_category NOT NULL DEFAULT 'other',
    subcategory     TEXT,
    abv             NUMERIC(5, 2) NOT NULL CHECK (abv >= 0 AND abv <= 100),
    description     TEXT,
    flavor_profile  TEXT[],
    image_url       TEXT,
    availability    TEXT,
    status          product_status NOT NULL DEFAULT 'active',
    created_at      TIMESTAMPTZ DEFAULT NOW(),
    updated_at      TIMESTAMPTZ DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_products_name ON products USING GIN (name gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_products_category ON products (category);
CREATE INDEX IF NOT EXISTS idx_products_status ON products (status);

-- ── Ingredients ───────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS ingredients (
    id         UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name       TEXT NOT NULL UNIQUE,
    category   TEXT,
    description TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- ── Cocktails ─────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS cocktails (
    id            UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name          TEXT NOT NULL,
    category      TEXT,
    base_spirit   TEXT,
    method        TEXT,   -- stirred, shaken, built, blended
    instructions  TEXT,
    glass_type    TEXT,
    abv_estimated NUMERIC(5, 2),
    garnish       TEXT,
    created_at    TIMESTAMPTZ DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_cocktails_name ON cocktails USING GIN (name gin_trgm_ops);

-- ── Cocktail Ingredients (join table) ─────────────────────────────────
CREATE TABLE IF NOT EXISTS cocktail_ingredients (
    id            UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    cocktail_id   UUID NOT NULL REFERENCES cocktails(id) ON DELETE CASCADE,
    ingredient_id UUID REFERENCES ingredients(id),
    product_id    UUID REFERENCES products(id),
    role          ingredient_role NOT NULL DEFAULT 'other',
    amount_oz     NUMERIC(6, 2),
    amount_ml     NUMERIC(6, 2),
    notes         TEXT
);

-- ── Seed Data: Common Brands ──────────────────────────────────────────
INSERT INTO brands (name, country) VALUES
    ('Anheuser-Busch InBev', 'Belgium'),
    ('Heineken', 'Netherlands'),
    ('Constellation Brands', 'USA'),
    ('Diageo', 'UK'),
    ('Pernod Ricard', 'France'),
    ('Brown-Forman', 'USA'),
    ('Bacardi', 'Bermuda'),
    ('Craft Collective', 'USA')
ON CONFLICT DO NOTHING;

-- ── Seed Data: Sample Products ────────────────────────────────────────
INSERT INTO products (name, category, abv, description, flavor_profile) VALUES
    ('Bud Light', 'beer', 4.2, 'Light American lager', ARRAY['crisp', 'light', 'refreshing']),
    ('Heineken Original', 'beer', 5.0, 'Dutch premium lager', ARRAY['malt', 'bitter', 'clean']),
    ('Corona Extra', 'beer', 4.6, 'Mexican pale lager', ARRAY['light', 'citrus', 'smooth']),
    ('Stella Artois', 'beer', 5.2, 'Belgian pilsner', ARRAY['malt', 'hoppy', 'crisp']),
    ('Chardonnay (generic)', 'wine', 13.5, 'White wine - Chardonnay varietal', ARRAY['oak', 'butter', 'apple']),
    ('Cabernet Sauvignon (generic)', 'wine', 14.0, 'Full-bodied red wine', ARRAY['dark fruit', 'tannin', 'oak']),
    ('Prosecco (generic)', 'wine', 11.5, 'Italian sparkling wine', ARRAY['peach', 'apple', 'floral', 'bubbly']),
    ('Jameson Irish Whiskey', 'spirit', 40.0, 'Triple-distilled Irish whiskey', ARRAY['smooth', 'vanilla', 'light spice']),
    ('Jack Daniel''s Old No. 7', 'spirit', 40.0, 'Tennessee whiskey', ARRAY['oak', 'vanilla', 'caramel']),
    ('Grey Goose Vodka', 'spirit', 40.0, 'French premium vodka', ARRAY['clean', 'smooth', 'neutral']),
    ('Patron Silver Tequila', 'spirit', 40.0, 'Silver blanco tequila', ARRAY['agave', 'citrus', 'clean']),
    ('Bacardi Superior Rum', 'spirit', 40.0, 'White rum', ARRAY['clean', 'subtle sweetness', 'vanilla'])
ON CONFLICT DO NOTHING;

-- ── Seed Data: Common Cocktails ───────────────────────────────────────
INSERT INTO cocktails (name, category, base_spirit, method, instructions, abv_estimated) VALUES
    ('Margarita', 'classic', 'tequila', 'shaken',
     'Rim glass with salt. Combine tequila, lime juice, and triple sec in shaker with ice. Shake well. Strain into rimmed glass.',
     14.0),
    ('Old Fashioned', 'classic', 'whiskey', 'stirred',
     'Muddle sugar and bitters. Add ice and whiskey. Stir until chilled. Garnish with orange peel.',
     32.0),
    ('Mojito', 'classic', 'rum', 'built',
     'Muddle mint and lime. Add rum, sugar syrup, ice. Top with soda water. Stir gently.',
     10.0),
    ('Gin & Tonic', 'highball', 'gin', 'built',
     'Fill glass with ice. Pour gin. Top with tonic water. Add lime wedge.',
     8.0),
    ('Moscow Mule', 'highball', 'vodka', 'built',
     'Fill copper mug with ice. Add vodka and lime juice. Top with ginger beer.',
     8.0),
    ('Aperol Spritz', 'spritz', 'aperol', 'built',
     'Fill wine glass with ice. Add Prosecco, Aperol, and a splash of soda. Garnish with orange slice.',
     9.0)
ON CONFLICT DO NOTHING;
