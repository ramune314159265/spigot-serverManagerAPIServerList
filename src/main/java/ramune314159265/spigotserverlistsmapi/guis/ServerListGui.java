package ramune314159265.spigotserverlistsmapi.guis;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ramune314159265.spigotserverlistsmapi.ServerData;
import ramune314159265.spigotserverlistsmapi.ServerList;
import ramune314159265.spigotserverlistsmapi.ServerListSMapi;

import java.net.http.WebSocket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public class ServerListGui implements WebSocket.Listener {
	private final Inventory inventory;
	private final Byte size;

	public ServerListGui() {
		this.size = 9;
		this.inventory = Bukkit.createInventory(null, this.size, "&aサーバーを選択");

		this.init();
	}

	public void init() {
		HashMap<String, ServerData> servers = ServerList.get();
		servers.forEach((k, v) -> Bukkit.getLogger().info(k + v.name));
		ServerData proxy = servers.get(ServerListSMapi.proxyId);
		Bukkit.getLogger().info(ServerList.servers.toString());
		for (String id : proxy.childIds) {
			ServerData server = ServerList.servers.get(id);
			Material material = Objects.requireNonNullElse(Material.matchMaterial(server.minecraftItemIcon), Material.STONE);
			ItemStack item = new ItemStack(material, Math.max(1, server.players.length));
			ItemMeta meta = item.getItemMeta();

			meta.setDisplayName("&a" + Objects.requireNonNullElse(server.name, "不明"));
			meta.setLore(Arrays.asList("ID: " + server.id, server.description));
			item.setItemMeta(meta);

			this.inventory.addItem(item);
		}
		ItemStack closeItem = new ItemStack(Material.BARRIER, 1);
		ItemMeta meta = closeItem.getItemMeta();

		meta.setDisplayName("閉じる");
		closeItem.setItemMeta(meta);

		this.inventory.setItem(this.size - 1, closeItem);
	}

	public void open(Player player) {
		player.openInventory(this.inventory);
	}
}
