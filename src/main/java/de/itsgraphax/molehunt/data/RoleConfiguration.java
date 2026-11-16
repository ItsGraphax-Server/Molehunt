
package de.itsgraphax.molehunt.data;

import de.itsgraphax.molehunt.data.goals.Goal;
import de.itsgraphax.molehunt.data.goals.Impossible;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static de.itsgraphax.molehunt.Molehunt.mhp;

public class RoleConfiguration {

    public String id = "none";
    public Component name = Component.text("Name");
    public Component description = Component.text("Description");
    public TextColor color = NamedTextColor.DARK_GRAY;

    public Goal goal = new Impossible();

    public GameMode gameMode = GameMode.SURVIVAL;

    public boolean hardcore = false;

    public boolean invertBlacklist = false;
    public final List<String> chatBlacklist = new ArrayList<>();

    public Component getName() {
        return Component.empty()
                .color(color)
                .append(name);
    }


    private static Component showGoal(Goal goal) {
        var result = Component.text();

        result.append(Component.text(" • ", NamedTextColor.DARK_GRAY))
                .append(goal.asComponent())
                .appendNewline();

        for (var sub : goal.getSubGoals()) {
            result
                    .append(Component.text(" • ", NamedTextColor.DARK_GRAY))
                    .append(showGoal(sub));
        }

        return result.build();
    }

    public Component asComponent() {
        var result = Component.text()
                .append(getName().decorate(TextDecoration.BOLD))
                .appendNewline()
                .append(description.decorate(TextDecoration.ITALIC))
                .appendNewline()
                .appendNewline()
                .append(Component.text("Goals:", NamedTextColor.GRAY).decorate(TextDecoration.BOLD))
                .appendNewline();

        result.append(showGoal(goal));

        result
                .appendNewline()
                .append(Component.text("Gamemode", NamedTextColor.GRAY).decorate(TextDecoration.BOLD))
                .append(Component.text(": ", NamedTextColor.GRAY))
                .append(Component.text(gameMode.name(), NamedTextColor.WHITE))
                .appendNewline()
                .appendNewline()
                .append(Component.text("Attributes:", NamedTextColor.GRAY).decorate(TextDecoration.BOLD))
                .appendNewline()
                .append(attribute("Hardcore", hardcore))
                .append(attribute("Locator Bar", locatorBarEnabled))
                .appendNewline()
                .append(Component.text(
                        invertBlacklist ? "Sends chat to:" : "Doesn't send chat to:",
                        NamedTextColor.GRAY
                ).decorate(TextDecoration.BOLD))
                .appendNewline();

        for (var role : chatBlacklist) {
            result
                    .append(Component.text(" • ", NamedTextColor.DARK_GRAY))
                    .append(Component.text(role))
                    .appendNewline();
        }

        result.appendNewline();

        return result.build();
    }

    private static Component attribute(String name, boolean enabled) {
        return Component.text(" • " + name, enabled
                        ? NamedTextColor.GREEN
                        : NamedTextColor.RED)
                .decoration(TextDecoration.STRIKETHROUGH, !enabled)
                .appendNewline();
    }

    public void onGet(Player player) {
        player.setGameMode(gameMode);
        player.setWaypointColor(Color.fromRGB(color.value()));
        updateWaypointReceive(player);
    }


    private static final AttributeModifier WAYPOINT_RECEIVE_BLOCK =
            new AttributeModifier(
                    new NamespacedKey(mhp, "waypointRecieveBlock"),
                    Math.nextUp(-1.0),
                    AttributeModifier.Operation.MULTIPLY_SCALAR_1
            );
    public boolean locatorBarEnabled = true;


    private void updateWaypointReceive(Player player) {
        var attribute = player.getAttribute(Attribute.WAYPOINT_RECEIVE_RANGE);
        if (attribute == null) return;

        boolean hasModifier = attribute.getModifier(WAYPOINT_RECEIVE_BLOCK.getKey()) != null;

        if (locatorBarEnabled && hasModifier) {
            attribute.removeModifier(WAYPOINT_RECEIVE_BLOCK);
        } else if (!locatorBarEnabled && !hasModifier) {
            attribute.addModifier(WAYPOINT_RECEIVE_BLOCK);
        }
    }
}
