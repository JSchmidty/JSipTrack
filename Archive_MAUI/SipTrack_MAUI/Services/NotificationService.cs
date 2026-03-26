using System;
using System.Threading.Tasks;
using SipTrack.Models;

namespace SipTrack.Services
{
    /// <summary>
    /// Manages local push notifications for BAC warnings, reminders to drink water,
    /// and "safe to drive" alerts using Plugin.LocalNotification.
    /// </summary>
    public class NotificationService
    {
        private const int BacWarningNotificationId = 1001;
        private const int SafeToDriveNotificationId = 1002;
        private const int DrinkWaterReminderId = 1003;

        public async Task<bool> RequestPermissionAsync()
        {
            try
            {
#if IOS || ANDROID
                return await Plugin.LocalNotification.LocalNotificationCenter.Current.RequestNotificationPermission();
#else
                return await Task.FromResult(false);
#endif
            }
            catch { return false; }
        }

        public async Task ScheduleBACWarningAsync(double currentBac, double personalLimit, TimeSpan timeUntilLimit)
        {
            try
            {
                if (currentBac >= personalLimit) return;

                var notification = new Plugin.LocalNotification.NotificationRequest
                {
                    NotificationId = BacWarningNotificationId,
                    Title = "⚠️ SipTrack BAC Alert",
                    Description = $"You're approaching your personal limit of {personalLimit:F2}. Consider slowing down.",
                    Schedule = new Plugin.LocalNotification.NotificationRequestSchedule
                    {
                        NotifyTime = DateTime.Now + timeUntilLimit
                    }
                };

#if IOS || ANDROID
                await Plugin.LocalNotification.LocalNotificationCenter.Current.Show(notification);
#endif
            }
            catch (Exception ex)
            {
                System.Diagnostics.Debug.WriteLine($"ScheduleBACWarning error: {ex.Message}");
            }
        }

        public async Task ScheduleSafeToDriveAsync(DateTime safeToDriveTime)
        {
            try
            {
                if (safeToDriveTime <= DateTime.Now) return;

                var notification = new Plugin.LocalNotification.NotificationRequest
                {
                    NotificationId = SafeToDriveNotificationId,
                    Title = "✅ SipTrack – Safe to Drive",
                    Description = "Your BAC has dropped below the legal limit. Drive safely!",
                    Schedule = new Plugin.LocalNotification.NotificationRequestSchedule
                    {
                        NotifyTime = safeToDriveTime
                    }
                };

#if IOS || ANDROID
                await Plugin.LocalNotification.LocalNotificationCenter.Current.Show(notification);
#endif
            }
            catch (Exception ex)
            {
                System.Diagnostics.Debug.WriteLine($"ScheduleSafeToDrive error: {ex.Message}");
            }
        }

        public async Task ScheduleHydrationReminderAsync(int minutesFromNow = 30)
        {
            try
            {
                var notification = new Plugin.LocalNotification.NotificationRequest
                {
                    NotificationId = DrinkWaterReminderId,
                    Title = "💧 Hydration Reminder",
                    Description = "Have a glass of water between drinks. Stay safe!",
                    Schedule = new Plugin.LocalNotification.NotificationRequestSchedule
                    {
                        NotifyTime = DateTime.Now.AddMinutes(minutesFromNow)
                    }
                };

#if IOS || ANDROID
                await Plugin.LocalNotification.LocalNotificationCenter.Current.Show(notification);
#endif
            }
            catch (Exception ex)
            {
                System.Diagnostics.Debug.WriteLine($"ScheduleHydration error: {ex.Message}");
            }
        }

        public async Task CancelAllNotificationsAsync()
        {
            try
            {
#if IOS || ANDROID
                await Plugin.LocalNotification.LocalNotificationCenter.Current.CancelAll();
#endif
            }
            catch { }
        }

        public async Task SendEmergencyAlertAsync(string contactPhone, string locationLink = "")
        {
            try
            {
                if (string.IsNullOrWhiteSpace(contactPhone)) return;
                var message = string.IsNullOrWhiteSpace(locationLink)
                    ? "I need help. Please call me — SipTrack emergency alert."
                    : $"I need help. My approximate location: {locationLink} — SipTrack emergency alert.";
                var smsUri = new Uri($"sms:{contactPhone}?body={Uri.EscapeDataString(message)}");
                if (await Launcher.CanOpenAsync(smsUri))
                    await Launcher.OpenAsync(smsUri);
                else
                    await PhoneDialer.Open(contactPhone);
            }
            catch (Exception ex)
            {
                System.Diagnostics.Debug.WriteLine($"SendEmergencyAlert error: {ex.Message}");
            }
        }

        public async Task OpenUberAsync()
        {
            try
            {
                var uberUri = new Uri("uber://");
                if (await Launcher.CanOpenAsync(uberUri))
                    await Launcher.OpenAsync(uberUri);
                else
                    await Browser.OpenAsync("https://m.uber.com", BrowserLaunchMode.SystemPreferred);
            }
            catch (Exception ex)
            {
                System.Diagnostics.Debug.WriteLine($"OpenUber error: {ex.Message}");
            }
        }
    }
}
