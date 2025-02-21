package dev.aurelium.auraskills.bukkit.item;

import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import dev.aurelium.auraskills.api.bukkit.BukkitTraitHandler;
import dev.aurelium.auraskills.api.item.ModifierType;
import dev.aurelium.auraskills.api.registry.NamespaceIdentified;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.skill.Multiplier;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.api.stat.StatModifier;
import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.api.trait.TraitModifier;
import dev.aurelium.auraskills.api.util.AuraSkillsModifier;
import dev.aurelium.auraskills.api.util.NumberUtil;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.mana.ManaAbilityProvider;
import dev.aurelium.auraskills.bukkit.modifier.Modifiers;
import dev.aurelium.auraskills.bukkit.modifier.Multipliers;
import dev.aurelium.auraskills.bukkit.requirement.GlobalRequirement;
import dev.aurelium.auraskills.bukkit.requirement.Requirements;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.message.type.CommandMessage;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class SkillsItem {

    private final AuraSkills plugin;
    private final ItemStack item;
    private final ItemMeta meta;
    private static final String NAME_KEY = "name";
    private static final String VALUE_KEY = "value";

    public SkillsItem(ItemStack item, AuraSkills plugin) {
        this.item = item.clone();
        this.meta = this.item.getItemMeta();
        this.plugin = plugin;
    }

    public ItemStack getItem() {
        item.setItemMeta(meta);
        return item;
    }

    public List<StatModifier> getStatModifiers(ModifierType type) {
        PersistentDataContainer container = getContainer(MetaType.MODIFIER, type);
        List<StatModifier> modifiers = new ArrayList<>();

        for (NamespacedKey statKey : container.getKeys()) {
            // Process new format
            PersistentDataContainer modifiersContainer = container.get(statKey, PersistentDataType.TAG_CONTAINER);
            if (modifiersContainer == null) continue;

            Stat stat = plugin.getStatRegistry().getOrNull(NamespacedId.fromDefault(statKey.getKey()));
            if (stat == null) continue;

            for (NamespacedKey nameKey : modifiersContainer.getKeys()) {
                PersistentDataContainer modifierData = modifiersContainer.get(nameKey, PersistentDataType.TAG_CONTAINER);
                if (modifierData == null) continue;

                String keyname = modifierData.get(new NamespacedKey(plugin, NAME_KEY), PersistentDataType.STRING);
                String name = generateFinalName(type, stat.name(), keyname);

                Double value = modifierData.get(new NamespacedKey(plugin, VALUE_KEY), PersistentDataType.DOUBLE);

                if (value != null) {
                    modifiers.add(new StatModifier(name, stat, value));
                }
            }
        }
        return modifiers;
    }

    public List<StatModifier> getOriginalStatModifiers(ModifierType type) {
        PersistentDataContainer container = getContainer(MetaType.MODIFIER, type);
        List<StatModifier> modifiers = new ArrayList<>();

        for (NamespacedKey statKey : container.getKeys()) {
            // Process new format
            PersistentDataContainer modifiersContainer = container.get(statKey, PersistentDataType.TAG_CONTAINER);
            if (modifiersContainer == null) continue;

            Stat stat = plugin.getStatRegistry().getOrNull(NamespacedId.fromDefault(statKey.getKey()));
            if (stat == null) continue;

            for (NamespacedKey nameKey : modifiersContainer.getKeys()) {
                PersistentDataContainer modifierData = modifiersContainer.get(nameKey, PersistentDataType.TAG_CONTAINER);
                if (modifierData == null) continue;

                String keyname = modifierData.get(new NamespacedKey(plugin, NAME_KEY), PersistentDataType.STRING);
                Double value = modifierData.get(new NamespacedKey(plugin, VALUE_KEY), PersistentDataType.DOUBLE);

                if (value != null) {
                    modifiers.add(new StatModifier(keyname, stat, value));
                }
            }
        }
        return modifiers;
    }


    public List<TraitModifier> getTraitModifiers(ModifierType type) {
        PersistentDataContainer container = getContainer(MetaType.TRAIT_MODIFIER, type);
        List<TraitModifier> modifiers = new ArrayList<>();

        for (NamespacedKey traitKey : container.getKeys()) {
            // Process new format
            PersistentDataContainer modifiersContainer = container.get(traitKey, PersistentDataType.TAG_CONTAINER);
            if (modifiersContainer == null) continue;

            Trait trait = plugin.getTraitRegistry().getOrNull(NamespacedId.fromDefault(traitKey.getKey()));
            if (trait == null) continue;

            for (NamespacedKey nameKey : modifiersContainer.getKeys()) {
                PersistentDataContainer modifierData = modifiersContainer.get(nameKey, PersistentDataType.TAG_CONTAINER);
                if (modifierData == null) continue;

                String keyname = modifierData.get(new NamespacedKey(plugin, NAME_KEY), PersistentDataType.STRING);
                String name = generateFinalName(type, trait.name(), keyname);
                Double value = modifierData.get(new NamespacedKey(plugin, VALUE_KEY), PersistentDataType.DOUBLE);

                if (value != null) {
                    modifiers.add(new TraitModifier(name, trait, value));
                }
            }
        }
        return modifiers;
    }

    public List<TraitModifier> getOriginalTraitModifiers(ModifierType type) {
        PersistentDataContainer container = getContainer(MetaType.TRAIT_MODIFIER, type);
        List<TraitModifier> modifiers = new ArrayList<>();

        for (NamespacedKey traitKey : container.getKeys()) {
            // Process new format
            PersistentDataContainer modifiersContainer = container.get(traitKey, PersistentDataType.TAG_CONTAINER);
            if (modifiersContainer == null) continue;

            Trait trait = plugin.getTraitRegistry().getOrNull(NamespacedId.fromDefault(traitKey.getKey()));
            if (trait == null) continue;

            for (NamespacedKey nameKey : modifiersContainer.getKeys()) {
                PersistentDataContainer modifierData = modifiersContainer.get(nameKey, PersistentDataType.TAG_CONTAINER);
                if (modifierData == null) continue;

                String name = modifierData.get(new NamespacedKey(plugin, NAME_KEY), PersistentDataType.STRING);
                Double value = modifierData.get(new NamespacedKey(plugin, VALUE_KEY), PersistentDataType.DOUBLE);

                if (value != null) {
                    modifiers.add(new TraitModifier(name, trait, value));
                }
            }
        }
        return modifiers;
    }

    public List<StatModifier> getStatModifiersByName(ModifierType type, String nameFilter) {
        PersistentDataContainer container = getContainer(MetaType.MODIFIER, type);
        List<StatModifier> modifiers = new ArrayList<>();

        for (NamespacedKey statKey : container.getKeys()) {
            // Process new format
            PersistentDataContainer modifiersContainer = container.get(statKey, PersistentDataType.TAG_CONTAINER);
            if (modifiersContainer == null) continue;

            Stat stat = plugin.getStatRegistry().getOrNull(NamespacedId.fromDefault(statKey.getKey()));
            if (stat == null) continue;

            for (NamespacedKey nameKey : modifiersContainer.getKeys()) {
                PersistentDataContainer modifierData = modifiersContainer.get(nameKey, PersistentDataType.TAG_CONTAINER);
                if (modifierData == null) continue;

                String keyname = modifierData.get(new NamespacedKey(plugin, NAME_KEY), PersistentDataType.STRING);

                if (Objects.equals(keyname, nameFilter)) {
                    Double value = modifierData.get(new NamespacedKey(plugin, VALUE_KEY), PersistentDataType.DOUBLE);

                    if (value != null) {
                        modifiers.add(new StatModifier(keyname, stat, value));
                    }
                }
            }
        }
        return modifiers;
    }

    private String generateFinalName(ModifierType type, String modifiername, String keyname) {

        if (type == ModifierType.ITEM) {
            return "AuraSkills.Modifiers.Item." + modifiername + "." + keyname;
        } else {
            return "AuraSkills.Modifiers.Armor." + getSlotName() + "." + modifiername + "." + keyname;
        }

    }

    //Maybe a stacking option should be implemented to determine if modifiers overwrites/stack
    public void addModifier(MetaType metaType, ModifierType modifierType, NamespaceIdentified identified, String name, double value) {
        PersistentDataContainer container = getContainer(metaType, modifierType);
        NamespacedKey identifiedKey = new NamespacedKey(plugin, identified.getId().toString());

        // Get or create container for this stat/trait
        PersistentDataContainer modifiersContainer = container.getOrDefault(identifiedKey,
                PersistentDataType.TAG_CONTAINER,
                container.getAdapterContext().newPersistentDataContainer());

        // Create container for this specific modifier
        PersistentDataContainer modifierData = container.getAdapterContext().newPersistentDataContainer();
        modifierData.set(new NamespacedKey(plugin, NAME_KEY), PersistentDataType.STRING, name);
        modifierData.set(new NamespacedKey(plugin, VALUE_KEY), PersistentDataType.DOUBLE, value);

        // Add modifier data using name as key
        modifiersContainer.set(new NamespacedKey(plugin, name), PersistentDataType.TAG_CONTAINER, modifierData);

        // Save back to parent container
        container.set(identifiedKey, PersistentDataType.TAG_CONTAINER, modifiersContainer);
        saveTagContainer(container, metaType, modifierType);
    }

    public void removeModifier(MetaType metaType, ModifierType modifierType, NamespaceIdentified identified, String name, boolean lore) {
        PersistentDataContainer container = getContainer(metaType, modifierType);
        NamespacedKey identifiedKey = new NamespacedKey(plugin, identified.getId().toString());
        PersistentDataContainer modifiersContainer = container.get(identifiedKey, PersistentDataType.TAG_CONTAINER);
        if (modifiersContainer == null) return;
        if (lore){
            removeModifierLore(identified, name, modifierType);
        }

        modifiersContainer.remove(new NamespacedKey(plugin, name));

        if (modifiersContainer.isEmpty()) {
            container.remove(identifiedKey);
        } else {
            container.set(identifiedKey, PersistentDataType.TAG_CONTAINER, modifiersContainer);
        }
        saveTagContainer(container, metaType, modifierType);
        removeEmpty(container, metaType, modifierType);
    }

    public void removeAll(MetaType metaType, ModifierType modifierType) {
        PersistentDataContainer parent = meta.getPersistentDataContainer();
        parent.remove(new NamespacedKey(plugin, getContainerName(metaType, modifierType)));
    }

    public List<Multiplier> getMultipliers(ModifierType type) {
        PersistentDataContainer container = getContainer(MetaType.MULTIPLIER, type);
        List<Multiplier> multipliers = new ArrayList<>();

        for (NamespacedKey skillKey : container.getKeys()) {
             // Process new format
            PersistentDataContainer modifiersContainer = container.get(skillKey, PersistentDataType.TAG_CONTAINER);
            if (modifiersContainer == null) continue;

            Skill skill = plugin.getSkillRegistry().getOrNull(NamespacedId.fromDefault(skillKey.getKey()));
            if (skill == null) continue;

            for (NamespacedKey nameKey : modifiersContainer.getKeys()) {
                PersistentDataContainer modifierData = modifiersContainer.get(nameKey, PersistentDataType.TAG_CONTAINER);
                if (modifierData == null) continue;

                String keyname = modifierData.get(new NamespacedKey(plugin, NAME_KEY), PersistentDataType.STRING);
                String name = generateFinalName(type, skill.name(), keyname);
                Double value = modifierData.get(new NamespacedKey(plugin, VALUE_KEY), PersistentDataType.DOUBLE);
                if (value != null) {
                    multipliers.add(new Multiplier(name, skill, value));
                }
            }
        }
        return multipliers;
    }

    public List<Multiplier> getOriginalMultipliers(ModifierType type) {
        PersistentDataContainer container = getContainer(MetaType.MULTIPLIER, type);
        List<Multiplier> multipliers = new ArrayList<>();

        for (NamespacedKey skillKey : container.getKeys()) {
            // Try to read old storage
            if (container.has(skillKey, PersistentDataType.DOUBLE)) {
                Double oldValue = container.get(skillKey, PersistentDataType.DOUBLE);
                if (oldValue != null && oldValue != 0.0) {
                    Skill skill = plugin.getSkillRegistry().getOrNull(NamespacedId.fromDefault(skillKey.getKey()));
                    if (skill != null) {
                        String name = type == ModifierType.ITEM
                                ? "AuraSkills.Modifiers.Item." + getMultiplierName(skill)
                                : "AuraSkills.Modifiers.Armor." + getSlotName() + "." + getMultiplierName(skill);

                        // Migrate to new
                        PersistentDataContainer newContainer = container.getAdapterContext().newPersistentDataContainer();
                        newContainer.set(new NamespacedKey(plugin, NAME_KEY), PersistentDataType.STRING, name);
                        newContainer.set(new NamespacedKey(plugin, VALUE_KEY), PersistentDataType.DOUBLE, oldValue);

                        PersistentDataContainer newModifiersContainer = container.getAdapterContext().newPersistentDataContainer();
                        newModifiersContainer.set(new NamespacedKey(plugin, "default"), PersistentDataType.TAG_CONTAINER, newContainer);
                        container.set(skillKey, PersistentDataType.TAG_CONTAINER, newModifiersContainer);

                        // Remove old
                        container.remove(skillKey);

                        multipliers.add(new Multiplier(name, skill, oldValue));
                        continue;
                    }
                    continue;
                }
            }
            // Process new format
            PersistentDataContainer modifiersContainer = container.get(skillKey, PersistentDataType.TAG_CONTAINER);
            if (modifiersContainer == null) continue;

            Skill skill = plugin.getSkillRegistry().getOrNull(NamespacedId.fromDefault(skillKey.getKey()));
            if (skill == null) continue;

            for (NamespacedKey nameKey : modifiersContainer.getKeys()) {
                PersistentDataContainer modifierData = modifiersContainer.get(nameKey, PersistentDataType.TAG_CONTAINER);
                if (modifierData == null) continue;

                String name = modifierData.get(new NamespacedKey(plugin, NAME_KEY), PersistentDataType.STRING);
                Double value = modifierData.get(new NamespacedKey(plugin, VALUE_KEY), PersistentDataType.DOUBLE);
                if (value != null) {
                    multipliers.add(new Multiplier(name, skill, value));
                }
            }
        }
        return multipliers;
    }

    public void addMultiplier(ModifierType type, @Nullable Skill skill, String name, double value) {
        PersistentDataContainer container = getContainer(MetaType.MULTIPLIER, type);
        NamespacedKey skillKey = getSkillKey(skill);

        // Get or create container for this skill
        PersistentDataContainer modifiersContainer = container.getOrDefault(skillKey,
                PersistentDataType.TAG_CONTAINER,
                container.getAdapterContext().newPersistentDataContainer());

        // Create container for this specific modifier
        PersistentDataContainer modifierData = container.getAdapterContext().newPersistentDataContainer();
        modifierData.set(new NamespacedKey(plugin, NAME_KEY), PersistentDataType.STRING, name);
        modifierData.set(new NamespacedKey(plugin, VALUE_KEY), PersistentDataType.DOUBLE, value);

        // Add modifier data using name as key
        modifiersContainer.set(new NamespacedKey(plugin, name), PersistentDataType.TAG_CONTAINER, modifierData);

        // Save back to parent container
        container.set(skillKey, PersistentDataType.TAG_CONTAINER, modifiersContainer);
        saveTagContainer(container, MetaType.MULTIPLIER, type);
    }
    public void removeMultiplier(ModifierType type, Skill skill, String name, boolean lore) {
        PersistentDataContainer container = getContainer(MetaType.MULTIPLIER, type);
        NamespacedKey skillKey = getSkillKey(skill);

        PersistentDataContainer modifiersContainer = container.get(skillKey, PersistentDataType.TAG_CONTAINER);
        if (modifiersContainer == null) return;

        if (lore) {
            // Find the multiplier to remove its lore
            for (Multiplier multiplier : getOriginalMultipliers(type)) {
                if (multiplier.skill() == skill && multiplier.name().equals(name)) {
                    removeMultiplierLore(multiplier, plugin.getDefaultLanguage());
                    break;
                }
            }
        }

        modifiersContainer.remove(new NamespacedKey(plugin, name));

        if (modifiersContainer.isEmpty()) {
            container.remove(skillKey);
        } else {
            container.set(skillKey, PersistentDataType.TAG_CONTAINER, modifiersContainer);
        }
        saveTagContainer(container, MetaType.MULTIPLIER, type);
        removeEmpty(container, MetaType.MULTIPLIER, type);
    }

    public void removeMultiplierLore(Multiplier multiplier, Locale locale) {
        List<String> lore = meta.getLore();
        if (lore == null || lore.isEmpty()) return;

        String value = NumberUtil.format1(Math.abs(multiplier.value()));

        for (int i = lore.size() - 1; i >= 0; i--) {
            String line = lore.get(i);

            if (line.contains("%") && line.contains(value)) {
                if (!multiplier.isGlobal()) {
                    if (line.contains(multiplier.skill().getDisplayName(locale))) {
                        lore.remove(i);
                    }
                } else {
                    lore.remove(i);
                }
            }
        }
        meta.setLore(lore);
    }


    // Requirements methods remain largely unchanged
    public Map<Skill, Integer> getRequirements(ModifierType type) {
        PersistentDataContainer container = getContainer(MetaType.REQUIREMENT, type);
        Map<Skill, Integer> requirements = new HashMap<>();

        for (NamespacedKey key : container.getKeys()) {
            int value = container.getOrDefault(key, PersistentDataType.INTEGER, 0);
            if (value == 0) continue;

            Skill skill = plugin.getSkillRegistry().getOrNull(NamespacedId.fromDefault(key.getKey()));
            if (skill == null) continue;

            requirements.put(skill, value);
        }
        return requirements;
    }

    public void addRequirement(ModifierType type, Skill skill, int level) {
        PersistentDataContainer container = getContainer(MetaType.REQUIREMENT, type);
        NamespacedKey key = new NamespacedKey(plugin, skill.getId().toString());
        container.set(key, PersistentDataType.INTEGER, level);
        saveTagContainer(container, MetaType.REQUIREMENT, type);
    }

    public void removeRequirement(ModifierType type, Skill skill) {
        PersistentDataContainer container = getContainer(MetaType.REQUIREMENT, type);
        NamespacedKey key = new NamespacedKey(plugin, skill.getId().toString());
        container.remove(key);
        saveTagContainer(container, MetaType.REQUIREMENT, type);
        removeEmpty(container, MetaType.REQUIREMENT, type);
    }

    public void addIgnore() {
        PersistentDataContainer container = meta.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(plugin, ManaAbilityProvider.IGNORE_INTERACT_KEY);
        container.set(key, PersistentDataType.BYTE, (byte) 1);
    }

    public void removeIgnore() {
        PersistentDataContainer container = meta.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(plugin, ManaAbilityProvider.IGNORE_INTERACT_KEY);
        container.remove(key);
    }

    public void convertFromLegacy(ReadWriteNBT nbt) {
        if (plugin.isNbtApiDisabled()) return;

        // Convert stat modifiers
        Modifiers modifiers = new Modifiers(plugin);
        for (ModifierType type : ModifierType.values()) {
            List<StatModifier> legacy = modifiers.getLegacyModifiers(type, nbt);
            if (legacy.isEmpty()) continue;

            for (StatModifier modifier : legacy) {
                addModifier(MetaType.MODIFIER, type, modifier.stat(), modifier.name(), modifier.value());
            }
        }
        // Convert multipliers
        Multipliers multipliers = new Multipliers(plugin);
        for (ModifierType type : ModifierType.values()) {
            List<Multiplier> legacy = multipliers.getLegacyMultipliers(type, nbt);
            if (legacy.isEmpty()) continue;

            for (Multiplier multiplier : legacy) {
                addMultiplier(type, multiplier.skill(), multiplier.name(), multiplier.value());
            }
        }
        // Convert requirements
        Requirements requirements = new Requirements(plugin);
        for (ModifierType type : ModifierType.values()) {
            Map<Skill, Integer> legacy = requirements.getLegacyRequirements(type, nbt);
            if (legacy.isEmpty()) continue;

            for (Map.Entry<Skill, Integer> entry : legacy.entrySet()) {
                addRequirement(type, entry.getKey(), entry.getValue());
            }
        }
    }

    public boolean meetsRequirements(ModifierType type, Player player) {
        if (!plugin.configBoolean(Option.REQUIREMENT_ENABLED)) return true;
        if (player.hasMetadata("NPC")) return true;
        User user = plugin.getUser(player);
        Map<Skill, Integer> itemRequirements = getRequirements(type);

        // If override_global is true, only check global if the item has no defined NBT requirements
        if (!plugin.configBoolean(Option.REQUIREMENT_OVERRIDE_GLOBAL) || itemRequirements.isEmpty()) {
            // Check global requirements
            for (Map.Entry<Skill, Integer> entry : getGlobalRequirements(type).entrySet()) {
                if (user.getSkillLevel(entry.getKey()) < entry.getValue()) {
                    return false;
                }
            }
        }
        // Check requirements on item
        for (Map.Entry<Skill, Integer> entry : getRequirements(type).entrySet()) {
            if (user.getSkillLevel(entry.getKey()) < entry.getValue()) {
                return false;
            }
        }
        return true;
    }

    public Map<Skill, Integer> getGlobalRequirements(ModifierType type) {
        Map<Skill, Integer> requirements = new HashMap<>();
        for (GlobalRequirement global : plugin.getRequirementManager().getGlobalRequirementsType(type)) {
            if (global.getMaterial() == item.getType()) {
                requirements.putAll(global.getRequirements());
            }
        }
        return requirements;
    }

    public void addModifierLore(ModifierType type, NamespaceIdentified identified, double value, Locale locale) {
        List<String> lore;
        if (meta.getLore() != null) {
            if (!meta.getLore().isEmpty()) lore = meta.getLore();
            else lore = new LinkedList<>();
        } else {
            lore = new LinkedList<>();
        }
        CommandMessage message;
        if (value >= 0) {
            message = CommandMessage.valueOf(type.name() + "_MODIFIER_ADD_LORE");
        } else {
            message = CommandMessage.valueOf(type.name() + "_MODIFIER_ADD_LORE_SUBTRACT");
        }
        if (identified instanceof Stat stat) {
            lore.add(0, plugin.getMessageProvider().applyFormatting(TextUtil.replace(plugin.getMsg(message, locale),
                    "{stat}", stat.getDisplayName(locale),
                    "{value}", NumberUtil.format1(Math.abs(value)),
                    "{color}", stat.getColor(locale),
                    "{symbol}", stat.getSymbol(locale))));
        } else if (identified instanceof Trait trait) {
            @Nullable Stat stat = plugin.getTraitManager().getLinkedStats(trait).stream().findAny().orElse(null);
            String formatValue = getFormattedValue(value, trait);
            lore.add(0, plugin.getMessageProvider().applyFormatting(TextUtil.replace(plugin.getMsg(message, locale),
                    "{stat}", trait.getDisplayName(locale),
                    "{value}", formatValue,
                    "{color}", stat != null ? stat.getColor(locale) : "")));
        }
        meta.setLore(lore);
    }

    public @NotNull String getFormattedValue(double value, Trait trait) {
        BukkitTraitHandler impl = plugin.getTraitManager().getTraitImpl(trait);
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

    public void removeModifierLore(NamespaceIdentified identified, String name, ModifierType type){
            if (identified instanceof Stat stat) {
                for(StatModifier statModifier : getOriginalStatModifiers(type)){
                    if (statModifier.stat() == stat && statModifier.name().equals(name)) {
                        String value = NumberUtil.format0(statModifier.value());
                        removeModifierLoreLine(value, stat.getDisplayName(plugin.getDefaultLanguage()));
                        break;
                    }
                }
            }
        if (identified instanceof Trait trait) {
            for(TraitModifier traitModifier : getOriginalTraitModifiers(type)){
                if (traitModifier.trait() == trait && traitModifier.name().equals(name)) {
                    String value = NumberUtil.format0(traitModifier.value());
                    removeModifierLoreLine(value, trait.getDisplayName(plugin.getDefaultLanguage()));
                    break;
                }
            }
        }
    }

    public <T> void removeModifierLoreLine(String value, String display) {
        List<String> lore = meta.getLore();
        if (lore != null && !lore.isEmpty() && value != null) {
            lore.removeIf(line -> line.contains(display) && line.contains(value));
        }
        meta.setLore(lore);
    }


    public void removeAllModifierLore(MetaType metaType, ModifierType modifierType, Locale locale) {
        if (metaType == MetaType.MODIFIER) {
            // Get all stat modifiers and remove their lore
            List<StatModifier> statModifiers = getStatModifiers(modifierType);
            for (StatModifier modifier : statModifiers) {
                removeModifierLore(modifier.stat(), modifier.stat().getDisplayName(locale), modifierType);
            }
        } else if (metaType == MetaType.TRAIT_MODIFIER) {
            // Get all trait modifiers and remove their lore
            List<TraitModifier> traitModifiers = getTraitModifiers(modifierType);
            for (TraitModifier modifier : traitModifiers) {
                removeModifierLore(modifier.trait(), modifier.trait().getDisplayName(locale),modifierType);
            }
        } else if (metaType == MetaType.MULTIPLIER) {
            // Same here
            List<Multiplier> multipliers = getMultipliers(modifierType);
            for (Multiplier multiplier : multipliers) {
                removeMultiplierLore(multiplier, locale);
            }
        } else if (metaType == MetaType.REQUIREMENT) {
            // And here
            Map<Skill, Integer> requirements = getRequirements(modifierType);
            for (Map.Entry<Skill, Integer> entry : requirements.entrySet()) {
                removeRequirementLore(entry.getKey());
            }
        }
    }

    public void addMultiplierLore(ModifierType type, Skill skill, double value, Locale locale) {
        List<String> lore;
        int indexFromBottom = 4;
        if (meta.getLore() != null) {
            if (!meta.getLore().isEmpty()) {
                lore = meta.getLore();
            } else {
                lore = new LinkedList<>();
            }
        } else {
            lore = new LinkedList<>();
        }
        if (skill != null) { // Skill multiplier
            CommandMessage message;
            if (value >= 0) {
                message = CommandMessage.valueOf(type.name() + "_MULTIPLIER_ADD_SKILL_LORE");
            } else {
                message = CommandMessage.valueOf(type.name() + "_MULTIPLIER_ADD_SKILL_LORE_SUBTRACT");
            }

            lore.add(lore.size() - indexFromBottom, TextUtil.replace(plugin.getMsg(message, locale),
                    "{skill}", skill.getDisplayName(locale),
                    "{value}", NumberUtil.format1(Math.abs(value))));
        } else { // Global multiplier
            CommandMessage message;
            if (value >= 0) {
                message = CommandMessage.valueOf(type.name() + "_MULTIPLIER_ADD_GLOBAL_LORE");
            } else {
                message = CommandMessage.valueOf(type.name() + "_MULTIPLIER_ADD_GLOBAL_LORE_SUBTRACT");
            }
            lore.add(lore.size() - indexFromBottom, TextUtil.replace(plugin.getMsg(message, locale),
                    "{value}", NumberUtil.format1(Math.abs(value))));
        }
        meta.setLore(lore);
    }



    public void addRequirementLore(ModifierType type, Skill skill, int level, Locale locale) {
        String text = TextUtil.replace(plugin.getMsg(CommandMessage.valueOf(type.name() + "_REQUIREMENT_ADD_LORE"), locale),
                "{skill}", skill.getDisplayName(locale),
                "{level}", String.valueOf(level));
        List<String> lore;
        if (meta.hasLore()) lore = meta.getLore();
        else lore = new ArrayList<>();
        if (lore != null) {
            lore.add(text);
            meta.setLore(lore);
        }
    }

    //remade this to work in any language (highly optimizable)
    public void removeRequirementLore(Skill skill) {
        List<String> lore = meta.getLore();
        if (lore == null || lore.isEmpty()) return;

        Locale defaultLocale = plugin.getDefaultLanguage();
        String messageFormat = plugin.getMsg(CommandMessage.valueOf(ModifierType.ITEM.name() + "_REQUIREMENT_ADD_LORE"), defaultLocale);

        String cleanFormat = messageFormat
                .replaceAll("<[^>]+>", "") //remove color tags
                .replace("{skill}", skill.getDisplayName(defaultLocale))
                .replace("{level}", ".*?") //remove placeholders
                .trim();

        for (int i = lore.size() - 1; i >= 0; i--) {
            String line = lore.get(i);
            String cleanLine = line.replaceAll("<[^>]+>", "").trim();
            if (cleanLine.matches(cleanFormat)) {
                lore.remove(i);
            }
        }
        meta.setLore(lore);
    }

    private NamespacedKey getSkillKey(@Nullable Skill skill) {
        if (skill != null) {
            return new NamespacedKey(plugin, skill.getId().toString());
        } else {
            return new NamespacedKey(plugin, "global");
        }
    }

    private PersistentDataContainer getContainer(MetaType metaType, ModifierType modifierType) {
        var container = meta.getPersistentDataContainer();
        String name = getContainerName(metaType, modifierType);
        NamespacedKey metaKey = new NamespacedKey(plugin, name); // Key for identifying meta type, like auraskills:modifiers
        var metaContainer = container.get(metaKey, PersistentDataType.TAG_CONTAINER);
        // Create and set new meta container if missing
        if (metaContainer == null) {
            metaContainer = container.getAdapterContext().newPersistentDataContainer();
        }
        return metaContainer;
    }

    private void saveTagContainer(PersistentDataContainer container, MetaType metaType, ModifierType modifierType) {
        PersistentDataContainer parent = meta.getPersistentDataContainer();
        String name = getContainerName(metaType, modifierType);
        parent.set(new NamespacedKey(plugin, name), PersistentDataType.TAG_CONTAINER, container);
    }

    private void removeEmpty(PersistentDataContainer container, MetaType metaType, ModifierType modifierType) {
        if (!container.isEmpty()) {
            return;
        }

        PersistentDataContainer parent = meta.getPersistentDataContainer();
        NamespacedKey metaKey = new NamespacedKey(plugin, getContainerName(metaType, modifierType));
        parent.remove(metaKey);
    }

    private String getContainerName(MetaType metaType, ModifierType modifierType) {
        return modifierType.toString().toLowerCase(Locale.ROOT) + "_" + metaType.getKey();
    }

    private String getName(Stat stat) {
        return TextUtil.capitalize(stat.name().toLowerCase(Locale.ROOT));
    }

    private String getName(Trait trait) {
        return TextUtil.capitalize(trait.name().toLowerCase(Locale.ROOT));
    }

    private String getMultiplierName(@Nullable Skill skill) {
        if (skill != null) {
            return TextUtil.capitalize(skill.toString().toLowerCase(Locale.ROOT));
        } else {
            return "Global";
        }
    }

    private String getSlotName() {
        String slot = "Helmet";
        String mat = item.getType().toString();
        if (mat.contains("CHESTPLATE") || item.getType() == Material.ELYTRA) {
            slot = "Chestplate";
        } else if (mat.contains("LEGGINGS")) {
            slot = "Leggings";
        } else if (mat.contains("BOOTS")) {
            slot = "Boots";
        }
        return slot;
    }

    public enum MetaType {
        MODIFIER("modifiers"),
        TRAIT_MODIFIER("trait_modifiers"),
        REQUIREMENT("requirements"),
        MULTIPLIER("multipliers");

        private final String key;

        MetaType(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }
    }
}