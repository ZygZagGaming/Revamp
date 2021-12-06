package com.zygzag.revamp.common.entity.goal.boss;

import com.zygzag.revamp.util.GeneralUtil;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.WrappedGoal;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Map;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RevampGoalSelector extends GoalSelector {
    public RevampGoalSelector(Supplier<ProfilerFiller> supplier) {
        super(supplier);
    }

    @Override
    public void tick() {
        ProfilerFiller profilerfiller = this.profiler.get();
        profilerfiller.push("goalCleanup");

        for(WrappedGoal wrappedgoal : this.availableGoals) {
            if (wrappedgoal.isRunning() && (goalContainsAnyFlags(wrappedgoal, this.disabledFlags) || !wrappedgoal.canContinueToUse())) {
                wrappedgoal.stop();
            }
        }

        this.lockedFlags.entrySet().removeIf(entry -> !entry.getValue().isRunning());

        profilerfiller.pop();
        profilerfiller.push("goalUpdate");

        ArrayList<WrappedGoal> possibleGoals = new ArrayList<>();
        for (WrappedGoal goal : this.availableGoals) { // loop thru goals
            if (!goal.isRunning() && !goalContainsAnyFlags(goal, disabledFlags) && goalCanBeReplacedForAllFlags(goal, lockedFlags) && goal.canUse()) { // if it's not running and it doesn't have any disabled flags, and it can be replaced and it can be used
                possibleGoals.add(goal);
            }
        }

        WrappedGoal wrappedGoal = GeneralUtil.randomFromList(possibleGoals);
        if (wrappedGoal != null) startGoal(wrappedGoal);

        profilerfiller.pop();
        this.tickRunningGoals(true);
    }

    @Override
    public void addGoal(int priority, Goal goal) {
        super.addGoal(priority, goal);
    }

    public void startGoal(WrappedGoal goal) {
        for (Goal.Flag goal$flag : goal.getFlags()) { // iterate thru all flags in goal
            WrappedGoal wrappedgoal1 = this.lockedFlags.getOrDefault(goal$flag, NO_GOAL); // Gets the current goal that is using the flag goal$flag
            wrappedgoal1.stop(); // Stop said goal
            this.lockedFlags.put(goal$flag, goal); // Replace it with goal
        }

        goal.start(); // start goal
    }

    /**
     * Returns true if all goals in <code>map</code> that share a flag with <code>goal</code> can be replaced by <code>goal</code>.
     * @param goal The goal to test
     * @param map The map to look through for conflicts
     * @return If all goals in @param map that share a flag with @param goal can be replaced by @param goal.
     */
    public static boolean goalCanBeReplacedForAllFlags(WrappedGoal goal, Map<Goal.Flag, WrappedGoal> map) {
        for(Goal.Flag goal$flag : goal.getFlags()) {
            if (!map.getOrDefault(goal$flag, NO_GOAL).canBeReplacedBy(goal)) {
                return false;
            }
        }

        return true;
    }
}
