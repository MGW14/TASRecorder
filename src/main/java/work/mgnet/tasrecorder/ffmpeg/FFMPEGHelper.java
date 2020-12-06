package work.mgnet.tasrecorder.ffmpeg;

import com.google.common.collect.ImmutableList;

public class FFMPEGHelper extends ConsoleBuffer {

	public FFMPEGHelper(String ffmpeg) {
		super(ffmpeg);
	}
	
	public void setArgument(String key, String value) {
		addAllArguments(ImmutableList.of(key, value));
	}
	
}
