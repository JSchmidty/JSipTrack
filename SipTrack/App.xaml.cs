using SipTrack.Services;
using SipTrack.Views;

namespace SipTrack;

public partial class App : Application
{
    private readonly DatabaseService _db;

    public App(DatabaseService db)
    {
        InitializeComponent();
        _db = db;
    }

    protected override async void OnStart()
    {
        base.OnStart();
        var profile = await _db.GetProfileAsync();
        MainPage = profile.OnboardingComplete ? new AppShell() : new OnboardingPage(_db);
    }
}
