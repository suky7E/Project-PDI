package model;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class BankCard {
    private static final SecureRandom random = new SecureRandom();

    public String generateCardNumber(String bin) {
        int length = 16; //card length
        StringBuilder cardNumber = new StringBuilder(bin);

        for (int i = bin.length(); i < length - 1; i++) {
            cardNumber.append(random.nextInt(10));
        }

        int checkDigit = calculateLuhnCheckDigit(cardNumber.toString());
        cardNumber.append(checkDigit);

        return cardNumber.toString();
    }

    public String generateCVV() {
        return String.format("%03d", random.nextInt(1000)); // Generate CVV
    }

    public String generateExpiryDate() {
        LocalDate expiry = LocalDate.now().plusYears(random.nextInt(3) + 3);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");
        return expiry.format(formatter);
    }

    private static int calculateLuhnCheckDigit(String number) {
        int sum = 0;
        boolean alternate = true;
        for (int i = number.length() - 1; i >= 0; i--) {
            int n = Character.getNumericValue(number.charAt(i));
            if (alternate) {
                n *= 2;
                if (n > 9) n -= 9;
            }
            sum += n;
            alternate = !alternate;
        }
        return (10 - (sum % 10)) % 10;
    }

}


