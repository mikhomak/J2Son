class TestObject {
    private String name;
    private String lastName;
    private TestObjectDependency testObjectDependency;

    public TestObjectDependency getTestObjectDependency() {
        return testObjectDependency;
    }

    public void setTestObjectDependency(TestObjectDependency testObjectDependency) {
        this.testObjectDependency = testObjectDependency;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
