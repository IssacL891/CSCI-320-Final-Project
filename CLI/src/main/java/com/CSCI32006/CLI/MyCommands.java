package com.CSCI32006.CLI;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
public class MyCommands {

	@ShellMethod(value = "Add numbers.", key = "sum")
	public int add(int a, int b) {
		return a + b;
	}
}
