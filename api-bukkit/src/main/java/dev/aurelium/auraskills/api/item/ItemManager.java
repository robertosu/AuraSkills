package dev.aurelium.auraskills.api.item;

import dev.aurelium.auraskills.api.config.ConfigNode;
import dev.aurelium.auraskills.api.skill.Multiplier;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.skill.Skills;
import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.api.stat.StatModifier;
import dev.aurelium.auraskills.api.stat.Stats;
import dev.aurelium.auraskills.api.trait.*;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * An interface used to add modifiers to items and parse items from configuration.
 */
public interface ItemManager {




    List<StatModifier> getOriginalStatModifiers(ItemStack item, ModifierType type);

    List<StatModifier> getStatModifiersByName(ItemStack item, ModifierType type, String name);




    /**
     * Adds a stat modifier to an item, with optional lore. This does not change the item passed in directly,
     * you must use the returned ItemStack. This means the original ItemStack passed in is not changed at all, a new
     * one is created.
     *
     * @param item the original item, will not be changed by the method
     * @param type the {@link ModifierType} to add
     * @param stat the stat to add (Use {@link Stats} enum for default stats)
     * @param name a name, used as ID of this modifier
     * @param value the value of the stat to add
     * @param lore whether to add lore
     * @return a new ItemStack with the static modifier
     */
    ItemStack addStatModifier(ItemStack item, ModifierType type, Stat stat, String name, double value, boolean lore);

    /**
     * Adds a trait modifier to an item, with optional lore. This does not change the item passed in directly,
     * you must use the returned ItemStack.
     *
     * @param item the original item
     * @param type the {@link ModifierType} to add
     * @param trait the trait to add (Use {@link Traits} enum for default stats)
     * @param name a name, used as ID of this modifier
     * @param value the value of the trait to add
     * @param lore whether to add lore
     * @return a new ItemStack with the trait modifier
     */
    ItemStack addTraitModifier(ItemStack item, ModifierType type, Trait trait, String name, double value, boolean lore);

    /**
     * @deprecated use {@link #addStatModifier(ItemStack, ModifierType, Stat, String, double, boolean)}
     */
    @Deprecated
    ItemStack addModifier(ItemStack item, ModifierType type, Stat stat, String name, double value, boolean lore);

    /**
     * Gets a list of stat modifiers on an item for a given modifier type.
     *
     * @param item the item to get the modifiers of
     * @param type the modifier type
     * @return a list of modifiers
     */
    List<StatModifier> getStatModifiers(ItemStack item, ModifierType type);

    /**
     * Gets a list of trait modifiers on an item for a given modifier type.
     *
     * @param item the item to get the trait modifiers of
     * @param type the modifier type
     * @return a list of modifiers
     */
    List<TraitModifier> getTraitModifiers(ItemStack item, ModifierType type);

    /**
     * @deprecated use {@link #getStatModifiers(ItemStack, ModifierType)}
     */
    @Deprecated
    List<StatModifier> getModifiers(ItemStack item, ModifierType type);

    /**
     * Removes a stat modifier from an item for a given modifier type and stat.
     * Does not modify the ItemStack passed in, instead returns a copy of the item with the
     * modifier removed. Will not remove any lore.
     *
     * @param item The item to remove the modifier from. Does not get modified.
     * @param type the modifier type
     * @param stat the stat of the modifier to remove
     * @param name a name, used as ID of this modifier
     * @param lore if the will be removed
     * @return the item with the modifier removed
     */
    ItemStack removeStatModifier(ItemStack item, ModifierType type, Stat stat, String name, boolean lore);

    /**
     * Removes a trait modifier from an item for a given modifier type and stat.
     * Does not modify the ItemStack passed in, instead returns a copy of the item with the
     * modifier removed. Will not remove any lore.
     *
     * @param item The item to remove the modifier from. Does not get modified.
     * @param type the modifier type
     * @param trait the trait of the modifier to remove
     * @param name the name of the modifier to remove
     * @param lore if the will be removed
     * @return the item with the modifier removed
     */
    ItemStack removeTraitModifier(ItemStack item, ModifierType type, Trait trait, String name, boolean lore);

