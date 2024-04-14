package ramune314159265.spigotserverlistsmapi.guis;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ramune314159265.spigotserverlistsmapi.ServerData;
import ramune314159265.spigotserverlistsmapi.SMServers;
import ramune314159265.spigotserverlistsmapi.ServerListSMapi;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ServerListGui implements Listener {
	public static HashMap<Inventory, HashMap<Integer, String>> slotMap;
	private final Inventory inventory;
	private final Integer size;

	public ServerListGui() {
		this.size = 18;
		this.slotMap = new HashMap<>();
		this.inventory = Bukkit.createInventory(null, this.size, "§2§l移動するサーバーを選択");
	}

	static public String getStatusColor(String status) {
		return switch (status) {
			case "online" -> "§a";
			case "booting" -> "§6";
			case "offline" -> "§c";
			default -> "";
		};
	}

	static public String getStatusMessage(String status) {
		return switch (status) {
			case "online" -> "オンライン";
			case "booting" -> "起動中";
			case "offline" -> "オフライン";
			default -> "";
		};
	}

	static public boolean getIsConnected(ServerData server, Player player) {
		List<String> serverPlayers = Arrays.asList(server.players);
		return serverPlayers.contains(player.getName());
	}

	public void init(Player player) {
		HashMap<Integer, String> slot = new HashMap<>();
		HashMap<String, ServerData> servers = SMServers.get();
		ServerData proxy = servers.get(ServerListSMapi.proxyId);
		if (Objects.isNull(proxy)) {
			player.sendMessage("§c取得できませんでした");
			return;
		}
		int index = 0;
		for (String id : proxy.childIds) {
			ServerData server = SMServers.servers.get(id);
			Material material = Objects.requireNonNullElse(Material.matchMaterial(server.minecraftItemIcon), Material.STONE);
			ItemStack item = new ItemStack(material, Math.max(1, server.players.length));
			ItemMeta meta = item.getItemMeta();

			meta.setDisplayName(ServerListGui.getStatusColor(server.status) + Objects.requireNonNullElse(server.name, "不明"));
			meta.setLore(Arrays.asList(
					"§r§7ID  : " + "§o" + server.id,
					"§r§f状態: " + ServerListGui.getStatusColor(server.status) + ServerListGui.getStatusMessage(server.status),
					"§r§f人数: " + server.players.length + "人",
					"",
					ServerListGui.getIsConnected(server, player) ? "§r§c§l既に接続されています" : "§r§f" + server.description
			));
			item.setItemMeta(meta);

			this.inventory.addItem(item);
			slot.put(index++, id);
		}
		ItemStack closeItem = new ItemStack(Material.BARRIER, 1);
		ItemMeta meta = closeItem.getItemMeta();

		meta.setDisplayName("§r§f" + "閉じる");
		closeItem.setItemMeta(meta);

		this.inventory.setItem(this.size - 1, closeItem);

		ServerListGui.slotMap.put(this.inventory, slot);
	}

	public void open(Player player) {
		this.init(player);

		player.openInventory(this.inventory);
		ServerListSMapi.playerInvMap.put(player.getUniqueId(), this.inventory);
	}

	@EventHandler
	public void onInventoryClick(final InventoryClickEvent e) {
		if (!e.getInventory().equals(ServerListSMapi.playerInvMap.get(e.getWhoClicked().getUniqueId()))) {
			return;
		}
		e.setCancelled(true);
		int clickedSlot = e.getRawSlot();
		if (clickedSlot == this.size - 1) {
			e.getWhoClicked().getOpenInventory().close();
			return;
		}

		String clickedServerId = ServerListGui.slotMap.get(e.getInventory()).get(clickedSlot);
		if (Objects.isNull(clickedServerId)) {
			return;
		}
		HashMap<String, ServerData> servers = SMServers.get();
		e.getWhoClicked().sendMessage(servers.get(clickedServerId).name + "に接続中です...");
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("Connect");
		out.writeUTF(clickedServerId);

		((Player) e.getWhoClicked()).sendPluginMessage(ServerListSMapi.getInstance(), "BungeeCord", out.toByteArray());
		e.getWhoClicked().getOpenInventory().close();
	}

	@EventHandler
	public void onInventoryClose(final InventoryCloseEvent e) {
		if (!e.getInventory().equals(ServerListSMapi.playerInvMap.get(e.getPlayer().getUniqueId()))) {
			return;
		}
		ServerListSMapi.playerInvMap.remove(e.getPlayer().getUniqueId());
	}
}
