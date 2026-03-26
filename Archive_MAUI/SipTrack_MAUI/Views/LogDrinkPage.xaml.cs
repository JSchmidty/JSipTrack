using SipTrack.ViewModels;

namespace SipTrack.Views;

public partial class LogDrinkPage : ContentPage
{
    public LogDrinkPage(LogDrinkViewModel vm)
    {
        InitializeComponent();
        BindingContext = vm;
    }
}
