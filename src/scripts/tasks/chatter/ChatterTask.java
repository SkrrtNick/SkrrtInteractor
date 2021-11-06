package scripts.tasks.chatter;

import scripts.api.framework.Priority;
import scripts.api.framework.Task;
import scripts.api.framework.TaskSet;
import scripts.data.InteractionTask;
import scripts.data.Teleports;
import scripts.tasks.BankingTask;

public class ChatterTask implements Task {
    private InteractionTask interactionTask;

    public ChatterTask(InteractionTask interactionTask) {
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
        TaskSet chatterTasks = new TaskSet(new BankingTask(interactionTask), new InteractingWithNPC(interactionTask));
        BankingTask.setShouldBank((interactionTask.needsTeleport() && interactionTask.getWorldTile().distance() > 50));
        Task task = chatterTasks.getValidTask();
        if (task != null) {
            task.execute();
        }
    }
}
