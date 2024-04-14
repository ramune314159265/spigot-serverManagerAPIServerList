package ramune314159265.spigotserverlistsmapi;

import com.moandjiezana.toml.Toml;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import ramune314159265.spigotserverlistsmapi.commands.ServerListExecutor;
import ramune314159265.spigotserverlistsmapi.commands.ServerOpenListExecutor;
import ramune314159265.spigotserverlistsmapi.guis.ServerListGui;
import ramune314159265.spigotserverlistsmapi.guis.ServerOpenListGui;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.UUID;

public final class ServerListSMapi extends JavaPlugin {
	static public String proxyId;
	static public String apiHostname;
	static public HashMap<UUID, Inventory> playerInvMap;
	private static ServerListSMapi instance;

	public ServerListSMapi() {
		loadConf();
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

	public void loadConf() {
		File configFile = new File(getDataFolder(), "conf.toml");
		if (!configFile.getParentFile().exists()) {
			configFile.getParentFile().mkdirs();
		}

		if (!configFile.exists()) {
			try (InputStream input = ServerListSMapi.class.getResourceAsStream("/" + configFile.getName())) {
				if (input != null) {
					Files.copy(input, configFile.toPath());
				} else {
					configFile.createNewFile();
				}
			} catch (IOException e) {
				Bukkit.getLogger().warning(e.toString());
			}
		}

		Toml configToml = new Toml().read(configFile);

		ServerListSMapi.apiHostname = configToml.getString("apiHostname");
		ServerListSMapi.proxyId = configToml.getString("proxyId");
	}
}
