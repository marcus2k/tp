package seedu.expense.logic.parser;

import static seedu.expense.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.expense.commons.core.Messages.MESSAGE_UNKNOWN_COMMAND;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.expense.commons.core.LogsCenter;
import seedu.expense.logic.LogicManager;
import seedu.expense.logic.commands.AddCategoryCommand;
import seedu.expense.logic.commands.AddCommand;
import seedu.expense.logic.commands.AliasCommand;
import seedu.expense.logic.commands.ClearCommand;
import seedu.expense.logic.commands.Command;
import seedu.expense.logic.commands.DeleteCategoryCommand;
import seedu.expense.logic.commands.DeleteCommand;
import seedu.expense.logic.commands.EditCommand;
import seedu.expense.logic.commands.ExitCommand;
import seedu.expense.logic.commands.FindCommand;
import seedu.expense.logic.commands.HelpCommand;
import seedu.expense.logic.commands.ListCommand;
import seedu.expense.logic.commands.RemarkCommand;
import seedu.expense.logic.commands.ResetAliasCommand;
import seedu.expense.logic.commands.SortCommand;
import seedu.expense.logic.commands.SwitchCommand;
import seedu.expense.logic.commands.TopupCommand;
import seedu.expense.logic.parser.exceptions.ParseException;
import seedu.expense.model.alias.AliasMap;

/**
 * Parses user input.
 */
public class ExpenseBookParser {

    /**
     * Used for initial separation of command word and args.
     */
    private static final Pattern BASIC_COMMAND_FORMAT = Pattern.compile("(?<commandWord>\\S+)(?<arguments>.*)");

    /**
     * Parses user input into command for execution.
     *
     * @param userInput full user input string
     * @return the command based on the user input
     * @throws ParseException if the user input does not conform the expected format
     */
    public Command parseCommand(String userInput) throws ParseException {
        return parseCommand(userInput, null);
    }

    /**
     * Parses user input into command for execution.
     * Accepts an aliasMap as a dictionary to translate aliases into COMMAND_WORD.
     *
     * @param userInput full user input string
     * @param aliasMap alias dictionary
     * @return the command based on the user input
     * @throws ParseException if the user input does not conform the expected format
     */
    public Command parseCommand(String userInput, AliasMap aliasMap) throws ParseException {
        final Matcher matcher = BASIC_COMMAND_FORMAT.matcher(userInput.trim());
        if (!matcher.matches()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE));
        }

        String commandWord = matcher.group("commandWord");
        String maybeAlias = commandWord;
        if (aliasMap != null && aliasMap.hasAlias(commandWord)) {
            commandWord = aliasMap.getValue(commandWord);
        }
        final String arguments = matcher.group("arguments");
        LogsCenter.getLogger(LogicManager.class).info(
                "----------------[USER COMMAND][" + commandWord + " " + arguments + "]");
        try {
            switch (commandWord) {

            case AddCommand.COMMAND_WORD:
                return new AddCommandParser().parse(arguments);

            case EditCommand.COMMAND_WORD:
                return new EditCommandParser().parse(arguments);

            case DeleteCommand.COMMAND_WORD:
                return new DeleteCommandParser().parse(arguments);

            case ClearCommand.COMMAND_WORD:
                return new ClearCommand();

            case FindCommand.COMMAND_WORD:
                return new FindCommandParser().parse(arguments);

            case ListCommand.COMMAND_WORD:
                return new ListCommand();

            case ExitCommand.COMMAND_WORD:
                return new ExitCommand();

            case HelpCommand.COMMAND_WORD:
                return new HelpCommand();

            case RemarkCommand.COMMAND_WORD:
                return new RemarkCommandParser().parse(arguments);

            case TopupCommand.COMMAND_WORD:
                return new TopupCommandParser().parse(arguments);

            case AddCategoryCommand.COMMAND_WORD:
                return new AddCategoryCommandParser().parse(arguments);

            case DeleteCategoryCommand.COMMAND_WORD:
                return new DeleteCategoryCommandParser().parse(arguments);

            case SwitchCommand.COMMAND_WORD:
                return new SwitchCommandParser().parse(arguments);

            case AliasCommand.COMMAND_WORD:
                return new AliasCommandParser().parse(arguments);

            case SortCommand.COMMAND_WORD:
                return new SortCommandParser().parse(arguments);

            case ResetAliasCommand.COMMAND_WORD:
                return new ResetAliasCommandParser().parse(arguments);

            default:
                throw new ParseException(MESSAGE_UNKNOWN_COMMAND);
            }
        } catch (ParseException e) {
            String updatedMessage = e.getMessage().replace(commandWord, maybeAlias);
            throw new ParseException(updatedMessage);
        }
    }
}
