package dev.aurelium.auraskills.bukkit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.annotation.*;
import dev.aurelium.auraskills.api.item.ModifierType;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.api.stat.StatModifier;
import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.api.trait.TraitModifier;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.item.SkillsItem;
import dev.aurelium.auraskills.bukkit.item.SkillsItem.MetaType;
import dev.aurelium.auraskills.bukkit.stat.StatFormat;
import dev.aurelium.auraskills.common.message.type.CommandMessage;
import dev.aurelium.auraskills.api.skill.Multiplier;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;
import java.util.Map;

@CommandAlias("%skills_alias")
@Subcommand("armor")
public class ArmorCommand extends BaseCommand {

    private final AuraSkills plugin;
    private final StatFormat format;
    private final Locale defaultLanguage;

    public ArmorCommand(AuraSkills plugin) {
        this.plugin = plugin;
        this.format = new StatFormat(plugin);
        this.defaultLanguage = plugin.getDefaultLanguage();
    }

    @Subcommand("modifier add")
    @CommandCompletion("@stats @nothing @nothing false|true")
    @CommandPermission("auraskills.command.armor.modifier")
    @Description("Adds an armor stat modifier to the item held, along with lore by default.")
    public void onArmorModifierAdd(@Flags("itemheld") Player player, Stat stat, String name, double value, @Default("true") boolean lore) {
        Locale locale = plugin.getUser(player).getLocale();
        ItemStack item = player.getInventory().getItemInMainHand();
        SkillsItem skillsItem = new SkillsItem(item, plugin);
        for (StatModifier statModifier : skillsItem.getOriginalStatModifiers(ModifierType.ARMOR)) {
            if (statModifier.name().equals(name) && statModifier.stat().equals(stat)) {
                player.sendMessage(plugin.getPrefix(locale) + format.applyPlaceholders(plugin.getMsg(CommandMessage.ARMOR_MODIFIER_ADD_ALREADY_EXISTS, locale), stat, name, locale));
                return;
            }
        }
        if (lore) {
            skillsItem.addModifierLore(ModifierType.ARMOR, stat, value, locale);
        }
        skillsItem.addModifier(MetaType.MODIFIER, ModifierType.ARMOR, stat, name, value);
        ItemStack newItem = skillsItem.getItem();
        player.getInventory().setItemInMainHand(newItem);
        player.sendMessage(plugin.getPrefix(locale) + format.applyPlaceholders(plugin.getMsg(CommandMessage.ARMOR_MODIFIER_ADD_ADDED, locale), stat, name, value, locale));

    }

    @Subcommand("modifier remove")
    @CommandCompletion("@stats @nothing false|true")
    @CommandPermission("auraskills.command.armor.modifier")
    @Description("Removes an armor stat modifier from the item held, and the lore associated with it by default.")
    public void onArmorModifierRemove(@Flags("itemheld") Player player, Stat stat, String name, @Default("true") boolean lore) {
        Locale locale = plugin.getUser(player).getLocale();
        ItemStack item = player.getInventory().getItemInMainHand();
        boolean removed = false;
        SkillsItem skillsItem = new SkillsItem(item, plugin);
        for (StatModifier modifier : skillsItem.getOriginalStatModifiers(ModifierType.ARMOR)) {
            if (modifier.stat().equals(stat) && modifier.name().equals(name)) {
                skillsItem.removeModifier(MetaType.MODIFIER, ModifierType.ARMOR, stat, name, lore);
                removed = true;
                break;
            }
        }

        item = skillsItem.getItem();
        player.getInventory().setItemInMainHand(item);
        if (removed) {
            player.sendMessage(plugin.getPrefix(locale) + format.applyPlaceholders(plugin.getMsg(CommandMessage.ARMOR_MODIFIER_REMOVE_REMOVED, locale), stat, name, locale));
        }
        else {
            player.sendMessage(plugin.getPrefix(locale) + format.applyPlaceholders(plugin.getMsg(CommandMessage.ARMOR_MODIFIER_REMOVE_DOES_NOT_EXIST, locale), stat, name, locale));
        }
    }

