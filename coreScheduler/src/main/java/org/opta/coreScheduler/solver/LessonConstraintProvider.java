package org.opta.coreScheduler.solver;

import org.opta.coreScheduler.domain.Lesson;
import org.opta.coreScheduler.domain.StudentGroup;
import org.opta.coreScheduler.domain.Teacher;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.Joiners;

import java.util.HashSet;

public class LessonConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory factory) {
        return new Constraint[] {
                conflictingLessonsForStudentGroups(factory),
                conflictingLessonsForTeachers(factory)
        };
    }

    //TODO: MORE EFFICIENCY??? (NOW ALL LESSON ARE COMPARE TO EACH OTHER...)
    Constraint conflictingLessonsForStudentGroups(ConstraintFactory factory) {
        return factory.forEach(Lesson.class)
                .join(Lesson.class,
                        Joiners.equal(Lesson::getTimeslot),
                        Joiners.lessThan(Lesson::getId)
                )
                .penalize( HardSoftScore.ONE_HARD,
                        (lesson, lesson2) -> {
                            HashSet<StudentGroup> intersection = new HashSet<>(lesson.getCourse().getStudentGroups());
                            intersection.retainAll(lesson2.getCourse().getStudentGroups());
                            return intersection.size();})
                .asConstraint("Student group conflict");
    }

    Constraint conflictingLessonsForTeachers(ConstraintFactory factory) {
        return factory.forEach(Lesson.class)
                .join(Lesson.class,
                        Joiners.equal(Lesson::getTimeslot),
                        Joiners.lessThan(Lesson::getId)
                )
                .penalize( HardSoftScore.ONE_HARD,
                        (lesson, lesson2) -> {
                            HashSet<Teacher> intersection = new HashSet<>(lesson.getCourse().getTeachers());
                            intersection.retainAll(lesson2.getCourse().getTeachers());
                            return intersection.size();})
                .asConstraint("Teacher conflict");
    }
}
