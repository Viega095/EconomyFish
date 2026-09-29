package me.antigravity.fishingeconomy.market;

import org.bukkit.entity.Player;
import org.bukkit.map.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MapStockRenderer extends MapRenderer {

    public static class Candle {
        public final double open;
        public final double close;
        public final double high;
        public final double low;

        public Candle(double open, double close, double high, double low) {
            this.open = open;
            this.close = close;
            this.high = high;
            this.low = low;
        }
    }

    private final String stockSymbol;
    private final List<Candle> history = new ArrayList<>();
    private boolean initialized = false;

    public MapStockRenderer(String stockSymbol) {
        super(true); // Contextual
        this.stockSymbol = stockSymbol;
        generateMockHistory();
    }

    private void generateMockHistory() {
        double current = 100.0;
        for (int i = 0; i < 18; i++) {
            double change = (Math.random() * 20.0) - 10.0;
            double open = current;
            double close = open + change;
            double high = Math.max(open, close) + Math.random() * 5.0;
            double low = Math.min(open, close) - Math.random() * 5.0;
            history.add(new Candle(open, close, high, low));
            current = close;
        }
    }

    @Override
    public void render(MapView map, MapCanvas canvas, Player player) {
        if (!initialized) {
            // Draw background
            for (int x = 0; x < 128; x++) {
                for (int y = 0; y < 128; y++) {
                    canvas.setPixel(x, y, MapPalette.DARK_GRAY);
                }
            }

            // Draw Title
            canvas.drawText(8, 8, MinecraftFont.Font, "§0" + stockSymbol + " - LIVE INDEX");

            // Draw Axis
            for (int y = 20; y < 110; y++) {
                canvas.setPixel(10, y, MapPalette.WHITE);
            }
            for (int x = 10; x < 120; x++) {
                canvas.setPixel(x, 110, MapPalette.WHITE);
            }

            // Draw Candlesticks
            int startX = 16;
            for (Candle c : history) {
                byte color = (c.close >= c.open) ? MapPalette.DARK_GREEN : MapPalette.RED;

                int yOpen = 100 - (int) (c.open * 0.5);
                int yClose = 100 - (int) (c.close * 0.5);
                int yHigh = 100 - (int) (c.high * 0.5);
                int yLow = 100 - (int) (c.low * 0.5);

                // Wick
                for (int y = Math.min(yHigh, yLow); y <= Math.max(yHigh, yLow); y++) {
                    if (y >= 20 && y <= 110) canvas.setPixel(startX + 2, y, MapPalette.WHITE);
                }

                // Body
                int top = Math.min(yOpen, yClose);
                int bottom = Math.max(yOpen, yClose);
                for (int x = startX; x <= startX + 4; x++) {
                    for (int y = top; y <= bottom; y++) {
                        if (y >= 20 && y <= 110 && x < 120) {
                            canvas.setPixel(x, y, color);
                        }
                    }
                }
                startX += 6;
            }

            initialized = true;
        }
    }
}