    @Subcommand("modifier list")
    @CommandPermission("auraskills.command.armor.modifier")
    @Description("Lists all armor stat modifiers on the item held.")
    public void onArmorModifierList(@Flags("itemheld") Player player) {
        Locale locale = plugin.getUser(player).getLocale();
        ItemStack item = player.getInventory().getItemInMainHand();
        StringBuilder message = new StringBuilder(plugin.getMsg(CommandMessage.ARMOR_MODIFIER_LIST_HEADER, locale));
        SkillsItem skillsItem = new SkillsItem(item, plugin);
        for (StatModifier modifier : skillsItem.getOriginalStatModifiers(ModifierType.ARMOR)) {
            message.append("\n").append(format.applyPlaceholders(plugin.getMsg(CommandMessage.ARMOR_MODIFIER_LIST_ENTRY, locale), modifier, locale));
        }
        player.sendMessage(message.toString());
    }

    @Subcommand("modifier removeall")
    @CommandPermission("auraskills.command.armor.modifier")
    @Description("Removes all armor stat modifiers from the item held.")
    public void onArmorModifierRemoveAll(@Flags("itemheld") Player player) {
        Locale locale = plugin.getUser(player).getLocale();
        SkillsItem skillsItem = new SkillsItem(player.getInventory().getItemInMainHand(), plugin);
        //if we remove first the modifiers, the lore lines are not detected due the lack of modifiers
        skillsItem.removeAllModifierLore(MetaType.MODIFIER,ModifierType.ARMOR, defaultLanguage);
        skillsItem.removeAll(SkillsItem.MetaType.MODIFIER, ModifierType.ARMOR);

        ItemStack item = skillsItem.getItem();
        player.getInventory().setItemInMainHand(item);
        player.sendMessage(plugin.getPrefix(locale) + plugin.getMsg(CommandMessage.ARMOR_MODIFIER_REMOVEALL_REMOVED, locale));
    }

    @Subcommand("trait add")
    @CommandCompletion("@traits @nothing @nothing false|true")
    @CommandPermission("auraskills.command.armor.modifier")
    @Description("Adds an armor trait modifier to the item held, along with lore by default.")
    public void onItemTraitAdd(@Flags("itemheld") Player player, Trait trait, String name, double value, @Default("true") boolean lore) {
        Locale locale = plugin.getUser(player).getLocale();
        ItemStack item = player.getInventory().getItemInMainHand();
        SkillsItem skillsItem = new SkillsItem(item, plugin);

        for (TraitModifier modifier : skillsItem.getOriginalTraitModifiers(ModifierType.ARMOR)) {
            if (modifier.trait().equals(trait) && modifier.name().equals(name)) {
                player.sendMessage(plugin.getPrefix(locale) + format.applyPlaceholders(plugin.getMsg(CommandMessage.ARMOR_TRAIT_ADD_ALREADY_EXISTS, locale), trait, name, locale));
                return;
            }
        }
        if (lore) {
            skillsItem.addModifierLore(ModifierType.ARMOR, trait, value, locale);
        }
        skillsItem.addModifier(MetaType.TRAIT_MODIFIER, ModifierType.ARMOR, trait, name, value);
        ItemStack newItem = skillsItem.getItem();
        player.getInventory().setItemInMainHand(newItem);
        player.sendMessage(plugin.getPrefix(locale) +
                format.applyPlaceholders(plugin.getMsg(CommandMessage.ARMOR_MODIFIER_ADD_ADDED, locale), trait, name, value, locale));
    }

