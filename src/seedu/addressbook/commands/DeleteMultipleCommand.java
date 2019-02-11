package seedu.addressbook.commands;

import seedu.addressbook.common.Messages;
import seedu.addressbook.data.person.ReadOnlyPerson;
import seedu.addressbook.data.person.UniquePersonList;

import static seedu.addressbook.ui.TextUi.DISPLAYED_INDEX_OFFSET;

/**
 * Deletes multiple people identified using the start of the last displayed index to
 * end of the last displayed index from the address book.
 */
public class DeleteMultipleCommand extends Command {

    private int endIndex = -1;

    public static final String COMMAND_WORD = "deletem";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes multiple persons identified by the start and end index number used in the last person listing.\n"
            + "Parameters: START_INDEX END_INDEX\n"
            + "Example: " + COMMAND_WORD + " 1" + " 5";

    private static final String MESSAGE_DELETE_PERSON_SUCCESS = "Deleted Person: ";

    private StringBuilder deletedPersonsList;

    public DeleteMultipleCommand(int targetVisibleIndex, int targetEndIndex) {
        super(targetVisibleIndex);
        this.endIndex = targetEndIndex;
        this.deletedPersonsList = new StringBuilder();
    }

    @Override
    public CommandResult execute() {

        int numOfPersonsToDelete = endIndex - getTargetIndex() + DISPLAYED_INDEX_OFFSET;


        if (relevantPersons.size() < endIndex) {
            numOfPersonsToDelete = relevantPersons.size() - getTargetIndex() + DISPLAYED_INDEX_OFFSET;
        }

        try {
            if (getTargetIndex() > relevantPersons.size()) {
                throw new IndexOutOfBoundsException();
            }

            while (numOfPersonsToDelete >= DISPLAYED_INDEX_OFFSET) {
                ReadOnlyPerson target = getTargetPerson();
                addressBook.removePerson(target);
                numOfPersonsToDelete--;
                setTargetIndex(getTargetIndex() + DISPLAYED_INDEX_OFFSET);
                buildDeletedPersonsList(target);
            }
        } catch (IndexOutOfBoundsException ie) {
            return new CommandResult(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        } catch (UniquePersonList.PersonNotFoundException pnfe) {
            return new CommandResult(Messages.MESSAGE_PERSON_NOT_IN_ADDRESSBOOK);
        }

        return new CommandResult(deletedPersonsList.toString());
    }

    private void buildDeletedPersonsList(ReadOnlyPerson target) {
        this.deletedPersonsList.append(MESSAGE_DELETE_PERSON_SUCCESS);
        this.deletedPersonsList.append(target);
        this.deletedPersonsList.append("\n");
    }

    public int getEndIndex() {
        return this.endIndex;
    }
}
