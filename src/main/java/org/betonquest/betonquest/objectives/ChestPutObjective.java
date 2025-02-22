package org.betonquest.betonquest.objectives;

import lombok.CustomLog;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.conditions.ChestItemCondition;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.events.ChestTakeEvent;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.id.NoID;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.betonquest.betonquest.utils.location.CompoundLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.InventoryHolder;

import java.util.List;

/**
 * Requires the player to put items in the chest. Items can optionally NOT
 * disappear once the chest is closed.
 */
@SuppressWarnings("PMD.CommentRequired")
@CustomLog
public class ChestPutObjective extends Objective implements Listener {

    private final Condition chestItemCondition;
    private final QuestEvent chestTakeEvent;
    private final CompoundLocation loc;
    /**
     * Argument to manage the chest access for one or multiple players. False by default which means only one player
     * can acess the chest at the same time.
     */
    private final boolean multipleAccess;

    public ChestPutObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        template = ObjectiveData.class;
        // extract location
        loc = instruction.getLocation();
        final String location = instruction.current();
        final String items = instruction.next();
        multipleAccess = Boolean.parseBoolean(instruction.getOptional("multipleaccess"));
        try {
            chestItemCondition = new ChestItemCondition(new Instruction(instruction.getPackage(), new NoID(instruction.getPackage()), "chestitem " + location + " " + items));
        } catch (InstructionParseException | ObjectNotFoundException e) {
            throw new InstructionParseException("Could not create inner chest item condition: " + e.getMessage(), e);
        }
        if (instruction.hasArgument("items-stay")) {
            chestTakeEvent = null;
        } else {
            try {
                chestTakeEvent = new ChestTakeEvent(new Instruction(instruction.getPackage(), new NoID(instruction.getPackage()), "chesttake " + location + " " + items));
            } catch (final ObjectNotFoundException e) {
                throw new InstructionParseException("Could not create inner chest take event: " + e.getMessage(), e);
            }
        }

    }

    /**
     * Permits multiple players to look into the chest, if set.
     *
     * @param event InventoryOpenEvent
     */
    @EventHandler
    public void onChestOpen(final InventoryOpenEvent event) {
        if (!multipleAccess && !checkForNoOtherPlayer(event)) {
            try {
                Config.sendNotify(null, (Player) event.getPlayer(), "chest_occupied", null);
            } catch (final QuestRuntimeException e) {
                LOG.warn("The notify system was unable to send the message for 'chest_occupied'. Error was: '"
                        + e.getMessage() + "'", e);
            }
            event.setCancelled(true);
        }
    }

    /**
     * Checks if there is no other player that has this inventory open
     *
     * @param event InventoryOpenEvent
     * @return true, if no other player using the inventory, else false
     */
    private boolean checkForNoOtherPlayer(final InventoryOpenEvent event) {
        return event.getInventory().getViewers().equals(List.of(event.getPlayer()));
    }

    @SuppressWarnings("PMD.CyclomaticComplexity")
    @EventHandler(ignoreCancelled = true)
    public void onChestClose(final InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }
        final String playerID = PlayerConverter.getID((Player) event.getPlayer());
        if (!containsPlayer(playerID)) {
            return;
        }
        try {
            final Location targetChestLocation = loc.getLocation(playerID);
            final Block block = targetChestLocation.getBlock();
            if (!(block.getState() instanceof InventoryHolder)) {
                final World world = targetChestLocation.getWorld();
                LOG.warn(instruction.getPackage(),
                        String.format("Error in '%s' chestput objective: Block at location x:%d y:%d z:%d in world '%s' isn't a chest!",
                                instruction.getID().getFullID(),
                                targetChestLocation.getBlockX(),
                                targetChestLocation.getBlockY(),
                                targetChestLocation.getBlockZ(),
                                world == null ? "null" : world.getName()));
                return;
            }
            final InventoryHolder chest = (InventoryHolder) block.getState();
            if (!chest.equals(event.getInventory().getHolder())) {
                return;
            }
            if (chestItemCondition.handle(playerID) && checkConditions(playerID)) {
                completeObjective(playerID);
                if (chestTakeEvent != null) {
                    chestTakeEvent.handle(playerID);
                }
            }
        } catch (final QuestRuntimeException e) {
            LOG.warn(instruction.getPackage(), "Error while handling '" + instruction.getID() + "' objective: " + e.getMessage(), e);
        }
    }

    @Override
    public void start() {
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    @Override
    public void stop() {
        HandlerList.unregisterAll(this);
    }

    @Override
    public String getDefaultDataInstruction() {
        return "";
    }

    @Override
    public String getProperty(final String name, final String playerID) {
        return "";
    }

}
