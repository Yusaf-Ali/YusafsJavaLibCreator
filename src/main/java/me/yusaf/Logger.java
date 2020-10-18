package me.yusaf;

import java.text.*;
import java.util.*;

public class Logger {
	private boolean enabled;
	private SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss.SSS", Locale.US);
	private String suffix;

	public void enable() {
		this.enabled = true;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public void log(String message) {
		if (enabled) {
			System.out.print(formatter.format(Calendar.getInstance().getTime()));
			System.out.print(" - " + suffix + ": ");
			System.out.println(message);
		}
	}
}
