# StaffNotes

A simple Minecraft server plugin that adds the command /notes so that server admins can store and review notes on players. 

Can be used to store information such previous staff interactions both good or bad or as a quick Ban Evasion reference for players whos sibilines or roommates get banned but they still want to play. 

Notes are stored based on user UUID so will persist username changes. 
Notes can be made for players who have never joined the server

## Commands:
/notes Add \<PlayerName\> \<NoteType\> \<Note\>  
/notes Remove \<PlayerName\> [NoteType]  
/notes Get \<PlayerName\> [NoteType]  
/notes Get All  

## Permissions:
notes.default&emsp;&emsp;&nbsp;Plugin won't listen to you without this permission  
notes.add&emsp;&emsp;&emsp;&nbsp;&nbsp;Gives permissions to add notes  
notes.remove&emsp;&emsp;Gives permissions to remove notes  
notes.view&emsp;&emsp;&emsp;&nbsp;Gives permissions to get notes  
notes.view.all&emsp;&emsp;Gives permissions to get all notes  

## ToDo
- Modify depreciated player.sendMessage()
- Comment code
- Verify all code paths
- Handle Bedrock players though Geyser better
