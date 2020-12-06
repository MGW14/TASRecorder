package work.mgnet.tasrecorder.ffmpeg;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConsoleBuffer {

	private String ffmpegDir;
	private List<String> args = new ArrayList<>();
	
	public ConsoleBuffer(String ffmpegDir) {
		this.ffmpegDir = ffmpegDir;
	}
	
	public void addAllArguments(List<String> args) {
		this.args.addAll(args);
	}
	
	public Process run() throws IOException {
		String command = ffmpegDir + "/ffmpeg.exe";
		for (String arg : args) {
			command = command + " " + arg;
		}
		System.out.println("Running Command: " + command);
		
		ProcessBuilder builder = new ProcessBuilder(command.split(" "));
		builder.directory(new File(ffmpegDir));
		return builder.start();
	}
	
}
