package org.betonquest.betonquest.quest.event;

import lombok.CustomLog;
import org.betonquest.betonquest.api.config.QuestPackage;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.PlayerConverter;

/**
 * Decorator for events that do not support execution with offline players.
 */
@CustomLog
public class OnlinePlayerRequiredEvent implements Event {

    /**
     * The event to execute.
     */
    private final Event event;

    /**
     * The quest package to use for reporting failed executions.
     */
    private final QuestPackage questPackage;

    /**
     * Wrap the given event to only be executed if the given player is online.
     *
     * @param event        event to execute
     * @param questPackage quest package to use for reporting execution failures
     */
    public OnlinePlayerRequiredEvent(final Event event, final QuestPackage questPackage) {
        this.event = event;
        this.questPackage = questPackage;
    }

    @Override
    public void execute(final String playerId) throws QuestRuntimeException {
        if (PlayerConverter.getPlayer(playerId) == null) {
            LOG.debug(questPackage, "Player " + playerId + " is offline, cannot fire event because it's not persistent.");
        } else {
            event.execute(playerId);
        }
    }
}
