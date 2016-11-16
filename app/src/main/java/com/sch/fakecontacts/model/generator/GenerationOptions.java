package com.sch.fakecontacts.model.generator;

public class GenerationOptions {
    private final int contactCount;
    private final String accountType;
    private final long groupId;
    private final boolean eraseExisting;
    private final boolean withEmails;
    private final boolean withPhones;
    private final boolean withAddresses;
    private final boolean withAvatars;
    private final boolean withEvents;

    GenerationOptions(Builder builder) {
        contactCount = builder.contactCount;
        accountType = builder.accountType;
        groupId = builder.groupId;
        eraseExisting = builder.eraseExisting;
        withEmails = builder.withEmails;
        withPhones = builder.withPhones;
        withAddresses = builder.withAddresses;
        withAvatars = builder.withAvatars;
        withEvents = builder.withEvents;
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

    public boolean withAddresses() {
        return withAddresses;
    }

    public boolean withAvatars() {
        return withAvatars;
    }

    public boolean withEvents() {
        return withEvents;
    }

    public static class Builder {
        int contactCount;
        String accountType = "com.google";
        long groupId = -1;
        boolean eraseExisting;
        boolean withEmails;
        boolean withPhones;
        boolean withAddresses;
        boolean withAvatars;
        boolean withEvents;

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
            withEmails = true;
            return this;
        }

        public Builder withPhones() {
            withPhones = true;
            return this;
        }

        public Builder withAddresses() {
            withAddresses = true;
            return this;
        }

        public Builder withAvatars() {
            withAvatars = true;
            return this;
        }

        public Builder withEvents() {
            withEvents = true;
            return this;
        }

        public GenerationOptions build() {
            return new GenerationOptions(this);
        }
    }
}