    @Subcommand("trait remove")
    @CommandCompletion("@traits @nothing false|true")
    @CommandPermission("auraskills.command.armor.modifier")
    @Description("Removes an armor trait modifier from the item held.")
    public void onItemTraitRemove(@Flags("itemheld") Player player, Trait trait, String name, boolean lore) {
        Locale locale = plugin.getUser(player).getLocale();
        ItemStack item = player.getInventory().getItemInMainHand();
        boolean removed = false;
        SkillsItem skillsItem = new SkillsItem(item, plugin);
        for (TraitModifier modifier : skillsItem.getOriginalTraitModifiers(ModifierType.ARMOR)) {
            if (modifier.trait().equals(trait) && modifier.name().equals(name)) {
                skillsItem.removeModifier(MetaType.TRAIT_MODIFIER, ModifierType.ARMOR, trait, name, lore);
                removed = true;
                break;
            }
        }
        item = skillsItem.getItem();
        player.getInventory().setItemInMainHand(item);
        if (removed) {
            player.sendMessage(plugin.getPrefix(locale) + format.applyPlaceholders(plugin.getMsg(CommandMessage.ARMOR_MODIFIER_REMOVE_REMOVED, locale), trait, name, locale));
        } else {
            player.sendMessage(plugin.getPrefix(locale) + format.applyPlaceholders(plugin.getMsg(CommandMessage.ARMOR_MODIFIER_REMOVE_DOES_NOT_EXIST, locale), trait, name, locale));
        }
    }

    @Subcommand("trait list")
    @CommandPermission("auraskills.command.armor.modifier")
    @Description("Lists all item trait modifiers on the item held.")
    public void onItemTraitList(@Flags("itemheld") Player player) {
        Locale locale = plugin.getUser(player).getLocale();
        ItemStack item = player.getInventory().getItemInMainHand();
        StringBuilder message = new StringBuilder(plugin.getMsg(CommandMessage.ARMOR_MODIFIER_LIST_HEADER, locale));
        SkillsItem skillsItem = new SkillsItem(item, plugin);
        for (TraitModifier modifier : skillsItem.getOriginalTraitModifiers(ModifierType.ARMOR)) {
            message.append("\n").append(format.applyPlaceholders(plugin.getMsg(CommandMessage.ARMOR_MODIFIER_LIST_ENTRY, locale), modifier, locale));
        }
        player.sendMessage(message.toString());
    }

    @Subcommand("trait removeall")
    @CommandPermission("auraskills.command.armor.modifier")
    @Description("Removes all armor trait modifiers from the item held.")
    public void onItemTraitRemoveAll(@Flags("itemheld") Player player) {
        Locale locale = plugin.getUser(player).getLocale();

        ItemStack item = player.getInventory().getItemInMainHand();
        SkillsItem skillsItem = new SkillsItem(item, plugin);

        skillsItem.removeAllModifierLore(MetaType.TRAIT_MODIFIER,ModifierType.ARMOR,defaultLanguage);
        skillsItem.removeAll(MetaType.TRAIT_MODIFIER, ModifierType.ARMOR);

        item = skillsItem.getItem();

        player.getInventory().setItemInMainHand(item);
        player.sendMessage(plugin.getPrefix(locale) + plugin.getMsg(CommandMessage.ARMOR_MODIFIER_REMOVEALL_REMOVED, locale));
    }

