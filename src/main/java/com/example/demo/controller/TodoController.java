package com.example.demo.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.model.TodoItem;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Controller
public class TodoController {

	private final String DATA_FILE = "todos.txt";
	private AtomicLong nextId = new AtomicLong(1);
	private List<TodoItem> todoList;
	
	public TodoController() {
		this.todoList = new ArrayList<TodoItem>();
	}
	
	@PostConstruct
	private void loadTodosFromFile() {
		try (BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE))) {
			String line;
			while ((line = reader.readLine()) != null) {
				String[] parts = line.split(",");
				if (parts.length == 2) {
					String task = parts[0];
					boolean completed = Boolean.parseBoolean(parts[1]);
					TodoItem item = new TodoItem(task);
					item.setId(nextId.getAndIncrement());
					item.setCompleted(completed);
					this.todoList.add(item);
				}
			}
			
			// Ensure nextId is greater than the maximum ID loaded
			todoList.stream().map(TodoItem::getId).max(Long::compare).ifPresent(maxId -> nextId.set(maxId + 1));
		} catch (IOException ex) {
			System.err.println("Error loading todos from file: " + ex.getMessage());
		}
	}
	
	@PreDestroy
	private void saveTodosToFile() {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_FILE))) {
			for (TodoItem item : this.todoList) {
				writer.write(item.getTask() + "," + item.isCompleted());
				writer.newLine();
			}
		} catch (IOException ex) {
			System.err.println("Error saving Todos to file: " + ex.getMessage());
		}
	}
	
	@GetMapping("/")
	public String showAll(@RequestParam(name = "filter", required = false) String filter, @RequestParam(name = "sort", required = false) String sort, Model model) {
		List<TodoItem> list = todoList;
		if (filter != null && !filter.isEmpty() && !filter.equals("all")) {
			final boolean completed = filter.equals("completed");
			list = todoList.stream()
					.filter(todo -> todo.isCompleted() == completed)
					.collect(Collectors.toList());
		}
		
		if (sort != null && !sort.isEmpty() && !sort.equals("")) {
			if (sort.equals("alphabet")) {
				list = list.stream()
						.sorted(Comparator.comparing(TodoItem::getTask))
						.collect(Collectors.toList());
			} else if (sort.equals("completion")) {
				list = list.stream()
						.sorted(Comparator.comparing(TodoItem::isCompleted))
						.collect(Collectors.toList());
			}
		}
		
		model.addAttribute("todoList", list);
		model.addAttribute("filter", filter);
		model.addAttribute("sort", sort);
		return "todolist";
	}
	
	@PostMapping("/addTodo")
	public String addTodo(@RequestParam(name = "task") String task) {
		if (task != null && !task.trim().isEmpty()) {
			TodoItem item = new TodoItem(task);
			item.setId(this.nextId.getAndIncrement());
			this.todoList.add(item);
			saveTodosToFile();
		}
		return "redirect:/";
	}
	
	@PostMapping("/toggleComplete")
	public String toggleComplete(@RequestParam(name = "id") Long id, @RequestParam(value = "completed", required = false) String completed) {
		Optional<TodoItem> itemOptional = findById(id);
		if (itemOptional.isPresent()) {
			TodoItem item = itemOptional.get();
			item.setCompleted(completed != null && completed.equals("on"));
			saveTodosToFile();
		}
		return "redirect:/";
	}
	
	private Optional<TodoItem> findById(Long id) {
		return this.todoList.stream().filter(item -> item.getId().equals(id)).findFirst();
	}
	
	@PostMapping("/deleteTodo")
	public String deleteTodo(@RequestParam(name = "id") Long id) {
		this.todoList.removeIf(item -> item.getId().equals(id));
		saveTodosToFile();
		return "redirect:/";
	}
	
	@PostMapping("/saveTodo")
	public String saveTodo(@RequestParam(name = "id") Long id, @RequestParam(name = "task") String task) {
		Optional<TodoItem> itemOptional = findById(id);
		if (itemOptional.isPresent()) {
			TodoItem item = itemOptional.get();
			item.setTask(task);			
			saveTodosToFile();
		}
		saveTodosToFile();
		return "redirect:/";
	}
	
}
