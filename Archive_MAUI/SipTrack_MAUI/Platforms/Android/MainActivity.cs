using Android.App;
using Android.Content.PM;
using Microsoft.Maui;
using Plugin.LocalNotification;

namespace SipTrack;

[Activity(
    Theme = "@style/Maui.SplashTheme",
    MainLauncher = true,
    LaunchMode = LaunchMode.SingleTask,
    ConfigurationChanges = ConfigChanges.ScreenSize | ConfigChanges.Orientation |
                           ConfigChanges.UiMode | ConfigChanges.ScreenLayout |
                           ConfigChanges.SmallestScreenSize | ConfigChanges.Density)]
public class MainActivity : MauiAppCompatActivity
{
    protected override void OnCreate(Android.OS.Bundle? savedInstanceState)
    {
        base.OnCreate(savedInstanceState);
        LocalNotificationCenter.CreateNotificationChannel(this);
        LocalNotificationCenter.NotifyNotificationTapped(Intent);
    }
}
