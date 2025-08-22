public record KeyValue(String key, String value) {

    @Override
    public String toString() {
        return key + " " + value;
    }
}