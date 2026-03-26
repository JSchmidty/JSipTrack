using Microsoft.Maui.Controls;
using Microsoft.Maui.Graphics;
using System;

namespace SipTrack.Controls
{
    /// <summary>
    /// Custom MAUI GraphicsView that draws a circular speedometer-style BAC gauge.
    /// 270-degree arc, color-coded: Green / Amber / Red based on BAC value.
    /// Supports animated transitions via the Value bindable property.
    /// </summary>
    public class BACGaugeView : GraphicsView, IDrawable
    {
        // ── Bindable Properties ──────────────────────────────────────────

        public static readonly BindableProperty ValueProperty =
            BindableProperty.Create(nameof(Value), typeof(double), typeof(BACGaugeView), 0.0,
                propertyChanged: (b, _, __) => ((BACGaugeView)b).Invalidate());

        public static readonly BindableProperty MaxValueProperty =
            BindableProperty.Create(nameof(MaxValue), typeof(double), typeof(BACGaugeView), 0.20,
                propertyChanged: (b, _, __) => ((BACGaugeView)b).Invalidate());

        public double Value { get => (double)GetValue(ValueProperty); set => SetValue(ValueProperty, Math.Max(0, value)); }
        public double MaxValue { get => (double)GetValue(MaxValueProperty); set => SetValue(MaxValueProperty, value); }

        public BACGaugeView()
        {
            Drawable = this;
            BackgroundColor = Colors.Transparent;
        }

        // ── IDrawable Implementation ─────────────────────────────────────

        public void Draw(ICanvas canvas, RectF dirtyRect)
        {
            float cx = dirtyRect.Width / 2f;
            float cy = dirtyRect.Height / 2f;
            float radius = Math.Min(cx, cy) - 16f;

            const float startAngle = 135f;   // degrees (bottom-left)
            const float sweepAngle = 270f;   // total arc

            // Background arc (dark gray)
            canvas.StrokeColor = Color.FromArgb("#2A2A2A");
            canvas.StrokeSize = 18f;
            canvas.StrokeLineCap = LineCap.Round;
            DrawArc(canvas, cx, cy, radius, startAngle, sweepAngle);

            // Value arc (color-coded)
            double ratio = MaxValue > 0 ? Math.Min(Value / MaxValue, 1.0) : 0;
            float valueSweep = (float)(sweepAngle * ratio);

            if (valueSweep > 0)
            {
                canvas.StrokeColor = GetBACColor(Value);
                canvas.StrokeSize = 18f;
                canvas.StrokeLineCap = LineCap.Round;
                DrawArc(canvas, cx, cy, radius, startAngle, valueSweep);
            }

            // Center BAC value text
            string bacText = Value.ToString("F3");
            canvas.FontColor = Colors.White;
            canvas.FontSize = 36f;
            canvas.DrawString(bacText, cx - 60, cy - 22, 120, 44, HorizontalAlignment.Center, VerticalAlignment.Center);

            // Sub label
            canvas.FontColor = Color.FromArgb("#AAAAAA");
            canvas.FontSize = 12f;
            canvas.DrawString("BAC", cx - 30, cy + 22, 60, 20, HorizontalAlignment.Center, VerticalAlignment.Center);

            // Min / Max tick labels
            canvas.FontColor = Color.FromArgb("#888888");
            canvas.FontSize = 11f;
            float labelOffset = radius + 28f;
            float startRad = ToRadians(startAngle);
            float endRad = ToRadians(startAngle + sweepAngle);
            float x0 = cx + labelOffset * (float)Math.Cos(startRad);
            float y0 = cy + labelOffset * (float)Math.Sin(startRad);
            float x1 = cx + labelOffset * (float)Math.Cos(endRad);
            float y1 = cy + labelOffset * (float)Math.Sin(endRad);
            canvas.DrawString("0.00", x0 - 18, y0 - 8, 36, 16, HorizontalAlignment.Center, VerticalAlignment.Center);
            canvas.DrawString($"{MaxValue:F2}", x1 - 18, y1 - 8, 36, 16, HorizontalAlignment.Center, VerticalAlignment.Center);
        }

        private static void DrawArc(ICanvas canvas, float cx, float cy, float radius, float startAngle, float sweepAngle)
        {
            // MAUI uses degrees; draw as a path
            var path = new PathF();
            float steps = Math.Max(sweepAngle / 2f, 1);
            bool first = true;
            for (float i = 0; i <= steps; i++)
            {
                float angle = ToRadians(startAngle + (sweepAngle * i / steps));
                float px = cx + radius * (float)Math.Cos(angle);
                float py = cy + radius * (float)Math.Sin(angle);
                if (first) { path.MoveTo(px, py); first = false; } else path.LineTo(px, py);
            }
            canvas.DrawPath(path);
        }

        private static Color GetBACColor(double bac) =>
            bac < 0.04 ? Color.FromArgb("#4CAF50")   // Green
            : bac < 0.08 ? Color.FromArgb("#FFC107") // Amber
            : Color.FromArgb("#F44336");              // Red

        private static float ToRadians(float degrees) => degrees * (float)(Math.PI / 180.0);
    }
}
