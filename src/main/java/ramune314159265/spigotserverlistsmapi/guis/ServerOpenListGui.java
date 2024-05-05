package ramune314159265.spigotserverlistsmapi.guis;

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
import ramune314159265.spigotserverlistsmapi.SMServers;
import ramune314159265.spigotserverlistsmapi.ServerData;
import ramune314159265.spigotserverlistsmapi.ServerListSMapi;

import java.net.http.HttpResponse;
import java.util.*;

public class ServerOpenListGui implements Listener {
	public static HashMap<Inventory, HashMap<Integer, String>> slotMap;

	static {
		slotMap = new HashMap<>();
	}

	private Inventory inventory;

	static public String getStatusColor(String status) {
		return switch (status) {
			case "online", "booting" -> "§8";
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
		if (Objects.isNull(servers)) {
			player.sendMessage("§c取得できませんでした");
			return;
		}
		ServerData proxy = servers.get(ServerListSMapi.proxyId);

		int size = ((int) Math.ceil((float) proxy.childIds.length / 9)) * 9;
		this.inventory = Bukkit.createInventory(null, size, "§c§l起動するサーバーを選択");

		int index = 0;
		for (String id : proxy.childIds) {
			ServerData server = SMServers.servers.get(id);
			Material material = Objects.requireNonNullElse(Material.matchMaterial(server.icon.minecraftItemId), Material.STONE);
			ItemStack item = new ItemStack(material, Math.max(1, server.players.length));
			ItemMeta meta = item.getItemMeta();

			meta.setDisplayName(ServerOpenListGui.getStatusColor(server.status) + Objects.requireNonNullElse(server.name, "不明"));
			List<String> lore = new LinkedList<>(Arrays.asList(
					"§r§7ID  : " + "§o" + server.id,
					"§r§f状態: " + ServerOpenListGui.getStatusColor(server.status) + ServerOpenListGui.getStatusMessage(server.status),
					"§r§f人数: " + server.players.length + "人",
					server.status.equals("offline") ? "" : "§r§c§l既に起動しています"
			));
			lore.addAll(server.description.stream().map(s -> "§r§f" + s).toList());
			meta.setLore(lore);
			item.setItemMeta(meta);

			this.inventory.addItem(item);
			slot.put(index++, id);
		}
		ItemStack closeItem = new ItemStack(Material.BARRIER, 1);
		ItemMeta meta = closeItem.getItemMeta();

		meta.setDisplayName("§r§f" + "閉じる");
		closeItem.setItemMeta(meta);

		this.inventory.setItem(size - 1, closeItem);

		ServerOpenListGui.slotMap.put(this.inventory, slot);
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
		if (clickedSlot == e.getInventory().getSize() - 1) {
			e.getWhoClicked().getOpenInventory().close();
			return;
		}

		String clickedServerId = ServerOpenListGui.slotMap.get(e.getInventory()).get(clickedSlot);
		if (Objects.isNull(clickedServerId)) {
			return;
		}

		e.getWhoClicked().getOpenInventory().close();

		HashMap<String, ServerData> servers = SMServers.get();
		e.getWhoClicked().sendMessage(servers.get(clickedServerId).name + "を起動中...");

		HttpResponse<String> openResult = SMServers.open(clickedServerId);
		if (openResult.statusCode() != 200) {
			e.getWhoClicked().sendMessage("§c" + servers.get(clickedServerId).name + "を起動をできませんでした ステータスコード:" + openResult.statusCode());
		}
	}

	@EventHandler
	public void onInventoryClose(final InventoryCloseEvent e) {
		if (!e.getInventory().equals(ServerListSMapi.playerInvMap.get(e.getPlayer().getUniqueId()))) {
			return;
		}
		ServerListSMapi.playerInvMap.remove(e.getPlayer().getUniqueId());
	}
}
