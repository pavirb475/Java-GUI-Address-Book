import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

// Contact class 
class Contact {
    // Encapsulation: private fields with public getters
    private String name;
    private String phone;
    private String email;
    private String address;
    private String birthday;

    public Contact(String name, String phone, String email, String address, String birthday) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.birthday = birthday;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phone;
    }

    public String getEmailAddress() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public String getBirthday() {
        return birthday;
    }

    // Override toString for better string representation of the object
    @Override
    public String toString() {
        return name + "  " + phone + "   " + email+ "   "+ address+ "   "+birthday;
    }
}





// AddressBook class managing a list of contacts
class AddressBook {
    // Encapsulation: private list of contacts
    private List<Contact> contacts;    //list "contacts"
    private static final String FILE_NAME = "contacts.txt";

    // Abstraction
    public AddressBook() {
        contacts = new ArrayList<>();
        loadContactsFromFile();
    }

    // ADD CONTACTS
    public void addContact(Contact contact) {
        contacts.add(contact);  //add() is a method of the List interface
        saveContactsToFile();
    }

    //DELETE CONTACTS
    public void deleteContact(Contact contact) {
        contacts.remove(contact);  //remove() is a method of the List
        saveContactsToFile();
    }

    //UPDATE CONTACTS
    public void updateContact(Contact oldContact, Contact newContact) {
        int index = contacts.indexOf(oldContact);   //takes index of old contact
        if (index != -1) {
            contacts.set(index, newContact);   //replaces the oldContact at the found index with the newContact
            saveContactsToFile();
        }
    }

    public List<Contact> getAllContacts() {
        return new ArrayList<>(contacts);
    }



    // Load contacts from a file
    private void loadContactsFromFile() {                                              //FileReader object is used to read characters from a file.
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {  //BufferedReader is a class in Java that reads text from a character-input stream,
            String line;
            while ((line = reader.readLine()) != null) {   //loop continues until readLine() returns null // reads each line from the file
                String[] parts = line.split(",");   //Splits the line into an array of strings using a comma 
                if (parts.length == 5) {  //5 elemnts
                    Contact contact = new Contact(parts[0], parts[1], parts[2], parts[3], parts[4]); //fields                            //String[]>>array of strings
                    contacts.add(contact);
                }
            }
        } catch (IOException e) {   //IO EXCEPTION occurs when an input or output operation fails or is interrupted
            // Exception handling
            System.out.println("Error loading contacts from file: " + e.getMessage());   //method retrieves the error message
        }
    }



    // Save contacts to a file using a separate thread (Multithreading)
    private void saveContactsToFile() {
        Thread saveThread = new Thread(() -> {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) { //opens a file specified for writing
                for (Contact contact : contacts) {                                                 
                    writer.write(String.join(",", contact.getName(), contact.getPhoneNumber(), contact.getEmailAddress(),
                            contact.getAddress(), contact.getBirthday()));          // It concatenates the contact details (name, phone number, email, address, birthday) separated by commas using String.join
                    writer.newLine();
                }
            } catch (IOException e) {
                // Exception handling
                System.out.println("Error saving contacts to file: " + e.getMessage());
            }
        });
        saveThread.start();  // Start the thread to save contacts
    }


}





// Main GUI class
public class AddressBookGUI extends JFrame {
    private AddressBook addressBook;
    private JList<Contact> contactList;
    private DefaultListModel<Contact> contactListModel;

    public AddressBookGUI() {
        // Abstraction: using AddressBook to manage contacts
        addressBook = new AddressBook();

        initComponents();
        setupLayout();
        setupListeners();
    }

    private void initComponents() {
        // GUI Programming with Swing
        setTitle("Address Book");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // default close operation

        JButton addButton = new JButton("Add Contact");
        JButton deleteButton = new JButton("Delete Contact");
        JButton updateButton = new JButton("Update Contact");

        contactListModel = new DefaultListModel<>();
        contactList = new JList<>(contactListModel);
        JScrollPane scrollPane = new JScrollPane(contactList);  //provides scrolling functionality if the list of contacts is too large

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);  //to hold the buttons (addButton, deleteButton, and updateButton).
        buttonPanel.add(updateButton);

