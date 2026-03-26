using SipTrack.Models;

namespace SipTrack.Views;

[QueryProperty(nameof(Session), "Session")]
public partial class SessionDetailPage : ContentPage
{
    private DrinkSession? _session;
    public DrinkSession? Session { get => _session; set { _session = value; BindingContext = this; OnPropertyChanged(); } }
    public SessionDetailPage() { InitializeComponent(); BindingContext = this; }
}
