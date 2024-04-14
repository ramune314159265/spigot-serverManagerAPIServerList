package ramune314159265.spigotserverlistsmapi.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ramune314159265.spigotserverlistsmapi.guis.ServerListGui;

public class ServerListExecutor implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("This command can only be executed by the player");
			return false;
		}
		ServerListGui gui = new ServerListGui();
		gui.open((Player) sender);
		return true;
	}
}
