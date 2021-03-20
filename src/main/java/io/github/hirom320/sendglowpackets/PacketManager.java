package io.github.hirom320.sendglowpackets;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.*;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class PacketManager {

	private final SendGlowPackets plugin;
	private final ProtocolManager protocolManager;

	public PacketManager(SendGlowPackets plugin) {
		this.plugin = plugin;
		this.protocolManager = ProtocolLibrary.getProtocolManager();
		new SendPacketTask(this).runTaskTimer(plugin, 0, 2);
	}

	private Set<Player> receivers = new HashSet<>();

	public void addReceiver(Player player) {
		receivers.add(player);
	}

	public void removeReceiver(Player player) {
		receivers.remove(player);
	}

	public boolean isReceiver(Player player) {
		return receivers.contains(player);
	}

	public Set<Player> getReceivers() {
		return receivers;
	}

	public void setReceivers(Set<Player> newReceivers) {
		receivers = newReceivers;
	}

	public void glowPlayersForReceivers() {
		for(Player receiver : getReceivers()) {
			glowPlayersFor(receiver);
		}
	}

	public void glowPlayerForReceivers(Player player) {
		for(Player receiver : getReceivers()) {
			PacketContainer metadata = glowMetadataPacket(player);
			try {
				protocolManager.sendServerPacket(receiver, metadata);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	private void glowPlayersFor(Player receiver) {
		for(Player player : plugin.getServer().getOnlinePlayers()) {
			glowPlayerForReceivers(player);
			PacketContainer metadata = glowMetadataPacket(player);
			try {
				protocolManager.sendServerPacket(receiver, metadata);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}
	
	// 参考になった: https://www.spigotmc.org/threads/protocollib-sending-entity_effect-packets.419030/
	// 参考になった: https://www.spigotmc.org/threads/simulating-potion-effect-glowing-with-protocollib.218828/
	private PacketContainer glowMetadataPacket(Player player) {
		PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
		packet.getModifier().writeDefaults();
		packet.getIntegers().write(0, player.getEntityId());
		WrappedDataWatcher watcher = new WrappedDataWatcher(); //Create data watcher, the Entity Metadata packet requires this
		Serializer serializer = Registry.get(Byte.class); //Found this through google, needed for some stupid reason
		watcher.setEntity(player); //Set the new data watcher's target
		watcher.setObject(0, serializer, (byte) (0x40)); //Set status to glowing, found on protocol page
		packet.getWatchableCollectionModifier().write(0, watcher.getWatchableObjects()); //Make the packet's datawatcher the one we created
		return packet;
	}

	private void unglowPlayersFor(Player receiver) {
		for(Player player : plugin.getServer().getOnlinePlayers()) {
			PacketContainer effect = unglowEffectPacket(player);
			try {
				protocolManager.sendServerPacket(receiver, effect);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	// https://wiki.vg/Protocol#Entity_Effect
	private PacketContainer glowEffectPacket(Player player) {
		PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_EFFECT);
		packet.getModifier().writeDefaults();
		packet.getIntegers()
			.write(0, player.getEntityId()) //Set packet's entity id
			.write(1, 6010 * 20);
		packet.getBytes()
			.write(0, (byte) 24)
			.write(1, (byte) 0)
			.write(2, (byte) (0x05));
		
		return packet;
	}

	// https://wiki.vg/Protocol#Remove_Entity_Effect
	private PacketContainer unglowEffectPacket(Player player) {
		PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.REMOVE_ENTITY_EFFECT);
		packet.getModifier().writeDefaults();
		packet.getIntegers().write(0, player.getEntityId());
		packet.getEffectTypes().write(0, PotionEffectType.GLOWING);
		return packet;
	}

}
