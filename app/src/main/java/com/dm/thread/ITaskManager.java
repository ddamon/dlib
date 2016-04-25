package com.dm.thread;

public interface ITaskManager {
	public void addTask(Task task);

	public void removeTask(String taskId, boolean cancelIfRunning);

	public void removeAllTask(boolean cancelIfRunning);

	public int getTaskCount(String taskId);
}
