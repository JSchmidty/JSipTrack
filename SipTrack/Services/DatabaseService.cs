using SQLite;
using System;
using System.Collections.Generic;
using System.IO;
using System.Threading.Tasks;
using SipTrack.Models;

namespace SipTrack.Services
{
    /// <summary>
    /// Handles all local SQLite persistence for SipTrack.
    /// Uses sqlite-net-pcl with async API.
    /// Database location: FileSystem.AppDataDirectory/siptrack.db3
    /// </summary>
    public class DatabaseService
    {
        private SQLiteAsyncConnection? _db;
        private static readonly string DbPath =
            Path.Combine(FileSystem.AppDataDirectory, "siptrack.db3");

        // ── Initialisation ───────────────────────────────────────────────

        private async Task<SQLiteAsyncConnection> GetDatabaseAsync()
        {
            if (_db != null) return _db;

            _db = new SQLiteAsyncConnection(DbPath, SQLiteOpenFlags.ReadWrite |
                                                     SQLiteOpenFlags.Create |
                                                     SQLiteOpenFlags.SharedCache);

            await _db.CreateTableAsync<UserProfile>();
            await _db.CreateTableAsync<DrinkSession>();
            await _db.CreateTableAsync<Drink>();

            return _db;
        }

        // ── UserProfile ──────────────────────────────────────────────────

        public async Task<UserProfile> GetProfileAsync()
        {
            var db = await GetDatabaseAsync();
            var profile = await db.FindAsync<UserProfile>(1);
            return profile ?? new UserProfile();
        }

        public async Task SaveProfileAsync(UserProfile profile)
        {
            var db = await GetDatabaseAsync();
            profile.Id = 1;
            await db.InsertOrReplaceAsync(profile);
        }

        // ── DrinkSession ─────────────────────────────────────────────────

        public async Task<DrinkSession> CreateSessionAsync(SessionMode mode = SessionMode.Normal)
        {
            var db = await GetDatabaseAsync();

            // End any existing active sessions first
            var active = await GetActiveSessionAsync();
            if (active != null)
            {
                active.IsActive = false;
                active.EndTime = DateTime.UtcNow;
                await db.UpdateAsync(active);
            }

            var session = new DrinkSession
            {
                StartTime = DateTime.UtcNow,
                IsActive = true,
                Mode = mode
            };

            await db.InsertAsync(session);
            return session;
        }

        public async Task<DrinkSession?> GetActiveSessionAsync()
        {
            var db = await GetDatabaseAsync();
            var session = await db.Table<DrinkSession>()
                                  .Where(s => s.IsActive)
                                  .FirstOrDefaultAsync();

            if (session != null)
            {
                session.Drinks = await GetDrinksForSessionAsync(session.Id);
            }

            return session;
        }

        public async Task<List<DrinkSession>> GetAllSessionsAsync()
        {
            var db = await GetDatabaseAsync();
            var sessions = await db.Table<DrinkSession>()
                                   .OrderByDescending(s => s.StartTime)
                                   .ToListAsync();

            foreach (var session in sessions)
            {
                session.Drinks = await GetDrinksForSessionAsync(session.Id);
            }

            return sessions;
        }

        public async Task EndSessionAsync(int sessionId)
        {
            var db = await GetDatabaseAsync();
            var session = await db.FindAsync<DrinkSession>(sessionId);
            if (session == null) return;

            session.IsActive = false;
            session.EndTime = DateTime.UtcNow;
            await db.UpdateAsync(session);
        }

        public async Task UpdateSessionPeakBacAsync(int sessionId, double peakBac)
        {
            var db = await GetDatabaseAsync();
            var session = await db.FindAsync<DrinkSession>(sessionId);
            if (session == null) return;

            if (peakBac > session.PeakBac)
            {
                session.PeakBac = peakBac;
                await db.UpdateAsync(session);
            }
        }

        public async Task DeleteSessionAsync(int sessionId)
        {
            var db = await GetDatabaseAsync();
            // Delete drinks in session first
            var drinks = await GetDrinksForSessionAsync(sessionId);
            foreach (var d in drinks)
                await db.DeleteAsync(d);

            await db.DeleteAsync<DrinkSession>(sessionId);
        }

        // ── Drinks ───────────────────────────────────────────────────────

        public async Task<Drink> SaveDrinkAsync(Drink drink)
        {
            var db = await GetDatabaseAsync();
            if (drink.Id == 0)
                await db.InsertAsync(drink);
            else
                await db.UpdateAsync(drink);
            return drink;
        }

        public async Task<List<Drink>> GetDrinksForSessionAsync(int sessionId)
        {
            var db = await GetDatabaseAsync();
            return await db.Table<Drink>()
                           .Where(d => d.SessionId == sessionId)
                           .OrderBy(d => d.LoggedAt)
                           .ToListAsync();
        }

        public async Task DeleteDrinkAsync(int drinkId)
        {
            var db = await GetDatabaseAsync();
            await db.DeleteAsync<Drink>(drinkId);
        }

        // ── Statistics ───────────────────────────────────────────────────

        /// <summary>Get all sessions in the past N days</summary>
        public async Task<List<DrinkSession>> GetSessionsInRangeAsync(DateTime from, DateTime to)
        {
            var db = await GetDatabaseAsync();
            var sessions = await db.Table<DrinkSession>()
                                   .Where(s => s.StartTime >= from && s.StartTime <= to)
                                   .OrderByDescending(s => s.StartTime)
                                   .ToListAsync();

            foreach (var s in sessions)
                s.Drinks = await GetDrinksForSessionAsync(s.Id);

            return sessions;
        }

        /// <summary>Export all sessions as a CSV string</summary>
        public async Task<string> ExportToCsvAsync()
        {
            var sessions = await GetAllSessionsAsync();
            var sb = new System.Text.StringBuilder();
            sb.AppendLine("SessionId,Date,Duration,DrinkCount,PeakBAC,DrinkName,ABV%,VolumeOz,LoggedAt,Calories");

            foreach (var session in sessions)
            {
                if (session.Drinks.Count == 0)
                {
                    sb.AppendLine($"{session.Id},{session.DateDisplay},{session.DurationDisplay},{session.DrinkCount},{session.PeakBac:F3},,,,, ");
                }
                else
                {
                    foreach (var drink in session.Drinks)
                    {
                        sb.AppendLine($"{session.Id},{session.DateDisplay},{session.DurationDisplay},{session.DrinkCount},{session.PeakBac:F3}," +
                                      $"\"{drink.Name}\",{drink.AbvPercent},{drink.VolumeOz:F2},{drink.LoggedAt:O},{drink.CaloriesEstimate:F1}");
                    }
                }
            }

            return sb.ToString();
        }

        /// <summary>Delete ALL data (nuclear reset)</summary>
        public async Task ResetAllDataAsync()
        {
            var db = await GetDatabaseAsync();
            await db.DeleteAllAsync<Drink>();
            await db.DeleteAllAsync<DrinkSession>();
            await db.DeleteAllAsync<UserProfile>();
        }
    }
}
