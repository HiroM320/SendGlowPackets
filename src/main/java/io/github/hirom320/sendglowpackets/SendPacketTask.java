package io.github.hirom320.sendglowpackets;

import org.bukkit.scheduler.BukkitRunnable;

public class SendPacketTask extends BukkitRunnable {
	
	private final PacketManager packetManager;

	public SendPacketTask(PacketManager packetManager) {
		this.packetManager = packetManager;
	}

	@Override
	public void run() {
		packetManager.glowPlayersForReceivers();
	}
}
