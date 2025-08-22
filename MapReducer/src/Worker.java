import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Worker implements Runnable {
    private final Coordinator coordinator;
    private final int workerId;

    public Worker(int workerId, Coordinator coordinator) {
        this.workerId = workerId;
        this.coordinator = coordinator;
    }

    @Override
    public void run() {
        System.out.println("Worker " + workerId + " has started");

        while (true) {
            try {
                Task task = coordinator.getNextTask();

                if (task == null) {
                    Thread.sleep(100);
                    continue;
                }

                if (task.getType() == TaskType.SHUTDOWN) {
                    System.out.println("Worker " + workerId + " is shutting down");
                    break;
                }


                switch (task.getType()) {
                    case MAP:
                        if (task instanceof MapTask) {
                            processMapTask((MapTask) task);
                        } else {
                            System.err.println("Worker " + workerId + " object is not of type of map");
                        }
                        break;
                    case REDUCE:
                        if (task instanceof ReduceTask) {
                            processReduceTask((ReduceTask) task);
                        } else {
                            System.err.println("Worker " + workerId + " object is not of type of reduce");
                        }
                        break;
                    default:
                        System.err.println("Worker " + workerId + " unknown type of task: " + task.getType());
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                System.err.println("Worker " + workerId + " ошибка: " + e.getMessage());
            }
        }
    }

    private void processMapTask(MapTask task) throws IOException {
        System.out.println("Worker " + workerId + " is processing map task " + task.taskId() +
                " для файла: " + task.inputFile());


        if (!Files.exists(Paths.get(task.inputFile()))) {
            System.err.println("Worker " + workerId + " file does not exist: " + task.inputFile());
            coordinator.completeMapTask(task.taskId(), Collections.emptyMap());
            return;
        }


        String content = new String(Files.readAllBytes(Paths.get(task.inputFile())));


        List<KeyValue> keyValues = coordinator.getFunctions().map(content);


        Map<Integer, List<KeyValue>> grouped = new HashMap<>();
        for (int i = 0; i < task.numReduceTasks(); i++) {
            grouped.put(i, new ArrayList<>());
        }

        for (KeyValue kv : keyValues) {
            int bucket = Math.abs(kv.key().hashCode()) % task.numReduceTasks();
            grouped.get(bucket).add(kv);
        }


        Map<Integer, String> outputFiles = new HashMap<>();
        for (Map.Entry<Integer, List<KeyValue>> entry : grouped.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                String filename = "mr-" + task.taskId() + "-" + entry.getKey();
                writeKeyValuesToFile(filename, entry.getValue());
                outputFiles.put(entry.getKey(), filename);
            }
        }

        coordinator.completeMapTask(task.taskId(), outputFiles);
        System.out.println("Worker " + workerId + " completed map task " + task.taskId());
    }

    private void processReduceTask(ReduceTask task) throws IOException {
        System.out.println("Worker " + workerId + " is processing reduce task: " + task.taskId());


        List<String> intermediateFiles = coordinator.getIntermediateFilesForReduce(task.taskId());

        if (intermediateFiles == null || intermediateFiles.isEmpty()) {
            System.out.println("Worker " + workerId + " no files for reduce tasks " + task.taskId());
            coordinator.completeReduceTask(task.taskId());
            return;
        }


        List<KeyValue> allKeyValues = new ArrayList<>();
        for (String filename : intermediateFiles) {
            allKeyValues.addAll(readKeyValuesFromFile(filename));
        }

        if (allKeyValues.isEmpty()) {
            System.out.println("Worker " + workerId + " there's no data for reduce tasks " + task.taskId());
            coordinator.completeReduceTask(task.taskId());
            return;
        }


        allKeyValues.sort(Comparator.comparing(KeyValue::key));


        Map<String, List<String>> groupedValues = new LinkedHashMap<>();
        for (KeyValue kv : allKeyValues) {
            groupedValues.computeIfAbsent(kv.key(), k -> new ArrayList<>()).add(kv.value());
        }


        List<String> results = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : groupedValues.entrySet()) {
            String result = coordinator.getFunctions().reduce(entry.getValue());
            results.add(entry.getKey() + " " + result);
        }


        String outputFilename = "mr-out-" + task.taskId();
        Files.write(Paths.get(outputFilename), results);

        coordinator.completeReduceTask(task.taskId());
        System.out.println("Worker " + workerId + " completed reduce task  " + task.taskId());
    }

    private void writeKeyValuesToFile(String filename, List<KeyValue> keyValues) throws IOException {
        List<String> lines = keyValues.stream()
                .map(KeyValue::toString)
                .collect(Collectors.toList());

        Files.write(Paths.get(filename), lines);
    }

    private List<KeyValue> readKeyValuesFromFile(String filename) throws IOException {
        Path path = Paths.get(filename);
        if (!Files.exists(path)) {
            return Collections.emptyList();
        }

        List<String> lines = Files.readAllLines(path);
        return lines.stream()
                .map(line -> {
                    String[] parts = line.split(" ", 2);
                    return new KeyValue(parts[0], parts.length > 1 ? parts[1] : "");
                })
                .filter(kv -> !kv.key().isEmpty())
                .collect(Collectors.toList());
    }
}