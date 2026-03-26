using CommunityToolkit.Mvvm.ComponentModel;
using System;
using System.Threading.Tasks;

namespace SipTrack.ViewModels
{
    public partial class BaseViewModel : ObservableObject
    {
        [ObservableProperty]
        [NotifyPropertyChangedFor(nameof(IsNotBusy))]
        private bool _isBusy;

        [ObservableProperty]
        private string _title = string.Empty;

        [ObservableProperty]
        private string _errorMessage = string.Empty;

        [ObservableProperty]
        private bool _hasError;

        public bool IsNotBusy => !IsBusy;

        protected async Task ExecuteSafelyAsync(Func<Task> action, string? errorContext = null)
        {
            if (IsBusy) return;
            try
            {
                IsBusy = true;
                HasError = false;
                ErrorMessage = string.Empty;
                await action();
            }
            catch (Exception ex)
            {
                HasError = true;
                ErrorMessage = errorContext != null ? $"{errorContext}: {ex.Message}" : ex.Message;
                System.Diagnostics.Debug.WriteLine($"ViewModel error [{errorContext}]: {ex}");
            }
            finally
            {
                IsBusy = false;
            }
        }
    }
}
