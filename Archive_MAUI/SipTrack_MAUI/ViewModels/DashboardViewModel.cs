using CommunityToolkit.Mvvm.ComponentModel;
using CommunityToolkit.Mvvm.Input;
using Microsoft.Maui.Graphics;
using SipTrack.Models;
using SipTrack.Services;
using System;
using System.Threading;
using System.Threading.Tasks;

namespace SipTrack.ViewModels
{
    public partial class DashboardViewModel : BaseViewModel
    {
        private readonly DatabaseService _db;
        private readonly BACCalculatorService _bacCalc;
        private readonly NotificationService _notifications;
        private Timer? _refreshTimer;

        [ObservableProperty] private double _currentBAC;
        [ObservableProperty][NotifyPropertyChangedFor(nameof(BACColorHex))] private Color _bACColor = Colors.Green;
        [ObservableProperty] private TimeSpan _safeToDriveIn;
        [ObservableProperty] private TimeSpan _soberIn;
        [ObservableProperty] private DrinkSession? _activeSession;
        [ObservableProperty] private int _drinkCount;
        [ObservableProperty] private double _caloriesConsumed;
        [ObservableProperty] private string _sessionDuration = "0m";
        [ObservableProperty] private SessionMode _appMode = SessionMode.Normal;
        [ObservableProperty] private bool _isSafeToDrive = true;
        [ObservableProperty] private string _statusLabel = "You are SAFE to drive";
        [ObservableProperty] private UserProfile? _userProfile;
        [ObservableProperty] private bool _isDesignatedDriver;
        [ObservableProperty] private string _bacZoneLabel = "Sober";

        public string BACColorHex => BACColor == Colors.Red ? "#F44336"
                                   : BACColor.ToArgbHex() == "FFC107" ? "#FFC107"
                                   : "#4CAF50";

        public DashboardViewModel(DatabaseService db, BACCalculatorService bacCalc, NotificationService notifications)
        {
            _db = db; _bacCalc = bacCalc; _notifications = notifications;
            Title = "Dashboard";
        }

        public async Task InitializeAsync()
        {
            await LoadDataAsync();
            StartRefreshTimer();
        }

        public void Dispose() => _refreshTimer?.Dispose();

        private void StartRefreshTimer()
        {
            _refreshTimer?.Dispose();
            _refreshTimer = new Timer(async _ =>
            {
                await MainThread.InvokeOnMainThreadAsync(RefreshBACAsync);
            }, null, TimeSpan.FromSeconds(60), TimeSpan.FromSeconds(60));
        }

        [RelayCommand]
        private async Task LoadDataAsync()
        {
            await ExecuteSafelyAsync(async () =>
            {
                UserProfile = await _db.GetProfileAsync();
                ActiveSession = await _db.GetActiveSessionAsync();
                AppMode = UserProfile.AppMode;
                IsDesignatedDriver = AppMode == SessionMode.DesignatedDriver;
                await RefreshBACAsync();
            }, "LoadData");
        }

        [RelayCommand]
        private async Task RefreshBACAsync()
        {
            try
            {
                var profile = UserProfile ?? await _db.GetProfileAsync();
                var session = ActiveSession ?? await _db.GetActiveSessionAsync();

                if (session == null || session.Drinks.Count == 0)
                {
                    CurrentBAC = 0; BACColor = Colors.Green; IsSafeToDrive = true;
                    StatusLabel = "You are SAFE to drive"; SafeToDriveIn = TimeSpan.Zero;
                    SoberIn = TimeSpan.Zero; DrinkCount = session?.Drinks.Count ?? 0;
                    CaloriesConsumed = 0; BACZoneLabel = "Sober"; return;
                }

                var now = DateTime.UtcNow;
                double bac = _bacCalc.CalculateCurrentBAC(profile, session.Drinks, now);
                CurrentBAC = bac;
                await _db.UpdateSessionPeakBacAsync(session.Id, bac);

                if (bac < 0.04) BACColor = Colors.Green;
                else if (bac < 0.08) BACColor = Color.FromArgb("#FFC107");
                else BACColor = Colors.Red;

                BACZoneLabel = _bacCalc.GetBACZoneLabel(bac);
                IsSafeToDrive = bac < profile.DriveLimit;
                StatusLabel = IsSafeToDrive ? "You are SAFE to drive" : "DO NOT DRIVE";

                var safeToDriveTime = _bacCalc.EstimateSafeToDriveTime(profile, session.Drinks, now, profile.DriveLimit);
                var soberTime = _bacCalc.EstimateSoberTime(profile, session.Drinks, now);
                SafeToDriveIn = safeToDriveTime > now ? safeToDriveTime - now : TimeSpan.Zero;
                SoberIn = soberTime > now ? soberTime - now : TimeSpan.Zero;
                DrinkCount = session.Drinks.Count;
                CaloriesConsumed = _bacCalc.EstimateCalories(session.Drinks);
                SessionDuration = session.DurationDisplay;

                if (_bacCalc.IsApproachingPersonalLimit(bac, profile.PersonalLimit) && profile.EnableNotifications)
                    await _notifications.ScheduleBACWarningAsync(bac, profile.PersonalLimit, TimeSpan.FromMinutes(5));
            }
            catch (Exception ex) { System.Diagnostics.Debug.WriteLine($"RefreshBAC error: {ex.Message}"); }
        }

        [RelayCommand]
        private async Task EndSessionAsync()
        {
            if (ActiveSession == null) return;
            await ExecuteSafelyAsync(async () =>
            {
                await _db.EndSessionAsync(ActiveSession.Id);
                await _notifications.CancelAllNotificationsAsync();
                ActiveSession = null;
                await RefreshBACAsync();
            }, "EndSession");
        }

        [RelayCommand]
        private async Task EmergencyAlertAsync()
        {
            var profile = UserProfile ?? await _db.GetProfileAsync();
            await _notifications.SendEmergencyAlertAsync(profile.EmergencyContactPhone);
        }

        [RelayCommand]
        private async Task CallUberAsync() => await _notifications.OpenUberAsync();

        [RelayCommand]
        private async Task SwitchModeAsync(SessionMode mode)
        {
            await ExecuteSafelyAsync(async () =>
            {
                var profile = UserProfile ?? await _db.GetProfileAsync();
                profile.AppMode = mode;
                await _db.SaveProfileAsync(profile);
                UserProfile = profile;
                AppMode = mode;
                IsDesignatedDriver = mode == SessionMode.DesignatedDriver;
            }, "SwitchMode");
        }

        [RelayCommand]
        private async Task ToggleDesignatedDriverAsync()
        {
            var newMode = AppMode == SessionMode.DesignatedDriver ? SessionMode.Normal : SessionMode.DesignatedDriver;
            await SwitchModeAsync(newMode);
        }
    }
}
