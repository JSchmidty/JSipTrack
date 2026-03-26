using Foundation;
using UIKit;
using Plugin.LocalNotification;

namespace SipTrack;

[Register("AppDelegate")]
public class AppDelegate : MauiUIApplicationDelegate
{
    protected override MauiApp CreateMauiApp() => MauiProgram.CreateMauiApp();

    public override bool FinishedLaunching(UIApplication application, NSDictionary launchOptions)
    {
        LocalNotificationCenter.NotifyNotificationTapped(application, launchOptions);
        return base.FinishedLaunching(application, launchOptions);
    }
}
