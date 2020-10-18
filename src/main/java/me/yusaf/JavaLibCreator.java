package me.yusaf;

import java.io.*;
import java.util.*;

public class JavaLibCreator {
	private boolean javacVerbose;
	private boolean jarVerbose;
	private boolean isCompiled;
	private String jarInputFolderName;
	private String jarOutputFolderName;
	private String jarOutputFileName;
	private String javacRootFolder;
	private String javaHome;
	private Logger javacLog = new Logger();
	private Logger jarLog = new Logger();

	public JavaLibCreator() {
		this.javaHome = System.getProperty("java.home");
		this.javacLog.setSuffix("JAVAC.EXE");
		this.jarLog.setSuffix("JAR.EXE");
		this.isCompiled = false;
	}

	/**
	 * Enable non-cmd internal method logs
	 * 
	 * @return
	 */
	public JavaLibCreator enableJavacVerbose() {
		this.javacVerbose = true;
		javacLog.enable();
		return this;
	}

	/**
	 * Enable non-cmd with or without cmd logs
	 * 
	 * @param cmdLog
	 *            True to let cmd process return output to this program
	 * @return
	 */
	public JavaLibCreator enableJarVerbose(boolean cmdLog) {
		this.jarVerbose = cmdLog;
		jarLog.enable();
		return this;
	}

	/**
	 * Input folder name for createJar method. Don't use if creating jar after compiling.
	 * 
	 * @param jarInputFolderName
	 */
	public void setJarInputFolderName(String jarInputFolderName) {
		this.jarInputFolderName = jarInputFolderName;
	}

	/**
	 * Output folder where lib should be placed by createJar method.
	 * 
	 * @param outputFolder
	 * @return
	 */
	public JavaLibCreator setOutputFolder(String outputFolder) {
		this.jarOutputFolderName = outputFolder;
		return this;
	}

	/**
	 * File name with extension for lib that is generated by createJar method.
	 * 
	 * @param jarName
	 * @return
	 */
	public JavaLibCreator setJarOutputName(String jarName) {
		this.jarOutputFileName = jarName;
		return this;
	}

	/**
	 * If compiling, set this folder to specify folder containing .java files that will
	 * be compiled to .class files by compile method.
	 * 
	 * @param rootFolder
	 * @return
	 */
	public JavaLibCreator setCompileRootFolder(String rootFolder) {
		this.javacRootFolder = rootFolder;
		return this;
	}

	/**
	 * Custom java home location on which jar.exe and javac.exe depends. Must not set
	 * if not required.
	 * 
	 * @param home
	 *            Javahome location without bin in the path
	 * @return
	 */
	public JavaLibCreator customJavaHomeLocation(String home) {
		this.javaHome = home;
		return this;
	}

	/**
	 * Compile method that will generate a temporary folder containing all compiled classes.
	 * Uses cmd for compiling. Uses ProcessBuilder. Uses a temporary classes file to hold all
	 * paths to classes with their names, and removes automatically.
	 * 
	 * @return
	 * @throws FileNotFoundException
	 *             If Javac is not found
	 * @throws IOException
	 *             If Process from ProcessBulder throws IOException
	 * @throws InterruptedException
	 *             If Process.waitFor throws InterruptedException
	 */
	public JavaLibCreator compile() throws FileNotFoundException, IOException, InterruptedException {
		javacLog.log("Init Compile");
		File javac = new File(bin("javac.exe"));
		File classesFile = null;
		if (javac.exists()) {
			List<File> list = FileOperations.getGenClassFileList(javacRootFolder);
			javacLog.log("Create Classes");
			classesFile = FileOperations.createClassesFile(list);
			StringBuilder sb = new StringBuilder(70);
			sb.append("\"\"");
			sb.append(javac.getAbsolutePath());
			sb.append("\" -d nifCompiled -sourcepath ");
			sb.append("");
			sb.append("-classpath @").append(classesFile.getName());
			sb.append("\"");
			ProcessBuilder builder = new ProcessBuilder("cmd", "/c", sb.toString());
			javacLog.log("Start Process");
			Process p = builder.start();
			javacLog.log("Waiting");
			if (javacVerbose)
				ProcessHelper.handleOutput(p);
			p.waitFor();
			javacLog.log("End Process");
		} else {
			throw new FileNotFoundException("Javac.exe not found!");
		}
		if (classesFile != null) {
			javacLog.log("Delete Classes file");
			classesFile.delete();
		}
		isCompiled = true;
		return this;
	}

	/**
	 * Creates lib as a jar file with default Manifest. Uses cmd to perform the task.
	 * Uses ProcessBuilder to execute cmd program. If compile method was called before,
	 * deletes nifCompiled temp folder. CommandLine program has its own log and can be disabled
	 * by passing false to {@link JavaLibCreator#enableJarVerbose(boolean)}
	 * 
	 * @return
	 * @throws FileNotFoundException
	 *             If Javac is not found
	 * @throws IOException
	 *             If Process from ProcessBulder throws IOException
	 * @throws InterruptedException
	 *             If Process.waitFor throws InterruptedException
	 */
	public JavaLibCreator createJar() throws FileNotFoundException, IOException, InterruptedException {
		jarLog.log("Init JarPackage");
		File jarExe = new File(bin("jar.exe"));
		File jarFile = new File(jarOutputFolderName, jarOutputFileName);
		if (isCompiled)
			jarInputFolderName = "nifCompiled";
		if (jarExe.exists()) {
			StringBuilder sb = new StringBuilder();
			sb.append("\"");
			sb.append(jarExe.getAbsolutePath());
			sb.append("\" cf");
			if (jarVerbose)
				sb.append("v");
			sb.append(" \"");
			sb.append(jarFile.getAbsolutePath());
			sb.append("\" -C ");
			sb.append(jarInputFolderName);
			sb.append(" .");
			ProcessBuilder builder = new ProcessBuilder("cmd", "/c", sb.toString());
			jarLog.log("Start Packaging");
			Process x = builder.start();
			jarLog.log("Waiting");
			if (jarVerbose)
				ProcessHelper.handleOutput(x);
			x.waitFor();
			jarLog.log("End Packaging");
			if (isCompiled) {
				jarLog.log("Remove Temp Compile Folder");
				File jarInput = new File(jarInputFolderName);
				FileOperations.deleteDir(jarInput);
			}
		} else {
			throw new FileNotFoundException("Jar.exe not found!");
		}
		return this;
	}

	private String bin(String program) {
		return javaHome + "/bin/" + program;
	}
}
