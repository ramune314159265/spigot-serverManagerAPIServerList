package ramune314159265.spigotserverlistsmapi;

import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import ramune314159265.spigotserverlistsmapi.commands.ServerListExecutor;
import ramune314159265.spigotserverlistsmapi.guis.ServerListGui;

import java.util.HashMap;
import java.util.UUID;

public final class ServerListSMapi extends JavaPlugin {
	static public String proxyId;
	static public HashMap<UUID, Inventory> playerInvMap;

	public ServerListSMapi() {
		proxyId = "proxy";
		ServerListSMapi.playerInvMap = new HashMap<>();
	}

	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(new ServerListGui(), this);
		this.getCommand("serverlist").setExecutor(new ServerListExecutor());
	}

	@Override
	public void onDisable() {
	}
}
