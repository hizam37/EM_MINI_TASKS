import java.util.ArrayList;
import java.util.List;

public class MapReduceFunctions {

    public List<KeyValue> map( String content) {
        List<KeyValue> result = new ArrayList<>();

        String[] words = content.toLowerCase()
                .replaceAll("[^a-zA-Zа-яА-Я0-9\\s]", " ")
                .split("\\s+");

        for (String word : words) {
            if (!word.trim().isEmpty()) {
                result.add(new KeyValue(word.trim(), "1"));
            }
        }
        return result;
    }


    public String reduce( List<String> values) {
        int sum = 0;
        for (String value : values) {
            try {
                sum += Integer.parseInt(value);
            } catch (NumberFormatException e) {
                System.out.println(e.getMessage());
            }
        }
        return String.valueOf(sum);
    }
}