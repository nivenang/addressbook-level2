package seedu.addressbook.commands;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import seedu.addressbook.common.Messages;
import seedu.addressbook.data.AddressBook;
import seedu.addressbook.data.exception.IllegalValueException;
import seedu.addressbook.data.person.Address;
import seedu.addressbook.data.person.Email;
import seedu.addressbook.data.person.Name;
import seedu.addressbook.data.person.Person;
import seedu.addressbook.data.person.Phone;
import seedu.addressbook.data.person.ReadOnlyPerson;
import seedu.addressbook.data.person.UniquePersonList.PersonNotFoundException;
import seedu.addressbook.ui.TextUi;
import seedu.addressbook.util.TestUtil;
public class DeleteMultipleCommandTest {
    private AddressBook emptyAddressBook;
    private AddressBook addressBook;

    private List<ReadOnlyPerson> emptyDisplayList;
    private List<ReadOnlyPerson> listWithEveryone;
    private List<ReadOnlyPerson> listWithSurnameDoe;

    @Before
    public void setUp() throws Exception {
        Person johnDoe = new Person(new Name("John Doe"), new Phone("61234567", false),
                new Email("john@doe.com", false), new Address("395C Ben Road", false), Collections.emptySet());
        Person janeDoe = new Person(new Name("Jane Doe"), new Phone("91234567", false),
                new Email("jane@doe.com", false), new Address("33G Ohm Road", false), Collections.emptySet());
        Person samDoe = new Person(new Name("Sam Doe"), new Phone("63345566", false),
                new Email("sam@doe.com", false), new Address("55G Abc Road", false), Collections.emptySet());
        Person davidGrant = new Person(new Name("David Grant"), new Phone("61121122", false),
                new Email("david@grant.com", false), new Address("44H Define Road", false),
                Collections.emptySet());
        Person aliceBob = new Person(new Name("Alice Bob"), new Phone("66996699", false),
                new Email("alice@bob.com", false), new Address("639 Lala Road", false),
                Collections.emptySet());

        emptyAddressBook = TestUtil.createAddressBook();
        addressBook = TestUtil.createAddressBook(johnDoe, janeDoe, davidGrant, samDoe, aliceBob);

        emptyDisplayList = TestUtil.createList();

        listWithEveryone = TestUtil.createList(johnDoe, janeDoe, davidGrant, samDoe, aliceBob);
        listWithSurnameDoe = TestUtil.createList(johnDoe, janeDoe, samDoe);
    }

    @Test
    public void execute_emptyAddressBook_returnsPersonNotFoundMessage() {
        assertDeletionFailsDueToNoSuchPerson(1, 1, emptyAddressBook, listWithEveryone);
    }

    @Test
    public void execute_noPersonDisplayed_returnsInvalidIndexMessage() {
        assertDeletionFailsDueToInvalidStartIndex(-1, 0, addressBook, emptyDisplayList);
    }

    @Test
    public void execute_targetPersonNotInAddressBook_returnsPersonNotFoundMessage()
            throws IllegalValueException {
        Person notInAddressBookPerson = new Person(new Name("Not In Book"), new Phone("63331444", false),
                new Email("notin@book.com", false), new Address("156D Grant Road", false), Collections.emptySet());
        List<ReadOnlyPerson> listWithPersonNotInAddressBook = TestUtil.createList(notInAddressBookPerson);

        assertDeletionFailsDueToNoSuchPerson(1, 1, addressBook, listWithPersonNotInAddressBook);
    }

    @Test
    public void execute_invalidIndex_returnsInvalidIndexMessage() {
        assertDeletionFailsDueToInvalidStartIndex(0, 0, addressBook, listWithEveryone);
        assertDeletionFailsDueToInvalidStartIndex(-1, -1, addressBook, listWithEveryone);
        assertDeletionFailsDueToInvalidStartIndex(listWithEveryone.size() + 1, listWithEveryone.size() + 2,
                addressBook, listWithEveryone);
    }

