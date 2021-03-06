/*
 * ELW : e-learning workspace
 * Copyright (C) 2010  Anton Kraievoy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package elw.web;

import base.pattern.Result;
import elw.dao.Queries;
import elw.dao.ctx.*;
import elw.dp.mips.MipsValidator;
import elw.dp.mips.TaskBean;
import elw.vo.*;
import org.akraievoy.base.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

public class StudentCodeValidator extends Executors.Task {
    private static final Logger log = LoggerFactory.getLogger(StudentCodeValidator.class);

    //  LATER this should be configurable/injectable on per-course/taskType basis
    public static final String COURSE_ID_AOS = "aos";
    public static final String TASKTYPE_ID_LR = "lr";
    public static final String SLOT_ID_SOLUTIONS = "code";
    public static final String SLOT_ID_STATEMENT = "statement";
    public static final String SLOT_ID_TEST = "test";
    private static final String[] NO_PENDING_ENROLLMENTS = new String[0];

    private int periodMillis = 300000;
    private final Queries queries;
    private final MipsValidator validator;

    public StudentCodeValidator(
            ScheduledExecutorService executor,
            Queries queries) {
        super(executor);
        this.validator = new MipsValidator();
        this.queries = queries;
    }

    private final List<String> pendingEnrollmentIds = new ArrayList<String>();

    public void setPeriodMillis(int periodMillis) {
        this.periodMillis = periodMillis;
    }

    protected long getRestartDelay() {
        return periodMillis;
    }

    protected long getInitialDelay() {
        return periodMillis;
    }

    public StudentCodeValidator workPending(String enrollmentId) {
        synchronized (pendingEnrollmentIds) {
            if (!pendingEnrollmentIds.contains(enrollmentId)) {
                pendingEnrollmentIds.add(enrollmentId);
            }
        }
        return this;
    }

    protected void runInternal() throws Throwable {
        final String[] pendingEnrollmentsIdArr;
        synchronized (pendingEnrollmentIds) {
            if (pendingEnrollmentIds.isEmpty()) {
                pendingEnrollmentsIdArr = NO_PENDING_ENROLLMENTS;
            } else {
                pendingEnrollmentsIdArr = pendingEnrollmentIds.toArray(new String[pendingEnrollmentIds.size()]);
                pendingEnrollmentIds.clear();
                Arrays.sort(pendingEnrollmentsIdArr);
            }
        }

        if (pendingEnrollmentsIdArr.length == 0) {
            return;
        }

        final List<Enrollment> enrs = queries.enrollments();
        for (Enrollment enr : enrs) {
            final CtxEnrollment ctxEnr;
            {
                final Course course = queries.course(enr.getCourseId());
                final Group group = queries.group(enr.getGroupId());
                ctxEnr = new CtxEnrollment(enr, course, group);
            }


            if (
                !ctxEnr.course.getId().contains(COURSE_ID_AOS) ||
                Arrays.binarySearch(pendingEnrollmentsIdArr, ctxEnr.enr.getId()) < 0
            ) {
                continue;
            }

            processEnrollment(ctxEnr);
        }
    }

    protected void processEnrollment(CtxEnrollment ctxEnr) {
        for (CtxStudent ctxStud : ctxEnr.students) {
            for (CtxTask ctxVer : ctxStud.tasks) {
                if (!TASKTYPE_ID_LR.equals(ctxVer.tType.getId())) {
                    continue;
                }

                final CtxSlot ctxSlotCode =
                        ctxVer.slot(SLOT_ID_SOLUTIONS);
                final CtxSlot ctxSlotStatement =
                        ctxVer.slot(SLOT_ID_STATEMENT);
                final CtxSlot ctxSlotTest =
                        ctxVer.slot(SLOT_ID_TEST);

                final List<Solution> files =
                        queries.solutions(ctxSlotCode);

                for (Solution f : files) {
                    if (f.getValidatorStamp() > 0 && f.getScore() != null) {
                        continue;
                    }

                    final CtxSolution ctxSolution =
                            ctxSlotCode.solution(f);

                    processSolution(
                            ctxSlotStatement,
                            ctxSlotTest,
                            ctxSolution
                    );
                }
            }
        }
    }

    protected void processSolution(
            CtxSlot ctxSlotStatement,
            CtxSlot ctxSlotTest,
            CtxSolution ctxSolution
    ) {
        Score score = null;
        try {
            final Result[] resRef = {new Result("unknown", false)};
            final int[] passFailCounts = new int[2];
            final List<Attachment> allStatements =
                    queries.attachments(ctxSlotStatement);
            final List<Attachment> allTests =
                    queries.attachments(ctxSlotTest);
            final List<String> allTestsStr =
                    new ArrayList<String>();
            for (Attachment allTest : allTests) {
                allTestsStr.add(
                        queries.fileText(
                                allTest,
                                FileBase.CONTENT
                        )
                );
            }
            final TaskBean taskBean = new TaskBean(
                    queries.fileText(
                        allStatements.get(
                                allStatements.size() - 1
                        ),
                        FileBase.CONTENT
                    ),
                    allTestsStr,
                    ""
            );
            validator.batch(
                resRef,
                taskBean,
                queries.fileLines(ctxSolution.solution, FileBase.CONTENT),
                passFailCounts
            );
            ctxSolution.solution.setTestsFailed(passFailCounts[1]);
            ctxSolution.solution.setTestsPassed(passFailCounts[0]);

            score = ctxSolution.preliminary();
            if (passFailCounts[0] > 0) {
                score.setApproved(passFailCounts[1] == 0);
            }
        } catch (Throwable t) {
            log.warn(
                "failed to validate {} / {} / {}: {}",
                new Object[]{
                        ctxSolution,
                        ctxSolution.solution.getId(),
                        ctxSolution.solution.getStamp(),
                        String.valueOf(t)
                }
            );
            log.debug("exception trace", t);
        }

        if (score != null) {
            try {
                final long scoreStamp =
                        queries.createScore(ctxSolution, score);

                ctxSolution.solution.setValidatorStamp(scoreStamp);
                queries.updateSolution(ctxSolution.solution);
            } catch (Throwable t) {
                log.warn(
                    "failed to store update {} / {} / {}: {}",
                    new Object[]{
                            ctxSolution,
                            ctxSolution.solution.getId(),
                            ctxSolution.solution.getStamp(),
                            String.valueOf(t)
                    }
                );
                log.debug("exception trace", t);
            }
        }
    }
}
