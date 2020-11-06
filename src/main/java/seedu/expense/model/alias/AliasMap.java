package seedu.expense.model.alias;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.IntPredicate;

import seedu.expense.logic.commands.AddCategoryCommand;
import seedu.expense.logic.commands.AddCommand;
import seedu.expense.logic.commands.AliasCommand;
import seedu.expense.logic.commands.ClearCommand;
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

/**
 * Wraps all data at the expense-book level
 * Duplicates are not allowed (by .isSameExpense comparison)
 */
public class AliasMap {

    public static final String MESSAGE_RESERVED = "The [%s] keyword is reserved.";
    public static final String DUPLICATE_KEYWORD_FOUND = "The [%s] keyword already exists.";
    public static final String UNCHANGED_ALIAS = "Previous and updated alias must not be the same.";
    public static final String ALIAS_NOT_FOUND = "The [%s] alias cannot be found.";
    public static final String ALIAS_ALPHABETS_ONLY = "Only case-sensitive alphabets can be used as aliases.";
    public static final String ALIAS_COMMAND_UNALIASABLE = "`alias` and `resetAlias` commands cannot have aliases.";
    public static final String MESSAGE_OVERRIDE_ALIAS = "Override existing alias instead: %s";
    public static final IntPredicate IS_ALPHABET_ASCII = x -> (x > 96 && x < 123 || x > 64 && x < 91);
    public static final Set<String> RESERVED_KEYWORDS = Set.of(
            AddCommand.COMMAND_WORD, DeleteCommand.COMMAND_WORD, ClearCommand.COMMAND_WORD,
            EditCommand.COMMAND_WORD, ExitCommand.COMMAND_WORD, FindCommand.COMMAND_WORD,
            HelpCommand.COMMAND_WORD, ListCommand.COMMAND_WORD, RemarkCommand.COMMAND_WORD,
            TopupCommand.COMMAND_WORD, AliasCommand.COMMAND_WORD, AddCategoryCommand.COMMAND_WORD,
            DeleteCategoryCommand.COMMAND_WORD, SwitchCommand.COMMAND_WORD, SortCommand.COMMAND_WORD,
            ResetAliasCommand.COMMAND_WORD
    );

    // Maps String alias to String default_command
    private final HashMap<String, String> aliasMap;

    /**
     * Constructs a new {@code AliasMap}.
     */
    public AliasMap() {
        aliasMap = new HashMap<>();
    }

    /**
     * Creates an AliasMap using the AliasMap in the {@code toBeCopied}
     */
    public AliasMap(AliasMap toBeCopied) {
        this();
        resetData(toBeCopied);
    }

    //// list overwrite operations

    /**
     * Replaces the contents of the alias list with {@code aliases}.
     * {@code aliases} must not contain duplicate expenses.
     */
    public void setAliases(List<AliasEntry> aliases) {
        for (AliasEntry e: aliases) {
            this.aliasMap.put(e.getKey(), e.getValue());
        }
    }

    /**
     * Resets the existing data of this {@code AliasMap} with {@code newData}.
     */
    public void resetData(AliasMap newData) {
        requireNonNull(newData);

        setAliases(newData.getAliasList());
    }

    //// alias-level operations

    /**
     * Returns true if an AliasEntry with the same identity as {@code aliasEntry} exists in the alias map.
     */
    public boolean hasAlias(AliasEntry aliasEntry) {
        requireNonNull(aliasEntry);
        return this.aliasMap.containsKey(aliasEntry.getKey());
    }

    /**
     * Returns true if a String with the same identity as {@code aliasString} exists in the alias map.
     */
    public boolean hasAlias(String aliasString) {
        requireNonNull(aliasString);
        return this.aliasMap.containsKey(aliasString);
    }

    public String getValue(String aliasString) throws IllegalArgumentException {
        requireNonNull(aliasString);
        // by definition, RESERVED_KEYWORD cannot be aliases
        if (RESERVED_KEYWORDS.contains(aliasString)) {
            return aliasString;
        }
        if (!this.aliasMap.containsKey(aliasString)) {
            throw new IllegalArgumentException(String.format(ALIAS_NOT_FOUND, aliasString));
        }
        return this.aliasMap.get(aliasString);
    }

    /**
     * Adds an AliasEntry to the alias map.
     * The entry must not already exist in the alias map.
     */
    public void addAlias(AliasEntry aliasEntry) {
        aliasMap.put(aliasEntry.getKey(), aliasEntry.getValue());
    }

    /**
     * Replaces the given etnry {@code prev} in the list with {@code update}.
     * {@code prev} must exist in the expense book.
     * The alias of {@code update} must not be the same as another existing
     * alias in the alias map.
     */
    public void setAlias(AliasEntry prev, AliasEntry update) throws IllegalArgumentException {
        requireNonNull(prev);
        requireNonNull(update);
        assert (prev.getValue().equals(update.getValue())) : "Must replace the same value (command) alias";
        if (prev.getKey().equals(AliasCommand.COMMAND_WORD)
                || prev.getKey().equals(ResetAliasCommand.COMMAND_WORD)) {
            throw new IllegalArgumentException(ALIAS_COMMAND_UNALIASABLE);
        }
        if (!update.getKey().chars().allMatch(IS_ALPHABET_ASCII)) {
            throw new IllegalArgumentException(ALIAS_ALPHABETS_ONLY);
        }
        if (aliasMap.containsKey(update.getKey())) {
            throw new IllegalArgumentException(String.format(DUPLICATE_KEYWORD_FOUND, update.getKey()));
        }
        if (RESERVED_KEYWORDS.contains(update.getKey()) && !prev.getValue().equals(update.getKey())) {
            throw new IllegalArgumentException(String.format(MESSAGE_RESERVED, update.getKey()));
        }
        if (prev.getKey().equals(update.getKey())) {
            throw new IllegalArgumentException(UNCHANGED_ALIAS);
        }
        if (RESERVED_KEYWORDS.contains(prev.getKey())) {
            for (HashMap.Entry<String, String> e: this.aliasMap.entrySet()) {
                if (e.getValue().equals(prev.getKey())) {
                    throw new IllegalArgumentException(String.format(MESSAGE_OVERRIDE_ALIAS, e.getKey()));
                }
            }
        }
        this.aliasMap.remove(prev.getKey());
        if (!RESERVED_KEYWORDS.contains(update.getKey())) {
            addAlias(update);
        }
    }

    public List<AliasEntry> getAliasList() {
        List<AliasEntry> aliases = new ArrayList<>();
        for (Map.Entry<String, String> e: this.aliasMap.entrySet()) {
            aliases.add(new AliasEntry(e.getKey(), e.getValue()));
        }
        return aliases;
    }

    /**
     * Removes {@code alias} from this {@code AliasMap}.
     * {@code alias} must exist in the expense book.
     */
    public void removeAlias(AliasEntry alias) {
        aliasMap.remove(alias.getKey());
    }

    public void removeAllAliases() {
        aliasMap.clear();
    }

    public boolean isEmpty() {
        return this.aliasMap.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof AliasMap)) {
            return false;
        }
        return this.aliasMap.equals(((AliasMap) o).aliasMap);
    }
}
