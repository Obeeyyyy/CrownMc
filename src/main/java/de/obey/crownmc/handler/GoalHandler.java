package de.obey.crownmc.handler;

import de.obey.crownmc.CrownMain;
import de.obey.crownmc.backend.enums.DataType;
import de.obey.crownmc.objects.Goal;
import de.obey.crownmc.util.MessageUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

import javax.persistence.GeneratedValue;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class GoalHandler {

    MessageUtil messageUtil;
    UserHandler userHandler;

    @NonFinal
    Goal goal;


    public GoalHandler(MessageUtil messageUtil, UserHandler userHandler) {
        this.messageUtil = messageUtil;
        this.userHandler = userHandler;
    }

    public boolean isGoal() {
        return (goal != null && goal.getCurrentAmount() < goal.getGoal());
    }

    public void startNewGoal(Goal goal) {
        this.goal = goal;
    }

    public void endGoal(Goal goal) {
        goal.endGoal();
        this.goal = null;
    }

    public void shutdown() {
        if (isGoal()) {
            goal.getParticipants().forEach((player, aLong) -> {
                CrownMain.getInstance().getInitializer().getUserHandler().getUserInstant(player.getUniqueId()).addLong(DataType.MONEY, aLong);
            });
        }
    }


}
