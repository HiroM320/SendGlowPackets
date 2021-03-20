package io.github.hirom320.sendglowpackets;

import java.util.List;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class EntityMetadataListener extends PacketAdapter {

	@SuppressWarnings("unused")
	private final SendGlowPackets plugin;
	private final PacketManager packetManager;
	

	public EntityMetadataListener(SendGlowPackets plugin, PacketManager packetManager) {
        super(plugin, ListenerPriority.NORMAL, PacketType.Play.Server.ENTITY_METADATA);
		this.plugin = plugin;
		this.packetManager = packetManager;
	}
	
	@Override
    public void onPacketSending(PacketEvent packetEvent) {
        PacketContainer packet = packetEvent.getPacket();
		if(packet.getType() == PacketType.Play.Server.ENTITY_METADATA) {

			final int entityId = packet.getIntegers().read(0);
			if (entityId < 0) {
				final int invertedEntityId = -entityId;
				packet.getIntegers().write(0, invertedEntityId);
				return;
			}

			final List<WrappedWatchableObject> metaData = packet.getWatchableCollectionModifier().read(0);
			if (metaData == null || metaData.isEmpty()) return;

			final Player player = packetEvent.getPlayer();
			final World world = player.getWorld();
			final Entity entity = world
				.getEntities()
				.parallelStream()
				.filter(worldEntity -> worldEntity.getEntityId() == entityId)
				.findAny()
				.orElse(null);
			if (entity == null) return;

			// check if player is a receiver
			if (packetManager.isReceiver(player)) {
				// check if entity is a player
				if (entity instanceof Player) {
					// Update the DataWatcher Item
					final WrappedWatchableObject wrappedEntityObj = metaData.get(0);
					final Object entityObj = wrappedEntityObj.getValue();
					if (!(entityObj instanceof Byte)) return;
					byte entityByte = (byte) entityObj;
					entityByte = (byte) (entityByte | (byte) 0x40);
					wrappedEntityObj.setValue(entityByte);
				}
			}
		}
    }
}
