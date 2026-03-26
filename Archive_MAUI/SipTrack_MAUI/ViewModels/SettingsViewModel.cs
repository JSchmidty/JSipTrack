using CommunityToolkit.Mvvm.ComponentModel;
using CommunityToolkit.Mvvm.Input;
using SipTrack.Models;
using SipTrack.Services;
using System;
using System.Threading.Tasks;

namespace SipTrack.ViewModels
{
    public partial class SettingsViewModel : BaseViewModel
    {
        private readonly DatabaseService _db;
        private readonly NotificationService _notifications;

        [ObservableProperty] private double _weightLbs = 160;
        [ObservableProperty] private double _weightKg = 72.5;
        [ObservableProperty] private int _age = 25;
        [ObservableProperty] private string _selectedGender = "Male";
        [ObservableProperty] private double _personalLimit = 0.05;
        [ObservableProperty] private double _driveLimit = 0.08;
        [ObservableProperty] private string _emergencyContactName = string.Empty;
        [ObservableProperty] private string _emergencyContactPhone = string.Empty;
        [ObservableProperty] private bool _enableNotifications = true;
        [ObservableProperty] private bool _preferMetric;
        [ObservableProperty] private bool _enableHealthKit;
        [ObservableProperty] private string _selectedAppMode = "Normal";
        [ObservableProperty] private bool _isDarkMode;

        public string[] GenderOptions { get; } = { "Male", "Female", "Other" };
        public string[] AppModeOptions { get; } = { "Normal", "Discreet", "ProfessionalTasting", "Recovery", "DesignatedDriver" };

        public SettingsViewModel(DatabaseService db, NotificationService notifications)
        {
            _db = db; _notifications = notifications;
            Title = "Settings";
        }

        [RelayCommand]
        private async Task LoadSettingsAsync()
        {
            await ExecuteSafelyAsync(async () =>
            {
                var profile = await _db.GetProfileAsync();
                WeightLbs = profile.WeightLbs;
                WeightKg = profile.WeightKg;
                Age = profile.Age;
                SelectedGender = profile.Gender.ToString();
                PersonalLimit = profile.PersonalLimit;
                DriveLimit = profile.DriveLimit;
                EmergencyContactName = profile.EmergencyContactName;
                EmergencyContactPhone = profile.EmergencyContactPhone;
                EnableNotifications = profile.EnableNotifications;
                PreferMetric = profile.PreferMetric;
                EnableHealthKit = profile.EnableHealthKit;
                SelectedAppMode = profile.AppMode.ToString();
            }, "LoadSettings");
        }

        [RelayCommand]
        private async Task SaveProfileAsync()
        {
            await ExecuteSafelyAsync(async () =>
            {
                var profile = await _db.GetProfileAsync();
                profile.WeightLbs = WeightLbs;
                profile.Age = Age;
                profile.Gender = Enum.Parse<Gender>(SelectedGender, true);
                profile.PersonalLimit = PersonalLimit;
                profile.DriveLimit = DriveLimit;
                profile.EmergencyContactName = EmergencyContactName;
                profile.EmergencyContactPhone = EmergencyContactPhone;
                profile.EnableNotifications = EnableNotifications;
                profile.PreferMetric = PreferMetric;
                profile.EnableHealthKit = EnableHealthKit;
                profile.AppMode = Enum.Parse<SessionMode>(SelectedAppMode, true);
                profile.OnboardingComplete = true;
                await _db.SaveProfileAsync(profile);

                if (EnableNotifications)
                    await _notifications.RequestPermissionAsync();

                await Shell.Current.DisplayAlert("Saved", "Your profile has been updated.", "OK");
            }, "SaveProfile");
        }

        [RelayCommand]
        private async Task SetEmergencyContactAsync()
        {
            var name = await Shell.Current.DisplayPromptAsync("Emergency Contact", "Contact name:", initialValue: EmergencyContactName);
            if (name == null) return;
            var phone = await Shell.Current.DisplayPromptAsync("Emergency Contact", "Phone number:", keyboard: Keyboard.Telephone, initialValue: EmergencyContactPhone);
            if (phone == null) return;
            EmergencyContactName = name;
            EmergencyContactPhone = phone;
        }

        [RelayCommand]
        private async Task ResetDataAsync()
        {
            bool confirmed = await Shell.Current.DisplayAlert(
                "Reset All Data",
                "This will delete ALL your drink history and reset your profile. This cannot be undone.",
                "Delete Everything",
                "Cancel");

            if (!confirmed) return;

            await ExecuteSafelyAsync(async () =>
            {
                await _db.ResetAllDataAsync();
                await LoadSettingsAsync();
                await Shell.Current.DisplayAlert("Done", "All data has been reset.", "OK");
            }, "ResetData");
        }
    }
}
