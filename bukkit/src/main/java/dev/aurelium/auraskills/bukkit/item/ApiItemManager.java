package dev.aurelium.auraskills.bukkit.item;

import dev.aurelium.auraskills.api.config.ConfigNode;
import dev.aurelium.auraskills.api.item.ItemFilter;
import dev.aurelium.auraskills.api.item.ItemManager;
import dev.aurelium.auraskills.api.item.ModifierType;
import dev.aurelium.auraskills.api.skill.Multiplier;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.api.stat.StatModifier;
import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.api.trait.TraitModifier;
import dev.aurelium.auraskills.api.util.NumberUtil;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.item.SkillsItem.MetaType;
import dev.aurelium.auraskills.bukkit.trait.BukkitTraitManager;
import dev.aurelium.auraskills.bukkit.util.ConfigurateItemParser;
import dev.aurelium.auraskills.common.api.implementation.ApiConfigNode;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("deprecation")
public class ApiItemManager implements ItemManager {

    private final AuraSkills plugin;
    private final ConfigurateItemParser itemParser;


    public ApiItemManager(AuraSkills plugin) {
        this.plugin = plugin;
        this.itemParser = new ConfigurateItemParser(plugin);
    }

    public BukkitTraitManager getTraitManager() {
        return plugin.getTraitManager();
    }

    @Override
    public List<StatModifier> getStatModifiersByName(ItemStack item, ModifierType type, String name) {
        SkillsItem skillsItem = new SkillsItem(item, plugin);
        return skillsItem.getStatModifiersByName(type, name);
    }

    @Override
    public List<StatModifier> getOriginalStatModifiers(ItemStack item, ModifierType type) {
        SkillsItem skillsItem = new SkillsItem(item, plugin);
        return skillsItem.getOriginalStatModifiers(type);
    }


    @Override
    public ItemStack addStatModifier(ItemStack item, ModifierType type, Stat stat, String name, double value, boolean lore) {
        SkillsItem skillsItem = new SkillsItem(item, plugin);
        skillsItem.addModifier(MetaType.MODIFIER, type, stat, name, value);
        if (lore) {
            skillsItem.addModifierLore(type, stat, value, plugin.getDefaultLanguage());
        }
        return skillsItem.getItem();
    }

    @Override
    public ItemStack addTraitModifier(ItemStack item, ModifierType type, Trait trait, String name, double value, boolean lore) {
        SkillsItem skillsItem = new SkillsItem(item, plugin);
        skillsItem.addModifier(MetaType.TRAIT_MODIFIER, type, trait, name, value);
        if (lore) {
            skillsItem.addModifierLore(type, trait, value, plugin.getDefaultLanguage());
        }
        return skillsItem.getItem();
    }

    @Override
    public ItemStack addModifier(ItemStack item, ModifierType type, Stat stat, String name, double value, boolean lore) {
        return addStatModifier(item, type, stat, name, value, lore);
    }

    @Override
    public List<StatModifier> getStatModifiers(ItemStack item, ModifierType type) {
        SkillsItem skillsItem = new SkillsItem(item, plugin);
        return skillsItem.getStatModifiers(type);
    }

    @Override
    public List<TraitModifier> getTraitModifiers(ItemStack item, ModifierType type) {
        SkillsItem skillsItem = new SkillsItem(item, plugin);
        return skillsItem.getTraitModifiers(type);
    }

    @Override
    public List<StatModifier> getModifiers(ItemStack item, ModifierType type) {
        return getStatModifiers(item, type);
    }

    @Override
    public ItemStack removeStatModifier(ItemStack item, ModifierType type, Stat stat, String name, boolean lore) {
        SkillsItem skillsItem = new SkillsItem(item, plugin);
        skillsItem.removeModifier(MetaType.MODIFIER, type, stat, name, lore);

        return skillsItem.getItem();
    }

