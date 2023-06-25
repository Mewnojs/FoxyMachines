package me.gallowsdove.foxymachines.implementation.tools;

import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.libraries.dough.config.Config;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import me.gallowsdove.foxymachines.FoxyMachines;
import me.gallowsdove.foxymachines.Items;
import me.gallowsdove.foxymachines.abstracts.AbstractWand;
import me.gallowsdove.foxymachines.utils.SimpleLocation;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SpongeWand extends AbstractWand {
    public SpongeWand() {
        super(Items.SPONGE_WAND, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
                Items.NUCLEAR_SALT, Items.WIRELESS_TRANSMITTER, Items.NUCLEAR_SALT,
                Items.DEMONIC_PLATE, SlimefunItems.PROGRAMMABLE_ANDROID_2, Items.DEMONIC_PLATE,
                Items.NUCLEAR_SALT, Items.COMPRESSED_SPONGE, Items.NUCLEAR_SALT
        });
    }

    @Override
    protected boolean isRemoving() {return true;}

    @Override
    protected float getCostPerBBlock() {
        return 0.24F;
    }

    @Override
    protected List<Location> getLocations(@Nonnull Player player) {
        ArrayList<Location> locs = new ArrayList<>();
        PersistentDataContainer container = player.getPersistentDataContainer();
        SimpleLocation loc1 = SimpleLocation.fromPersistentStorage(container, "primary_position");
        SimpleLocation loc2 = SimpleLocation.fromPersistentStorage(container, "secondary_position");

        if (loc1 == null || loc2 == null || !loc1.getWorldUUID().equals(loc2.getWorldUUID())) {
            player.sendMessage(ChatColor.RED + "请先使用位置选择器确定位置!");
            return locs;
        }

        if (loc1.getX() < loc2.getX()) {
            int tmp = loc1.getX();
            loc1.setX(loc2.getX());
            loc2.setX(tmp);
        }

        if (loc1.getY() < loc2.getY()) {
            int tmp = loc1.getY();
            loc1.setY(loc2.getY());
            loc2.setY(tmp);
        }

        if (loc1.getZ() < loc2.getZ()) {
            int tmp = loc1.getZ();
            loc1.setZ(loc2.getZ());
            loc2.setZ(tmp);
        }

        int max = new Config(FoxyMachines.getInstance()).getInt("max-sponge-wand-blocks");
        if ((loc1.getX() - loc2.getX()) * (loc1.getY() - loc2.getY()) * (loc1.getZ() - loc2.getZ()) > max) {
            player.sendMessage(ChatColor.RED + "选中的区域过大!");
            return locs;
        }

        World world = Bukkit.getWorld(UUID.fromString(loc1.getWorldUUID()));

        if (world == null) {
            player.sendMessage(ChatColor.RED + "请先使用位置选择器确定位置!");
            return locs;
        }

        for (int x = loc2.getX(); x <= loc1.getX(); x++) {
            for (int y = loc2.getY(); y <= loc1.getY(); y++) {
                for (int z = loc2.getZ(); z <= loc1.getZ(); z++) {
                    Block block = world.getBlockAt(x, y, z);
                    if ((block.getType() == Material.WATER || block.getType() == Material.LAVA) &&
                            Slimefun.getProtectionManager().hasPermission(player, block, Interaction.BREAK_BLOCK)) {
                        locs.add(block.getLocation());
                    }
                }
            }
        }
        if (locs.isEmpty()) {
            player.sendMessage(ChatColor.RED + "选择的区域无效!");
        }

        return locs;
    }

    @Override
    public float getMaxItemCharge(ItemStack itemStack) {
        return 2000;
    }
}