    @Subcommand("requirement add")
    @CommandPermission("auraskills.command.armor.requirement")
    @CommandCompletion("@skills @nothing false|true")
    @Description("Adds an armor requirement to the item held, along with lore by default")
    public void onArmorRequirementAdd(@Flags("itemheld") Player player, Skill skill, int level, @Default("true") boolean lore) {
        Locale locale = plugin.getUser(player).getLocale();
        ItemStack item = player.getInventory().getItemInMainHand();
        SkillsItem skillsItem = new SkillsItem(item, plugin);
        if (skillsItem.getRequirements(ModifierType.ARMOR).containsKey(skill)) {
            player.sendMessage(plugin.getPrefix(locale) + TextUtil.replace(plugin.getMsg(CommandMessage.ARMOR_REQUIREMENT_ADD_ALREADY_EXISTS, locale),
                    "{skill}", skill.getDisplayName(locale)));
            return;
        }
        skillsItem.addRequirement(ModifierType.ARMOR, skill, level);
        if (lore) {
            skillsItem.addRequirementLore(ModifierType.ARMOR, skill, level, locale);
        }
        item = skillsItem.getItem();
        player.getInventory().setItemInMainHand(item);
        player.sendMessage(plugin.getPrefix(locale) + TextUtil.replace(plugin.getMsg(CommandMessage.ARMOR_REQUIREMENT_ADD_ADDED, locale),
                "{skill}", skill.getDisplayName(locale),
                "{level}", String.valueOf(level)));
    }

    @Subcommand("requirement remove")
    @CommandPermission("auraskills.command.armor.requirement")
    @CommandCompletion("@skills false|true")
    @Description("Removes an armor requirement from the item held, along with the lore associated it by default.")
    public void onArmorRequirementRemove(@Flags("itemheld") Player player, Skill skill, @Default("true") boolean lore) {
        Locale locale = plugin.getUser(player).getLocale();
        ItemStack item = player.getInventory().getItemInMainHand();
        SkillsItem skillsItem = new SkillsItem(item, plugin);
        if (skillsItem.getRequirements(ModifierType.ARMOR).containsKey(skill)) {
            skillsItem.removeRequirement(ModifierType.ARMOR, skill);
            if (lore) {
                skillsItem.removeRequirementLore(skill);
                skillsItem.removeRequirementLore(skill);
            }
            item = skillsItem.getItem();
            player.getInventory().setItemInMainHand(item);
            player.sendMessage(plugin.getPrefix(locale) + TextUtil.replace(plugin.getMsg(CommandMessage.ARMOR_REQUIREMENT_REMOVE_REMOVED, locale),
                    "{skill}", skill.getDisplayName(locale)));
        }
        else {
            player.sendMessage(plugin.getPrefix(locale) + TextUtil.replace(plugin.getMsg(CommandMessage.ARMOR_REQUIREMENT_REMOVE_DOES_NOT_EXIST, locale),
                    "{skill}", skill.getDisplayName(locale)));
        }
    }

    @Subcommand("requirement list")
    @CommandPermission("auraskills.command.armor.requirement")
    @Description("Lists the armor requirements on the item held.")
    public void onArmorRequirementList(@Flags("itemheld") Player player) {
        Locale locale = plugin.getUser(player).getLocale();
        player.sendMessage(plugin.getMsg(CommandMessage.ARMOR_REQUIREMENT_LIST_HEADER, locale));
        SkillsItem skillsItem = new SkillsItem(player.getInventory().getItemInMainHand(), plugin);
        for (Map.Entry<Skill, Integer> entry : skillsItem.getRequirements(ModifierType.ARMOR).entrySet()) {
            player.sendMessage(TextUtil.replace(plugin.getMsg(CommandMessage.ARMOR_REQUIREMENT_LIST_ENTRY, locale),
                    "{skill}", entry.getKey().getDisplayName(locale),
                    "{level}", String.valueOf(entry.getValue())));
        }
    }

    @Subcommand("requirement removeall")
    @CommandPermission("auraskills.command.armor.requirement")
    @Description("Removes all armor requirements from the item held.")
    public void onArmorRequirementRemoveAll(@Flags("itemheld") Player player) {
        Locale locale = plugin.getUser(player).getLocale();
        SkillsItem skillsItem = new SkillsItem(player.getInventory().getItemInMainHand(), plugin);

        skillsItem.removeAllModifierLore(MetaType.REQUIREMENT,ModifierType.ARMOR,defaultLanguage);
        skillsItem.removeAll(SkillsItem.MetaType.REQUIREMENT, ModifierType.ARMOR);

        ItemStack item = skillsItem.getItem();
        player.getInventory().setItemInMainHand(item);
        player.sendMessage(plugin.getPrefix(locale) + plugin.getMsg(CommandMessage.ARMOR_REQUIREMENT_REMOVEALL_REMOVED, locale));
    }