    @Override
    public ItemStack removeTraitModifier(ItemStack item, ModifierType type, Trait trait, String name, boolean lore) {
        SkillsItem skillsItem = new SkillsItem(item, plugin);
        skillsItem.removeModifier(MetaType.TRAIT_MODIFIER, type, trait, name, lore);
        return skillsItem.getItem();
    }

    @Override
    public ItemStack removeModifier(ItemStack item, ModifierType type, Stat stat, String name) {
        return removeStatModifier(item, type, stat, name, false);
    }

    @Override
    public ItemStack addMultiplier(ItemStack item, ModifierType type, Skill skill, String name, double value, boolean lore) {
        SkillsItem skillsItem = new SkillsItem(item, plugin);
        skillsItem.addMultiplier(type, skill, name, value);
        if (lore) {
            skillsItem.addMultiplierLore(type, skill, value, plugin.getDefaultLanguage());
        }
        return skillsItem.getItem();
    }

    @Override
    public List<Multiplier> getMultipliers(ItemStack item, ModifierType type) {
        SkillsItem skillsItem = new SkillsItem(item, plugin);
        return skillsItem.getMultipliers(type);
    }

    @Override
    public ItemStack removeMultiplier(ItemStack item, ModifierType type, Skill skill, String name, boolean lore) {
        SkillsItem skillsItem = new SkillsItem(item, plugin);
        skillsItem.removeMultiplier(type, skill, name, lore);
        return skillsItem.getItem();
    }

    @Override
    public ItemStack addRequirement(ItemStack item, ModifierType type, Skill skill, int level, boolean lore) {
        SkillsItem skillsItem = new SkillsItem(item, plugin);
        skillsItem.addRequirement(type, skill, level);
        if (lore) {
            skillsItem.addRequirementLore(type, skill, level, plugin.getDefaultLanguage());
        }
        return skillsItem.getItem();
    }

    @Override
    public Map<Skill, Integer> getRequirements(ItemStack item, ModifierType type) {
        SkillsItem skillsItem = new SkillsItem(item, plugin);
        return skillsItem.getRequirements(type);
    }

    @Override
    public ItemStack removeRequirement(ItemStack item, ModifierType type, Skill skill) {
        SkillsItem skillsItem = new SkillsItem(item, plugin);
        skillsItem.removeRequirement(type, skill);
        return skillsItem.getItem();
    }

    @Override
    public boolean passesFilter(ItemStack item, ItemFilter filter) {
        return plugin.getItemRegistry().passesFilter(item, filter);
    }

    @Override
    public ItemStack parseItem(ConfigNode config) {
        return itemParser.parseItem(((ApiConfigNode) config).getBacking());
    }

    @Override
    @SuppressWarnings("deprecation")
    public ItemStack parseItem(ConfigurationNode config) {
        return itemParser.parseItem(config);
    }

    @Override
    public List<ItemStack> parseMultipleItems(ConfigNode config) {
        try {
            return itemParser.parseMultipleItems(((ApiConfigNode) config).getBacking());
        } catch (SerializationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public List<ItemStack> parseMultipleItems(ConfigurationNode config) {
        try {
            return itemParser.parseMultipleItems(config);
        } catch (SerializationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Set<Stat> getLinkedStats(Trait trait) {
        return plugin.getTraitManager().getLinkedStats(trait);
    }


    @Override
    public String getFormattedTraitValue(double value, Trait trait) {
        var impl = plugin.getTraitManager().getTraitImpl(trait);

        String formatValue;
        // Don't use menu display for gathering luck traits (farming_luck, etc.) because it has extra info
        if (impl != null && !trait.getId().getKey().contains("_luck")) {
            formatValue = impl.getMenuDisplay(value, trait, plugin.getDefaultLanguage());
        } else {
            formatValue = NumberUtil.format1(Math.abs(value));
        }
        if (formatValue.startsWith("+")) { // Prevent double plus sign in lore
            formatValue = formatValue.substring(1);
        }
        return formatValue;
    }
}
