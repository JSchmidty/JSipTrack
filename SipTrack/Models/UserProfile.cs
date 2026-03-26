using SQLite;

namespace SipTrack.Models
{
    public enum Gender
    {
        Male,
        Female,
        Other
    }

    [Table("UserProfile")]
    public class UserProfile
    {
        [PrimaryKey]
        public int Id { get; set; } = 1; // Single-row profile

        public double WeightLbs { get; set; } = 160.0;

        public double WeightKg
        {
            get => WeightLbs * 0.453592;
            set => WeightLbs = value / 0.453592;
        }

        public Gender Gender { get; set; } = Gender.Male;

        public int Age { get; set; } = 25;

        /// <summary>
        /// Widmark R factor: Male=0.68, Female=0.55, Other=0.60
        /// Controls how alcohol distributes through body water.
        /// </summary>
        public double WidmarkR
        {
            get => Gender switch
            {
                Gender.Male => 0.68,
                Gender.Female => 0.55,
                _ => 0.60
            };
        }

        /// <summary>
        /// Alcohol metabolic rate in BAC units per hour.
        /// Default 0.015 (15 mg/dL/hr — average adult).
        /// </summary>
        public double MetabolicRate { get; set; } = 0.015;

        /// <summary>Legal BAC driving limit (default 0.08 USA)</summary>
        public double DriveLimit { get; set; } = 0.08;

        /// <summary>User-set personal warning limit (default 0.05)</summary>
        public double PersonalLimit { get; set; } = 0.05;

        public string EmergencyContactName { get; set; } = string.Empty;
        public string EmergencyContactPhone { get; set; } = string.Empty;

        public bool EnableHealthKit { get; set; } = false;
        public bool EnableNotifications { get; set; } = true;
        public bool PreferMetric { get; set; } = false;

        public SessionMode AppMode { get; set; } = SessionMode.Normal;

        /// <summary>True if onboarding has been completed</summary>
        public bool OnboardingComplete { get; set; } = false;

        // ── Computed display helpers ──────────────────────────────────────
        [Ignore]
        public string WeightDisplay => PreferMetric
            ? $"{WeightKg:0.#} kg"
            : $"{WeightLbs:0.#} lbs";

        [Ignore]
        public string GenderDisplay => Gender switch
        {
            Gender.Male => "Male",
            Gender.Female => "Female",
            _ => "Other"
        };
    }
}
