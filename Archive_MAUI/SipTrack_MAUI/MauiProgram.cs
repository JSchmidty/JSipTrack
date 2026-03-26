using CommunityToolkit.Maui;
using Microsoft.Maui.Controls.Hosting;
using Microsoft.Maui.Hosting;
using Plugin.LocalNotification;
using SipTrack.Services;
using SipTrack.ViewModels;
using SipTrack.Views;

namespace SipTrack;

public static class MauiProgram
{
    public static MauiApp CreateMauiApp()
    {
        var builder = MauiApp.CreateBuilder();

        builder
            .UseMauiApp<App>()
            .UseMauiCommunityToolkit()
            .UseLocalNotification()
            .ConfigureFonts(fonts =>
            {
                fonts.AddFont("OpenSans-Regular.ttf", "OpenSansRegular");
                fonts.AddFont("OpenSans-Semibold.ttf", "OpenSansSemibold");
            });

        // ── Services (singletons) ────────────────────────────────────────
        builder.Services.AddSingleton<DatabaseService>();
        builder.Services.AddSingleton<BACCalculatorService>();
        builder.Services.AddSingleton<NotificationService>();
        builder.Services.AddSingleton<BeverageApiService>();

        // ── ViewModels (transient — recreated per navigation) ────────────
        builder.Services.AddTransient<DashboardViewModel>();
        builder.Services.AddTransient<LogDrinkViewModel>();
        builder.Services.AddTransient<HistoryViewModel>();
        builder.Services.AddTransient<SettingsViewModel>();

        // ── Views ────────────────────────────────────────────────────────
        builder.Services.AddTransient<DashboardPage>();
        builder.Services.AddTransient<LogDrinkPage>();
        builder.Services.AddTransient<HistoryPage>();
        builder.Services.AddTransient<SessionDetailPage>();
        builder.Services.AddTransient<SettingsPage>();
        builder.Services.AddTransient<OnboardingPage>();

        return builder.Build();
    }
}
