package seedu.expense.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.expense.logic.parser.CliSyntax.PREFIX_AMOUNT;
import static seedu.expense.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.expense.logic.parser.CliSyntax.PREFIX_DESCRIPTION;
import static seedu.expense.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.expense.model.Model.PREDICATE_SHOW_ALL_EXPENSES;

import java.util.List;
import java.util.Optional;

import seedu.expense.commons.core.Messages;
import seedu.expense.commons.core.index.Index;
import seedu.expense.commons.util.CollectionUtil;
import seedu.expense.logic.commands.exceptions.CommandException;
import seedu.expense.model.Model;
import seedu.expense.model.expense.Amount;
import seedu.expense.model.expense.Date;
import seedu.expense.model.expense.Description;
import seedu.expense.model.expense.Expense;
import seedu.expense.model.expense.Remark;
import seedu.expense.model.tag.Tag;

/**
 * Edits the details of an existing expense in the expense book.
 */
public class EditCommand extends Command {

    public static final String COMMAND_WORD = "edit";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the details of the expense identified "
            + "by the index number used in the displayed expense list. "
            + "Existing values will be overwritten by the input values.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "[" + PREFIX_DESCRIPTION + "DESCRIPTION] "
            + "[" + PREFIX_AMOUNT + "AMOUNT] "
            + "[" + PREFIX_DATE + "DATE] "
            + "[" + PREFIX_TAG + "TAG]...\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_AMOUNT + "10 "
            + PREFIX_DATE + "01-07-2020";

    public static final String MESSAGE_EDIT_EXPENSE_SUCCESS = "Edited expense: %1$s ";
    public static final String MESSAGE_NOT_EDITED = "At least one field to ed\u200eit must be provided. ";
    public static final String MESSAGE_DUPLICATE_EXPENSE = "This expense already exists in the expense book. ";
    public static final String MESSAGE_INVALID_CATEGORY = "The \"%s\" category does not exist in the expense book. "
            + "Please add it using the \"AddCat\" command first.";

    private final Index index;
    private final EditExpenseDescriptor editExpenseDescriptor;

    /**
     * @param index                of the expense in the filtered expense list to edit
     * @param editExpenseDescriptor details to edit the expense with
     */
    public EditCommand(Index index, EditExpenseDescriptor editExpenseDescriptor) {
        requireNonNull(index);
        requireNonNull(editExpenseDescriptor);

        this.index = index;
        this.editExpenseDescriptor = new EditExpenseDescriptor(editExpenseDescriptor);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Expense> lastShownList = model.getFilteredExpenseList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_EXPENSE_DISPLAYED_INDEX);
        }

        if (editExpenseDescriptor.getTag().isPresent() && !model.hasCategory(editExpenseDescriptor.getTag().get())) {
            throw new CommandException(String.format(MESSAGE_INVALID_CATEGORY, editExpenseDescriptor.getTag().get()));
        }

        Expense expenseToEdit = lastShownList.get(index.getZeroBased());
        Expense editedExpense = createEditedExpense(expenseToEdit, editExpenseDescriptor);

        if (!expenseToEdit.isSameExpense(editedExpense) && model.hasExpense(editedExpense)) {
            throw new CommandException(MESSAGE_DUPLICATE_EXPENSE);
        }

        model.setExpense(expenseToEdit, editedExpense);
        model.updateFilteredExpenseList(PREDICATE_SHOW_ALL_EXPENSES);
        return new CommandResult(String.format(MESSAGE_EDIT_EXPENSE_SUCCESS, editedExpense));
    }

    /**
     * Creates and returns a {@code Expense} with the details of {@code expenseToEdit}
     * edited with {@code editExpenseDescriptor}.
     */
    private static Expense createEditedExpense(Expense expenseToEdit, EditExpenseDescriptor editExpenseDescriptor) {
        assert expenseToEdit != null;

        Description updatedDescription = editExpenseDescriptor.getDescription().orElse(expenseToEdit.getDescription());
        Amount updatedAmount = editExpenseDescriptor.getAmount().orElse(expenseToEdit.getAmount());
        Date updatedDate = editExpenseDescriptor.getDate().orElse(expenseToEdit.getDate());
        Remark updatedRemark = expenseToEdit.getRemark(); // edit command does not allow editing remarks
        Tag updatedTag = editExpenseDescriptor.getTag().orElse(expenseToEdit.getTag());

        return new Expense(updatedDescription, updatedAmount, updatedDate, updatedRemark, updatedTag);
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EditCommand)) {
            return false;
        }

        // state check
        EditCommand e = (EditCommand) other;
        return index.equals(e.index)
                && editExpenseDescriptor.equals(e.editExpenseDescriptor);
    }

    /**
     * Stores the details to edit the expense with. Each non-empty field value will replace the
     * corresponding field value of the expense.
     * Important Note: Not able to change {@code Remark}.
     */
    public static class EditExpenseDescriptor {
        private Description description;
        private Amount amount;
        private Date date;
        private Tag tag;

        public EditExpenseDescriptor() {
        }

        /**
         * Copy constructor.
         * A defensive copy of {@code tags} is used internally.
         */
        public EditExpenseDescriptor(EditExpenseDescriptor toCopy) {
            setDescription(toCopy.description);
            setAmount(toCopy.amount);
            setDate(toCopy.date);
            setTag(toCopy.tag);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(description, amount, date, tag);
        }

        public void setDescription(Description description) {
            this.description = description;
        }

        public Optional<Description> getDescription() {
            return Optional.ofNullable(description);
        }

        public void setAmount(Amount amount) {
            this.amount = amount;
        }

        public Optional<Amount> getAmount() {
            return Optional.ofNullable(amount);
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public Optional<Date> getDate() {
            return Optional.ofNullable(date);
        }

        public void setTag(Tag tag) {
            this.tag = tag;
        }

        /**
         * Returns an unmodifiable tag set, which throws {@code UnsupportedOperationException}
         * if modification is attempted.
         * Returns {@code Optional#empty()} if {@code tag} is null.
         */
        public Optional<Tag> getTag() {
            return Optional.ofNullable(tag);
        }

        @Override
        public boolean equals(Object other) {
            // short circuit if same object
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditExpenseDescriptor)) {
                return false;
            }

            // state check
            EditExpenseDescriptor e = (EditExpenseDescriptor) other;

            return getDescription().equals(e.getDescription())
                    && getAmount().equals(e.getAmount())
                    && getDate().equals(e.getDate())
                    && getTag().equals(e.getTag());
        }
    }
}
