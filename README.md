# MediaTool
Flexible tool to process HDHomeRun recordings and archive them

This tool automates the renaming of programs recorded by your HDHomeRun. It Parses the HDHomeRun tags out of recordings to determine what the show it and allows you to rename them based on a template of your choosing.

Contains the following options:

Option | Description
------------ | -------------
ProcessDelay | Configurable delay before processing file
ShowLookup | Augment information parsed out of the HDHomeRun tags with show information looked up from [theTVDB](https://www.thetvdb.com/)
RenameMedia | Rename the show based on a configurable template. Will create the directory if it does not exist
InfoCreator | Generate a .NFO file for your PlexServer/Kodi or other media client


Template Options:
-----------
All Text inside curly-braces is assumed to be a tag. Text outside braces is considered a string literal.

Example:

`{Show}/Season {Season}/{Show} - s{Season}s{Episode} - {Title}.{Format}`

Supported Tags
- {Show} - Name of the Show
- {Season} - Two digit Season of the show
- {Episode} - Two digit Episode Number of the show
- {Title} - Title of the Show
- {Format} - Extension of the show (Eg: MP4, MPG, etc)



### This is a work in Progress.
