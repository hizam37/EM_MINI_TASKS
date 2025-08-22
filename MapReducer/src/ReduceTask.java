record ReduceTask(int taskId) implements Task {
    @Override
    public TaskType getType() {
        return TaskType.REDUCE;
    }
}

