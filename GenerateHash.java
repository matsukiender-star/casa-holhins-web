import at.favre.lib.crypto.bcrypt.BCrypt;

public class GenerateHash {
    public static void main(String[] args) {
        System.out.println("admin123: " + BCrypt.withDefaults().hashToString(12, "admin123".toCharArray()));
        System.out.println("staff123: " + BCrypt.withDefaults().hashToString(12, "staff123".toCharArray()));
    }
}
