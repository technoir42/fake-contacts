package com.sch.fakecontacts.model.generator;

public class GenerationOptions {
    private final int contactCount;
    private final String accountType;
    private final long groupId;
    private final boolean eraseExisting;
    private final boolean withEmails;
    private final boolean withPhones;

    GenerationOptions(Builder builder) {
        contactCount = builder.contactCount;
        accountType = builder.accountType;
        groupId = builder.groupId;
        eraseExisting = builder.eraseExisting;
        withEmails = builder.withEmails;
        withPhones = builder.withPhones;
    }

    public int getContactCount() {
        return contactCount;
    }

    public String getAccountType() {
        return accountType;
    }

    public long getGroupId() {
        return groupId;
    }

    public boolean eraseExisting() {
        return eraseExisting;
    }

    public boolean withEmails() {
        return withEmails;
    }

    public boolean withPhones() {
        return withPhones;
    }

    public static class Builder {
        private int contactCount;
        private String accountType = "com.google";
        private long groupId = -1;
        private boolean eraseExisting;
        private boolean withEmails;
        private boolean withPhones;

        public Builder setContactCount(int contactCount) {
            this.contactCount = contactCount;
            return this;
        }

        public Builder setAccountType(String accountType) {
            this.accountType = accountType;
            return this;
        }

        public Builder setGroupId(long groupId) {
            this.groupId = groupId;
            return this;
        }

        public Builder setEraseExisting(boolean eraseExisting) {
            this.eraseExisting = eraseExisting;
            return this;
        }

        public Builder withEmails() {
            this.withEmails = true;
            return this;
        }

        public Builder withPhones() {
            this.withPhones = true;
            return this;
        }

        public GenerationOptions build() {
            return new GenerationOptions(this);
        }
    }
}
