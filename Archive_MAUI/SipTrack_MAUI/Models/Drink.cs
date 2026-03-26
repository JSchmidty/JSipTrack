using SQLite;
using System;

namespace SipTrack.Models
{
    public enum DrinkCategory
    {
        Beer,
        Wine,
        Spirit,
        Cocktail,
        Custom
    }

    [Table("Drinks")]
    public class Drink
    {
        [PrimaryKey, AutoIncrement]
        public int Id { get; set; }

        [MaxLength(200)]
        public string Name { get; set; } = string.Empty;

        public DrinkCategory Category { get; set; } = DrinkCategory.Custom;

        /// <summary>Alcohol by volume as percentage (e.g. 5.0 for 5%)</summary>
        public double AbvPercent { get; set; }

        /// <summary>Volume in fluid ounces</summary>
        public double VolumeOz { get; set; }

        /// <summary>Volume in millilitres (kept in sync with VolumeOz)</summary>
        public double VolumeMl
        {
            get => VolumeOz * 29.5735;
            set => VolumeOz = value / 29.5735;
        }

        /// <summary>
        /// Standard drinks = (VolumeOz * AbvPercent/100 * 0.816) / 0.6
        /// (based on 14g pure alcohol = 1 standard drink)
        /// </summary>
        public double StandardDrinks => (VolumeOz * (AbvPercent / 100.0) * 0.816) / 0.6;

        /// <summary>Estimated calories: 7 kcal per gram of alcohol</summary>
        public double CaloriesEstimate
        {
            get
            {
                double alcoholGrams = VolumeOz * 29.5735 * (AbvPercent / 100.0) * 0.789;
                return alcoholGrams * 7.0;
            }
        }

        public DateTime LoggedAt { get; set; } = DateTime.UtcNow;

        public int SessionId { get; set; }

        public bool IsCustom { get; set; }

        [MaxLength(500)]
        public string Notes { get; set; } = string.Empty;

        // ── Ignored (not stored in DB) ───────────────────────────────────
        [Ignore]
        public string CategoryIcon => Category switch
        {
            DrinkCategory.Beer => "🍺",
            DrinkCategory.Wine => "🍷",
            DrinkCategory.Spirit => "🥃",
            DrinkCategory.Cocktail => "🍹",
            _ => "🥤"
        };

        [Ignore]
        public string DisplayVolume => $"{VolumeOz:0.#} oz";

        [Ignore]
        public string DisplayAbv => $"{AbvPercent:0.#}%";
    }
}
