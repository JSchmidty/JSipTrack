using CommunityToolkit.Mvvm.ComponentModel;
using CommunityToolkit.Mvvm.Input;
using SipTrack.Models;
using SipTrack.Services;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Threading.Tasks;

namespace SipTrack.ViewModels
{
    public record QuickDrink(string Name, string Icon, DrinkCategory Category, double AbvPercent, double VolumeOz);

    public partial class LogDrinkViewModel : BaseViewModel
    {
        private readonly DatabaseService _db;
        private readonly BACCalculatorService _bacCalc;
        private readonly BeverageApiService _beverageApi;

        // Quick drinks
        public List<QuickDrink> QuickDrinks { get; } = new()
        {
            new("Beer",      "🍺", DrinkCategory.Beer,    5.0,  12.0),
            new("Wine",      "🍷", DrinkCategory.Wine,    12.0,  5.0),
            new("Shot",      "🥃", DrinkCategory.Spirit,  40.0,  1.5),
            new("Cocktail",  "🍹", DrinkCategory.Cocktail,15.0,  4.0),
            new("Taste",     "🔬", DrinkCategory.Custom,  40.0,  0.5),
            new("Custom",    "✏️", DrinkCategory.Custom,   0.0,  0.0),
        };

        // Custom drink fields
        [ObservableProperty] private string _customName = string.Empty;
        [ObservableProperty] private double _customAbv = 5.0;
        [ObservableProperty] private double _customVolume = 12.0;
        [ObservableProperty] private bool _useOz = true;
        [ObservableProperty] private string _searchQuery = string.Empty;
        [ObservableProperty] private bool _showCustomForm;
        [ObservableProperty] private string _volumeUnit = "oz";
        [ObservableProperty] private DrinkCategory _selectedCategory = DrinkCategory.Beer;

        // Search results
        public ObservableCollection<BeverageApiService.BeverageResult> SearchResults { get; } = new();

        public LogDrinkViewModel(DatabaseService db, BACCalculatorService bacCalc, BeverageApiService beverageApi)
        {
            _db = db; _bacCalc = bacCalc; _beverageApi = beverageApi;
            Title = "Log a Drink";
        }

        [RelayCommand]
        private async Task LogQuickDrinkAsync(QuickDrink quick)
        {
            if (quick.Name == "Custom") { ShowCustomForm = true; return; }
            await ExecuteSafelyAsync(async () =>
            {
                var session = await GetOrCreateSessionAsync();
                var drink = new Drink
                {
                    Name = quick.Name,
                    Category = quick.Category,
                    AbvPercent = quick.AbvPercent,
                    VolumeOz = quick.VolumeOz,
                    LoggedAt = DateTime.UtcNow,
                    SessionId = session.Id
                };
                await _db.SaveDrinkAsync(drink);
                await Shell.Current.GoToAsync("..");
            }, "LogQuickDrink");
        }

        [RelayCommand]
        private async Task LogCustomDrinkAsync()
        {
            if (string.IsNullOrWhiteSpace(CustomName)) return;
            await ExecuteSafelyAsync(async () =>
            {
                var session = await GetOrCreateSessionAsync();
                double volumeOz = UseOz ? CustomVolume : CustomVolume / 29.5735;
                var drink = new Drink
                {
                    Name = CustomName,
                    Category = SelectedCategory,
                    AbvPercent = CustomAbv,
                    VolumeOz = volumeOz,
                    LoggedAt = DateTime.UtcNow,
                    SessionId = session.Id,
                    IsCustom = true
                };
                await _db.SaveDrinkAsync(drink);
                await Shell.Current.GoToAsync("..");
            }, "LogCustomDrink");
        }

        [RelayCommand]
        private async Task SearchBeveragesAsync()
        {
            if (string.IsNullOrWhiteSpace(SearchQuery)) return;
            await ExecuteSafelyAsync(async () =>
            {
                var results = await _beverageApi.SearchBeveragesAsync(SearchQuery);
                SearchResults.Clear();
                foreach (var r in results) SearchResults.Add(r);
            }, "SearchBeverages");
        }

        [RelayCommand]
        private async Task LogSearchResultAsync(BeverageApiService.BeverageResult result)
        {
            await ExecuteSafelyAsync(async () =>
            {
                var session = await GetOrCreateSessionAsync();
                double defaultVol = result.Category.ToLower() switch
                {
                    "beer" => 12.0, "wine" => 5.0, "spirit" or "spirits" => 1.5, _ => 4.0
                };
                var drink = _beverageApi.BeverageResultToDrink(result, defaultVol);
                drink.SessionId = session.Id;
                await _db.SaveDrinkAsync(drink);
                await Shell.Current.GoToAsync("..");
            }, "LogSearchResult");
        }

        private async Task<DrinkSession> GetOrCreateSessionAsync()
        {
            var session = await _db.GetActiveSessionAsync();
            if (session == null)
            {
                var profile = await _db.GetProfileAsync();
                session = await _db.CreateSessionAsync(profile.AppMode);
            }
            return session;
        }
    }
}
