package bankcredits.model.entity;

public class LegalEntity {
    private int legalEntityId;
    private String legalEntityName;
    private String ownershipType;
    private String legalAddress;
    private String phoneNumber;
    private String contactPerson;

    public LegalEntity() {
    }

    public LegalEntity(int legalEntityId, String legalEntityName, String ownershipType,
                       String legalAddress, String phoneNumber, String contactPerson) {
        this.legalEntityId = legalEntityId;
        this.legalEntityName = legalEntityName;
        this.ownershipType = ownershipType;
        this.legalAddress = legalAddress;
        this.phoneNumber = phoneNumber;
        this.contactPerson = contactPerson;
    }

    public int getLegalEntityId() {
        return legalEntityId;
    }

    public void setLegalEntityId(int legalEntityId) {
        this.legalEntityId = legalEntityId;
    }

    public String getLegalEntityName() {
        return legalEntityName;
    }

    public void setLegalEntityName(String legalEntityName) {
        this.legalEntityName = legalEntityName;
    }

    public String getOwnershipType() {
        return ownershipType;
    }

    public void setOwnershipType(String ownershipType) {
        this.ownershipType = ownershipType;
    }

    public String getLegalAddress() {
        return legalAddress;
    }

    public void setLegalAddress(String legalAddress) {
        this.legalAddress = legalAddress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    @Override
    public String toString() {
        return legalEntityId + " - " + legalEntityName;
    }
}
