'use strict';

const express = require('express');
const { v4: uuidv4 } = require('uuid');
const db = require('../db/connection');

const router = express.Router();

// ── Auth middleware ───────────────────────────────────────────────────
function requireApiKey(req, res, next) {
  const key = req.headers['x-api-key'] || req.query.api_key;
  if (!process.env.API_KEY || key === process.env.API_KEY) {
    return next();
  }
  return res.status(401).json({ error: 'Unauthorized — invalid API key' });
}

// ── GET /api/v1/health ────────────────────────────────────────────────
router.get('/health', async (req, res) => {
  try {
    await db.query('SELECT 1');
    res.json({ status: 'ok', timestamp: new Date().toISOString(), version: '1.0.0' });
  } catch (err) {
    res.status(503).json({ status: 'error', message: err.message });
  }
});

// ── GET /api/v1/beverages/search ──────────────────────────────────────
router.get('/search', async (req, res) => {
  try {
    const { q = '', category, limit = 20, page = 1 } = req.query;
    const safeLimit = Math.min(Math.max(parseInt(limit, 10) || 20, 1), 100);
    const offset = (Math.max(parseInt(page, 10) || 1, 1) - 1) * safeLimit;
    const params = [];
    let idx = 1;

    let where = [];
    let joinClause = 'LEFT JOIN brands b ON p.brand_id = b.id';

    if (q.trim()) {
      where.push(`(p.name ILIKE $${idx} OR b.name ILIKE $${idx})`);
      params.push(`%${q.trim()}%`);
      idx++;
    }
    if (category) {
      where.push(`p.category = $${idx}::beverage_category`);
      params.push(category.toLowerCase());
      idx++;
    }
    where.push(`p.status = 'active'`);

    const whereClause = where.length ? `WHERE ${where.join(' AND ')}` : '';

    const countRes = await db.query(
      `SELECT COUNT(*) FROM products p ${joinClause} ${whereClause}`,
      params
    );

    params.push(safeLimit, offset);
    const dataRes = await db.query(
      `SELECT p.id, p.name, b.name AS brand, p.category, p.subcategory,
              p.abv, p.description, p.flavor_profile, p.image_url, p.availability
       FROM products p
       ${joinClause}
       ${whereClause}
       ORDER BY p.name ASC
       LIMIT $${idx} OFFSET $${idx + 1}`,
      params
    );

    res.json({
      data: dataRes.rows,
      total: parseInt(countRes.rows[0].count, 10),
      page: parseInt(page, 10) || 1,
      limit: safeLimit,
    });
  } catch (err) {
    console.error('Search error:', err);
    res.status(500).json({ error: 'Internal server error', details: err.message });
  }
});

// ── GET /api/v1/beverages/:id ─────────────────────────────────────────
router.get('/:id', async (req, res) => {
  try {
    const { id } = req.params;
    const result = await db.query(
      `SELECT p.id, p.name, b.name AS brand, p.category, p.subcategory,
              p.abv, p.description, p.flavor_profile, p.image_url, p.availability, p.status
       FROM products p
       LEFT JOIN brands b ON p.brand_id = b.id
       WHERE p.id = $1`,
      [id]
    );
    if (result.rows.length === 0) return res.status(404).json({ error: 'Not found' });
    res.json(result.rows[0]);
  } catch (err) {
    res.status(500).json({ error: 'Internal server error', details: err.message });
  }
});

// ── POST /api/v1/beverages ────────────────────────────────────────────
router.post('/', requireApiKey, async (req, res) => {
  try {
    const { name, brand_id, category, subcategory, abv, description, flavor_profile, image_url, availability } = req.body;

    if (!name || !category || abv === undefined) {
      return res.status(400).json({ error: 'name, category, and abv are required' });
    }

    const result = await db.query(
      `INSERT INTO products (id, name, brand_id, category, subcategory, abv, description, flavor_profile, image_url, availability)
       VALUES ($1, $2, $3, $4::beverage_category, $5, $6, $7, $8, $9, $10)
       RETURNING *`,
      [uuidv4(), name, brand_id || null, category.toLowerCase(), subcategory || null,
       abv, description || null, flavor_profile || null, image_url || null, availability || null]
    );

    res.status(201).json(result.rows[0]);
  } catch (err) {
    console.error('Create error:', err);
    res.status(500).json({ error: 'Internal server error', details: err.message });
  }
});

// ── GET /api/v1/cocktails/search ──────────────────────────────────────
router.get('/cocktails/search', async (req, res) => {
  try {
    const { q = '', limit = 20 } = req.query;
    const safeLimit = Math.min(parseInt(limit, 10) || 20, 50);
    const params = q.trim() ? [`%${q.trim()}%`, safeLimit] : [safeLimit];
    const where = q.trim() ? 'WHERE name ILIKE $1' : '';
    const limitParam = q.trim() ? '$2' : '$1';

    const result = await db.query(
      `SELECT id, name, category, base_spirit, method, abv_estimated
       FROM cocktails ${where}
       ORDER BY name ASC LIMIT ${limitParam}`,
      params
    );
    res.json({ data: result.rows, total: result.rowCount });
  } catch (err) {
    res.status(500).json({ error: 'Internal server error' });
  }
});

module.exports = router;
