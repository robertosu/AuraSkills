package dev.aurelium.auraskills.bukkit.skills.excavation;

import dev.aurelium.auraskills.api.ability.Abilities;
import dev.aurelium.auraskills.api.event.loot.LootDropEvent;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.ability.AbilityImpl;
import dev.aurelium.auraskills.bukkit.item.BukkitItemHolder;
import dev.aurelium.auraskills.bukkit.util.BukkitLocationHolder;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class ExcavationAbilities extends AbilityImpl {

    public ExcavationAbilities(AuraSkills plugin) {
        super(plugin, Abilities.METAL_DETECTOR, Abilities.EXCAVATOR, Abilities.SPADE_MASTER, Abilities.BIGGER_SCOOP, Abilities.LUCKY_SPADES);
    }

    public void spadeMaster(EntityDamageByEntityEvent event, Player player, User user) {
        var ability = Abilities.SPADE_MASTER;

        if (isDisabled(ability)) return;

        if (failsChecks(player, ability)) return;

        if (user.getAbilityLevel(ability) == 0) return;

        event.setDamage(event.getDamage() * (1 + (getValue(ability, user) / 100)));
    }

    public void biggerScoop(Player player, User user, Block block) {
        var ability = Abilities.BIGGER_SCOOP;

        if (isDisabled(ability)) return;

        if (failsChecks(player, ability)) return;

        if (player.getGameMode() != GameMode.SURVIVAL) return;

        if (rand.nextDouble() < (getValue(ability, user) / 100)) {
            ItemStack tool = player.getInventory().getItemInMainHand();
            Material mat =  block.getType();
            for (ItemStack item : block.getDrops(tool)) {
                ItemStack drop;
                if (tool.getEnchantmentLevel(Enchantment.SILK_TOUCH) > 0) {
                    // Drop the material of the block if silk touch
                    drop = new ItemStack(mat, 2);
                } else {
                    // Drop the normal block drops if not silk touch
                    drop = item.clone();
                    drop.setAmount(2);
                }
                var itemHolder = new BukkitItemHolder(drop);
                Location loc = block.getLocation().add(0.5, 0.5, 0.5);
                var locHolder = new BukkitLocationHolder(loc);

                LootDropEvent event = new LootDropEvent(plugin.getApi(), user.toApi(), itemHolder, locHolder, LootDropEvent.Cause.BIGGER_SCOOP);
                plugin.getEventManager().callEvent(event);
                if (!event.isCancelled()) {
                    block.getWorld().dropItem(event.getLocation().get(Location.class), event.getItem().get(ItemStack.class));
                }
            }
        }
    }

}
