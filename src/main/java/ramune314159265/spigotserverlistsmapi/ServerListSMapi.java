package ramune314159265.spigotserverlistsmapi;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import ramune314159265.spigotserverlistsmapi.commands.ServerListExecutor;

public final class ServerListSMapi extends JavaPlugin {
	static public String proxyId;

	public ServerListSMapi() {
		proxyId = "proxy";
	}

	@Override
	public void onEnable() {
		this.getCommand("serverlist").setExecutor(new ServerListExecutor());
	}

	@Override
	public void onDisable() {
	}
}
