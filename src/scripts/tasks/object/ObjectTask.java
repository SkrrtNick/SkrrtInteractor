package scripts.tasks.object;

import org.tribot.script.sdk.Widgets;
import scripts.api.framework.Priority;
import scripts.api.framework.Task;
import scripts.api.framework.TaskSet;
import scripts.data.GUIOptions;
import scripts.data.InteractionTask;
import scripts.tasks.BankingTask;

public class ObjectTask implements Task {
    private InteractionTask interactionTask;

    public ObjectTask(InteractionTask interactionTask) {
        this.interactionTask = interactionTask;
    }

    @Override
    public Priority priority() {
        return Priority.HIGH;
    }

    @Override
    public boolean validate() {
        return !interactionTask.isCompleted();
    }

    @Override
    public void execute() {
        TaskSet objectTasks = new TaskSet(new BankingTask(interactionTask), new WorldHopperTask(interactionTask), new EvadePK(interactionTask), new GrandExchangeTask(interactionTask));
        Task t = interactionTask.getType().equals(GUIOptions.InteractionType.TELEGRABBING) ? new TelegrabbingGroundItem(interactionTask) : new InteractingWithObject(interactionTask);
        objectTasks.add(t);
        Task task = objectTasks.getValidTask();
        if (task != null) {
            task.execute();
            Widgets.closeAll();
        }
    }
}
