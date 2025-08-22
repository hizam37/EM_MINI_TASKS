import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Coordinator {
    private final int numReduceTasks;
    private final List<String> inputFiles;
    private final MapReduceFunctions functions;

    private final BlockingQueue<Task> mapTasks = new LinkedBlockingQueue<>();
    private final BlockingQueue<Task> reduceTasks = new LinkedBlockingQueue<>();
    private final Map<Integer, List<String>> intermediateFiles = new ConcurrentHashMap<>();
    private final AtomicInteger completedMapTasks = new AtomicInteger(0);
    private final AtomicInteger completedReduceTasks = new AtomicInteger(0);
    private volatile boolean allMapTasksCompleted = false;

    public Coordinator(List<String> inputFiles, int numReduceTasks, MapReduceFunctions functions) {
        this.inputFiles = inputFiles;
        this.numReduceTasks = numReduceTasks;
        this.functions = functions;
        initializeTasks();
    }

    private void initializeTasks() {

        for (int i = 0; i < inputFiles.size(); i++) {
            mapTasks.add(new MapTask(i, inputFiles.get(i), numReduceTasks));
        }


        for (int i = 0; i < numReduceTasks; i++) {
            intermediateFiles.put(i, new CopyOnWriteArrayList<>());
        }

        for (int i = 0; i < numReduceTasks; i++) {
            reduceTasks.add(new ReduceTask(i));
        }
    }

    public synchronized Task getNextTask() {
        if (!mapTasks.isEmpty()) {
            Task task = mapTasks.poll();
            System.out.println("Issued " + task.getType() +  "Task" +
                    (task instanceof MapTask ? ((MapTask) task).taskId() : "") +
                    (task instanceof MapTask ? " For the file: " + ((MapTask) task).inputFile() : ""));
            return task;
        }


        if (completedMapTasks.get() == inputFiles.size() && !allMapTasksCompleted) {
            allMapTasksCompleted = true;
            System.out.println("All map tasks are completed, and now its at reduce phase");
        }


        if (allMapTasksCompleted && !reduceTasks.isEmpty()) {
            Task task = reduceTasks.poll();
            System.out.println("Issued " + task.getType() + " task " +
                    (task instanceof ReduceTask ? ((ReduceTask) task).taskId() : ""));
            return task;
        }


        if (completedReduceTasks.get() == numReduceTasks) {
            System.out.println("All tasks are completed, sending completion signal");
            return new ShutdownTask();
        }


        return null;
    }

    public synchronized void completeMapTask(int taskId, Map<Integer, String> outputFiles) {
        completedMapTasks.incrementAndGet();


        for (Map.Entry<Integer, String> entry : outputFiles.entrySet()) {
            intermediateFiles.get(entry.getKey()).add(entry.getValue());
        }

        System.out.println("Map task is completed " + taskId + " (" + completedMapTasks.get() + "/" + inputFiles.size() + ")");
    }

    public synchronized void completeReduceTask(int taskId) {
        completedReduceTasks.incrementAndGet();
        System.out.println("Reduce task is completed" + taskId + " (" + completedReduceTasks.get() + "/" + numReduceTasks + ")");
    }

    public List<String> getIntermediateFilesForReduce(int reduceTaskId) {
        return intermediateFiles.get(reduceTaskId);
    }

    public boolean isAllTasksCompleted() {
        return completedMapTasks.get() == inputFiles.size() &&
                completedReduceTasks.get() == numReduceTasks;
    }

    public MapReduceFunctions getFunctions() {
        return functions;
    }
}