    @Subcommand("multiplier add")
    @CommandCompletion("@skills_global @nothing @nothing true|false")
    @CommandPermission("auraskills.command.armor.multiplier")
    @Description("Adds an armor multiplier to the held item to global or a specific skill where value is the percent more XP gained.")
    public void onArmorMultiplierAdd(@Flags("itemheld") Player player, String target, String name, double value, @Default("true") boolean lore) {
        ItemStack item = player.getInventory().getItemInMainHand();
        Skill skill = plugin.getSkillRegistry().getOrNull(NamespacedId.fromDefault(target));
        Locale locale = plugin.getUser(player).getLocale();

        SkillsItem skillsItem = new SkillsItem(item, plugin);
        if (skill != null) { // Add multiplier for specific skill
            for (Multiplier multiplier : skillsItem.getOriginalMultipliers(ModifierType.ARMOR)) {
                if (multiplier.skill() == skill && multiplier.name().equals(name)) {
                    player.sendMessage(plugin.getPrefix(locale) + TextUtil.replace(plugin.getMsg(CommandMessage.ARMOR_MULTIPLIER_ADD_ALREADY_EXISTS, locale),
                            "{target}", skill.getDisplayName(locale),"{name}",name));
                    return;
                }
            }
            if (lore) {
                skillsItem.addMultiplierLore(ModifierType.ARMOR, skill, value, locale);
            }
            skillsItem.addMultiplier(ModifierType.ARMOR, skill, name, value);
            ItemStack newItem = skillsItem.getItem();
            player.getInventory().setItemInMainHand(newItem);
            player.sendMessage(plugin.getPrefix(locale) + TextUtil.replace(plugin.getMsg(CommandMessage.ARMOR_MULTIPLIER_ADD_ADDED, locale),
                    "{target}", skill.getDisplayName(locale), "{value}", String.valueOf(value),"{name}",name));
        } else if (target.equalsIgnoreCase("global")) { // Add multiplier for all skills
            String global = plugin.getMsg(CommandMessage.MULTIPLIER_GLOBAL, locale);
            for (Multiplier multiplier : skillsItem.getMultipliers(ModifierType.ARMOR)) {
                if (multiplier.skill() == null) {
                    player.sendMessage(plugin.getPrefix(locale) + TextUtil.replace(plugin.getMsg(CommandMessage.ARMOR_MULTIPLIER_ADD_ALREADY_EXISTS, locale),
                            "{target}", global,"{name}",name));
                    return;
                }
            }
            if (lore) {
                skillsItem.addMultiplierLore(ModifierType.ARMOR, null, value, locale);
            }
            skillsItem.addMultiplier(ModifierType.ARMOR, null, name ,value);
            ItemStack newItem = skillsItem.getItem();
            player.getInventory().setItemInMainHand(newItem);
            player.sendMessage(plugin.getPrefix(locale) + TextUtil.replace(plugin.getMsg(CommandMessage.ARMOR_MULTIPLIER_ADD_ADDED, locale),
                    "{target}", global, "{value}", String.valueOf(value),"{name}",name));
        } else {
            throw new InvalidCommandArgument("Target must be valid skill name or global");
        }
    }

