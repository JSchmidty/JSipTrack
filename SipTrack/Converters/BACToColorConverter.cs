using Microsoft.Maui.Controls;
using Microsoft.Maui.Graphics;
using System;
using System.Globalization;

namespace SipTrack.Converters
{
    /// <summary>
    /// Converts a BAC double to a colour for UI binding.
    /// Green  = safe  (< 0.04)
    /// Amber  = caution (0.04 - 0.079)
    /// Red    = danger (>= 0.08)
    /// </summary>
    public class BACToColorConverter : IValueConverter
    {
        public object Convert(object? value, Type targetType, object? parameter, CultureInfo culture)
        {
            if (value is not double bac) return Colors.Green;

            return bac switch
            {
                < 0.04 => Color.FromArgb("#4CAF50"),
                < 0.08 => Color.FromArgb("#FFC107"),
                _      => Color.FromArgb("#F44336")
            };
        }

        public object ConvertBack(object? value, Type targetType, object? parameter, CultureInfo culture)
            => throw new NotImplementedException();
    }

    /// <summary>Returns the inverse of a bool — used for IsVisible toggling.</summary>
    public class InverseBoolConverter : IValueConverter
    {
        public object Convert(object? value, Type targetType, object? parameter, CultureInfo culture)
            => value is bool b && !b;

        public object ConvertBack(object? value, Type targetType, object? parameter, CultureInfo culture)
            => value is bool b && !b;
    }

    /// <summary>Returns true when object is not null.</summary>
    public class IsNotNullConverter : IValueConverter
    {
        public object Convert(object? value, Type targetType, object? parameter, CultureInfo culture)
            => value is not null;

        public object ConvertBack(object? value, Type targetType, object? parameter, CultureInfo culture)
            => throw new NotImplementedException();
    }

    /// <summary>Compares a string binding to ConverterParameter — used for onboarding carousel steps.</summary>
    public class StringEqualConverter : IValueConverter
    {
        public object Convert(object? value, Type targetType, object? parameter, CultureInfo culture)
            => value is string s && parameter is string p && s == p;

        public object ConvertBack(object? value, Type targetType, object? parameter, CultureInfo culture)
            => throw new NotImplementedException();
    }
}
