package elw.dao.ctx;

import elw.vo.State;
import elw.vo.*;
import elw.vo.Class;

import java.util.List;

/**
 * Parameter Object, storing the full Solution context.
 */
public class CtxSlot extends CtxTask {
    public final FileSlot slot;

    public CtxSlot(
            Enrollment enr,
            Group group,
            Student student,
            Course course,
            int idx,
            Task task,
            TaskType tType,
            Version ver,
            FileSlot slot
    ) {
        super(
                enr, course, group,
                student, idx, task, tType, ver
        );

        this.slot = slot;
    }

    public CtxSolution solution(final Solution solution) {
        return new CtxSolution(
                enr, group, student, course,
                idx, task, tType, ver,
                slot, solution
        );
    }

    public State state(List<Solution> solutions) {
        if (!open()) {
            return State.CLOSED;
        }

        if (solutions.isEmpty()) {
            return State.OPEN;
        }

        if (someInState(solutions, State.APPROVED)) {
            return State.APPROVED;
        }

        if (allInState(solutions, State.DECLINED)) {
            return State.DECLINED;
        }

        return State.PENDING;
    }

    public Class dueClass() {
        final int classDueIndex =
                idxEntry.getClassDue().get(slot.getId());

        final Class classDue =
                enr.getClasses().get(classDueIndex);

        return classDue;
    }

    public long dueMillis() {
        return dueClass().getToDateTime().getMillis();
    }

    public boolean due() {
        long now = System.currentTimeMillis();
        return dueMillis() <= now;
    }

    public static boolean someInState(
            final List<Solution> solutions,
            final State state
    ) {
        for (Solution solution : solutions) {
            if (solution.getScore().state() == state) {
                return true;
            }
        }

        return false;
    }

    public static boolean allInState(
            final List<Solution> solutions,
            final State state
    ) {
        if (solutions.isEmpty()) {
            return false;
        }

        for (Solution solution : solutions) {
            if (solution.getScore().state() != state) {
                return false;
            }
        }

        return true;
    }

    public CtxSolution bestApproved(List<Solution> solutions) {
        CtxSolution best = null;

        for (Solution solution : solutions) {
            if (solution.getScore().state() == State.APPROVED) {
                final CtxSolution curr = solution(solution);
                if (best == null) {
                    best = curr;
                    continue;
                }

                double bestPoints = best.lastScore().pointsForSolution();
                double currPoints = curr.lastScore().pointsForSolution();

                if (bestPoints < currPoints) {
                    best = curr;
                }
            }
        }
        
        return best;
    }

    public CtxSolution lastPending(List<Solution> solutions) {
        CtxSolution last = null;

        for (Solution solution : solutions) {
            if (solution.getScore().state() == State.PENDING) {
                final CtxSolution curr = solution(solution);
                if (last == null) {
                    last = curr;
                    continue;
                }

                final Long lastStamp = last.solution.getStamp();
                final Long currStamp = curr.solution.getStamp();

                if (lastStamp < currStamp) {
                    last = curr;
                }
            }
        }

        return last;
    }
    
    public double pointsForSlot() {
        return idxEntry.getScoreBudget() * slot.getScoreWeight();
    }
}