    /**
     * @deprecated use {@link #removeStatModifier(ItemStack, ModifierType, Stat, String, boolean)}
     */
    @Deprecated
    ItemStack removeModifier(ItemStack item, ModifierType type, Stat stat, String name);

    /**
     * Adds a multiplier to an item, with optional lore. This does not change the item passed in directly,
     * you must use the returned ItemStack. This means the original ItemStack passed in is not changed at all, a new
     * one is created.
     *
     * @param item the original item, will not be changed by the method
     * @param type the {@link ModifierType} to add
     * @param skill the skill to add (Use {@link Skills} enum for default skills)
     * @param name the name of the modifier to remove
     * @param value the value of the multiplier (in percentage points) to add
     * @param lore whether to add lore
     * @return a new ItemStack with the multiplier
     */
    ItemStack addMultiplier(ItemStack item, ModifierType type, Skill skill, String name, double value, boolean lore);

    /**
     * Gets a list of skill multipliers on an item for a given modifier type.
     *
     * @param item the item to get the multipliers of
     * @param type the modifier type
     * @return a list of multipliers
     */
    List<Multiplier> getMultipliers(ItemStack item, ModifierType type);

    /**
     * Removes a multiplier from an item for the given skill. Does not modify the item, instead returns
     * a copy with the multiplier removed. Will not remove any lore.
     *
     * @param item item to remove the multiplier from
     * @param type the type of modifier
     * @param skill the skill of the multiplier to remove, or null for global multipliers
     * @param name the name of the multiplier to remove
     * @param lore if the lore line is removed
     * @return the modified item with the multiplier removed
     */
    ItemStack removeMultiplier(ItemStack item, ModifierType type, Skill skill, String name, boolean lore);

    /**
     * Adds a skill requirement to use an item, with optional lore. This does not change the item passed in
     * directly, you must use the returned ItemStack. This means the original ItemStack passed in is not changed at all, a new
     * one is created.
     *
     * @param item the original item, will not be changed by the method
     * @param type the {@link ModifierType} to add
     * @param skill the skill the requirement is for (Use {@link Skills} enum for default skills)
     * @param level the skill level required to use the item
     * @param lore whether to add lore
     * @return a new ItemStack with the requirement
     */
    ItemStack addRequirement(ItemStack item, ModifierType type, Skill skill, int level, boolean lore);

    /**
     * Gets the skill requirements for an item.
     *
     * @param item the item to get the requirements of
     * @param type the modifier type
     * @return a map of skill requirements, where the key is the skill and the level requirements is the value
     */
    Map<Skill, Integer> getRequirements(ItemStack item, ModifierType type);

    /**
     * Removes a skill requirement from an item. Does not modify the item, instead returns a copy of the
     * item with the requirement removed. Does not modify lore.
     *
     * @param item the item to remove the requirement from
     * @param type the modifier type
     * @param skill the skill of the requirement to remove
     * @return the modified item with the requirement removed
     */
    ItemStack removeRequirement(ItemStack item, ModifierType type, Skill skill);

    /**
     * Test whether a given ItemStack passes a source {@link ItemFilter}.
     * Used to implement the leveler of a {@link dev.aurelium.auraskills.api.source.CustomSource} with an
     * {@link ItemFilter} field.
     *
     * @param item the item to test
     * @param filter the filter to test with
     * @return whether the item passes the filter
     */
    boolean passesFilter(ItemStack item, ItemFilter filter);

    /**
     * Parses an ItemStack from a ConfigurationNode section in the same
     * format used to parse items in default loot and menus.
     *
     * @param config the Configurate ConfigurationNode to parse keys from, should be a mapping
     * @return the parsed ItemStack
     */
    ItemStack parseItem(ConfigNode config);

    /**
     * Use {@link #parseItem(ConfigNode)}
     */
    @Deprecated
    ItemStack parseItem(ConfigurationNode config);

    /**
     * Parses a list of ItemStack from a ConfigurationNode if it has a materials list. Only the material
     * differs between each item, the amount and all meta remains the same across items. If a regular material
     * string is defined, a single item will be parsed like {@link #parseItem(ConfigNode)} and the list
     * returned will be of size 1.
     *
     * @param config the Configurate ConfigurationNode to parse keys from, should be a mapping
     * @return a list of parsed ItemStack
     */
    List<ItemStack> parseMultipleItems(ConfigNode config);

