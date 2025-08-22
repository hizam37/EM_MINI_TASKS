import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MapReduceFramework  {
    private final List<String> inputFiles;
    private final int numReduceTasks;
    private final int numWorkers;
    private final MapReduceFunctions functions;

    public MapReduceFramework(List<String> inputFiles, int numReduceTasks,
                              int numWorkers, MapReduceFunctions functions) {
        this.inputFiles = inputFiles;
        this.numReduceTasks = numReduceTasks;
        this.numWorkers = numWorkers;
        this.functions = functions;
    }

    public void execute() throws InterruptedException {
        Coordinator coordinator = new Coordinator(inputFiles, numReduceTasks, functions);
        ExecutorService executor = Executors.newFixedThreadPool(numWorkers);


        for (int i = 0; i < numWorkers; i++) {
            executor.execute(new Worker(i, coordinator));
        }


        while (!coordinator.isAllTasksCompleted()) {
            Thread.sleep(100);
        }

        executor.shutdown();
        if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
            executor.shutdownNow();
        }

        System.out.println("All tasks of mapreduce are completed");
    }


    public static void main(String[] args) {


        List<String> inputFiles = Arrays.asList("test1.txt", "test2.txt");
        int numReduceTasks = 4;
        int numWorkers = 3;

        MapReduceFunctions mapReduceFunctions = new MapReduceFunctions();


        MapReduceFramework framework = new MapReduceFramework(
                inputFiles, numReduceTasks, numWorkers, mapReduceFunctions
        );

        try {
            framework.execute();
            System.out.println("Results:");
            printResults(numReduceTasks);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            cleanTempFiles(inputFiles.size(), numReduceTasks);
        }
    }
    private static void cleanTempFiles(int numMapTasks, int numReduceTasks) {
        for (int i = 0; i < numMapTasks; i++) {
            for (int j = 0; j < numReduceTasks; j++) {
                String filename = "mr-" + i + "-" + j;
                try {
                    Files.deleteIfExists(Paths.get(filename));
                } catch (IOException e) {
                    e.getStackTrace();
                }
            }
        }
    }

    private static void printResults(int numReduceTasks) {
        for (int i = 0; i < numReduceTasks; i++) {
            String filename = "mr-out-" + i;
            try {
                Path path = Paths.get(filename);

                if (Files.exists(path)) {
                    System.out.println("File " + filename + ":");
                    Files.readAllLines(path).forEach(System.out::println);
                }
            } catch (IOException e) {
                System.out.println("Failed to read the file " + filename);
            }
        }
    }

}