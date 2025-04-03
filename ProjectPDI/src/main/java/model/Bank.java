package model;

public class Bank {
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private String confirmpassword;
    private String icon;
    private String dateofbirth;
    private String phonenumber;
    private String nationalid;
    private String countrycode;
    private String passcode;
    private String cardnumber;
    private String cvv;
    private String expirydate;
    private String balance;

    // Static instance to store logged-in user
    private static Bank loggedInUser = null;

    // Default Constructor
    public Bank() {}

    // Constructor with all fields (optional)
    public Bank(String firstname, String lastname, String email, String password, 
                String confirmpassword, String icon, String dateofbirth, 
                String phonenumber, String nationalid, String countrycode, String passcode) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.confirmpassword = confirmpassword;
        this.icon = icon;
        this.dateofbirth = dateofbirth;
        this.phonenumber = phonenumber;
        this.nationalid = nationalid;
        this.countrycode = countrycode;
        this.passcode = passcode;
    }

    // Static method to store the logged-in user
    public static void setLoggedInUser(Bank user) {
        loggedInUser = user;
    }

    // Static method to get the logged-in user
    public static Bank getLoggedInUser() {
        return loggedInUser;
    }

    // Getters and Setters
    public String getFirstname() { return firstname; }
    public void setFirstname(String firstname) { this.firstname = firstname; }

    public String getLastname() { return lastname; }
    public void setLastname(String lastname) { this.lastname = lastname; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getConfirmpassword() { return confirmpassword; }
    public void setConfirmpassword(String confirmpassword) { this.confirmpassword = confirmpassword; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public String getDateofbirth() { return dateofbirth; }
    public void setDateofbirth(String dateofbirth) { this.dateofbirth = dateofbirth; }

    public String getPhonenumber() { return phonenumber; }
    public void setPhonenumber(String phonenumber) { this.phonenumber = phonenumber; }

    public String getNationalid() { return nationalid; }
    public void setNationalid(String nationalid) { this.nationalid = nationalid; }

    public String getCountrycode() { return countrycode; }
    public void setCountrycode(String countrycode) { this.countrycode = countrycode; }

    public String getPasscode() { return passcode; }
    public void setPasscode(String passcode) { this.passcode = passcode; }
    
    public String getCardnumber() { return cardnumber; }
    public void setCardnumber(String cardnumber) { this.cardnumber = cardnumber; }
    
    public String getCvv() { return cvv; }
    public void setCvv(String cvv) { this.cvv = cvv; }
    
    public String getExpirydate() { return expirydate; }
    public void setExpirydate(String expirydate) { this.expirydate = expirydate; }
    
    public String getBalance() { return balance; }
    public void setBalance(String balance) { this.balance = balance; }

    // Debugging: Print object details
    @Override
    public String toString() {
        return "Bank{" +
                "firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", email='" + email + '\'' +
                ", phonenumber='" + phonenumber + '\'' +
                ", nationalid='" + nationalid + '\'' +
                ", countrycode='" + countrycode + '\'' +
                '}';
    }
}
