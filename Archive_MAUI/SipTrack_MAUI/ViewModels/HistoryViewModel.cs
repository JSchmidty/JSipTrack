using CommunityToolkit.Mvvm.ComponentModel;
using CommunityToolkit.Mvvm.Input;
using SipTrack.Models;
using SipTrack.Services;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.IO;
using System.Linq;
using System.Threading.Tasks;

namespace SipTrack.ViewModels
{
    public class WeekGroup
    {
        public string WeekLabel { get; set; } = string.Empty;
        public List<DrinkSession> Sessions { get; set; } = new();
        public int TotalDrinks => Sessions.Sum(s => s.DrinkCount);
        public double MaxBac => Sessions.Count > 0 ? Sessions.Max(s => s.PeakBac) : 0;
        public double AvgBac => Sessions.Count > 0 ? Sessions.Average(s => s.PeakBac) : 0;
    }

    public partial class HistoryViewModel : BaseViewModel
    {
        private readonly DatabaseService _db;

        [ObservableProperty] private int _totalDrinksThisWeek;
        [ObservableProperty] private int _drinkFreeDaysThisWeek;
        [ObservableProperty] private int _currentStreak;
        [ObservableProperty] private double _maxBacThisWeek;
        [ObservableProperty] private double _avgBacThisWeek;
        [ObservableProperty] private DrinkSession? _selectedSession;

        public ObservableCollection<WeekGroup> WeekGroups { get; } = new();
        public ObservableCollection<DrinkSession> AllSessions { get; } = new();

        public HistoryViewModel(DatabaseService db) { _db = db; Title = "History"; }

        [RelayCommand]
        private async Task LoadHistoryAsync()
        {
            await ExecuteSafelyAsync(async () =>
            {
                var sessions = await _db.GetAllSessionsAsync();
                AllSessions.Clear();
                WeekGroups.Clear();

                foreach (var s in sessions) AllSessions.Add(s);

                // Weekly stats (last 7 days)
                var now = DateTime.UtcNow;
                var weekStart = now.AddDays(-7);
                var thisWeek = sessions.Where(s => s.StartTime >= weekStart).ToList();
                TotalDrinksThisWeek = thisWeek.Sum(s => s.DrinkCount);
                MaxBacThisWeek = thisWeek.Count > 0 ? thisWeek.Max(s => s.PeakBac) : 0;
                AvgBacThisWeek = thisWeek.Count > 0 ? thisWeek.Average(s => s.PeakBac) : 0;

                var daysWithDrinks = thisWeek.Select(s => s.StartTime.Date).Distinct().Count();
                DrinkFreeDaysThisWeek = 7 - daysWithDrinks;

                // Streak calculation
                CurrentStreak = CalculateStreak(sessions);

                // Group by week
                var grouped = sessions
                    .GroupBy(s => GetWeekLabel(s.StartTime))
                    .Select(g => new WeekGroup { WeekLabel = g.Key, Sessions = g.ToList() });

                foreach (var g in grouped) WeekGroups.Add(g);
            }, "LoadHistory");
        }

        [RelayCommand]
        private async Task ExportCsvAsync()
        {
            await ExecuteSafelyAsync(async () =>
            {
                var csv = await _db.ExportToCsvAsync();
                var path = Path.Combine(FileSystem.CacheDirectory, $"siptrack_export_{DateTime.Now:yyyyMMdd_HHmmss}.csv");
                await File.WriteAllTextAsync(path, csv);
                await Share.RequestAsync(new ShareFileRequest
                {
                    Title = "SipTrack Export",
                    File = new ShareFile(path)
                });
            }, "ExportCsv");
        }

        [RelayCommand]
        private async Task SelectSessionAsync(DrinkSession session)
        {
            SelectedSession = session;
            await Shell.Current.GoToAsync("SessionDetailPage", new Dictionary<string, object>
            {
                ["Session"] = session
            });
        }

        private int CalculateStreak(List<DrinkSession> sessions)
        {
            var today = DateTime.UtcNow.Date;
            int streak = 0;
            var daysWithDrinks = new HashSet<DateTime>(
                sessions.Where(s => s.DrinkCount > 0).Select(s => s.StartTime.Date));

            for (var day = today; ; day = day.AddDays(-1))
            {
                if (daysWithDrinks.Contains(day)) break;
                streak++;
                if (streak > 365) break;
            }
            return streak;
        }

        private string GetWeekLabel(DateTime date)
        {
            var startOfWeek = date.Date.AddDays(-(int)date.DayOfWeek);
            var endOfWeek = startOfWeek.AddDays(6);
            return $"{startOfWeek:MMM d} – {endOfWeek:MMM d, yyyy}";
        }
    }
}
