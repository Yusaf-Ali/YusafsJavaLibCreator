package me.yusaf;

import java.io.*;

public class ProcessHelper {
	public static void handleOutput(Process x) {
		try {
			x.getInputStream().transferTo(System.out);
			x.getErrorStream().transferTo(System.err);
			while (x.isAlive()) {
				if (x.getErrorStream().available() > 0) {
					System.err.println("E: " + (char) x.getErrorStream().read());
				}
				if (x.getInputStream().available() > 0) {
					System.err.println("I: " + (char) x.getInputStream().read());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
