using SQLite;
using System;
using System.Collections.Generic;

namespace SipTrack.Models
{
    public enum SessionMode
    {
        Normal,
        Discreet,
        ProfessionalTasting,
        Recovery,
        DesignatedDriver
    }

    [Table("DrinkSessions")]
    public class DrinkSession
    {
        [PrimaryKey, AutoIncrement]
        public int Id { get; set; }

        public DateTime StartTime { get; set; } = DateTime.UtcNow;

        public DateTime? EndTime { get; set; }

        /// <summary>Peak BAC recorded during session</summary>
        public double PeakBac { get; set; }

        public bool IsActive { get; set; } = true;

        public SessionMode Mode { get; set; } = SessionMode.Normal;

        // ── Not stored in DB — populated by DatabaseService ──────────────
        [Ignore]
        public List<Drink> Drinks { get; set; } = new();

        [Ignore]
        public int DrinkCount => Drinks.Count;

        [Ignore]
        public TimeSpan Duration => (EndTime ?? DateTime.UtcNow) - StartTime;

        [Ignore]
        public string DurationDisplay
        {
            get
            {
                var d = Duration;
                return d.TotalHours >= 1
                    ? $"{(int)d.TotalHours}h {d.Minutes}m"
                    : $"{d.Minutes}m";
            }
        }

        [Ignore]
        public string DateDisplay => StartTime.ToLocalTime().ToString("MMM d, yyyy");

        [Ignore]
        public string TimeDisplay => StartTime.ToLocalTime().ToString("h:mm tt");

        [Ignore]
        public double TotalCalories
        {
            get
            {
                double total = 0;
                foreach (var d in Drinks) total += d.CaloriesEstimate;
                return total;
            }
        }
    }
}
