package io.github.hirom320.sendglowpackets;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandListener implements CommandExecutor {
	private final SendGlowPackets plugin;
	private final PacketManager packetManager;

	private final String prefix;
	
	private enum Permissions {
		RECEIVE("sendglowpackets.receive"),
		MANAGE("sendglowpackets.manage");
		private final String text;
		private Permissions(final String text) {
			this.text = text;
		}
		public String getString() {
			return this.text;
		}
	}

	public CommandListener(SendGlowPackets sendGlowPackets, PacketManager packetManager) {
		this.plugin = sendGlowPackets;
		this.packetManager = packetManager;
		this.prefix = "[SGP]";
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if(args == null || args.length == 0 || args[0].equalsIgnoreCase("help")) {
			if(sender.hasPermission(Permissions.RECEIVE.getString())) {
				send(sender, "/"+label+" [on/off]  -- 発光の受信をON/OFF");
				return true;
			}
			if(sender.hasPermission(Permissions.MANAGE.getString())) {
				send(sender, "/"+label+" [on/off] <player>  -- プレイヤーの画面上で発光の受信をON/OFF");
				send(sender, "/"+label+" list -- 受信プレイヤーリスト");
				return true;
			}
		}

		else if(args[0].equalsIgnoreCase("on")) {
			if(args.length >= 2) {
				if(sender.hasPermission(Permissions.MANAGE.getString())) {
					Player player = plugin.getServer().getPlayer(args[2]);
					if(player == null) {
						send(sender, "プレイヤーはいません");
						return true;
					} else {
						packetManager.addReceiver(player);
						send(sender, "プレイヤーは発光を受信します");
						return true;
					}
				}
			}
			if(sender.hasPermission(Permissions.RECEIVE.getString())) {
				if(sender instanceof Player) {
					packetManager.addReceiver((Player) sender);
					send(sender, "発光を受信します");
					return true;
				}
			}
		}

		else if(args[0].equalsIgnoreCase("off")) {
			if(args.length >= 2) {
				if(sender.hasPermission(Permissions.MANAGE.getString())) {
					Player player = plugin.getServer().getPlayer(args[2]);
					if(player == null) {
						send(sender, "プレイヤーはいません");
						return true;
					} else {
						packetManager.removeReceiver(player);
						send(sender, "プレイヤーは発光を受信しません");
						return true;
					}
				}
			}
			if(sender.hasPermission(Permissions.RECEIVE.getString())) {
				if(sender instanceof Player) {
					packetManager.removeReceiver((Player) sender);
					send(sender, "発光を受信しません");
					return true;
				}
			}
		}

		else if(args[0].equalsIgnoreCase("list")) {
			if(sender.hasPermission(Permissions.MANAGE.getString())) {
				send(sender, "受信プレイヤーリスト:");
				for(Player player : packetManager.getReceivers()) {
					send(sender, player.getName());
				}
				return true;
			}
		}

		return false;
	}

	private void send(CommandSender sender, String message) {
		sender.sendMessage(this.prefix + " " + message);
	}

}
