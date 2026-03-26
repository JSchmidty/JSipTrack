namespace SipTrack;

public partial class AppShell : Shell
{
    public AppShell()
    {
        InitializeComponent();
        // Register modal routes
        Routing.RegisterRoute("SessionDetailPage", typeof(Views.SessionDetailPage));
    }
}
