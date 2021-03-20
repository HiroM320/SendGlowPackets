package io.github.hirom320.sendglowpackets;

import org.bukkit.plugin.java.JavaPlugin;

public final class SendGlowPackets extends JavaPlugin {

	private PacketManager packetManager;

	@Override
	public void onEnable() {
		packetManager = new PacketManager(this);

		getCommand("sgp").setExecutor(new CommandListener(this, packetManager));

	}
}
