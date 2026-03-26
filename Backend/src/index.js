'use strict';

require('dotenv').config();
const express = require('express');
const helmet = require('helmet');
const cors = require('cors');
const rateLimit = require('express-rate-limit');

const beveragesRouter = require('./routes/beverages');

const app = express();
const PORT = process.env.PORT || 3000;

// ── Security middleware ───────────────────────────────────────────────
app.use(helmet());
app.use(cors({
  origin: process.env.CORS_ORIGIN || '*',
  methods: ['GET', 'POST', 'PUT', 'PATCH', 'DELETE'],
  allowedHeaders: ['Content-Type', 'x-api-key'],
}));

// ── Rate limiting ─────────────────────────────────────────────────────
const limiter = rateLimit({
  windowMs: 15 * 60 * 1000, // 15 minutes
  max: 200,
  standardHeaders: true,
  legacyHeaders: false,
  message: { error: 'Too many requests, please try again later.' },
});
app.use(limiter);

// ── Body parsing ──────────────────────────────────────────────────────
app.use(express.json({ limit: '1mb' }));
app.use(express.urlencoded({ extended: false }));

// ── Routes ────────────────────────────────────────────────────────────
app.use('/api/v1/beverages', beveragesRouter);

// Health check also available at root level
app.get('/api/v1/health', (req, res) => {
  res.json({ status: 'ok', service: 'siptrack-beverage-api', version: '1.0.0' });
});

// ── 404 handler ───────────────────────────────────────────────────────
app.use((req, res) => {
  res.status(404).json({ error: 'Not found', path: req.path });
});

// ── Global error handler ──────────────────────────────────────────────
app.use((err, req, res, next) => {
  console.error('Unhandled error:', err);
  res.status(500).json({ error: 'Internal server error', message: err.message });
});

// ── Start server ──────────────────────────────────────────────────────
app.listen(PORT, () => {
  console.log(`SipTrack Beverage API running on port ${PORT}`);
  console.log(`  ENV: ${process.env.NODE_ENV || 'development'}`);
  console.log(`  DB:  ${process.env.DATABASE_URL ? 'configured' : 'NOT configured (set DATABASE_URL)'}`);
});

module.exports = app;
