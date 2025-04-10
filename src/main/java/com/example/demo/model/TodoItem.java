package com.example.demo.model;

import java.util.Objects;

public class TodoItem {
		
	private Long id;
	private String task;
	private boolean isCompleted;
	
	public TodoItem() {
		
	}
	
	public TodoItem(String task) {
		this.task = task;
		this.isCompleted = false;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getTask() {
		return task;
	}
	
	public void setTask(String task) {
		this.task = task;
	}
	
	public boolean isCompleted() {
		return isCompleted;
	}
	
	public void setCompleted(boolean isCompleted) {
		this.isCompleted = isCompleted;
	}

	@Override
	public String toString() {
		return "TodoItem [id=" + id + ", task=" + task + ", isCompleted=" + isCompleted + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, isCompleted, task);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TodoItem other = (TodoItem) obj;
		return id == other.id && isCompleted == other.isCompleted && Objects.equals(task, other.task);
	}
	
}
