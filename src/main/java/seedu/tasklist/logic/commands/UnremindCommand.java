package seedu.tasklist.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.tasklist.model.Model.PREDICATE_SHOW_ALL_ASSIGNMENT;

import java.util.List;

import seedu.tasklist.commons.core.Messages;
import seedu.tasklist.commons.core.index.Index;
import seedu.tasklist.logic.commands.exceptions.CommandException;
import seedu.tasklist.model.Model;
import seedu.tasklist.model.assignment.Assignment;
import seedu.tasklist.model.assignment.Done;
import seedu.tasklist.model.assignment.Priority;
import seedu.tasklist.model.assignment.Remind;
import seedu.tasklist.model.assignment.Schedule;
import seedu.tasklist.model.task.ModuleCode;
import seedu.tasklist.model.task.Name;
import seedu.tasklist.model.task.Time;


/**
 * Removes reminders for an assignment identified using it's displayed index from ProductiveNus.
 */
public class UnremindCommand extends NegateCommand {

    public static final String COMMAND_WORD_SUFFIX = "remind";
    public static final String COMMAND_WORD = NegateCommand.COMMAND_WORD + COMMAND_WORD_SUFFIX;

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Removes the reminder from the assignment identified by the index number "
            + "used in the displayed reminders list."
            + " Assignments will no longer have reminders set and will be removed from the displayed reminders list.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_UNREMIND_ASSIGNMENT_SUCCESS = "Removed reminder for Assignment: %1$s";

    /**
     * Constructs an UnremindCommand to remove reminders from the specified assignment.
     * @param targetIndex index of the assignment in the reminded assignments list to remove reminders
     */
    public UnremindCommand(Index targetIndex) {
        super(targetIndex);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Assignment> remindedAssignmentsList = model.getRemindedAssignmentsList();

        if (getTargetIndex().getZeroBased() >= remindedAssignmentsList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_ASSIGNMENT_DISPLAYED_INDEX);
        }

        Assignment assignmentToUnremind = remindedAssignmentsList.get(getTargetIndex().getZeroBased());
        assert(assignmentToUnremind.isReminded());

        Assignment unremindedAssignment = createUnremindedAssignment(assignmentToUnremind);

        model.setAssignment(assignmentToUnremind, unremindedAssignment);
        model.updateFilteredAssignmentList(PREDICATE_SHOW_ALL_ASSIGNMENT);
        return new CommandResult(String.format(MESSAGE_UNREMIND_ASSIGNMENT_SUCCESS, unremindedAssignment));
    }

    /**
     * Creates and returns an {@code Assignment} with the details of {@code assignmentToUnremind}.
     */
    private static Assignment createUnremindedAssignment(Assignment assignmentToUnremind) {
        assert assignmentToUnremind != null;

        Name updatedName = assignmentToUnremind.getName();
        Time updatedDeadline = assignmentToUnremind.getDeadline();
        ModuleCode updatedModuleCode = assignmentToUnremind.getModuleCode();
        Remind updatedRemind = new Remind();
        Schedule updatedSchedule = assignmentToUnremind.getSchedule();
        Priority priority = assignmentToUnremind.getPriority();
        Done updatedDone = assignmentToUnremind.getDone();

        return new Assignment(updatedName, updatedDeadline, updatedModuleCode, updatedRemind, updatedSchedule,
                priority, updatedDone);
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof UnremindCommand // instanceof handles nulls
                && getTargetIndex().equals(((UnremindCommand) other).getTargetIndex())); // state check
    }
}
