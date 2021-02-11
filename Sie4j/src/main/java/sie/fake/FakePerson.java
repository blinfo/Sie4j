package sie.fake;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 *
 * @author Håkan Lidén - 
 * <a href="mailto:hl@hex.nu">hl@hex.nu</a>
 */
class FakePerson {

    private String name;
    private String image;
    private String pin;
    private String email;
    private String address;
    private String phone;
    private String password;

    public FakePerson() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String initials() {
        return Arrays.stream(name.split(" ")).map(s -> s.substring(0, 1)).collect(Collectors.joining());
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "FakePerson{" + "name=" + name + ", image=" + image + ", pin=" + pin + ", email=" + email + ", address=" + address + ", phone=" + phone + ", password=" + password + '}';
    }
}
