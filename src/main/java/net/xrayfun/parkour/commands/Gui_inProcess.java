package net.xrayfun.parkour.commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class Gui_inProcess implements Listener {
    private static Inventory inv;
    public Gui_inProcess() {
        inv = Bukkit.createInventory(null, 18, "Parkours");
        initInvItems();
    }

    public void initInvItems() {
        inv.addItem(createGuiItem(
                Material.GLASS,
                (short) 5,
                "test",
                "§aНажмите, чтобы перейти на паркур!"));

    }

    protected ItemStack createGuiItem(final Material material, final short materialData, final String name, final String... lore) {
        final ItemStack item = new ItemStack(material, 1, materialData);
        final ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));

        item.setItemMeta(meta);
        return item;
    }

    public static void openInventory(final Player player) {
        player.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        if (!e.getInventory().equals(inv)) return;
        e.setCancelled(true);
        ItemStack clickedItem = e.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;
        player.sendMessage("Слот #" + e.getRawSlot());
    }

    @EventHandler
    public void onInventoryClick(final InventoryDragEvent e) {
        Player player = (Player) e.getWhoClicked();
        if (e.getInventory().equals(inv)) {
            player.sendMessage("test");
            e.setCancelled(false);
        }
    }
}
