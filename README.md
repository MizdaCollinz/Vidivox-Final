# Vidivox

### Run the application from the jar file:
`java -jar VidivoxReleaseBC.jar`

Ensure that the jar is being run on Java 7 as 8 does not seem to correctly load the button icons.

All files generated and output by the program are stored in a folder named **"CustomVidivoxFolder"** which is in the current directory, alternatively use the **Output Folder** to set a custom folder to output files into which will be in the current directory.

Required Libraries are stored in the jar file and additionally provided externally in a folder for use with the source code.
The "imgs" folder must be in the same folder as the jar file to allow icons to be loaded into the program.

### Usage instructions

1. Press the **Select Video** button before doing anything else to select and autoplay a video
2. Press the **Synthesize Audio** button to generate MP3 audio files using text to speech
3. Press the **Select MP3 File** button to add an MP3 file to the video at any point in the video
4. After selecting an mp3 file it will be previewed alongside the video 
  * Previewing audio is not compatible with Fast Forward and Rewind, please finalize the merge to make full use of player functionality
  * To finalize the embedding of the audio, press the **Merge Selected Audio** button to output a video file with the **Selected** audio embedded into it.
  * To re-sync the preview use the ** File -> Reset Preview** button to restart the video and audio together
5. Alternatively to revert to the original version of a video before audio was embedded, simply click **Strip Audio** and select **Revert to pre-merged video**
6. The **Strip Audio** button may also be used to remove all audio from the selected video file and output an audioless video file 
7. To use Vidivox as a more standard player it is recommended to hide the sidepanel using **View -> Toggle Side Panel **
