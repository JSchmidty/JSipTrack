using System;
using System.Collections.Generic;
using System.Net.Http;
using System.Net.Http.Json;
using System.Threading.Tasks;
using SipTrack.Models;

namespace SipTrack.Services
{
    /// <summary>
    /// Connects to the SipTrack Node.js/PostgreSQL beverage API.
    /// Base URL is configurable via AppSettings (defaults to localhost:3000 for dev).
    /// </summary>
    public class BeverageApiService
    {
        private readonly HttpClient _httpClient;
        private string _baseUrl;

        // ── Response DTOs ────────────────────────────────────────────────

        public record BeverageResult(
            string Id,
            string Name,
            string Brand,
            string Category,
            string Subcategory,
            double Abv,
            string Description,
            string[] FlavorProfile,
            string ImageUrl
        );

        public record SearchResponse(List<BeverageResult> Data, int Total, int Page);

        // ── Constructor ──────────────────────────────────────────────────

        public BeverageApiService(string baseUrl = "http://localhost:3000")
        {
            _baseUrl = baseUrl.TrimEnd('/');
            _httpClient = new HttpClient
            {
                Timeout = TimeSpan.FromSeconds(10)
            };
        }

        public void SetBaseUrl(string url) => _baseUrl = url.TrimEnd('/');

        // ── API Methods ──────────────────────────────────────────────────

        public async Task<bool> CheckHealthAsync()
        {
            try
            {
                var response = await _httpClient.GetAsync($"{_baseUrl}/api/v1/health");
                return response.IsSuccessStatusCode;
            }
            catch { return false; }
        }

        /// <summary>Search beverages by query string and optional category</summary>
        public async Task<List<BeverageResult>> SearchBeveragesAsync(
            string query,
            string? category = null,
            int limit = 20)
        {
            try
            {
                var url = $"{_baseUrl}/api/v1/beverages/search?q={Uri.EscapeDataString(query)}&limit={limit}";
                if (!string.IsNullOrWhiteSpace(category))
                    url += $"&category={Uri.EscapeDataString(category)}";

                var response = await _httpClient.GetFromJsonAsync<SearchResponse>(url);
                return response?.Data ?? new List<BeverageResult>();
            }
            catch (Exception ex)
            {
                System.Diagnostics.Debug.WriteLine($"BeverageApiService.Search error: {ex.Message}");
                return new List<BeverageResult>();
            }
        }

        /// <summary>Get a single beverage by ID</summary>
        public async Task<BeverageResult?> GetBeverageAsync(string id)
        {
            try
            {
                return await _httpClient.GetFromJsonAsync<BeverageResult>(
                    $"{_baseUrl}/api/v1/beverages/{id}");
            }
            catch (Exception ex)
            {
                System.Diagnostics.Debug.WriteLine($"BeverageApiService.Get error: {ex.Message}");
                return null;
            }
        }

        /// <summary>Convert a BeverageResult into a Drink model for logging</summary>
        public Drink BeverageResultToDrink(BeverageResult beverage, double volumeOz)
        {
            return new Drink
            {
                Name = beverage.Name,
                AbvPercent = beverage.Abv,
                VolumeOz = volumeOz,
                Category = beverage.Category.ToLower() switch
                {
                    "beer" => DrinkCategory.Beer,
                    "wine" => DrinkCategory.Wine,
                    "spirit" or "spirits" => DrinkCategory.Spirit,
                    "cocktail" or "cocktails" => DrinkCategory.Cocktail,
                    _ => DrinkCategory.Custom
                },
                IsCustom = false,
                LoggedAt = DateTime.UtcNow
            };
        }
    }
}
