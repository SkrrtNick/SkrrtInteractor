package scripts.tasks.chatter;

import lombok.Getter;
import org.tribot.script.sdk.ChatScreen;
import org.tribot.script.sdk.MyPlayer;
import org.tribot.script.sdk.Waiting;
import org.tribot.script.sdk.Widgets;
import org.tribot.script.sdk.query.Query;
import org.tribot.script.sdk.types.WorldTile;
import scripts.api.Logger;
import scripts.api.framework.Priority;
import scripts.api.framework.Task;
import scripts.api.functions.Walking;
import scripts.data.GUIOptions;
import scripts.data.InteractionTask;
import scripts.data.Teleports;

public class InteractingWithNPC implements Task {
    @Getter
    private final InteractionTask interactionTask;
    private Logger logger = new Logger().setHeader("Chatter");


    public InteractingWithNPC(InteractionTask interactionTask) {
        this.interactionTask = interactionTask;
    }

    @Override
    public Priority priority() {
        return Priority.MEDIUM;
    }

    @Override
    public boolean validate() {
        return !interactionTask.isCompleted();
    }

    @Override
    public void execute() {
        Widgets.closeAll();
        if (Walking.walkTo(interactionTask.getWorldTile(),interactionTask::walkState)) {
           var interact = Query.npcs()
                    .nameEquals(interactionTask.getName())
                    .findBestInteractable()
                    .map(i -> i.interact(interactionTask.getAction()) && Waiting.waitUntil(()->!MyPlayer.isMoving()))
                    .map(d -> interactionTask.getDialogue().isBlank() || (Waiting.waitUntil(ChatScreen::isOpen) && ChatScreen.handle(interactionTask.getDialogue().split(":"))))
                   .orElse(false);
           if(interact){
              interactionTask.setCompleted(true);
           }
        }
    }


}
