package seedu.expense.logic.commands;

import static seedu.expense.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.expense.logic.parser.CliSyntax.PREFIX_REMARK;
import static seedu.expense.model.Model.PREDICATE_SHOW_ALL_EXPENSES;

import java.util.List;

import seedu.expense.commons.core.Messages;
import seedu.expense.commons.core.index.Index;
import seedu.expense.logic.commands.exceptions.CommandException;
import seedu.expense.model.Model;
import seedu.expense.model.expense.Expense;
import seedu.expense.model.expense.Remark;

/**
 * Changes the remark of an existing expense in the expense book.
 */
public class RemarkCommand extends Command {

    public static final String COMMAND_WORD = "remark";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the rem\u200eark of the expense identified "
            + "by the index number used in the last expense listing. "
            + "Existing rem\u200eark will be overwritten by the input.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + PREFIX_REMARK + "[REMARK]\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_REMARK + "Likes to swim.";

    public static final String MESSAGE_ADD_REMARK_SUCCESS = "Added remark to Expense: %1$s ";
    public static final String MESSAGE_DELETE_REMARK_SUCCESS = "Removed remark from Expense: %1$s ";

    private final Index index;
    private final Remark remark;

    /**
     * @param index  of the expense in the filtered expense list to edit the remark
     * @param remark of the expense to be updated to
     */
    public RemarkCommand(Index index, Remark remark) {
        requireAllNonNull(index, remark);

        this.index = index;
        this.remark = remark;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        List<Expense> lastShownList = model.getFilteredExpenseList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_EXPENSE_DISPLAYED_INDEX);
        }

        Expense expenseToEdit = lastShownList.get(index.getZeroBased());
        Expense editedExpense = new Expense(expenseToEdit.getDescription(),
                expenseToEdit.getAmount(), expenseToEdit.getDate(),
                remark, expenseToEdit.getTag());

        model.setExpense(expenseToEdit, editedExpense);
        model.updateFilteredExpenseList(PREDICATE_SHOW_ALL_EXPENSES);

        return new CommandResult(generateSuccessMessage(editedExpense));
    }

    /**
     * Generates a command execution success message based on whether the remark is added to or removed from
     * {@code expenseToEdit}.
     */
    private String generateSuccessMessage(Expense expenseToEdit) {
        String message = !remark.value.isEmpty() ? MESSAGE_ADD_REMARK_SUCCESS : MESSAGE_DELETE_REMARK_SUCCESS;
        return String.format(message, expenseToEdit);
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof RemarkCommand)) {
            return false;
        }

        // state check
        RemarkCommand e = (RemarkCommand) other;
        return index.equals(e.index)
                && remark.equals(e.remark);
    }
}
