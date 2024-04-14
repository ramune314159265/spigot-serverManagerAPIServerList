package ramune314159265.spigotserverlistsmapi;

import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import ramune314159265.spigotserverlistsmapi.commands.ServerListExecutor;
import ramune314159265.spigotserverlistsmapi.commands.ServerOpenListExecutor;
import ramune314159265.spigotserverlistsmapi.guis.ServerListGui;
import ramune314159265.spigotserverlistsmapi.guis.ServerOpenListGui;

import java.util.HashMap;
import java.util.UUID;

public final class ServerListSMapi extends JavaPlugin {
	static public String proxyId;
	static public HashMap<UUID, Inventory> playerInvMap;
	private static ServerListSMapi instance;

	public ServerListSMapi() {
		proxyId = "proxy";
		ServerListSMapi.instance = this;
		ServerListSMapi.playerInvMap = new HashMap<>();
	}

	public static ServerListSMapi getInstance() {
		return instance;
	}

	@Override
	public void onEnable() {
		getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		getServer().getPluginManager().registerEvents(new ServerListGui(), this);
		getServer().getPluginManager().registerEvents(new ServerOpenListGui(), this);
		this.getCommand("serverlist").setExecutor(new ServerListExecutor());
		this.getCommand("serveropenlist").setExecutor(new ServerOpenListExecutor());
	}

	@Override
	public void onDisable() {
	}
}