    @Test
    public void execute_validIndex_personIsDeleted() throws PersonNotFoundException {
        assertDeletionSuccessful(1, 2, addressBook, listWithSurnameDoe);
        assertDeletionSuccessful(1, listWithSurnameDoe.size(), addressBook, listWithSurnameDoe);

        int middleIndex = (listWithSurnameDoe.size() / 2) + 1;
        assertDeletionSuccessful(middleIndex, listWithSurnameDoe.size(), addressBook, listWithSurnameDoe);
    }

    /**
     * Creates a new delete command.
     *
     * @param targetVisibleIndex of the person that we want to delete
     */
    private DeleteMultipleCommand createDeleteMultipleCommand(int targetVisibleIndex, int endVisibleIndex, AddressBook addressBook,
                                              List<ReadOnlyPerson> displayList) {

        DeleteMultipleCommand command = new DeleteMultipleCommand(targetVisibleIndex, endVisibleIndex);
        command.setData(addressBook, displayList);

        return command;
    }

    /**
     * Executes the command, and checks that the execution was what we had expected.
     */
    private void assertCommandBehaviour(DeleteMultipleCommand deleteMultipleCommand, String expectedMessage,
                                        AddressBook expectedAddressBook, AddressBook actualAddressBook) {

        CommandResult result = deleteMultipleCommand.execute();

        assertEquals(expectedMessage, result.feedbackToUser);
        assertEquals(expectedAddressBook.getAllPersons(), actualAddressBook.getAllPersons());
    }

    /**
     * Asserts that the index is not valid for the given display list.
     */
    private void assertDeletionFailsDueToInvalidStartIndex(int invalidStartVisibleIndex, int endVisibleIndex,
                                                           AddressBook addressBook, List<ReadOnlyPerson> displayList) {

        String expectedMessage = Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX;

        DeleteMultipleCommand command = createDeleteMultipleCommand(invalidStartVisibleIndex, endVisibleIndex, addressBook, displayList);
        assertCommandBehaviour(command, expectedMessage, addressBook, addressBook);
    }

    /**
     * Asserts that the person at the specified index cannot be deleted, because that person
     * is not in the address book.
     */
    private void assertDeletionFailsDueToNoSuchPerson(int startVisibleIndex, int endVisibleIndex, AddressBook addressBook,
                                                      List<ReadOnlyPerson> displayList) {

        String expectedMessage = Messages.MESSAGE_PERSON_NOT_IN_ADDRESSBOOK;

        DeleteMultipleCommand command = createDeleteMultipleCommand(startVisibleIndex, endVisibleIndex, addressBook, displayList);
        assertCommandBehaviour(command, expectedMessage, addressBook, addressBook);
    }

    /**
     * Asserts that the person at the specified index can be successfully deleted.
     *
     * The addressBook passed in will not be modified (no side effects).
     *
     * @throws PersonNotFoundException if the selected person is not in the address book
     */
    private void assertDeletionSuccessful(int targetVisibleIndex, int endVisibleIndex, AddressBook addressBook,
                                          List<ReadOnlyPerson> displayList) throws PersonNotFoundException {

        AddressBook expectedAddressBook = TestUtil.clone(addressBook);
        StringBuilder expectedMessage = new StringBuilder();

        for (int i = targetVisibleIndex; i <= endVisibleIndex; i++) {
            ReadOnlyPerson targetPerson = displayList.get(i - TextUi.DISPLAYED_INDEX_OFFSET);
            expectedAddressBook.removePerson(targetPerson);
            expectedMessage.append(String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS, targetPerson));
            expectedMessage.append("\n");
        }
        AddressBook actualAddressBook = TestUtil.clone(addressBook);

        DeleteMultipleCommand command = createDeleteMultipleCommand(targetVisibleIndex, endVisibleIndex, actualAddressBook, displayList);
        assertCommandBehaviour(command, expectedMessage.toString(), expectedAddressBook, actualAddressBook);
    }
}

