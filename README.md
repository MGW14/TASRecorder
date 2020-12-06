# TASRecorder
The TASRecorder Mod is an addition to the TASTickratechanger by <a href="https://github.com/ScribbleLP/TASTickratechanger">@ScribbleLP</a>.
It records in 30 fps depending on your Tickrate and then speeds it up without the use of an Video Editing software.

It currently only supports 30 fps recordings and can slow down the game a bit when using shaders (or being on a low end pc).
I will add support for 60 fps and sound somewhen later

<h3>This requires FFMpeg</h3>
You have to put <a href="https://github.com/BtbN/FFmpeg-Builds/releases">ffmpeg gpl</a> to your Documents Folder
<img src="https://i.ibb.co/QcCzywx/image.png"></img>
<h3>How to install</h3>
Download Gradle or the Gradle Wrapper and run the task "shadow"
<h3>How to use</h3>
Once you are in your world, set the default tickrate and then set the quality of your TAS-Video by using /quality (low-ultra)
Quality Settings basically just set the bitrate of ffmpeg. I recommend Medium for Lower-End PC's and Low for short/8mb tas files.

Then type in /record to start the recording.
It will not record the Main Menu, but will automatically start after you join a world again.

Type /record again to stop recording.

Now you just need to run /generate and your Tas will appear under Documents/ffmpeg.