    @Subcommand("multiplier remove")
    @CommandCompletion("@skills_global @nothing true|false")
    @CommandPermission("auraskills.command.armor.multiplier")
    @Description("Removes an armor multiplier of a the specified skill or global from the held item.")
    public void onArmorMultiplierRemove(@Flags("itemheld") Player player, String target, String name, boolean lore) {
        Locale locale = plugin.getUser(player).getLocale();
        ItemStack item = player.getInventory().getItemInMainHand();
        Skill skill = plugin.getSkillRegistry().getOrNull(NamespacedId.fromDefault(target));
        boolean removed = false;

        SkillsItem skillsItem = new SkillsItem(item, plugin);
        for (Multiplier multiplier : skillsItem.getOriginalMultipliers(ModifierType.ARMOR)) {
            if (multiplier.skill() == skill && multiplier.name().equals(name)) {
                skillsItem.removeMultiplier(ModifierType.ARMOR, skill, name, lore);
                skillsItem.removeMultiplierLore(multiplier, defaultLanguage);
                removed = true;
                break;
            }
        }
        item = skillsItem.getItem();
        player.getInventory().setItemInMainHand(item);
        // Use skill display name if skill is not null, otherwise use global name
        String targetName;
        if (skill != null) {
            targetName = skill.getDisplayName(locale);
        } else if (target.equalsIgnoreCase("global")) {
            targetName = plugin.getMsg(CommandMessage.MULTIPLIER_GLOBAL, locale);
        } else {
            throw new InvalidCommandArgument("Target must be valid skill name or global");
        }
        if (removed) {
            player.sendMessage(plugin.getPrefix(locale) + TextUtil.replace(plugin.getMsg(CommandMessage.ARMOR_MULTIPLIER_REMOVE_REMOVED, locale),
                    "{target}", targetName,"{name}",name));
        } else {
            player.sendMessage(plugin.getPrefix(locale) + TextUtil.replace(plugin.getMsg(CommandMessage.ARMOR_MULTIPLIER_REMOVE_DOES_NOT_EXIST, locale),
                    "{target}", targetName,"{name}",name));
        }
    }

    @Subcommand("multiplier list")
    @CommandPermission("auraskills.command.armor.multiplier")
    @Description("Lists all armor multipliers on the held item.")
    public void onArmorMultiplierList(@Flags("itemheld") Player player) {
        Locale locale = plugin.getUser(player).getLocale();
        ItemStack item = player.getInventory().getItemInMainHand();
        StringBuilder message = new StringBuilder(plugin.getMsg(CommandMessage.ARMOR_MULTIPLIER_LIST_HEADER, locale));
        SkillsItem skillsItem = new SkillsItem(item, plugin);
        for (Multiplier multiplier : skillsItem.getOriginalMultipliers(ModifierType.ARMOR)) {
            String targetName;
            if (multiplier.skill() != null) {
                targetName = multiplier.skill().getDisplayName(locale);
            } else {
                targetName = plugin.getMsg(CommandMessage.MULTIPLIER_GLOBAL, locale);
            }
            message.append("\n").append(TextUtil.replace(plugin.getMsg(CommandMessage.ARMOR_MULTIPLIER_LIST_ENTRY, locale),
                    "{target}", targetName, "{value}", String.valueOf(multiplier.value()),"{name}",multiplier.name()));
        }
        player.sendMessage(message.toString());
    }

    @Subcommand("multiplier removeall")
    @CommandPermission("auraskills.command.armor.multiplier")
    @Description("Removes all armor multipliers from the item held.")
    public void onArmorMultiplierRemoveAll(@Flags("itemheld") Player player) {
        Locale locale = plugin.getUser(player).getLocale();
        SkillsItem skillsItem = new SkillsItem(player.getInventory().getItemInMainHand(), plugin);

        skillsItem.removeAllModifierLore(MetaType.MULTIPLIER, ModifierType.ARMOR, defaultLanguage);
        skillsItem.removeAll(SkillsItem.MetaType.MULTIPLIER, ModifierType.ARMOR);

        player.getInventory().setItemInMainHand(skillsItem.getItem());
        player.sendMessage(plugin.getPrefix(locale) + plugin.getMsg(CommandMessage.ARMOR_MULTIPLIER_REMOVEALL_REMOVED, locale));
    }

}
