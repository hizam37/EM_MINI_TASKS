record MapTask(int taskId, String inputFile, int numReduceTasks) implements Task {
    @Override
    public TaskType getType() {
        return TaskType.MAP;
    }
}