    /**
     * Use {@link #parseMultipleItems(ConfigNode)}
     */
    @Deprecated
    List<ItemStack> parseMultipleItems(ConfigurationNode config);


    /**
     * @deprecated Use {@link #addStatModifier(ItemStack, ModifierType, Stat, String, double, boolean)}
     * Adds a stat modifier to an item with a default name, maintaining backward compatibility.
     *
     * @param item the original item
     * @param type the {@link ModifierType} to add
     * @param stat the stat to add (Use {@link Stats} enum for default stats)
     * @param value the value of the stat to add
     * @param lore whether to add lore
     * @return a new ItemStack with the static modifier
     */
    @Deprecated
    default ItemStack addStatModifier(ItemStack item, ModifierType type, Stat stat, double value, boolean lore) {
        return addStatModifier(item, type, stat, "default", value, lore);
    }

    /**
     * @deprecated Use {@link #addTraitModifier(ItemStack, ModifierType, Trait, String, double, boolean)}
     * Adds a trait modifier to an item with a default name, maintaining backward compatibility.
     *
     * @param item the original item
     * @param type the {@link ModifierType} to add
     * @param trait the trait to add (Use {@link Traits} enum for default traits)
     * @param value the value of the trait to add
     * @param lore whether to add lore
     * @return a new ItemStack with the trait modifier
     */
    @Deprecated
    default ItemStack addTraitModifier(ItemStack item, ModifierType type, Trait trait, double value, boolean lore) {
        return addTraitModifier(item, type, trait, "default", value, lore);
    }

    /**
     * @deprecated Use {@link #removeStatModifier(ItemStack, ModifierType, Stat, String, boolean)}
     * Removes a stat modifier from an item with a default name, maintaining backward compatibility.
     *
     * @param item The item to remove the modifier from
     * @param type the modifier type
     * @param stat the stat of the modifier to remove
     * @return the item with the modifier and his lore line removed
     */
    @Deprecated
    default ItemStack removeStatModifier(ItemStack item, ModifierType type, Stat stat) {
        return removeStatModifier(item, type, stat, "default", false);
    }

    /**
     * @deprecated Use {@link #removeTraitModifier(ItemStack, ModifierType, Trait, String, boolean)}
     * Removes a trait modifier from an item with a default name, maintaining backward compatibility.
     *
     * @param item The item to remove the modifier from
     * @param type the modifier type
     * @param trait the trait of the modifier to remove
     * @return the item with the modifier removed and his lore line removed
     */
    @Deprecated
    default ItemStack removeTraitModifier(ItemStack item, ModifierType type, Trait trait) {
        return removeTraitModifier(item, type, trait, "default", false);
    }

    /**
     * @deprecated Use {@link #addMultiplier(ItemStack, ModifierType, Skill, String, double, boolean)}
     * Adds a multiplier to an item with a default name, maintaining backward compatibility.
     *
     * @param item the original item
     * @param type the {@link ModifierType} to add
     * @param skill the skill to add (Use {@link Skills} enum for default skills)
     * @param value the value of the multiplier (in percentage points) to add
     * @param lore whether to add lore
     * @return a new ItemStack with the multiplier
     */
    @Deprecated
    default ItemStack addMultiplier(ItemStack item, ModifierType type, Skill skill, double value, boolean lore) {
        return addMultiplier(item, type, skill, "default", value, lore);
    }

    /**
     * @deprecated Use {@link #removeMultiplier(ItemStack, ModifierType, Skill, String, boolean)}
     * Removes a multiplier from an item with a default name, maintaining backward compatibility.
     *
     * @param item item to remove the multiplier from
     * @param type the type of modifier
     * @param skill the skill of the multiplier to remove
     * @return the modified item with the multiplier removed
     */
    @Deprecated
    default ItemStack removeMultiplier(ItemStack item, ModifierType type, Skill skill) {
        return removeMultiplier(item, type, skill, "default", false);
    }

    Set<Stat> getLinkedStats(Trait trait);


    String getFormattedTraitValue (double value, Trait trait);
}
