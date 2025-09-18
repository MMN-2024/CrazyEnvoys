package com.badbones69.crazyenvoys.support.placeholders;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazyenvoys.CrazyEnvoys;
import com.badbones69.crazyenvoys.api.CrazyManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import com.badbones69.crazyenvoys.config.ConfigManager;
import com.badbones69.crazyenvoys.config.types.ConfigKeys;
import com.badbones69.crazyenvoys.config.types.MessageKeys;

public class PlaceholderAPISupport extends PlaceholderExpansion {

    private @NotNull final CrazyEnvoys plugin = CrazyEnvoys.get();

    private @NotNull final SettingsManager config = ConfigManager.getConfig();
    private @NotNull final SettingsManager messages = ConfigManager.getMessages();

    private @NotNull final CrazyManager crazyManager = this.plugin.getCrazyManager();

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String identifier) {
        String lower = identifier.toLowerCase();

        boolean isEnabled = this.config.getProperty(ConfigKeys.envoys_grace_period_toggle);

        if (lower.equals("envoys_time")) {
            if (isEnabled) {
                if (this.crazyManager.getCountdownTimer() != null) {
                    int seconds = this.crazyManager.getCountdownTimer().getSecondsLeft();

                    if (seconds != 0) return seconds + this.config.getProperty(ConfigKeys.envoys_grace_period_time_unit);
                }

                return this.config.getProperty(ConfigKeys.envoys_grace_period_unlocked);
            }

            return this.config.getProperty(ConfigKeys.envoys_grace_period_unlocked);
        }

        return switch (lower) {
            case "cooldown" -> this.crazyManager.isEnvoyActive() ? this.messages.getProperty(MessageKeys.hologram_on_going) : this.crazyManager.getNextEnvoyTime();
            case "time_left" -> this.crazyManager.isEnvoyActive() ? this.crazyManager.getEnvoyRunTimeLeft() : this.messages.getProperty(MessageKeys.hologram_not_running);
            case "cooldown_hours_minutes" -> this.crazyManager.isEnvoyActive() ? this.messages.getProperty(MessageKeys.hologram_on_going) : convertToHoursMinutes(this.crazyManager.getNextEnvoyTime());
            case "envoys_left" -> String.valueOf(this.crazyManager.getActiveEnvoys().size());
            default -> "";
        };
    }

    private String convertToHoursMinutes(String timeString) {
        // Parse the existing time format and convert to hours and minutes only
        if (timeString == null || timeString.isEmpty()) return "";
        
        // Remove any existing formatting and extract time components
        String[] parts = timeString.split(" ");
        int totalMinutes = 0;
        
        for (String part : parts) {
            if (part.contains("h")) {
                try {
                    int hours = Integer.parseInt(part.replace("h", "").replace(",", ""));
                    totalMinutes += hours * 60;
                } catch (NumberFormatException ignored) {}
            } else if (part.contains("m")) {
                try {
                    int minutes = Integer.parseInt(part.replace("m", "").replace(",", ""));
                    totalMinutes += minutes;
                } catch (NumberFormatException ignored) {}
            } else if (part.contains("d")) {
                try {
                    int days = Integer.parseInt(part.replace("d", "").replace(",", ""));
                    totalMinutes += days * 24 * 60;
                } catch (NumberFormatException ignored) {}
            }
        }
        
        // Convert back to hours and minutes format
        int hours = totalMinutes / 60;
        int minutes = totalMinutes % 60;
        
        if (hours > 0 && minutes > 0) {
            return hours + "h " + minutes + "m";
        } else if (hours > 0) {
            return hours + "h";
        } else if (minutes > 0) {
            return minutes + "m";
        } else {
            return "0m";
        }
    }
    
    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    @NotNull
    public String getIdentifier() {
        return this.plugin.getName().toLowerCase();
    }
    
    @Override
    @NotNull
    public String getAuthor() {
        return this.plugin.getDescription().getAuthors().toString();
    }
    
    @Override
    @NotNull
    public String getVersion() {
        return this.plugin.getDescription().getVersion();
    }
}