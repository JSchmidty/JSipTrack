using SipTrack.ViewModels;

namespace SipTrack.Views;

public partial class DashboardPage : ContentPage
{
    private readonly DashboardViewModel _vm;

    public DashboardPage(DashboardViewModel vm)
    {
        InitializeComponent();
        _vm = vm;
        BindingContext = vm;
    }

    protected override async void OnAppearing()
    {
        base.OnAppearing();
        await _vm.InitializeAsync();
    }

    protected override void OnDisappearing()
    {
        base.OnDisappearing();
        _vm.Dispose();
    }

    private async void OnLogDrinkClicked(object sender, EventArgs e)
        => await Shell.Current.GoToAsync("//LogDrink");

    private async void OnSettingsClicked(object sender, EventArgs e)
        => await Shell.Current.GoToAsync("//Settings");
}
