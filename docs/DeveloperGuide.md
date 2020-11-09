---
layout: page
title: Developer Guide
---
* Table of Contents
{:toc}

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

### Architecture

<img src="images/ArchitectureDiagram.png" width="450" />

The ***Architecture Diagram*** given above explains the high-level design of the App. Given below is a quick overview of each component.

<div markdown="span" class="alert alert-primary">

:bulb: **Tip:** The `.puml` files used to create diagrams in this document can be found in the [diagrams](https://github.com/AY2021S1-CS2103-W14-3/tp/tree/master/docs/diagrams/) folder. Refer to the [_PlantUML Tutorial_ at se-edu/guides](https://se-education.org/guides/tutorials/plantUml.html) to learn how to create and edit diagrams.

</div>

**`Main`** has two classes called [`Main`](https://github.com/AY2021S1-CS2103-W14-3/tp/tree/master/src/main/java/seedu/expense/Main.java) and [`MainApp`](https://github.com/AY2021S1-CS2103-W14-3/tp/tree/master/src/main/java/seedu/expense/MainApp.java). It is responsible for,
* At app launch: Initializes the components in the correct sequence, and connects them up with each other.
* At shut down: Shuts down the components and invokes cleanup methods where necessary.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

The rest of the App consists of four components.

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

Each of the four components,

* defines its *API* in an `interface` with the same name as the Component.
* exposes its functionality using a concrete `{Component Name}Manager` class (which implements the corresponding API `interface` mentioned in the previous point.

For example, the `Logic` component (see the class diagram given below) defines its API in the `Logic.java` interface and exposes its functionality using the `LogicManager.java` class which implements the `Logic` interface.

![Class Diagram of the Logic Component](images/LogicClassDiagram.png)

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `delete 1`.

<img src="images/ArchitectureSequenceDiagram.png" width="574" />

The sections below give more details of each component.

### UI component

![Structure of the UI Component](images/UiClassDiagram.png)

**API** :
[`Ui.java`](https://github.com/AY2021S1-CS2103-W14-3/tp/tree/master/src/main/java/seedu/expense/ui/Ui.java)

The UI consists of a `MainWindow` that is made up of parts e.g.`CommandBox`, `ResultDisplay`, `ExpenseListPanel`, `StatusBarFooter` etc. All these, including the `MainWindow`, inherit from the abstract `UiPart` class.

The `UI` component uses JavaFx UI framework. The layout of these UI parts are defined in matching `.fxml` files that are in the `src/main/resources/view` folder. For example, the layout of the [`MainWindow`](https://github.com/AY2021S1-CS2103-W14-3/tp/tree/master/src/main/java/seedu/expense/ui/MainWindow.java) is specified in [`MainWindow.fxml`](https://github.com/AY2021S1-CS2103-W14-3/tp/tree/master/src/main/resources/view/MainWindow.fxml)

The `UI` component,

* Executes user commands using the `Logic` component.
* Listens for changes to `Model` data so that the UI can be updated with the modified data.

### Logic component

![Structure of the Logic Component](images/LogicClassDiagram.png)

**API** :
[`Logic.java`](https://github.com/AY2021S1-CS2103-W14-3/tp/tree/master/src/main/java/seedu/expense/logic/Logic.java)

1. `Logic` uses the `ExpenseBookParser` class to parse the user command.
1. This results in a `Command` object which is executed by the `LogicManager`.
1. The command execution can affect the `Model` (e.g. adding a expense).
1. The result of the command execution is encapsulated as a `CommandResult` object which is passed back to the `Ui`.
1. In addition, the `CommandResult` object can also instruct the `Ui` to perform certain actions, such as displaying help to the user.

Given below is the Sequence Diagram for interactions within the `Logic` component for the `execute("delete 1")` API call.

![Interactions Inside the Logic Component for the `delete 1` Command](images/DeleteSequenceDiagram.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline reaches the end of diagram.
</div>

### Model component

![Structure of the Model Component](images/ModelClassDiagram.png)

**API** : [`Model.java`](https://github.com/AY2021S1-CS2103-W14-3/tp/tree/master/src/main/java/seedu/expense/model/Model.java)

The `Model`,

* stores a `UserPref` object that represents the user’s preferences.
* stores the expense book data.
* exposes an unmodifiable `ObservableList<Expense>` that can be 'observed' e.g. the UI can be bound to this list so that the UI automatically updates when the data in the list change.
* does not depend on any of the other three components.


<div markdown="span" class="alert alert-info">:information_source: **Note:** An alternative (arguably, a more OOP) model is given below. It has a `Tag` list in the `ExpenseBook`, which `Expense` references. This allows `ExpenseBook` to only require one `Tag` object per unique `Tag`, instead of each `Expense` needing their own `Tag` object.<br>
![BetterModelClassDiagram](images/BetterModelClassDiagram.png)

</div>


### Storage component

![Structure of the Storage Component](images/StorageClassDiagram.png)

**API** : [`Storage.java`](https://github.com/AY2021S1-CS2103-W14-3/tp/tree/master/src/main/java/seedu/expense/storage/Storage.java)

The `Storage` component,
* can save `UserPref` objects in json format and read it back.
* can save the expense book data in json format and read it back.

### Common classes

Classes used by multiple components are in the `seedu.expense.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### Category Account Switching Feature

#### Implementation

The switching mechanism is facilitated by `ExpenseBook` and initialised by
`Model#switchCategory(Tag category)`. Additionally, `ExpenseBook` implements the following operations:

* `ExpenseBook#containsCategory(Tag toCheck)` — Checks if the given tag matches the tag of category budget. <a name="switchOperations"></a>
* `ExpenseBook#updateExpenseBookCategory(Tag category)` - Updates the expense to only show expenses and budgets that matches the category.
* `ExpenseBook#updateFilteredBudgets(Predicate<CategoryBudget> predicate)` — Filters the budget list to the given predicate.
* `ExpenseBook#updateFilteredExpenses(Predicate<Expense> predicate)` — Filters the expense list to the given predicate  .

These operations are exposed in the `Model` interface as
* `Model#hasCategory(Tag toCheck)`
* `Model#updateExpenseBookCategory(Tag category)`
* `Model#updateFilteredExpenseList(Predicate<Expense> predicate)`
* `Model#updateFilteredBudgetList(Predicate<CategoryBudget> predicate)`

Given below is a successful usage scenario example and how the switching mechanism behaves at each step.

Step 1. The user launches the application for the first time. The `ExpenseBook` will be initialized with the
initial expense book state in Model.

Step 2. The user executes `switch t/Food` command to switch to `ExpenseBook` with "Food" tag in category budget
and expenses in the expense book. The `switch` command calls `Model#switchCategory(Tag category)`. The method then
calls the operations mentioned [above](#switchOperations) causing the category budgets and expenses to be filtered.

The following sequence diagram shows how the switch operation works:

![SwitchSequenceDiagram](images/SwitchSequenceDiagram.png)
![SwitchSequenceDiagram2](images/SwitchSequenceDiagram2.png)
![SwitchSequenceDiagram3](images/SwitchSequenceDiagram3.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** The lifeline for `SwitchCommand`
should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline reaches the end of diagram.
</div>

<br/>

Step 3. The user then decides to execute the command `topup`. Commands that modify the expense book,
such as `topup`, `delete`, `edit`, will usually call their respectively method in Model. As such the budget balance
display of the application will change accordingly to reflect on their corresponding values.

Step 4. The user then decides to execute the command `list`. Commands that do not modify the expense book but have strong
relationship with altering the budget display such as `list`, will usually revert the displayed budget to the overall
amount instead of category-specific. In addition, the expenses calculated will be based on the displayed list total
amount.

Step 5. The user then decides to execute the command `find`. Commands that do not modify the expense book and do not
have a clear relationship with the budget display such as `find`, will usually omit out the budget display after
their execution. However, exceptions are given to command such as `help`, `exit` which will not alter the visibility
of the current budget display.

#### Design consideration:

##### Aspect: How Category switching executes

* **Alternative 1 (current choice):** Filters the entire expense book by category tag.
  * Pros: Easy to implement.
  * Cons: May have performance issues in terms of execution speed. (Require repeated calculation and filtering)

* **Alternative 2:** Create multiple expense books for each category.
  * Pros: Will be faster during execution. (Calculation and sorting/filtering are done during initialisation)
  * Cons: Slower initialisation and higher overhead cost and more memory used.

### Expense Sorting Feature

#### Implementation

The expense sorting command is facilitated by `UniqueExpenseList` and `ExpenseBook`. In addition
, a new `Command` subclass, `SortCommand`, is required.   Specifically, the following operations are relevant to this
 command:

* `ExpenseBook#sortExpenses(Comparator<Expense> comparator)` — Sorts its `UniqueExpenseList` according to the comparator
 provided.
* `UniqueExpenseList#sortExpenses(Comparator<Expense> comparator)` — Sorts its `ObservableList<Expense>` according to the
 comparator provided.

These operations are exposed in the `Model` interface as `Model#sortExpenseList(Comparator<Expense> comparator)`

##### `SortCommand` and `SortCommandParser`

`SortCommand` will take in at least one, and up to three keywords which specify the order and the parameters to sort
 by (date, description, amount). The conversion of the `String` input to a `SortKeyComparator`, which implements `Comparator<Expense>`, is facilitated by
  `SortCommandParser#parse()`, and the **order** of the sorting parameters is implemented via the `Comparator#thenComparing()` method.

The concrete implementation of the `#Compare(Expense other)` method is present in subclasses of `SortKeyComparator`: `AmountComparator`, `DateComparator`, and `DescriptionComparator`.
These are the methods that are called when combining `SortKeyComparators` through the `Comparator#thenComparing()` method.

Command Example:
* `sort -by date` — Sorted by chronological order.
* `sort -by date -by descriptionR` — Sorted in chronological order, then based on reverse alphabetical
 order of descriptions.
* `sort -by date -by amount -by description` — Sorted in following order of priority: Chronological order, then by
 increasing order of amounts, then by alphabetical order of descriptions.

##### Example Usage
Given below is an example usage scenario and how the sorting command behaves at each step.

Step 1. The user launches the application. The `ExpenseBook` shows the list of expenses in the expense book.

Step 2. The user executes `sort by/date by/descriptionR` command to sort the `Expenses` in `ExpenseBook` first by
 date, then in reverse alphabetical order of the descriptions.
 A comparator is created reflecting the above sorting.
 The `sort` command calls `Model#sortExpenseList(Comparator<Expense> c)`, causing the `ExpenseBook` expenses to be
  sorted according to the comparator, and the `filteredExpenses` in `Model` to be modified since it is a listener.

The following sequence diagrams shows how the sort command works:
![SortSequenceDiagram Pt1](images/SortSequenceDiagram1.png)

![SortSequenceDiagram Pt2](images/SortSequenceDiagram2.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** The lifeline for `SortCommand` and
 `Comparator<Expense>` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline reaches the end of diagram.
</div>

Step 3. The user then decides to execute the command `delete`. Commands that modify the expense book, such as `delete`, `edit`, will usually call their respectively method in Model, but using the new index ordering of the sorted list.

Step 4. The user then decides to execute the command `list`. This will revert the display view to initial
 `ExpenseBook`.


#### Design consideration:

##### Aspect: How Sorting Command executes

* **Alternative 1 (current choice):** Add an additional sort on top of current list implementation.
  * Pros: Easy to implement.
  * Cons: Lack customizability in list views.

* **Alternative 2:** Create multiple list views each representing a particular sort.
  * Pros: Greater degree of customisation with regards to GUI.
  * Cons: Difficult to implement as it requires the creation of multiple subclasses of `ListCommand`.

### Customisation of Command Keywords using Alias Feature

#### Implementation

The implementation for the customisation of command keyword shortcuts for various `Command` subclasses is by introducing a class called `AliasMap` which stores mappings between a command shortcut ('alias') and the actual command word of the class, as well as a class called `AliasCommand` which takes in two user input as parameters. The first parameter is the default command word for which the user wishes to create an alias, or if there is an existing alias for the command word, it must be the existing alias for which the user wishes to alter into a new alias. The second keyword determines what the command's (new) alias would be.

Sample usage: By default, the only command word for `FindCommand` is `"find"`

* `alias find get` -> The user can now trigger a `FindCommand` with `"get"` alias. The `"find"` command word will still work.
* `alias get find` -> The "get" alias is removed from `AliasMap` object and only `"find"` can now trigger `FindCommand`. In other words, `FindCommand` no longer has an alias.

To maintain some degree of simplicity and neatness, we require that `AliasCommand` and `ResetAliasCommand` cannot have aliases themselves. Furthermore, any custom alias is restricted to 10 case-sensitive alphabetical characters and each command can only have up to a single alias at any point in time. Default command words of each `Command` subclasses cannot be used as aliases.

To allow for customisation to remain even after the user exits the app and subsequently restarts it, a customised alias-to-command mapping will be stored in JSON format, which can be converted to `AliasMap` and `AliasEntry` objects when Bamboo runs.

The `ExpenseBookParser`'s `parseCommand()` method takes in an AliasMap object in addition to the user input, which allows the parser to map aliases to the default keyword and allows the execution of the associated `Command` object.

Step 1. The user launches the application for the first time. Assume no alias is present (by default, aliases in the JSON file will be the default command word).

Step 2. The user inputs `alias find get` to update the alias for `FindCommand` as `”get”`.
This will not only update the current AliasMap object, but will also update the JSON mapping with the help of StorageManager which handles all types of storage including JsonAliasMapStorage.

The following is a sequence diagram showing how it works:
![AliasSequenceDiagram](images/AliasSequenceDiagram.png)

Step 3. The user can now use the following command to trigger a FindCommand.

* `get -d lunch at macs`

#### Design consideration:

##### Aspect: How alias executes
* **Alternative 1 (current choice):** Allows aliases of all command words except for `AliasCommand`. Does not override default command words but merely adds an alias. Reserved keywords cannot be applied unless it is for its associated subclass (i.e. removing the custom alias).
  * Pros: Neater implementation especially if the user might frequently change his alias.
  * Cons: Restricts degree of customisation.

* **Alternative 2:** Allows aliases of all command words. Does not override default command words but merely adds an alias. Reserved keywords cannot be applied unless it is for its associated subclass (i.e. removing the custom alias).
  * Pros: More flexibility than Alternative 1.
  * Cons: Restricts degree of customisation due to reserved keywords not being allowed to use as alias for other Commands.

* **Alternative 3:** Allows customisation of ALL command words.
  * Pros: Highest degree of flexibility, better for users who can easily get used to Command Line Apps.
  * Cons: May be messy and slower learning users may get confused.
_{more aspects and alternatives to be added}_

### Default Category

The function of the default category is to subsume all "untagged" `expenses` under some category.
This is especially important for possible occasions such as when the User uses the application without any categories,
or when the User deletes a category that existing `expenses` are linked to.

#### Implementation

The default category generally functions the same way as any user-created category, except that it cannot be deleted or
renamed. It is contained separately from the user-created categories (if any) for this reason. If a new ExpenseBook is
started, the default category is automatically initialized so that the User can use the full range of the basic
features even without creating customized categories.

### Expense and Budget Categorization Feature

The budgets and expenses in the expense book are grouped according to the user's specifications, as they can freely create
the categories that they need, top-up the desired amount for each category, and tag their expenses (both new and old) to
the respective categories to keep track of their spending.

#### Implementation

Each expense is tagged to exactly one category, and the category must exist in the expense book before any expenses can
be tagged to it. When a category is created in the expense book, the corresponding budget is automatically initialized
with zero amount. Thereafter, the user can top-up (or reduce) each budget accordingly, and the balance after deducting
all expenses within that category is shown to the user.

![AddingCategorizedExpenseActivityDiagram](images/CategoryBudgetActivityDiagram.png)

The activity diagram above shows a possible flow of the user attempting to add an expense tagged to a specific category.

![ToppingUpCategoryBudgetActivityDiagram](images/CategoryBudgetActivityDiagram2.png)

This second activity diagram shows a possible flow of the user trying to top-up the budget of a specific category.

### Graphical Representation Feature

#### Implementation

The graphical representation feature will allow the user to view a pie chart of his/her total spending, with each wedge representing the percentage of spending that corresponds to a specific tag.
This graphical representation will be displayed on `GraphDisplayWindow` upon the execution of `GraphCommand`. This mechanism will be facilitated by `PieChartData` to retrieve the required data from `ExpenseBook`.
`PieChartData` extends `ChartData` which allows abstracting out the implementation of different graphical representation formats for future versions.

* `PieChartData#collectData(ObservableList<Expense>)` — Retrieves required data.
* `PieChartData#getData()` — Returns data of each `Tag` and the corresponding total expenditure.

The interactions between `ExpenseBook` (which contains the in-memory data of expenses) and `GraphicalDisplayWindow` (which specifies the UI displayed) facilitated by `PieChartData` is given below. </br>

![ClassDiagram](images/GraphicalRepresentationClassDiagram.png)

The user initiates this function by executing the graph command. Refer Logic Component architecture diagram for the mechanism by which strings are read and parsed into Command objects.

The sequence diagram below shows the proposed mechanism by which the required data necessary to initialise a pie chart is retrieved. This data is then used to format the UI output.

![SequenceDiagram](images/GraphicalRepresentationSequenceDiagram.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** Expenses that are untagged will be displayed as a single group. Tags should not be case sensitive.

</div>

A possible edge case would be the user having empty expense record prior to executing the graph command. In such a case, the program will display a default message.

The following activity diagram summarizes what happens when a user enters a graph command:

![ActivityDiagram](images/GraphicalRepresentationActivityDiagram.png)

#### Points to Note:

* **UI classes to only act as placeholders**
  * UI formatting is separated from the backend logic.
  * Allows for dynamic updating of graphs.

* **Data retrieval occurs upon every execution**
  * Updates graph accordingly.
  * Will not show outdated graphs.

* **Easily extensible**
  * Different sub-classes of `ChartData` can be implemented to collect a variety of meaningful data from `ExpenseBook` to be displayed by various UI classes in different formats.

--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, configuration, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [Configuration guide](Configuration.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product scope

**Target user profile**:

* is a college student
* spends money mainly on food, transportation, social life and material goods
* wants to keep track of his personal expenses
* considers oneself as "tech-folk"
* is familiar and comfortable with the command line
* types fast
* wants to set and keep to a budget
* likes things that are fast and simple
* is attached
* pays for his own bills
* is all for cashless
* is cautious about digital security
* prefers storing things digitally rather than on paper
* likes flexibility/customization
* plays games and likes achievements
* is not earning income
* is lazy enough to find a solution to manage his expenses for him

**Value proposition**: manage expenses faster and simpler than a typical mouse/GUI driven app



### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | As a …​                                    | I want to …​                                                                                 | So that I can…​                                                        |
| -------- | ------------------------------------------ | ---------------------------------------------------------------------------------------------------- | ---------------------------------------------------------------------- |
| `* * *`  | user                                       | add a new expense                                                                                    | update my new spending                                                 |
| `* * *`  | user                                       | delete an expense                                                                                    | remove entries that I no longer need                                   |
| `* * *`  | user                                       | set a budget                                                                                         | track if I am sticking to my financial goals                           |
| `* * *`  | user                                       | top up a budget                                                                                      | increase my spending limit                                             |
| `* * *`  | user                                       | edit an expense                                                                                      | change details of the expense that is outdated or wrong                |
| `* *`    | organized user                             | categorise my expenditure                                                                            | better segregate and manage various areas of my spending               |
| `* *`    | new user                                   | use a help command                                                                                   | refer to instructions when I forget how to use the App                 |
| `* *`    | efficient user                             | customise my command keywords                                                                        | customize my user experience and workflow according to my preferences  |
| `* *`    | user with many expenses                    | find expenses via date, keywords, or category                                                        | locate a specific expense easily                                       |
| `* *`    | careless user                              | sort expenses based on date, description, or amount                                                  | organise my expenses better and gain a big picture view                |
| `*`      | meticulous user                            | track my saving progress to buy big ticket items                                                     | know how far away am I from the target                                 |
| `*`      | user who likes to see progress             | use a progress tracker to motivate myself                                                            | keep working at saving up                                              |
| `*`      | cautious user                              | view my ledger data in a human-readable format and only edit the file when commands are executed     | be assured that the accounts are updated and accurate                  |
| `*`      | long-time user                             | archive older data from my view                                                                      | manage my expenses easier                                              |
| `*`      | careless user                              | revert my commands                                                                                   | easily undo changes I made to my budgeting                             |
| `*`      | user who likes to plan in advance          | simulate future spending                                                                             | visualize my journey towards my financial goals                        |
| `*`      | forgetful user                             | receive notifications of budget limits and bill payments                                             | better plan for daily expenditure and make payments on time            |

*{More to be added}*

### Use cases

(For all use cases below, the System is the Bamboo and the Actor is the user, unless specified otherwise)

#### Use case U1: Add an expense

**MSS**

1. User requests to add an expense.
2. Bamboo adds the expense.
3. Bamboo lists all expenses and shows the new budget balance.

    Use case ends.

**Extensions**

* 1a. The given command format is invalid.
    * 1a1. Bamboo shows an error message.

      Use case ends.

* 1b. The given field value is invalid.
    * 1b1. Bamboo shows an error message.

      Use case ends.

#### Use case U2: Top-up budget

**MSS**

1. User requests to top up a specified budget by an amount and category he provides.
2. Bamboo tops up the user's budget in the category and by the amount given by the user.
3. Bamboo updates the budget balance.

    Use case ends.

**Extensions**

* 1a. The given top-up value is invalid.
    * 1a1. Bamboo shows an error message.

      Use case ends.

* 1b. The given category value is invalid.
    * 1b1. Bamboo shows an error message.

      Use case ends.

#### Use case U3: Delete an expense

**MSS**

1. User requests to list expenses (U5).
2. Bamboo shows a list of expenses.
3. User requests to delete a specific expense in the list.
4. Bamboo deletes the expense.
5. Bamboo lists all expenses and shows the budget balance.

   Use case ends.

**Extensions**

* 2a. The list is empty.

  Use case ends.

* 3a. The given expense does not exist.
    * 3a1. Bamboo shows an error message.

      Use case resumes at step 2.

#### Use case U4: Edit an expense

**MSS**

1. User requests to list expenses (U5).
2. Bamboo shows a list of expenses.
3. User requests to edit an expense with the new fields given.
4. Bamboo edits the expense.
5. Bamboo feedbacks to user what was changed.
6. Bamboo lists all expenses and shows the new budget balance.

   Use case ends.

**Extensions**

* 2a. The list is empty.
    * 2a1. Bamboo shows an error message

      Use case ends

* 3a. The given field value is invalid.
    * 3a1. Bamboo shows an error message.

      Use case ends.

#### Use case U5: List all expenses

**MSS**

1. User requests to list all expenses.
2. Bamboo shows a list of all expenses recorded and the current budget balance.

   Use case ends.

#### Use case U6: Add a remark to an expense

**Preconditions:**
* Expense List is not empty.

**MSS**
1. User requests to add a remark to specified expense item.
2. Bamboo adds remark to specified expense item.
3. Bamboo lists all expenses and shows the budget balance.

   Use case ends.

**Extensions**

* 1a. The given expense does not exist.
    * 1a1. Bamboo shows an error message.

    Use case ends.

* 1b. The given field value is invalid.
    * 1b1. Bamboo shows an error message.

      Use case ends.

#### Use case U7: Find an expense

**MSS**

1. User requests to find expense by certain identifiers and search terms.
2. Bamboo shows a list of expenses which match the identifiers and search terms, and shows the overall budget balance.

   Use case ends.

**Extensions**

* 1a. The given field values are invalid.
    * 1a1. Bamboo shows an error message.

      Use case ends.

#### Use case U8: Sort expense list
**MSS**

1. User requests to sort currently displayed expenses by certain sort criterion.
2. Bamboo sorts the currently displayed expenses according to user-specified sort criterion.
3. Bamboo displays the sorted expenses to the user with the budget balance.

   Use case ends.

**Extensions**

* 1a. The given field values are invalid.
    * 1a1. Bamboo shows an error message.

      Use case ends.

#### Use case U9: Add category
**MSS**

1. User requests to add a new category with a user-specified category name.
2. Bamboo creates a new category with the user-specified name and shows a success message.
3. Bamboo lists all expenses and shows the budget balance.

   Use case ends.

**Extensions**

* 1a. The given field values are invalid.
    * 1a1. Bamboo shows an error message.

      Use case ends.

* 1b. The given category name already exists.
    * 1b1. Bamboo shows an error message.

      Use case ends.

#### Use case U10: Delete category
Similar to U9, except it's the opposite.

**Extensions**

* 1a. The given field values are invalid.
    * 1a1. Bamboo shows an error message.

      Use case ends.

* 1b. The given category name does not exist.
    * 1b1. Bamboo shows an error message.

      Use case ends.

* 1c. The given category name is restricted e.g. `Default` category.
    * 1c1. Bamboo shows an error message.

      Use case ends.

#### Use case U11: Switch category
**MSS**

1. User requests to switch to a user-specified category
2. Bamboo shows all expenses which are tagged under the user-specified category, along with the budget for that category.

   Use case ends.

**Extensions**

* 1a. The given field values are invalid.
    * 1a1. Bamboo shows an error message.

      Use case ends.

* 1b. The given category does not exist.
    * 1b1. Bamboo shows an error message.

      Use case ends.

#### Use case U12: Change Command Keyword
**MSS**

1. User requests to change command keyword to a user-defined string.
2. Bamboo maps the command linked to the original command keyword to the user-defined string.

   Use case ends.

**Extensions**

* 1a. The given field values are invalid.
    * 1a1. Bamboo shows an error message.

      Use case ends.

* 1b. The user-defined string is already a command keyword.
    * 1b1. Bamboo shows an error message.

      Use case ends.

#### Use case U13: Reset Changed Command Keyword
**MSS**

1. User requests to reset changed command keyword to default command keyword.
2. Bamboo clears the changed commands linked to the original command keyword.

   Use case ends.

**Extensions**

* 1a. No command keyword changed previously.
    * 1a1. Bamboo shows an error message.

      Use case ends.

#### Use case U14: Reduce Category Budget
**MSS**

1. User requests to reduce a given amount of budget from a specific category.
2. Bamboo reduces the matching category-budget by the specified amount.
3. Bamboo shows the new budget amount for that category.
4. Bamboo updates the budget balance bar.

   Use case ends.

**Extensions**

* 1a. The category does not exist in the expense book.
    * 1a1. Bamboo shows an error message.

      Use case ends.

* 1b. The specified amount is invalid.
    * 1b1. Bamboo shows an error message.

      Use case ends.

* 1c. The specified amount exceeds the amount present in the specified budget.
    * 1c1. Bamboo reduces the amount in the specified budget to zero.
    * 1c2. Bamboo alerts the user that the amount reduced from the budget exceeded the amount present.
    * 1c3. Bamboo updates the budget balance bar.

      Use case resumes from 4.

#### Use case U15: Graph Command Keyword
**MSS**

1. User requests to view graph of his personal finance.
2. Bamboo displays a pop-up window containing a pie chart showing amount spent for each category.

   Use case ends.

**Extensions**

* 1a. User has not recorded any expenditures into the app.
    * 1a1. Bamboo shows a blank pop-up window.

      Use case ends.
      
### Non-Functional Requirement

Project Constraints:

* Product does not handle user's actual financial account
* User input in the form of command line
* System should be beginner-friendly
* User's spending data should be saved in an external storage file
* Storage file should be updated after each addition or update of spending data
* Product is offered as an offline application

Process Requirements:

* Project is expected to adhere to the set Milestones.
* User can execute actions using at most 1 command.

### Glossary

- **Mainstream OS**: Windows, Linux, Unix, OS-X
- **Expense**: A single instance of expenditure containing a description, amount spent, date, and category. Expenses are subtracted from the user's **budget**.
- **Budget**: The amount a user sets aside to spend.
- **Budget Balance**: The amount of budget left after deducting all expenses.

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

<div markdown="span" class="alert alert-info">:information_source: **Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.

</div>

### Launch and shutdown

1. Initial launch

   1. Download the jar file and copy into an empty folder

   1. Double-click the jar file Expected: Shows the GUI with a set of sample contacts. The window size may not be optimum.
   Alternatively, enter `java -jar expensebook-v1.4.jar` in Command Prompt after switching to the directory with the jar file.

1. Saving window preferences

   1. Resize the window to an optimum size. Move the window to a different location. Close the window.

   1. Re-launch the app by double-clicking the jar file.<br>
       Expected: The most recent window size and location is retained.

### Deleting an expense

1. Deleting an expense while all expenses are being shown

   1. Prerequisites: List all expenses using the `list` command. At least 1 expense in the list.

   1. Test case: `delete 1`<br>
      Expected: First expense is deleted from the list. Details of the deleted expense shown in the command box with a uscess.

   1. Test case: `delete 0`<br>
      Expected: No expense is deleted. Error details shown in the command box. 

   1. Other incorrect delete commands to try: `delete`, `delete x`, `...` (where x is larger than the list size)<br>
      Expected: Similar to previous.

### Adding an expense

1. Adding an expense while Expense Book is empty. 

    1. Prerequisites: List all expenses using `list` command. Expense Book is empty.
    
    1. Test case: `add -d lunch -$10`<br>
       Expected: An Expense with the description "lunch", costing $10, with the date set as the present date, is added to the expense book. A success message detailing this is shown in the command box.
       
    1. Test case: `add -d lunch -$10 -@27/09/2020`<br>
       Expected: An Expense with the description "lunch", costing $10, occuring on 27th September 2020 is added to the expense book. A success message detailing this is shown in the command box.
    
    1. Test cases: `add -d lunch`<br>
       Expected: No expense is added. Error details shown in the command box.

1. Adding an expense while Expense Book has some expenses
    
    1. Prerequisites: List all expenses using `list` command. Expense Book has at least 1 entry.
    
    1. Test case: Add an expense with the same description, amount and date as an existing expense in the expense book.<br>
       Expected: No expense is added. Error details shown in the command box.

### Topup budget

1. Topping up budget without a category specified

    1. Test case: `topup -$-10`<br>
    Expected: Budget is not topped up. Error message shown in command box because of negative value provided.
    
    1. Test case: `topup -$10`<br>
    Expected: "Default" category budget is topped up by $10. A success message detailing this is shown in the command box.
    
1. Topping up budget with a category specified

    1. Test case: `topup -$10 t/Food` ("Food" category exists)<br>
    Expected: "Food" category budget is topped up by $10. A success message detailing this is shown in the command box.
    
    1. Test case: `topup -$10 t/Transport` ("Transport" category does not exist yet)<br>
    Expected: No budget is topped up. Error message shown in command box because the "Transport" category does not exist. 

### Saving data

1. Saving data automatically after every command

   1. Prerequisites: At least one expense is in the expense book.
   
   1. Add an expense: `add -d test -$10`
   
   1. Test case: Close the app, then start it up again. <br>
      Expected: the expense book should be the same as when the app was closed. 


