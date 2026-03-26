using System;
using System.Collections.Generic;
using System.Linq;
using SipTrack.Models;

namespace SipTrack.Services
{
    /// <summary>
    /// Implements the Widmark formula for Blood Alcohol Content calculation.
    ///
    /// BAC = ((AlcoholGrams) / (BodyWeightGrams × WidmarkR)) × 100
    ///       - (MetabolicRate × HoursElapsed)
    ///
    /// Where:
    ///   AlcoholGrams   = VolumeOz × 29.5735 (mL/oz) × (ABV/100) × 0.789 (g/mL ethanol)
    ///   BodyWeightGrams = WeightKg × 1000
    ///   BAC is never allowed to fall below 0.000
    /// </summary>
    public class BACCalculatorService
    {
        private const double MlPerOz = 29.5735;
        private const double EthanolDensity = 0.789;   // g/mL
        private const double CaloriesPerGramAlcohol = 7.0;

        // ── Core BAC calculation ─────────────────────────────────────────

        /// <summary>
        /// Calculate the total alcohol grams absorbed in the body at the given moment.
        /// Each drink contributes alcohol from the moment it was logged.
        /// </summary>
        private double CalculateTotalAlcoholGrams(IEnumerable<Drink> drinks)
        {
            double grams = 0;
            foreach (var drink in drinks)
            {
                grams += drink.VolumeOz * MlPerOz * (drink.AbvPercent / 100.0) * EthanolDensity;
            }
            return grams;
        }

        /// <summary>
        /// Calculate BAC at a specific point in time, accounting for all drinks
        /// consumed up to that time and metabolism since each drink was logged.
        /// </summary>
        public double CalculateBACAtTime(UserProfile profile, IEnumerable<Drink> drinks, DateTime atTime)
        {
            if (profile == null) return 0;

            var drinkList = drinks?.ToList() ?? new List<Drink>();
            if (drinkList.Count == 0) return 0;

            double bodyWeightGrams = profile.WeightKg * 1000.0;
            double bac = 0;

            foreach (var drink in drinkList)
            {
                if (drink.LoggedAt > atTime) continue; // Not yet consumed

                double hoursElapsed = (atTime - drink.LoggedAt).TotalHours;
                double alcoholGrams = drink.VolumeOz * MlPerOz * (drink.AbvPercent / 100.0) * EthanolDensity;

                // Widmark formula for this drink
                double drinkBac = (alcoholGrams / (bodyWeightGrams * profile.WidmarkR)) * 100.0;

                // Subtract metabolism since this drink was consumed
                double metabolized = profile.MetabolicRate * hoursElapsed;
                bac += Math.Max(0, drinkBac - metabolized);
            }

            return Math.Max(0, bac);
        }

        /// <summary>Calculate current BAC using DateTime.UtcNow</summary>
        public double CalculateCurrentBAC(UserProfile profile, IEnumerable<Drink> drinks, DateTime now)
            => CalculateBACAtTime(profile, drinks, now);

        // ── Time estimates ───────────────────────────────────────────────

        /// <summary>Estimate when BAC will reach 0.000 (completely sober)</summary>
        public DateTime EstimateSoberTime(UserProfile profile, IEnumerable<Drink> drinks, DateTime now)
        {
            double currentBac = CalculateCurrentBAC(profile, drinks, now);
            if (currentBac <= 0) return now;

            // BAC drops at MetabolicRate per hour; find when it hits 0
            double hoursToSober = currentBac / profile.MetabolicRate;
            return now.AddHours(hoursToSober);
        }

        /// <summary>Estimate when BAC will fall below the legal driving limit</summary>
        public DateTime EstimateSafeToDriveTime(UserProfile profile, IEnumerable<Drink> drinks, DateTime now, double legalLimit)
        {
            double currentBac = CalculateCurrentBAC(profile, drinks, now);
            if (currentBac <= legalLimit) return now;

            double hoursUntilSafe = (currentBac - legalLimit) / profile.MetabolicRate;
            return now.AddHours(hoursUntilSafe);
        }

        // ── BAC curve generation ─────────────────────────────────────────

        /// <summary>
        /// Generate a series of (time, BAC) data points for charting.
        /// Useful for showing the BAC curve over a session.
        /// </summary>
        public IEnumerable<(DateTime time, double bac)> GenerateBACCurve(
            UserProfile profile,
            IEnumerable<Drink> drinks,
            DateTime start,
            DateTime end,
            int intervalMinutes = 15)
        {
            var points = new List<(DateTime, double)>();
            var current = start;

            while (current <= end)
            {
                points.Add((current, CalculateBACAtTime(profile, drinks, current)));
                current = current.AddMinutes(intervalMinutes);
            }

            // Always include the endpoint
            if (points.Count == 0 || points[^1].Item1 < end)
            {
                points.Add((end, CalculateBACAtTime(profile, drinks, end)));
            }

            return points;
        }

        // ── Utility calculations ─────────────────────────────────────────

        /// <summary>
        /// Estimate total calories from all drinks.
        /// 7 kcal per gram of pure alcohol.
        /// </summary>
        public double EstimateCalories(IEnumerable<Drink> drinks)
        {
            double totalCalories = 0;
            foreach (var drink in drinks)
            {
                double alcoholGrams = drink.VolumeOz * MlPerOz * (drink.AbvPercent / 100.0) * EthanolDensity;
                totalCalories += alcoholGrams * CaloriesPerGramAlcohol;
            }
            return totalCalories;
        }

        /// <summary>
        /// Calculate number of standard drinks.
        /// 1 standard drink = 14g pure alcohol (USA).
        /// Formula: (VolumeOz × ABV/100 × 0.816) / 0.6
        /// </summary>
        public double CalculateStandardDrinks(double volumeOz, double abvPercent)
            => (volumeOz * (abvPercent / 100.0) * 0.816) / 0.6;

        /// <summary>Get a descriptive label for a BAC level</summary>
        public string GetBACZoneLabel(double bac)
        {
            return bac switch
            {
                <= 0.000 => "Sober",
                < 0.030 => "Minimal effect",
                < 0.060 => "Mild impairment",
                < 0.080 => "Moderate impairment",
                < 0.100 => "Legally impaired",
                < 0.150 => "High impairment",
                _ => "Severely impaired"
            };
        }

        /// <summary>True if BAC is at or above the provided driving limit</summary>
        public bool IsDrivingImpaired(double bac, double driveLimit) => bac >= driveLimit;

        /// <summary>True if BAC is approaching the personal limit (within 0.01)</summary>
        public bool IsApproachingPersonalLimit(double bac, double personalLimit)
            => bac >= (personalLimit - 0.01) && bac < personalLimit;
    }
}