        getContentPane().setLayout(new BorderLayout());                // content pane is a container where you add all the components that you want to display 
        getContentPane().add(buttonPanel, BorderLayout.NORTH);           //BorderLayout is a layout manager provided by Swing 
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);

        updateContactList(addressBook.getAllContacts());   //updates the contactListModel with all the contacts retrieved from the AddressBook and displays them in the contactList.
    }

    private void setupLayout() {
        updateContactList(addressBook.getAllContacts());
    }

    private void setupListeners() {
        JButton addButton = (JButton) ((JPanel) getContentPane().getComponent(0)).getComponent(0);  //retrives the add button
        addButton.addActionListener(e -> {               //adds an action listener to the addButton
            // Polymorphism: different implementations of ActionListener
            Contact contact = getContactDetailsFromUser(null);   //get the details of a new contact from the user
            if (contact != null) {
                addressBook.addContact(contact);  //adds the new contact (stored in the contact variable) to the addressBook.
                updateContactList(addressBook.getAllContacts());
            }
        });

        

        JButton deleteButton = (JButton) ((JPanel) getContentPane().getComponent(0)).getComponent(1);
        deleteButton.addActionListener(e -> {
            // Polymorphism: different implementations of ActionListener
            Contact selectedContact = contactList.getSelectedValue();
            if (selectedContact != null) {
                addressBook.deleteContact(selectedContact);
                updateContactList(addressBook.getAllContacts());
            }
        });

        JButton updateButton = (JButton) ((JPanel) getContentPane().getComponent(0)).getComponent(2);
        updateButton.addActionListener(e -> {
            // Polymorphism: different implementations of ActionListener
            Contact selectedContact = contactList.getSelectedValue();
            if (selectedContact != null) {
                Contact updatedContact = getContactDetailsFromUser(selectedContact);
                if (updatedContact != null) {
                    addressBook.updateContact(selectedContact, updatedContact);
                    updateContactList(addressBook.getAllContacts());
                }
            }
        });
    }

    private Contact getContactDetailsFromUser(Contact contact) {
        JTextField nameField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField addressField = new JTextField();
        JTextField birthdayField = new JTextField();

        if (contact != null) {
            nameField.setText(contact.getName());          //sets the text of the nameField to the name of the contact obtained from contact.getName()
            phoneField.setText(contact.getPhoneNumber());
            emailField.setText(contact.getEmailAddress()); 
            addressField.setText(contact.getAddress());
            birthdayField.setText(contact.getBirthday());
        }

        JPanel panel = new JPanel(new GridLayout(0, 1));  // layout will arrange the components in a single column.
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Phone Number:"));
        panel.add(phoneField);
        panel.add(new JLabel("Email Address:"));
        panel.add(emailField);
        panel.add(new JLabel("Address:"));
        panel.add(addressField);
        panel.add(new JLabel("Birthday:"));
        panel.add(birthdayField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Contact Details",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);   //displays a confirmation dialog box with a panel (panel) containing contact details
                                                                        //If the OK button is clicked, the code inside the if block will be executed
        if (result == JOptionPane.OK_OPTION) {
            return new Contact(nameField.getText(), phoneField.getText(), emailField.getText(),
                    addressField.getText(), birthdayField.getText());  // creates a new Contact object using the text entered in the text fields
        } else {
            return null;
        }
    }

    private void updateContactList(List<Contact> contacts) {
        contactListModel.clear();  // clears the existing elements
        for (Contact contact : contacts) { 
            contactListModel.addElement(contact);  //Inside the loop, this line adds each Contact object to the contactListModel. 
        }
    }




    public static void main(String[] args) {
        AddressBookGUI gui = new AddressBookGUI();          // GUI Programming with Swing
        gui.setVisible(true);
    }
}
