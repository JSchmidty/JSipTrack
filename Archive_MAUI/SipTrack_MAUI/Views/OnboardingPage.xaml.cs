using SipTrack.Models;
using SipTrack.Services;

namespace SipTrack.Views;

public partial class OnboardingPage : ContentPage
{
    private readonly DatabaseService _db;
    private int _currentStep = 0;

    public OnboardingPage(DatabaseService db)
    {
        InitializeComponent();
        _db = db;
    }

    private async void OnNextClicked(object sender, EventArgs e)
    {
        if (_currentStep < 2)
        {
            _currentStep++;
            OnboardingCarousel.Position = _currentStep;
            if (_currentStep == 2) ((Button)sender).Text = "Finish";
        }
        else
        {
            await SaveOnboardingDataAsync();
            Application.Current!.MainPage = new AppShell();
        }
    }

    private async void OnSkipClicked(object sender, EventArgs e)
    {
        var profile = await _db.GetProfileAsync();
        profile.OnboardingComplete = true;
        await _db.SaveProfileAsync(profile);
        Application.Current!.MainPage = new AppShell();
    }

    private async Task SaveOnboardingDataAsync()
    {
        var profile = await _db.GetProfileAsync();

        if (double.TryParse(WeightEntry?.Text, out var weight)) profile.WeightLbs = weight;
        if (int.TryParse(AgeEntry?.Text, out var age)) profile.Age = age;
        if (GenderPicker?.SelectedItem is string gender) profile.Gender = Enum.Parse<Gender>(gender);

        profile.EmergencyContactName = EmergencyNameEntry?.Text ?? string.Empty;
        profile.EmergencyContactPhone = EmergencyPhoneEntry?.Text ?? string.Empty;
        profile.OnboardingComplete = true;

        await _db.SaveProfileAsync(profile);
    }
}
