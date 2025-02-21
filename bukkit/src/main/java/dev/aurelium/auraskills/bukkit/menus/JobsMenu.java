package dev.aurelium.auraskills.bukkit.menus;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.menus.shared.GlobalItems;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import dev.aurelium.slate.action.trigger.ClickTrigger;
import dev.aurelium.slate.builder.MenuBuilder;
import dev.aurelium.slate.item.provider.ListBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Map;

public class JobsMenu {

    private final AuraSkills plugin;

    public JobsMenu(AuraSkills plugin) {
        this.plugin = plugin;
    }

    public void build(MenuBuilder menu) {
        menu.defaultOptions(Map.of("bar_length", 20));

        var globalItems = new GlobalItems(plugin);
        menu.item("close", globalItems::close);
        menu.fillItem(globalItems::fill);

        menu.item("skull", item -> {
            item.replace("player", p -> p.player().getName());

            item.replace("entries", p -> {
                User user = plugin.getUser(p.player());
                ListBuilder builder = new ListBuilder(p.data().listData());

                for (Skill job : user.getJobs()) {
                    String entry = p.menu().getFormat("player_job_entry");
                    entry = TextUtil.replace(entry,
                            "{color}", "<aqua>", // O puedes obtener el color del trabajo si existe
                            "{job}", job.getDisplayName(user.getLocale(), false),
                            "{level}", String.valueOf(user.getSkillLevel(job)));
                    builder.append(entry);
                }
                return builder.build();
            });

            item.modify(i -> {
                if (i.item().getItemMeta() instanceof SkullMeta meta) {
                    meta.setOwningPlayer(Bukkit.getOfflinePlayer(i.player().getUniqueId()));
                    i.item().setItemMeta(meta);
                }
                return i.item();
            });
        });

        menu.item("back", item -> {
            item.onClick(c -> {
                Player player = c.player();
                plugin.getSlate().openMenu(player, "skills");
            });
        });

        // Registramos los componentes
        menu.component("job_active", Skill.class, component -> {
            component.shouldShow(t -> {
                User user = plugin.getUser(t.player());
                Skill skill = t.value();
                return user.getJobs().contains(skill);
            });
        });

        menu.component("job_available", Skill.class, component -> {
            component.shouldShow(t -> {
                User user = plugin.getUser(t.player());
                Skill skill = t.value();
                return !user.getJobs().contains(skill) && user.getJobs().size() < user.getJobLimit();
            });
        });

        menu.component("job_limit", Skill.class, component -> {
            component.shouldShow(t -> {
                User user = plugin.getUser(t.player());
                Skill skill = t.value();
                return !user.getJobs().contains(skill) && user.getJobs().size() >= user.getJobLimit();
            });
        });

        menu.template("job", Skill.class, template -> {
            template.replace("job_name", p -> p.value().getDisplayName(p.locale(), false));
            template.replace("job_description", p -> p.value().getDescription(p.locale(), false).replace("XP", "Dinero"));
            template.replace("level", p -> String.valueOf(plugin.getUser(p.player()).getSkillLevel(p.value())));

            template.onClick(ClickTrigger.LEFT, c -> {
                Player player = c.player();
                Skill selectedSkill = c.value();
                User user = plugin.getUser(player);

                if (!user.getJobs().contains(selectedSkill) && user.getJobs().size() < user.getJobLimit()) {
                    user.addJob(selectedSkill);
                    plugin.getSlate().openMenu(player, "jobs");
                }
            });

            template.onClick(ClickTrigger.RIGHT, c -> {
                Player player = c.player();
                Skill selectedSkill = c.value();
                User user = plugin.getUser(player);

                if (user.getJobs().contains(selectedSkill)) {
                    user.removeJob(selectedSkill);
                    plugin.getSlate().openMenu(player, "jobs");
                }
            });

            template.modify(t -> {
                User user = plugin.getUser(t.player());
                Skill skill = t.value();

                // Si el usuario tiene este trabajo, hacer que brille
                if (user.getJobs().contains(skill)) {
                    // Ocultar el texto del encantamiento
                    var meta = t.item().getItemMeta();
                    if (meta != null) {
                        meta.setEnchantmentGlintOverride(true);
                    }
                    t.item().setItemMeta(meta);
                }
                return t.item();
            });


            template.definedContexts(m -> plugin.getSkillManager().getEnabledSkills());
        });
    }
}