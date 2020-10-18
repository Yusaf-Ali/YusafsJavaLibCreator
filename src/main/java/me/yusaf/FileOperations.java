package me.yusaf;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

public class FileOperations {
	public static List<File> getGenClassFileList(String rootName) {
		List<File> files = new ArrayList<File>();
		getGenClassFileList(rootName, files);
		return files;
	}

	public static void getGenClassFileList(String folderName, List<File> fileList) {
		File root = new File(folderName);
		File[] files = root.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				getGenClassFileList(files[i].getAbsolutePath(), fileList);
			} else {
				fileList.add(files[i]);
			}
		}
	}

	public static File createClassesFile(List<File> x) {
		File f = null;
		try {
			f = new File("javac_classes.txt");
			String classes = x.stream().map(m -> "\"" + m.getAbsolutePath().replaceAll("\\\\", "/") + "\"")
					.collect(Collectors.joining("\n"));
			PrintWriter pw = new PrintWriter(f);
			pw.println(classes);
			pw.flush();
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return f;
	}

	public static void deleteDir(File jarInput) {
		File[] contents = jarInput.listFiles();
		if (contents != null) {
			for (File f : contents) {
				if (!Files.isSymbolicLink(f.toPath())) {
					deleteDir(f);
				}
			}
		}
		jarInput.delete();
	}
}
