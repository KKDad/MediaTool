# MediaTool
MediaTool is a flexible media manager designed to process HDHomeRun recordings and archive them.

This tool can automate the process of renaming of recording make by your HDHomeRun. It will parse the HDHomeRun tags out of recordings to determine the show it and allows you to rename them based on a template of your choosing. You can also optional have .NFO files create to ease importing your recordings into Plex or Kodi.



Contains the following options:

Option | Description
------------ | -------------
ProcessDelay | Configurable delay before processing file. Useful if you wish to keep the recording availible to 
ShowLookup | Augment information parsed out of the HDHomeRun tags with show information looked up from [theTVDB](https://www.thetvdb.com/)
RenameMedia | Rename the show based on a configurable template. Will create the directory if it does not exist
InfoCreator | Generate a .NFO file for your PlexServer/Kodi or other media client
CommercialDetector | Detect commercials using [Comskip](https://github.com/erikkaashoek/Comskip). The results of the commercial detection can be feed into a Chapter list for fast forward or can be used to create a cut list to strip commercials from the recording.  
RemoveCommercials | Use [ffmpeg](https://ffmpeg.org/) to remove commercials. Requires a cut list as created by the CommercialDetect option 
Transcoding | Convert the recordings to h265 to save storage space.   


Template Options:
-----------
All Text inside curly-braces is assumed to be a tag. Text outside braces is considered a string literal.

Example:

`{Show}/Season {Season}/{Show} - s{Season}s{Episode} - {Title}.{Format}`

Supported Tags
- {Show} - Name of the Show (Eg: (S.W.A.T.))
- {Series Name} - Name of the Show with Year if multiple shows with the same name exist (Eg: S.W.A.T (2017))
- {Season} - Two digit Season of the show
- {Episode} - Two digit Episode Number of the show
- {Title} - Title of the Show. When absent, wil return {episodeName}
- {Format} - Extension of the show (Eg: MP4, MPG, etc)

Additional Tags from [theTVDB](https://www.thetvdb.com/):
- {absoluteNumber} - Absolute number of the episode
- {airedEpisodeNumber} - Aired season number
- {airedSeason} - Aired episode number
- {dvdEpisodeNumber} - DVD season number
- {dvdSeason} - DVD episode number
- {episodeName} - Title of the show
- {firstAired} - Date when Episode was originally showwn on TV
- {episodeOverview} - Synopsis of the Episode
- {banner} -
- {id} - theTvVB show Id
- {network} - Network that show is on
- {showOverview} - Short description of the Show
- {status} - Status of the show. One of: Continuing, Ended
