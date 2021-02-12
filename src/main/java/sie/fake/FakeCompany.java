package sie.fake;

/**
 *
 * @author Håkan Lidén
 *
 */
class FakeCompany {

    private String companyName;
    private String orgNum;
    private String vatCode;
    private String address;
    private String bankgiro;
    private String contact;
    private String contactEmail;
    private String contactPhone;

    public FakeCompany() {
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getOrgNum() {
        return orgNum;
    }

    public void setOrgNum(String orgNum) {
        this.orgNum = orgNum;
    }

    public String getVatCode() {
        return vatCode;
    }

    public void setVatCode(String vatCode) {
        this.vatCode = vatCode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBankgiro() {
        return bankgiro;
    }

    public void setBankgiro(String bankgiro) {
        this.bankgiro = bankgiro;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    @Override
    public String toString() {
        return "FakeCompany{" + "companyName=" + companyName + ", orgNum=" + orgNum + ", vatCode=" + vatCode + ", address=" + address + ", bankgiro=" + bankgiro + ", contact=" + contact + ", contactEmail=" + contactEmail + ", contactPhone=" + contactPhone + '}';
    }

}
